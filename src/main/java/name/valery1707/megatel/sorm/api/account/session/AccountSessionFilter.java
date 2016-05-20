package name.valery1707.megatel.sorm.api.account.session;

import name.valery1707.megatel.sorm.domain.AccountSession;

import java.time.ZonedDateTime;
import java.util.List;

public class AccountSessionFilter {
	private String id;
	private String accountUsername;
	private List<ZonedDateTime> loginAt;
	private AccountSession.Login loginAs;
	private String sessionId;
	private String details;
	private List<ZonedDateTime> logoutAt;
	private AccountSession.Logout logoutAs;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAccountUsername() {
		return accountUsername;
	}

	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	public List<ZonedDateTime> getLoginAt() {
		return loginAt;
	}

	public void setLoginAt(List<ZonedDateTime> loginAt) {
		this.loginAt = loginAt;
	}

	public AccountSession.Login getLoginAs() {
		return loginAs;
	}

	public void setLoginAs(AccountSession.Login loginAs) {
		this.loginAs = loginAs;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<ZonedDateTime> getLogoutAt() {
		return logoutAt;
	}

	public void setLogoutAt(List<ZonedDateTime> logoutAt) {
		this.logoutAt = logoutAt;
	}

	public AccountSession.Logout getLogoutAs() {
		return logoutAs;
	}

	public void setLogoutAs(AccountSession.Logout logoutAs) {
		this.logoutAs = logoutAs;
	}
}
