package name.valery1707.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

//todo В таком виде класс не должен быть в ядре
// Если оставлять в ядре, то нужно избавиться от специфики.
// Если удалять из ядра, то нужно логирование нужно оставить, но сделать его более абстрактным
@Entity
public class Event extends ABaseEntity {
	public enum EventType {
		//todo Стоит реализовать логирование полезных событий
//		LOGIN(0.0), name.valery1707.core.app.AppUserDetailsService.loadUserByUsername() + name.valery1707.core.app.AccountSessionListener

		//todo Работа с сессиями никак не логируется: name.valery1707.core.api.account.session.AccountSessionController
		//todo Работа со статусами серверов никак не логируется: name.valery1707.megatel.sorm.api.server.status.ServerStatusController
		//todo Работа с Органами контроля никак не логируется: name.valery1707.megatel.sorm.api.agency.AgencyController

		ADMIN_ACCOUNT_CREATE(1.1),
		ADMIN_ACCOUNT_UPDATE(1.2),
		ADMIN_ACCOUNT_DELETE(1.3),
		ADMIN_ACCOUNT_LIST(1.4),
		ADMIN_TASK_CREATE(1.5),
		ADMIN_TASK_DELETE(1.7),
		ADMIN_TASK_LIST(1.6),
		ADMIN_TASK_PERMIT_CREATE(1.8),
		ADMIN_TASK_PERMIT_DELETE(1.10),
		ADMIN_TASK_PERMIT_LIST(1.9),
		SERVER_STATUS(1.14),//todo
		ADMIN_CHANGE_PASSWORD(1.15),

		OPERATOR_TASK_LIST(2.1),
		OPERATOR_TASK_VIEW(2.3),
		//(2.4),//Просмотр списка реализованных запросов на получение служебной информации
		//(2.5),//Получение служебной информации
		OPERATOR_CHANGE_PASSWORD(2.6),

		SUPERVISOR_ACCOUNT_LIST(3.1),
		SUPERVISOR_TASK_LIST(3.2),
		SUPERVISOR_CHANGE_PASSWORD(3.4),
		//End
		;

		private final double code;

		EventType(double code) {
			this.code = code;
		}
	}

	@ManyToOne
	private Account createdBy;

	@NotNull
	private ZonedDateTime createdAt;

	@NotNull
	@Enumerated(EnumType.STRING)
	private EventType type;

	@NotNull
	private boolean success;

	@NotNull
	private String ip;

	@ManyToOne
	private AccountSession session;

	@NotNull
	@Lob
	private String ext;

	public Event() {
	}

	public Event(Account createdBy) {
		this();
		setCreatedBy(createdBy);
		setCreatedAt(ZonedDateTime.now());
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

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public AccountSession getSession() {
		return session;
	}

	public void setSession(AccountSession session) {
		this.session = session;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
}
