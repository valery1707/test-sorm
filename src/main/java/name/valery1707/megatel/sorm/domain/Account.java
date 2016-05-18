package name.valery1707.megatel.sorm.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

@Entity
@SuppressWarnings("unused")
public class Account extends ABaseEntity {
	public enum Role {
		SUPER(Arrays.asList("operator.task.list")),
		ADMIN(Arrays.asList("admin.task.list")),
		OPERATOR(Arrays.asList("operator.task.list")),
		SUPERVISOR(Arrays.asList("operator.task.list"));

		private final Collection<String> rights;

		Role(Collection<String> rights) {
			this.rights = rights;
		}

		public Collection<String> getRights() {
			return rights;
		}

		public Stream<String> getRightsStream() {
			return getRights().stream();
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
