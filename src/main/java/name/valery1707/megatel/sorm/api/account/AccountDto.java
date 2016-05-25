package name.valery1707.megatel.sorm.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Account;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private long id;
	private String username;
	private boolean isActive;
	private String role;
	private String activeUntil;
	private String agency;

	public AccountDto() {
	}

	public AccountDto(Account src) {
		this();
		setId(src.getId());
		setUsername(src.getUsername());
		setActive(src.isActive());
		setRole(src.getRole().name());
		setActiveUntil(src.getActiveUntil() != null ? src.getActiveUntil().format(FORMATTER) : null);
		setAgency(src.getAgency());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(String activeUntil) {
		this.activeUntil = activeUntil;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}
}
