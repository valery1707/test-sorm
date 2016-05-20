package name.valery1707.megatel.sorm.api.account.session;

import java.time.ZonedDateTime;
import java.util.List;

public class AccountSessionFilter {
	private String id;
	private String accountUsername;
	private List<ZonedDateTime> loginAt;
	private String loginAs;
	private String sessionId;
	private String details;
	private List<ZonedDateTime> logoutAt;
	private String logoutAs;

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

	public String getLoginAs() {
		return loginAs;
	}

	public void setLoginAs(String loginAs) {
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

	public String getLogoutAs() {
		return logoutAs;
	}

	public void setLogoutAs(String logoutAs) {
		this.logoutAs = logoutAs;
	}
}
