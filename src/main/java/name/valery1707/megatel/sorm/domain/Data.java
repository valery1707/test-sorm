package name.valery1707.megatel.sorm.domain;

import javax.persistence.Entity;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Data extends ABaseEntity {
	private ZonedDateTime dateTime;
	private long srcIp;
	private int srcPort;
	private long dstIp;
	private int dstPort;
	private String protocol;

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public long getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(long srcIp) {
		this.srcIp = srcIp;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public long getDstIp() {
		return dstIp;
	}

	public void setDstIp(long dstIp) {
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
}
