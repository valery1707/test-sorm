package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.LogicRemovableEntity;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Санкции на доступ к заданиям на перехват сообщений
 */
@Entity
@SuppressWarnings("unused")
public class TaskPermit extends ABaseEntity implements LogicRemovableEntity {
	/**
	 * Пользователь, создавший запись
	 */
	@NotNull
	@ManyToOne
	@CreatedBy
	private Account createdBy;

	/**
	 * Время создания записи
	 */
	@NotNull
	@CreatedDate
	private ZonedDateTime createdAt;

	/**
	 * Наименование органа, осуществляющего проведение ОРМ или надзор
	 */
	@NotNull
	private String agency;

	/**
	 * Задание на перехват сообщений, на которую выдана санкция
	 */
	@NotNull
	@ManyToOne
	private Task task;

	/**
	 * Пользователь, которому выдана санкция
	 */
	@NotNull
	@ManyToOne
	private Account account;

	/**
	 * Время действия санции, начало
	 */
	@NotNull
	private ZonedDateTime periodStart;

	/**
	 * Время действия санции, конец
	 */
	@NotNull
	private ZonedDateTime periodFinish;

	/**
	 * Признак активности записи
	 */
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

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}
}
