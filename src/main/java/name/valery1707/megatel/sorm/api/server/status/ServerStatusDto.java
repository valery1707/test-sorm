package name.valery1707.megatel.sorm.api.server.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.core.api.BaseDto;
import name.valery1707.megatel.sorm.domain.ServerStatus;

import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerStatusDto implements BaseDto {
	private long id;

	private String serverName;

	private String modifiedAt;

	private boolean hostStatus;

	private boolean dbStatus;

	public ServerStatusDto() {
	}

	public ServerStatusDto(ServerStatus src) {
		this();
		setId(src.getId());
		setServerName(src.getServer() != null ? src.getServer().getName() : "Пульт управления");//todo Константа
		setModifiedAt(formatDateTime(src.getModifiedAt()));
		setHostStatus(src.isHostStatus());
		setDbStatus(src.isDbStatus());
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(String modifiedAt) {
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
