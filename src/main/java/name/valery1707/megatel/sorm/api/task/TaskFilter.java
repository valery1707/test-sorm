package name.valery1707.megatel.sorm.api.task;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class TaskFilter {
	private String id;
	private String agency;
	private String clientAlias;
	private List<ZonedDateTime> periodStart;
	private List<ZonedDateTime> periodFinish;
	private String note;
	private Boolean showOnlyActive;
	private Set<Long> allowedIds;

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
