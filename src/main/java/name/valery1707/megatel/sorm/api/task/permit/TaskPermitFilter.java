package name.valery1707.megatel.sorm.api.task.permit;

import java.time.ZonedDateTime;
import java.util.List;

public class TaskPermitFilter {
	private String id;
	private String agency;
	private String taskId;
	private String accountId;
	private List<ZonedDateTime> periodStart;
	private List<ZonedDateTime> periodFinish;
	private Boolean showOnlyActive;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public List<ZonedDateTime> getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(List<ZonedDateTime> periodStart) {
		this.periodStart = periodStart;
	}

	public List<ZonedDateTime> getPeriodFinish() {
		return periodFinish;
	}

	public void setPeriodFinish(List<ZonedDateTime> periodFinish) {
		this.periodFinish = periodFinish;
	}

	public Boolean getShowOnlyActive() {
		return showOnlyActive;
	}

	public void setShowOnlyActive(Boolean showOnlyActive) {
		this.showOnlyActive = showOnlyActive;
	}
}
