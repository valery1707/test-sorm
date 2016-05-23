package name.valery1707.megatel.sorm.api.account.session;

import name.valery1707.megatel.sorm.api.auth.AccountSessionRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.AccountSession;
import name.valery1707.megatel.sorm.domain.AccountSession_;
import name.valery1707.megatel.sorm.domain.Account_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
				.withNumber(AccountSessionFilter::getId, AccountSession_.id)
				.withString(AccountSessionFilter::getAccountUsername, AccountSession_.account, Account_.username)
				.withDateTime(AccountSessionFilter::getLoginAt, AccountSession_.loginAt)
				.withEquals(AccountSessionFilter::getLoginAs, AccountSession_.loginAs)
				.withDateTime(AccountSessionFilter::getLogoutAt, AccountSession_.logoutAt)
				.withEquals(AccountSessionFilter::getLogoutAs, AccountSession_.logoutAs)
				.withString(AccountSessionFilter::getSessionId, AccountSession_.sessionId)
				.withString(AccountSessionFilter::getDetails, AccountSession_.details)
				.withNumber(AccountSessionFilter::getDuration, AccountSession_.duration)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<AccountSessionDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault(sort = "loginAt", direction = Sort.Direction.DESC) Pageable pageable,
			@RequestBody(required = false) AccountSessionFilter filter
	) {
		accountService.requireAnyRole(Account.Role.SUPER);
		Specification<AccountSession> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(AccountSessionDto::new);
	}
}
