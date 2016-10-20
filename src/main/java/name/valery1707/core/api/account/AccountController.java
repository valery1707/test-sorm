package name.valery1707.core.api.account;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.api.auth.AccountRepo;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Account_;
import name.valery1707.core.domain.Event.EventType;
import name.valery1707.megatel.sorm.api.agency.AgencyController;
import name.valery1707.megatel.sorm.api.agency.AgencyDto;
import name.valery1707.megatel.sorm.api.agency.AgencyRepo;
import name.valery1707.megatel.sorm.domain.Agency_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static name.valery1707.core.utils.DateUtils.parseDate;
import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@RequestMapping("/api/account")
public class AccountController extends BaseEntityController<Account, AccountRepo, AccountFilter, AccountDto> {
	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AccountRepo repo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AgencyRepo agencyRepo;

	@Inject
	private AgencyController agencyController;

	/**
	 * (username = :username) AND (id != :id) AND (isActive IS TRUE)
	 */
	private SpecificationBuilder<Account, AccountDto> validateUsernameDistinct;

	public AccountController() {
		super("account");
	}

	@Override
	protected SpecificationBuilder<Account, AccountFilter> buildUserFilter() {
		return new SpecificationBuilder<Account, AccountFilter>(SpecificationMode.AND)
				.withNumber(AccountFilter::getId, Account_.id)
				.withString(AccountFilter::getUsername, Account_.username)
				.withEquals(AccountFilter::isActive, Account_.isActive)
				.withEquals(AccountFilter::getRole, Account_.role)
				.withDate(AccountFilter::getActiveUntil, Account_.activeUntil)
				.withString(AccountFilter::getAgencyName, Account_.agency, Agency_.name)
				;
	}

	@Override
	protected EventType eventCreate() {
		return EventType.ADMIN_ACCOUNT_CREATE;
	}

	@Override
	protected EventType eventRead() {
		return null;
	}

	@Override
	protected EventType eventUpdate() {
		return EventType.ADMIN_ACCOUNT_UPDATE;
	}

	@Override
	protected EventType eventDelete() {
		return EventType.ADMIN_ACCOUNT_DELETE;
	}

	@Override
	protected EventType eventFind() {
		return accountService().getCurrentAuditor().getRole().equals(Account.Role.ADMIN)
				? EventType.ADMIN_ACCOUNT_LIST
				: EventType.SUPERVISOR_ACCOUNT_LIST;
	}

	@PostConstruct
	public void init() {
		super.init();
		validateUsernameDistinct = new SpecificationBuilder<Account, AccountDto>(SpecificationMode.AND)
				.withEquals(AccountDto::getUsername, Account_.username)
				.withCustomSimple(AccountDto::getId, Account_.id, cb -> (expression, value) -> cb.notEqual(expression, value))
				.withCustomSimple(AccountDto::isActive, Account_.isActive, cb -> (expression, value) -> cb.isTrue(expression))
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<AccountDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault(sort = "id", direction = ASC) Pageable pageable,
			@RequestBody(required = false) AccountFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}

	@Inject
	private PasswordEncoder passwordEncoder;

	@Override
	protected void validate(AccountDto dto, BindingResult validation) {
		if (dto.getUsername() != null) {
			long count = repo.count(validateUsernameDistinct.build(dto));
			if (count > 0) {
				validation.rejectValue("username", "{Account.username.constraint.notUnique}", "Username must be unique");
			}
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid AccountDto dto, BindingResult validation) {
		return super.save(dto, validation);
	}

	@Override
	protected void dto2domain(AccountDto dto, Account entity) {
		entity.setUsername(dto.getUsername());
		if (dto.getId() != 0 && dto.getNewPassword() != null) {
			entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		} else if (dto.getId() == 0 && dto.getPassword() != null) {
			entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		}
		entity.setActive(dto.isActive());
		entity.setRole(dto.getRole());
		entity.setActiveUntil(parseDate(dto.getActiveUntil()));
		if (dto.getAgency() != null) {
			entity.setAgency(agencyRepo.findOne(dto.getAgency().getId()));
		}
	}

	@RequestMapping(path = "comboAgency")
	public List<AgencyDto> comboAgency() {
		return agencyController.comboAgency();
	}
}
