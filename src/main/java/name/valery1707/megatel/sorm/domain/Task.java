package name.valery1707.megatel.sorm.domain;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Task extends ABaseEntity implements LogicRemovableEntity {
	@NotNull
	@ManyToOne
	@CreatedBy
	private Account createdBy;

	@NotNull
	@CreatedDate
	private ZonedDateTime createdAt;

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

	@NotNull
	private boolean isActive;

	public Account getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Account createdBy) {
		this.createdBy = createdBy;
	}

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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
