package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Актуальный статус мониторинга оборудования
 */
@Entity
@SuppressWarnings("unused")
public class ServerStatus extends ABaseEntity {
	@ManyToOne
	private Server server;

	@NotNull
	private ZonedDateTime modifiedAt;

	@NotNull
	private boolean hostStatus;

	@NotNull
	private boolean dbStatus;

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public ZonedDateTime getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(ZonedDateTime modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public boolean isHostStatus() {
		return hostStatus;
	}

	public void setHostStatus(boolean hostStatus) {
		this.hostStatus = hostStatus;
	}

	public boolean isDbStatus() {
		return dbStatus;
	}

	public void setDbStatus(boolean dbStatus) {
		this.dbStatus = dbStatus;
	}
}
