package name.valery1707.megatel.sorm.api.account.session;

import name.valery1707.megatel.sorm.api.auth.AccountService;
import name.valery1707.megatel.sorm.api.auth.AccountSessionRepo;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.AccountSession;
import name.valery1707.megatel.sorm.domain.AccountSession_;
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

@RestController
@RequestMapping("/api/account/session")
public class AccountSessionController {
	@Inject
	private AccountSessionRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<AccountSession, AccountSessionFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<AccountSession, AccountSessionFilter>(SpecificationMode.AND)
				.withNumber(AccountSession_.id, AccountSessionFilter::getId)
				.withString(AccountSessionFilter::getAccountUsername, AccountSession_.account, Account_.username)
				.withDateTime(AccountSession_.loginAt, AccountSessionFilter::getLoginAt)
				.withDateTime(AccountSession_.logoutAt, AccountSessionFilter::getLogoutAt)
				.withString(AccountSession_.sessionId, AccountSessionFilter::getSessionId)
				.withString(AccountSession_.details, AccountSessionFilter::getDetails)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<AccountSessionDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) AccountSessionFilter filter
	) {
		accountService.requireAnyRole(Account.Role.SUPER);
		Specification<AccountSession> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(AccountSessionDto::new);
	}
}
