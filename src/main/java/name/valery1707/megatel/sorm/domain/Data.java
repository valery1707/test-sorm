package name.valery1707.megatel.sorm.domain;

import name.valery1707.core.domain.ABaseEntity;

import javax.persistence.Entity;
import java.math.BigInteger;
import java.time.ZonedDateTime;

@Entity
@SuppressWarnings("unused")
public class Data extends ABaseEntity {
	private ZonedDateTime dateTime;
	private BigInteger srcIp;
	private int srcPort;
	private BigInteger dstIp;
	private int dstPort;
	private String protocol;

	public ZonedDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(ZonedDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public BigInteger getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(BigInteger srcIp) {
		this.srcIp = srcIp;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public BigInteger getDstIp() {
		return dstIp;
	}

	public void setDstIp(BigInteger dstIp) {
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
