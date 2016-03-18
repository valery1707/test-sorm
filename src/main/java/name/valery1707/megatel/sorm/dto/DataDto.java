package name.valery1707.megatel.sorm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.IpUtils;
import name.valery1707.megatel.sorm.domain.Data;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private String dateTime;
	private String srcIp;
	private int srcPort;
	private String dstIp;
	private int dstPort;
	private String protocol;

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstIp() {
		return dstIp;
	}

	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public static DataDto fromEntity(Data data) {
		DataDto dto = new DataDto();
		dto.setDateTime(data.getDateTime().format(FORMATTER));
		dto.setSrcIp(IpUtils.numberToIp(data.getSrcIp()).getHostAddress());
		dto.setSrcPort(data.getSrcPort());
		dto.setDstIp(IpUtils.numberToIp(data.getDstIp()).getHostAddress());
		dto.setDstPort(data.getDstPort());
		dto.setProtocol(data.getProtocol());
		return dto;
	}
}
