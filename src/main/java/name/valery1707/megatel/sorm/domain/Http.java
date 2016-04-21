package name.valery1707.megatel.sorm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@SuppressWarnings("unused")
public class Http {
	@Id
	@NotNull
	private BigDecimal ts;

	@NotNull
	private String uid;

	@Column(name = "id_orig_h")
	@NotNull
	private String idOrigHost;

	@Column(name = "id_orig_p")
	@NotNull
	private int idOrigPort;

	@Column(name = "id_resp_h")
	@NotNull
	private String idRespHost;

	@Column(name = "id_resp_p")
	@NotNull
	private int idRespPort;

	@NotNull
	private int transDepth;

	private String method;
	@Lob
	private String host;
	@Lob
	private String uri;

	@Lob
	private String referrer;
	@Lob
	private String userAgent;

	private Integer requestBodyLen;
	private Integer responseBodyLen;

	private Integer statusCode;
	@Lob
	private String statusMsg;

	private Integer infoCode;
	@Lob
	private String infoMsg;

	@Lob
	private String filename;

	private String tags;

	@Lob
	private String username;
	@Lob
	private String password;

	@Lob
	private String proxied;

	@Lob
	private String origFuids;
	@Lob
	private String origMimeTypes;

	@Lob
	private String respFuids;
	@Lob
	private String respMimeTypes;

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

	public int getTransDepth() {
		return transDepth;
	}

	public void setTransDepth(int transDepth) {
		this.transDepth = transDepth;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Integer getRequestBodyLen() {
		return requestBodyLen;
	}

	public void setRequestBodyLen(Integer requestBodyLen) {
		this.requestBodyLen = requestBodyLen;
	}

	public Integer getResponseBodyLen() {
		return responseBodyLen;
	}

	public void setResponseBodyLen(Integer responseBodyLen) {
		this.responseBodyLen = responseBodyLen;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public Integer getInfoCode() {
		return infoCode;
	}

	public void setInfoCode(Integer infoCode) {
		this.infoCode = infoCode;
	}

	public String getInfoMsg() {
		return infoMsg;
	}

	public void setInfoMsg(String infoMsg) {
		this.infoMsg = infoMsg;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

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

	public String getProxied() {
		return proxied;
	}

	public void setProxied(String proxied) {
		this.proxied = proxied;
	}

	public String getOrigFuids() {
		return origFuids;
	}

	public void setOrigFuids(String origFuids) {
		this.origFuids = origFuids;
	}

	public String getOrigMimeTypes() {
		return origMimeTypes;
	}

	public void setOrigMimeTypes(String origMimeTypes) {
		this.origMimeTypes = origMimeTypes;
	}

	public String getRespFuids() {
		return respFuids;
	}

	public void setRespFuids(String respFuids) {
		this.respFuids = respFuids;
	}

	public String getRespMimeTypes() {
		return respMimeTypes;
	}

	public void setRespMimeTypes(String respMimeTypes) {
		this.respMimeTypes = respMimeTypes;
	}
}
