package name.valery1707.megatel.sorm.api.http;

import java.time.ZonedDateTime;
import java.util.List;

public class HttpFilter {
	private List<ZonedDateTime> ts;
	private String idOrigHost;
	private String idOrigPort;
	private String idRespHost;
	private String idRespPort;
	private String method;
	private String host;
	private String uri;
	private String referrer;
	private String userAgent;
	private String requestBodyLen;
	private String responseBodyLen;
	private String statusCode;

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

	public String getRequestBodyLen() {
		return requestBodyLen;
	}

	public void setRequestBodyLen(String requestBodyLen) {
		this.requestBodyLen = requestBodyLen;
	}

	public String getResponseBodyLen() {
		return responseBodyLen;
	}

	public void setResponseBodyLen(String responseBodyLen) {
		this.responseBodyLen = responseBodyLen;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
