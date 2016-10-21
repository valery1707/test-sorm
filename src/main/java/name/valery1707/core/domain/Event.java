package name.valery1707.core.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

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
		//todo Работа с журналом событий не логируется: name.valery1707.core.api.event.EventController

		ADMIN_ACCOUNT_CREATE(1.1, "Добавление учётной записи"),
		ADMIN_ACCOUNT_UPDATE(1.2, "Модификация параметров учётной записи"),
		ADMIN_ACCOUNT_DELETE(1.3, "Удаление учётной записи"),
		ADMIN_ACCOUNT_LIST(1.4, "Просмотр списка учётных записей"),
		ADMIN_TASK_CREATE(1.5, "Задание на перехват сообщений абонента"),
		ADMIN_TASK_DELETE(1.7, "Удаление заданий на перехват сообщений абонента"),
		ADMIN_TASK_LIST(1.6, "Просмотр списка заданий на перехват сообщений абонента"),
		ADMIN_TASK_PERMIT_CREATE(1.8, "Выдача санкций на обработку перехваченных сообщений абонента"),
		ADMIN_TASK_PERMIT_DELETE(1.10, "Удаление санкции на обработку перехваченных сообщений абонента"),
		ADMIN_TASK_PERMIT_LIST(1.9, "Просмотр списка выданных санкций на обработку перехваченных сообщений абонента"),
		SERVER_STATUS(1.14, "Проверка технического состояния оборудования"),
		ADMIN_CHANGE_PASSWORD(1.15, "Изменение пароля администратора"),

		OPERATOR_TASK_LIST(2.1, "Просмотр списка санкционированных заданий на перехват сообщений абонента"),
		OPERATOR_TASK_VIEW(2.3, "Получение результатов перехвата сообщений абонента"),
		//(2.4),//Просмотр списка реализованных запросов на получение служебной информации
		//(2.5),//Получение служебной информации
		OPERATOR_CHANGE_PASSWORD(2.6, "Изменение пароля обработчика"),

		SUPERVISOR_ACCOUNT_LIST(3.1, "Просмотр сисков учётных записей"),
		SUPERVISOR_TASK_LIST(3.2, "Просмотр списка заданий на перехват сообщений абонента"),
		SUPERVISOR_CHANGE_PASSWORD(3.4, "Изменение пароля надзорщика"),
		//End
		//Replace: /([A-Z_]+)\((\d+\.\d+), "([^"]+)"\)/$1: '$2: $3'
		;

		private final double code;
		private final String note;

		EventType(double code, String note) {
			this.code = code;
			this.note = note;
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

	public static Event server(@Nonnull EventType type, boolean success, @Nullable String ip, @Nullable String ext) {
		Event event = new Event();
		event.setCreatedAt(ZonedDateTime.now());
		event.setType(type);
		event.setSuccess(success);
		event.setIp(defaultIfBlank(ip, "localhost"));
		event.setExt(trimToEmpty(ext));
		return event;
	}

	public static Event client(@Nonnull Account account, @Nonnull AccountSession session, @Nonnull EventType type, boolean success, @Nullable String ip, @Nullable String ext) {
		Event event = server(type, success, ip, ext);
		event.setCreatedBy(account);
		event.setSession(session);
		return event;
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
