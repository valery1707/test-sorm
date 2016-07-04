package name.valery1707.megatel.sorm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@SuppressWarnings("unused")
public class BroSmtp {
	@Id
	@NotNull
	private BigDecimal ts;

	@NotNull
	private String uid;

	@Column(name = "id_orig_h")
	@NotNull
	private String idOrigHost;
	@Column(name = "id_orig_h_ip")
	@NotNull
	private BigInteger idOrigIp;
	@Column(name = "id_orig_p")
	@NotNull
	private int idOrigPort;

	@Column(name = "id_resp_h")
	@NotNull
	private String idRespHost;
	@Column(name = "id_resp_h_ip")
	@NotNull
	private BigInteger idRespIp;
	@Column(name = "id_resp_p")
	@NotNull
	private int idRespPort;

	@NotNull
	private int transDepth;

	@Lob
	private String helo;
	@Column(name = "mailfrom")
	@Lob
	private String mailFrom;
	@Column(name = "rcptto")
	@Lob
	private String rcptTo;
	@Lob
	private String date;

	@Lob
	private String from;
	@Lob
	private String to;
	@Lob
	private String replyTo;
	@Lob
	private String msgId;
	@Lob
	private String inReplyTo;
	@Lob
	private String subject;

	private String xOriginatingIp;
	private BigInteger xOriginatingIpIp;

	@Lob
	private String firstReceived;
	@Lob
	private String secondReceived;
	@Lob
	private String lastReply;
	@Lob
	private String path;
	@Lob
	private String userAgent;

	private Boolean tls;

	@Lob
	private String fuids;

	private Boolean isWebmail;

	private String amtTasksList;

	public BigDecimal getTs() {
		return ts;
	}

	public void setTs(BigDecimal ts) {
		this.ts = ts;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIdOrigHost() {
		return idOrigHost;
	}

	public void setIdOrigHost(String idOrigHost) {
		this.idOrigHost = idOrigHost;
	}

	public BigInteger getIdOrigIp() {
		return idOrigIp;
	}

	public void setIdOrigIp(BigInteger idOrigIp) {
		this.idOrigIp = idOrigIp;
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

	public BigInteger getIdRespIp() {
		return idRespIp;
	}

	public void setIdRespIp(BigInteger idRespIp) {
		this.idRespIp = idRespIp;
	}

	public int getIdRespPort() {
		return idRespPort;
	}

	public void setIdRespPort(int idRespPort) {
		this.idRespPort = idRespPort;
	}

	public int getTransDepth() {
		return transDepth;
	}

	public void setTransDepth(int transDepth) {
		this.transDepth = transDepth;
	}

	public String getHelo() {
		return helo;
	}

	public void setHelo(String helo) {
		this.helo = helo;
	}

	public String getMailFrom() {
		return mailFrom;
	}

	public void setMailFrom(String mailFrom) {
		this.mailFrom = mailFrom;
	}

	public String getRcptTo() {
		return rcptTo;
	}

	public void setRcptTo(String rcptTo) {
		this.rcptTo = rcptTo;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getxOriginatingIp() {
		return xOriginatingIp;
	}

	public void setxOriginatingIp(String xOriginatingIp) {
		this.xOriginatingIp = xOriginatingIp;
	}

	public BigInteger getxOriginatingIpIp() {
		return xOriginatingIpIp;
	}

	public void setxOriginatingIpIp(BigInteger xOriginatingIpIp) {
		this.xOriginatingIpIp = xOriginatingIpIp;
	}

	public String getFirstReceived() {
		return firstReceived;
	}

	public void setFirstReceived(String firstReceived) {
		this.firstReceived = firstReceived;
	}

	public String getSecondReceived() {
		return secondReceived;
	}

	public void setSecondReceived(String secondReceived) {
		this.secondReceived = secondReceived;
	}

	public String getLastReply() {
		return lastReply;
	}

	public void setLastReply(String lastReply) {
		this.lastReply = lastReply;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public String getAmtTasksList() {
		return amtTasksList;
	}

	public void setAmtTasksList(String amtTasksList) {
		this.amtTasksList = amtTasksList;
	}
}
