package name.valery1707.megatel.sorm.api.server.status;

import java.time.ZonedDateTime;
import java.util.List;

public class ServerStatusFilter {
	private String id;
	private String serverName;
	private List<ZonedDateTime> modifiedAt;
	private Boolean hostStatus;
	private Boolean dbStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public List<ZonedDateTime> getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(List<ZonedDateTime> modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public Boolean getHostStatus() {
		return hostStatus;
	}

	public void setHostStatus(Boolean hostStatus) {
		this.hostStatus = hostStatus;
	}

	public Boolean getDbStatus() {
		return dbStatus;
	}

	public void setDbStatus(Boolean dbStatus) {
		this.dbStatus = dbStatus;
	}
}
