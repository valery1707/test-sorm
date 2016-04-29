package name.valery1707.megatel.sorm.api.bro.conn;

import java.time.ZonedDateTime;
import java.util.List;

public class BroConnFilter {
	private List<ZonedDateTime> ts;
	private String proto;
	private String idOrigHost;
	private String idOrigPort;
	private String idRespHost;
	private String idRespPort;

	public List<ZonedDateTime> getTs() {
		return ts;
	}

	public void setTs(List<ZonedDateTime> ts) {
		this.ts = ts;
	}

	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
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
}
