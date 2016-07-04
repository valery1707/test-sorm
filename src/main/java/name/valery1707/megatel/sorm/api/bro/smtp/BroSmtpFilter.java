package name.valery1707.megatel.sorm.api.bro.smtp;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

public class BroSmtpFilter {
	@NotNull
	private Long taskId;
	private List<ZonedDateTime> ts;
	private String idOrigHost;
	private String idOrigPort;
	private String idRespHost;
	private String idRespPort;
	private String from;
	private String to;
	private String subject;
	private String userAgent;
	private String fuids;

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public List<ZonedDateTime> getTs() {
		return ts;
	}

	public void setTs(List<ZonedDateTime> ts) {
		this.ts = ts;
	}

	public String getIdOrigHost() {
		return idOrigHost;
	}

	public void setIdOrigHost(String idOrigHost) {
		this.idOrigHost = idOrigHost;
	}

	public String getIdOrigPort() {
		return idOrigPort;
	}

	public void setIdOrigPort(String idOrigPort) {
		this.idOrigPort = idOrigPort;
	}

	public String getIdRespHost() {
		return idRespHost;
	}

	public void setIdRespHost(String idRespHost) {
		this.idRespHost = idRespHost;
	}

	public String getIdRespPort() {
		return idRespPort;
	}

	public void setIdRespPort(String idRespPort) {
		this.idRespPort = idRespPort;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getFuids() {
		return fuids;
	}

	public void setFuids(String fuids) {
		this.fuids = fuids;
	}
}
