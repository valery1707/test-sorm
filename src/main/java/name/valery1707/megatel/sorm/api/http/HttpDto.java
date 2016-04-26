package name.valery1707.megatel.sorm.api.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Http;

import java.time.format.DateTimeFormatter;

import static name.valery1707.megatel.sorm.DateUtils.bigDecimalToZonedDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private String ts;
	private String idOrigHost;
	private int idOrigPort;
	private String idRespHost;
	private int idRespPort;

	private String method;
	private String host;
	private String uri;

	private String referrer;
	private String userAgent;

	private Long requestBodyLen;
	private Long responseBodyLen;

	private Integer statusCode;
	private String statusMsg;

	private Integer infoCode;
	private String infoMsg;

	private boolean hasFiles;

	public HttpDto() {
	}

	public HttpDto(Http src) {
		this();
		setTs(bigDecimalToZonedDateTime(src.getTs()).format(FORMATTER));
		setIdOrigHost(src.getIdOrigHost());
		setIdOrigPort(src.getIdOrigPort());
		setIdRespHost(src.getIdRespHost());
		setIdRespPort(src.getIdRespPort());
		setMethod(src.getMethod());
		setHost(src.getHost());
		setUri(src.getUri());
		setReferrer(src.getReferrer());
		setUserAgent(src.getUserAgent());
		setRequestBodyLen(src.getRequestBodyLen());
		setResponseBodyLen(src.getResponseBodyLen());
		setStatusCode(src.getStatusCode());
		setStatusMsg(src.getStatusMsg());
		setInfoCode(src.getInfoCode());
		setInfoMsg(src.getInfoMsg());
		setHasFiles(src.getOrigFuids() != null || src.getRespFuids() != null);
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

	public Long getRequestBodyLen() {
		return requestBodyLen;
	}

	public void setRequestBodyLen(Long requestBodyLen) {
		this.requestBodyLen = requestBodyLen;
	}

	public Long getResponseBodyLen() {
		return responseBodyLen;
	}

	public void setResponseBodyLen(Long responseBodyLen) {
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

	public boolean isHasFiles() {
		return hasFiles;
	}

	public void setHasFiles(boolean hasFiles) {
		this.hasFiles = hasFiles;
	}
}
