package name.valery1707.megatel.sorm.domain;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class AccountSession extends ABaseEntity {
	public enum Login {MANUAL, INTERACTIVE, REMEMBER_ME}

	public enum Logout {MANUAL, TIMEOUT, SERVER_RESTART}

	@NotNull
	@ManyToOne
	private Account account;

	@NotNull
	@CreatedDate
	private ZonedDateTime loginAt;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Login loginAs;

	@NotNull
	private String sessionId;

	@NotNull
	@Lob
	private String details;

	private ZonedDateTime logoutAt;

	@Enumerated(EnumType.STRING)
	private Logout logoutAs;

	public AccountSession() {
	}

	public AccountSession(Account account, Login loginAs, String sessionId, String details) {
		this();
		this.account = account;
		this.loginAt = ZonedDateTime.now();
		this.loginAs = loginAs;
		this.sessionId = sessionId;
		this.details = details;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ZonedDateTime getLoginAt() {
		return loginAt;
	}

	public void setLoginAt(ZonedDateTime loginAt) {
		this.loginAt = loginAt;
	}

	public Login getLoginAs() {
		return loginAs;
	}

	public void setLoginAs(Login loginAs) {
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

	public ZonedDateTime getLogoutAt() {
		return logoutAt;
	}

	public void setLogoutAt(ZonedDateTime logoutAt) {
		this.logoutAt = logoutAt;
	}

	public Logout getLogoutAs() {
		return logoutAs;
	}

	public void setLogoutAs(Logout logoutAs) {
		this.logoutAs = logoutAs;
	}
}
