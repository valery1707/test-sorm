package name.valery1707.core.api.event;

import name.valery1707.core.domain.Event.EventType;

import java.time.ZonedDateTime;
import java.util.List;

public class EventFilter {
	private String id;
	private String username;
	private List<ZonedDateTime> createdAt;
	private EventType type;
	private Boolean success;
	private String ip;
	private String ext;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<ZonedDateTime> getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(List<ZonedDateTime> createdAt) {
		this.createdAt = createdAt;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
}
