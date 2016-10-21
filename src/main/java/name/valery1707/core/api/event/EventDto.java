package name.valery1707.core.api.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.core.api.BaseDto;
import name.valery1707.core.domain.Event;
import name.valery1707.core.domain.Event.EventType;

import javax.validation.constraints.NotNull;

import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDto implements BaseDto {
	private long id;

	private String username;

	@NotNull
	private String createdAt;

	@NotNull
	private EventType type;

	@NotNull
	private boolean success;

	@NotNull
	private String ip;

	@NotNull
	private String ext;

	public EventDto() {
	}

	public EventDto(Event src) {
		this();
		setId(src.getId());
		setUsername(src.getCreatedBy() != null ? src.getCreatedBy().getUsername() : null);
		setCreatedAt(formatDateTime(src.getCreatedAt()));
		setType(src.getType());
		setSuccess(src.isSuccess());
		setIp(src.getIp());
		setExt(src.getExt());
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
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

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
}
