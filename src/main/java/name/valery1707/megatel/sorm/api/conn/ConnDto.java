package name.valery1707.megatel.sorm.api.conn;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Conn;

import java.time.format.DateTimeFormatter;

import static name.valery1707.megatel.sorm.DateUtils.bigDecimalToZonedDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConnDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private String ts;
	private String idOrigHost;
	private int idOrigPort;
	private String idRespHost;
	private int idRespPort;
	private String proto;
	private String service;

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

	public static ConnDto fromEntity(Conn src) {
		ConnDto dto = new ConnDto();
		dto.setTs(bigDecimalToZonedDateTime(src.getTs()).format(FORMATTER));
		dto.setIdOrigHost(src.getIdOrigHost());
		dto.setIdOrigPort(src.getIdOrigPort());
		dto.setIdRespHost(src.getIdRespHost());
		dto.setIdRespPort(src.getIdRespPort());
		dto.setProto(src.getProto());
		dto.setService(src.getService());
		return dto;
	}
}
