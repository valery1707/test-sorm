package name.valery1707.megatel.sorm.api.account;

import name.valery1707.megatel.sorm.domain.Account;

import java.time.LocalDate;
import java.util.List;

public class AccountFilter {
	private String id;
	private String username;
	private Boolean isActive;
	private Account.Role role;
	private List<LocalDate> activeUntil;
	private String agency;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean isActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}

	public Account.Role getRole() {
		return role;
	}

	public void setRole(Account.Role role) {
		this.role = role;
	}

	public List<LocalDate> getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(List<LocalDate> activeUntil) {
		this.activeUntil = activeUntil;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}
}
