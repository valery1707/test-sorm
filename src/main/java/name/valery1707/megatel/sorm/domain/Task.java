package name.valery1707.megatel.sorm.domain;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Task extends ABaseEntity {
	@NotNull
	private ZonedDateTime createdAt;
	//	private Account createdBy;//todo implement
	@NotNull
	private String agency;
	@NotNull
	private String clientAlias;
	//	private Client client;//todo implement
	@NotNull
	private ZonedDateTime periodStart;
	@NotNull
	private ZonedDateTime periodFinish;
	//	private String filter;//todo implement
	private String note;

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
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

	public ZonedDateTime getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(ZonedDateTime periodStart) {
		this.periodStart = periodStart;
	}

	public ZonedDateTime getPeriodFinish() {
		return periodFinish;
	}

	public void setPeriodFinish(ZonedDateTime periodFinish) {
		this.periodFinish = periodFinish;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
