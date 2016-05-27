package name.valery1707.megatel.sorm.api.account;

import name.valery1707.megatel.sorm.api.auth.AccountRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.Account_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static name.valery1707.megatel.sorm.DateUtils.parseDate;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping("/api/account")
public class AccountController {
	@Inject
	private AccountRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<Account, AccountFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Account, AccountFilter>(SpecificationMode.AND)
				.withNumber(AccountFilter::getId, Account_.id)
				.withString(AccountFilter::getUsername, Account_.username)
				.withEquals(AccountFilter::isActive, Account_.isActive)
				.withEquals(AccountFilter::getRole, Account_.role)
				.withDate(AccountFilter::getActiveUntil, Account_.activeUntil)
				.withString(AccountFilter::getAgency, Account_.agency)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<AccountDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault(sort = "id", direction = ASC) Pageable pageable,
			@RequestBody(required = false) AccountFilter filter
	) {
		accountService.requireAnyRole(Account.Role.SUPER);
		Specification<Account> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(AccountDto::new);
	}

	@RequestMapping
	public AccountDto get(@RequestParam long id) {
		accountService.requireAnyRole(Account.Role.SUPER);
		Account entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", "Account", id));
		}
		return new AccountDto(entity);
	}

	@Inject
	private PasswordEncoder passwordEncoder;

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid AccountDto dto, BindingResult validation) {
		accountService.requireAnyRole(Account.Role.SUPER);

		if (validation.getErrorCount() > 0) {
			Map<String, List<FieldError>> fieldErrorMap = validation.getFieldErrors()
					.stream()
					.collect(Collectors.groupingBy(FieldError::getField, Collectors.toList()));
			return ResponseEntity.badRequest().body(fieldErrorMap);
		}

		Account entity = null;
		if (dto.getId() != 0) {
			entity = repo.findOne(dto.getId());
		}
		if (entity == null) {
			entity = new Account();
		}

		entity.setUsername(dto.getUsername());
		if (dto.getId() != 0 && dto.getOldPassword() != null && dto.getNewPassword() != null) {
			if (!passwordEncoder.matches(dto.getOldPassword(), entity.getPassword())) {
				throw new AccessDeniedException("Old password is invalid");
			}
			entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		} else if (dto.getId() == 0 && dto.getPassword() == null) {
			throw new AccessDeniedException("Password required");//todo Different message
		} else if (dto.getId() == 0 && dto.getPassword() != null) {
			entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		}
		entity.setActive(dto.isActive());
		entity.setRole(dto.getRole());
		entity.setActiveUntil(parseDate(dto.getActiveUntil()));
		entity.setAgency(dto.getAgency());
		repo.save(entity);
		return ResponseEntity.ok(Collections.emptyMap());
	}
}
