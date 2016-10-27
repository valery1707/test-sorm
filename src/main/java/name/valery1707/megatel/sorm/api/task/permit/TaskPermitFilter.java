package name.valery1707.megatel.sorm.api.task.permit;

import name.valery1707.megatel.sorm.domain.Agency;

import javax.validation.constraints.Null;
import java.time.ZonedDateTime;
import java.util.List;

public class TaskPermitFilter {
	private String id;
	@Null
	private Agency agency;
	private String agencyName;
	private String taskNumber;
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

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
		this.agency = agency;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(String taskNumber) {
		this.taskNumber = taskNumber;
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
