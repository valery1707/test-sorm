package name.valery1707.core.api.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.core.api.BaseDto;
import name.valery1707.core.domain.Account;
import name.valery1707.megatel.sorm.api.agency.AgencyDto;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static name.valery1707.core.utils.DateUtils.LOCAL_DATE_PATTERN;
import static name.valery1707.core.utils.DateUtils.formatDate;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
@ScriptAssert(lang = "JavaScript",
		script = "(_this.id == 0 && _this.password != null) || (_this.id > 0 && _this.password == null)",
		message = "{Account.password.constraint.requiredOnlyForNew}"
)
public class AccountDto implements BaseDto {

	private long id;
	@NotNull
	private String username;

	private String password;
	private String newPassword;

	@NotNull
	private boolean isActive;
	@NotNull
	private Account.Role role;
	@Pattern(regexp = LOCAL_DATE_PATTERN)
	private String activeUntil;

	private AgencyDto agency;
	private Long agencyId;
	private String agencyName;

	public AccountDto() {
	}

	public AccountDto(Account src) {
		this();
		setId(src.getId());
		setUsername(src.getUsername());
		setActive(src.isActive());
		setRole(src.getRole());
		setActiveUntil(formatDate(src.getActiveUntil()));
		if (src.getAgency() != null) {
			setAgency(new AgencyDto(src.getAgency()));
			setAgencyId(src.getAgency().getId());
			setAgencyName(src.getAgency().getName());
		}
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

	public AgencyDto getAgency() {
		return agency;
	}

	public void setAgency(AgencyDto agency) {
		this.agency = agency;
	}

	public Long getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(Long agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
}
