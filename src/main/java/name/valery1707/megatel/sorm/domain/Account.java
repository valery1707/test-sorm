package name.valery1707.megatel.sorm.domain;

import javaslang.collection.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

@Entity
@SuppressWarnings("unused")
public class Account extends ABaseEntity {
	public enum Role {
		//Специальный внутренний админ, для настройки системы
		SUPER(Arrays.asList(null
				//Пользователи
				, "account.list"
				, "account.create"
				, "account.update"
				//Сессии пользователей
				, "accountSession.list"
		)),
		//Администратор
		ADMIN(Arrays.asList(null
				//Задание на перехват
				, "task.list"
				, "task.create"
				, "task.update"
				, "task.delete"
				//Санкции на перехват
				, "taskPermit.list"
				, "taskPermit.create"
				, "taskPermit.delete"
		)),
		//Оператор/Обработчик
		OPERATOR(Arrays.asList(null
				//Задание на перехват
				, "task.list"
				, "task.view"
		)),
		//Надзор
		SUPERVISOR(Arrays.asList(null
				//Задание на перехват
				, "task.list"
				, "task.list.active"
		));

		private final Collection<String> rights;

		Role(Collection<String> rights) {
			this.rights = List.ofAll(rights)
					.filter(Objects::nonNull)
					.distinct()
					.sorted()
					.toJavaList();
		}

		public Collection<String> getRights() {
			return rights;
		}
	}

	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private boolean isActive;

	@NotNull
	@Enumerated(EnumType.STRING)
	private Role role;

	private LocalDate activeUntil;

	@NotNull
	private String agency;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDate getActiveUntil() {
		return activeUntil;
	}

	public void setActiveUntil(LocalDate activeUntil) {
		this.activeUntil = activeUntil;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}
}
