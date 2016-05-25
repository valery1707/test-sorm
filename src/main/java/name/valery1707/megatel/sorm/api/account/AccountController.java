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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
}
