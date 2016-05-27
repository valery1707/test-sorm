package name.valery1707.megatel.sorm.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Account;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static name.valery1707.megatel.sorm.DateUtils.LOCAL_DATE_PATTERN;
import static name.valery1707.megatel.sorm.DateUtils.formatDate;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ScriptAssert(lang = "JavaScript",
		script = "(_this.id == 0 && _this.password != null) || (_this.id > 0 && _this.password == null)",
		message = "{Account.password.constraint.requiredOnlyForNew}"
)
public class AccountDto {

	private long id;
	@NotNull
	//todo Distinct within id
	private String username;

	private String password;
	//todo old+new или оба null или оба не null
	private String oldPassword;
	private String newPassword;

	@NotNull
	private boolean isActive;
	@NotNull
	private Account.Role role;
	@Pattern(regexp = LOCAL_DATE_PATTERN)
	private String activeUntil;
	@NotNull
	@Size(min = 3)
	private String agency;

	public AccountDto() {
	}

	public AccountDto(Account src) {
		this();
		setId(src.getId());
		setUsername(src.getUsername());
		setActive(src.isActive());
		setRole(src.getRole());
		setActiveUntil(formatDate(src.getActiveUntil()));
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public Account.Role getRole() {
		return role;
	}

	public void setRole(Account.Role role) {
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
