package name.valery1707.megatel.sorm.api.data;

import java.time.ZonedDateTime;
import java.util.List;

public class DataFilter {
	private List<ZonedDateTime> dateTime;
	private String protocol;
	private String srcIp;
	private String srcPort;
	private String dstIp;
	private String dstPort;

	public List<ZonedDateTime> getDateTime() {
		return dateTime;
	}

	public void setDateTime(List<ZonedDateTime> dateTime) {
		this.dateTime = dateTime;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public String getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(String srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstIp() {
		return dstIp;
	}

	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}

	public String getDstPort() {
		return dstPort;
	}

	public void setDstPort(String dstPort) {
		this.dstPort = dstPort;
	}
}
