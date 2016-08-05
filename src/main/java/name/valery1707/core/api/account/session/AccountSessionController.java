package name.valery1707.core.api.account.session;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.api.auth.AccountSessionRepo;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.AccountSession;
import name.valery1707.core.domain.AccountSession_;
import name.valery1707.core.domain.Account_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account/session")
public class AccountSessionController extends BaseEntityController<AccountSession, AccountSessionRepo, AccountSessionFilter, AccountSessionDto> {

	public AccountSessionController() {
		super("accountSession");
	}

	@Override
	protected SpecificationBuilder<AccountSession, AccountSessionFilter> buildUserFilter() {
		return new SpecificationBuilder<AccountSession, AccountSessionFilter>(SpecificationMode.AND)
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
		return super.findByFilter(pageable, filter);
	}

	@Override
	protected void dto2domain(AccountSessionDto dto, AccountSession entity) {
	}
}
