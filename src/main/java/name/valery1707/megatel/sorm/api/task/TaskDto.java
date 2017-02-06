package name.valery1707.megatel.sorm.api.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.core.api.BaseDto;
import name.valery1707.core.utils.DateUtils;
import name.valery1707.megatel.sorm.api.agency.AgencyDto;
import name.valery1707.megatel.sorm.domain.Task;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto implements BaseDto {

	private long id;
	private String number;
	private String createdBy;
	private String createdAt;

	@NotNull
	private AgencyDto agency;
	private Long agencyId;
	private String agencyName;

	@NotNull
	@Size(min = 3)
	private String clientAlias;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodStart;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodFinish;
	@NotNull
	private Task.Mode mode;
	@NotNull
	@Size(min = 3)
	private String note;
	private Boolean isActive;

	public TaskDto() {
	}

	public TaskDto(Task src) {
		this();
		setId(src.getId());
		setNumber(src.getNumber());
		setCreatedBy(src.getCreatedBy().getUsername());
		setCreatedAt(formatDateTime(src.getCreatedAt()));
		setAgency(new AgencyDto(src.getAgency()));
		setAgencyId(src.getAgency().getId());
		setAgencyName(src.getAgency().getName());
		setClientAlias(src.getClientAlias());
		setPeriodStart(formatDateTime(src.getPeriodStart()));
		setPeriodFinish(formatDateTime(src.getPeriodFinish()));
		setMode(src.getMode());
		setNote(src.getNote());
		setActive(src.isActive());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
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

	public String getClientAlias() {
		return clientAlias;
	}

	public void setClientAlias(String clientAlias) {
		this.clientAlias = clientAlias;
	}

	public String getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(String periodStart) {
		this.periodStart = periodStart;
	}

	public String getPeriodFinish() {
		return periodFinish;
	}

	public void setPeriodFinish(String periodFinish) {
		this.periodFinish = periodFinish;
	}

	public Task.Mode getMode() {
		return mode;
	}

	public void setMode(Task.Mode mode) {
		this.mode = mode;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}
}
