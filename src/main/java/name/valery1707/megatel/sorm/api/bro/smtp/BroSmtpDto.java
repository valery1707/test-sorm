package name.valery1707.megatel.sorm.api.bro.smtp;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.BroSmtp;

import static name.valery1707.core.utils.DateUtils.bigDecimalToZonedDateTime;
import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BroSmtpDto {
	private String ts;
	private String idOrigHost;
	private int idOrigPort;
	private String idRespHost;
	private int idRespPort;

	private String from;
	private String to;
	private String subject;
	private String userAgent;
	private Boolean tls;
	private String fuids;
	private Boolean isWebmail;

	public BroSmtpDto() {
	}

	public BroSmtpDto(BroSmtp src) {
		this();
		setTs(formatDateTime(bigDecimalToZonedDateTime(src.getTs())));
		setIdOrigHost(src.getIdOrigHost());
		setIdOrigPort(src.getIdOrigPort());
		setIdRespHost(src.getIdRespHost());
		setIdRespPort(src.getIdRespPort());
		setFrom(src.getFrom());
		setTo(src.getTo());
		setSubject(src.getSubject());
		setUserAgent(src.getUserAgent());
		setTls(src.getTls());
		setFuids(src.getFuids());
		setWebmail(src.getWebmail());
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getIdOrigHost() {
		return idOrigHost;
	}

	public void setIdOrigHost(String idOrigHost) {
		this.idOrigHost = idOrigHost;
	}

	public int getIdOrigPort() {
		return idOrigPort;
	}

	public void setIdOrigPort(int idOrigPort) {
		this.idOrigPort = idOrigPort;
	}

	public String getIdRespHost() {
		return idRespHost;
	}

	public void setIdRespHost(String idRespHost) {
		this.idRespHost = idRespHost;
	}

	public int getIdRespPort() {
		return idRespPort;
	}

	public void setIdRespPort(int idRespPort) {
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

	public Boolean getTls() {
		return tls;
	}

	public void setTls(Boolean tls) {
		this.tls = tls;
	}

	public String getFuids() {
		return fuids;
	}

	public void setFuids(String fuids) {
		this.fuids = fuids;
	}

	public Boolean getWebmail() {
		return isWebmail;
	}

	public void setWebmail(Boolean webmail) {
		isWebmail = webmail;
	}
}
