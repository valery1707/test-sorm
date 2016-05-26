package name.valery1707.megatel.sorm.api.bro.conn;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.BroConn;

import static name.valery1707.megatel.sorm.DateUtils.bigDecimalToZonedDateTime;
import static name.valery1707.megatel.sorm.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BroConnDto {
	private String ts;
	private String idOrigHost;
	private int idOrigPort;
	private String idRespHost;
	private int idRespPort;

	private String proto;
	private String service;

	public BroConnDto() {
	}

	public BroConnDto(BroConn src) {
		this();
		setTs(formatDateTime(bigDecimalToZonedDateTime(src.getTs())));
		setIdOrigHost(src.getIdOrigHost());
		setIdOrigPort(src.getIdOrigPort());
		setIdRespHost(src.getIdRespHost());
		setIdRespPort(src.getIdRespPort());
		setProto(src.getProto());
		setService(src.getService());
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

	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
