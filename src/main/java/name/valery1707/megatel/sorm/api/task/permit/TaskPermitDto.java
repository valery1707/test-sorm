package name.valery1707.megatel.sorm.api.task.permit;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.DateUtils;
import name.valery1707.megatel.sorm.domain.TaskPermit;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static name.valery1707.megatel.sorm.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskPermitDto {
	private long id;
	private String createdBy;
	private String createdAt;
	@NotNull
	@Size(min = 3)
	private String agency;
	@NotNull
	@Min(0)
	private Long taskId;
	@NotNull
	@Min(0)
	private Long accountId;
	private String accountName;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodStart;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodFinish;
	private Boolean isActive;

	public TaskPermitDto() {
	}

	public TaskPermitDto(TaskPermit src) {
		this();
		setId(src.getId());
		setCreatedBy(src.getCreatedBy().getUsername());
		setCreatedAt(formatDateTime(src.getCreatedAt()));
		setAgency(src.getAgency());
		setTaskId(src.getTask().getId());
		setAccountId(src.getAccount().getId());
		setAccountName(src.getAccount().getUsername());
		setPeriodStart(formatDateTime(src.getPeriodStart()));
		setPeriodFinish(formatDateTime(src.getPeriodFinish()));
		setActive(src.isActive());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
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

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}
}
