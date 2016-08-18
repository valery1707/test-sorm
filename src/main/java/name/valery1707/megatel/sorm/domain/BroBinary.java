package name.valery1707.megatel.sorm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@SuppressWarnings("unused")
public class BroBinary {
	@Id
	@NotNull
	private BigDecimal ts;

	@NotNull
	private String uid;

	@Column(name = "id_orig_h")
	@NotNull
	private String idOrigHost;
	@Column(name = "id_orig_h_ip")
	@NotNull
	private BigInteger idOrigIp;
	@Column(name = "id_orig_p")
	@NotNull
	private int idOrigPort;

	@Column(name = "id_resp_h")
	@NotNull
	private String idRespHost;
	@Column(name = "id_resp_h_ip")
	@NotNull
	private BigInteger idRespIp;
	@Column(name = "id_resp_p")
	@NotNull
	private int idRespPort;

	@NotNull
	private String protocol;

	private String amtTasksList;

	public BigDecimal getTs() {
		return ts;
	}

	public void setTs(BigDecimal ts) {
		this.ts = ts;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getIdOrigHost() {
		return idOrigHost;
	}

	public void setIdOrigHost(String idOrigHost) {
		this.idOrigHost = idOrigHost;
	}

	public BigInteger getIdOrigIp() {
		return idOrigIp;
	}

	public void setIdOrigIp(BigInteger idOrigIp) {
		this.idOrigIp = idOrigIp;
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

	public BigInteger getIdRespIp() {
		return idRespIp;
	}

	public void setIdRespIp(BigInteger idRespIp) {
		this.idRespIp = idRespIp;
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

	public String getAmtTasksList() {
		return amtTasksList;
	}

	public void setAmtTasksList(String amtTasksList) {
		this.amtTasksList = amtTasksList;
	}
}
