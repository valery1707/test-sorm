package name.valery1707.megatel.sorm.api.task;

import name.valery1707.megatel.sorm.domain.Agency;
import name.valery1707.megatel.sorm.domain.Task;

import javax.validation.constraints.Null;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class TaskFilter {
	private String id;
	private String number;
	@Null
	private Agency agency;
	private String agencyName;
	private String clientAlias;
	private List<ZonedDateTime> periodStart;
	private List<ZonedDateTime> periodFinish;
	private Task.Mode mode;
	private String note;
	private Boolean showOnlyActive;
	private Set<Long> allowedIds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public String getClientAlias() {
		return clientAlias;
	}

	public void setClientAlias(String clientAlias) {
		this.clientAlias = clientAlias;
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

	public Boolean getShowOnlyActive() {
		return showOnlyActive;
	}

	public void setShowOnlyActive(Boolean showOnlyActive) {
		this.showOnlyActive = showOnlyActive;
	}

	public Set<Long> getAllowedIds() {
		return allowedIds;
	}

	public void setAllowedIds(Set<Long> allowedIds) {
		this.allowedIds = allowedIds;
	}
}
