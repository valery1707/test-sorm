package name.valery1707.megatel.sorm.api.account.session;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.api.BaseDto;
import name.valery1707.megatel.sorm.domain.AccountSession;

import static name.valery1707.megatel.sorm.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountSessionDto implements BaseDto {
	private long id;
	private String accountUsername;
	private String loginAt;
	private String loginAs;
	private String sessionId;
	private String details;
	private String logoutAt;
	private String logoutAs;
	private Long duration;

	public AccountSessionDto() {
	}

	public AccountSessionDto(AccountSession src) {
		this();
		this.setId(src.getId());
		this.setAccountUsername(src.getAccount().getUsername());
		this.setLoginAt(formatDateTime(src.getLoginAt()));
		this.setLoginAs(src.getLoginAs().name());
		this.setSessionId(src.getSessionId());
		this.setDetails(src.getDetails());
		this.setLogoutAt(formatDateTime(src.getLogoutAt()));
		this.setLogoutAs(src.getLogoutAs() != null ? src.getLogoutAs().name() : null);
		this.setDuration(src.getDuration());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountUsername() {
		return accountUsername;
	}

	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}

	public String getLoginAt() {
		return loginAt;
	}

	public void setLoginAt(String loginAt) {
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

	public String getLogoutAt() {
		return logoutAt;
	}

	public void setLogoutAt(String logoutAt) {
		this.logoutAt = logoutAt;
	}

	public String getLogoutAs() {
		return logoutAs;
	}

	public void setLogoutAs(String logoutAs) {
		this.logoutAs = logoutAs;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
}
