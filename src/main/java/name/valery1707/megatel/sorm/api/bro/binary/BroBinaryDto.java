package name.valery1707.megatel.sorm.api.bro.binary;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.BroBinary;

import static name.valery1707.core.utils.DateUtils.bigDecimalToZonedDateTime;
import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BroBinaryDto {
	private String ts;
	private String idOrigHost;
	private int idOrigPort;
	private String idRespHost;
	private int idRespPort;

	private String protocol;

	public BroBinaryDto() {
	}

	public BroBinaryDto(BroBinary src) {
		this();
		setTs(formatDateTime(bigDecimalToZonedDateTime(src.getTs())));
		setIdOrigHost(src.getIdOrigHost());
		setIdOrigPort(src.getIdOrigPort());
		setIdRespHost(src.getIdRespHost());
		setIdRespPort(src.getIdRespPort());
		setProtocol(src.getProtocol());
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

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
