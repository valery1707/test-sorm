package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.LogicRemovableEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Task extends ABaseEntity implements LogicRemovableEntity {
	public enum Mode {
		FULL, STATISTIC
	}

	@NotNull
	@Size(max = 512)
	private String number;

	@NotNull
	@ManyToOne
	@CreatedBy
	private Account createdBy;

	@NotNull
	@CreatedDate
	private ZonedDateTime createdAt;

	@NotNull
	@ManyToOne
	@LastModifiedBy
	private Account modifiedBy;

	@NotNull
	@LastModifiedDate
	private ZonedDateTime modifiedAt;

	@NotNull
	@ManyToOne
	private Agency agency;

	@NotNull
	private String clientAlias;

	//	private Client client;//todo implement

	@NotNull
	private ZonedDateTime periodStart;

	@NotNull
	private ZonedDateTime periodFinish;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Mode mode;

	private String note;

	@NotNull
	private boolean isActive;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

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

	public Account getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Account modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public ZonedDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(ZonedDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Agency getAgency() {
		return agency;
	}

	public void setAgency(Agency agency) {
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

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
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

	public static final class TaskBuilder {
		private Account createdBy;
		private ZonedDateTime createdAt;
		private Account modifiedBy;
		private ZonedDateTime modifiedAt;
		private Agency agency;
		private String clientAlias;
		private ZonedDateTime periodStart;
		private ZonedDateTime periodFinish;
		private Mode mode;
		private String note;
		private Long id;

		private TaskBuilder() {
		}

		public static TaskBuilder aTask() {
			return new TaskBuilder();
		}

		public TaskBuilder withCreatedBy(Account createdBy) {
			this.createdBy = createdBy;
			return this;
		}

		public TaskBuilder withCreatedAt(ZonedDateTime createdAt) {
			this.createdAt = createdAt;
			return this;
		}

		public TaskBuilder withModifiedBy(Account modifiedBy) {
			this.modifiedBy = modifiedBy;
			return this;
		}

		public TaskBuilder withModifiedAt(ZonedDateTime modifiedAt) {
			this.modifiedAt = modifiedAt;
			return this;
		}

		public TaskBuilder withAgency(Agency agency) {
			this.agency = agency;
			return this;
		}

		public TaskBuilder withClientAlias(String clientAlias) {
			this.clientAlias = clientAlias;
			return this;
		}

		public TaskBuilder withPeriodStart(ZonedDateTime periodStart) {
			this.periodStart = periodStart;
			return this;
		}

		public TaskBuilder withPeriodFinish(ZonedDateTime periodFinish) {
			this.periodFinish = periodFinish;
			return this;
		}

		public TaskBuilder withMode(Mode mode) {
			this.mode = mode;
			return this;
		}

		public TaskBuilder withNote(String note) {
			this.note = note;
			return this;
		}

		public TaskBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public Task build() {
			Task task = new Task();
			task.setCreatedBy(createdBy);
			task.setCreatedAt(createdAt);
			task.setModifiedBy(modifiedBy);
			task.setModifiedAt(modifiedAt);
			task.setAgency(agency);
			task.setClientAlias(clientAlias);
			task.setPeriodStart(periodStart);
			task.setPeriodFinish(periodFinish);
			task.setMode(mode);
			task.setNote(note);
			task.setId(id);
			return task;
		}
	}
}
