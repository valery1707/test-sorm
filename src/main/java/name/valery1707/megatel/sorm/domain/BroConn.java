package name.valery1707.megatel.sorm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@SuppressWarnings("unused")
public class BroConn {
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
	private String proto;

	private String service;

	private BigDecimal duration;

	private Long origBytes;
	private Long respBytes;

	private String connState;

	private Boolean localOrig;
	private Boolean localResp;

	private Long missedBytes;

	private String history;

	private Integer origPkts;
	private Long origIpBytes;
	private Integer respPkts;
	private Long respIpBytes;

	private String tunnelParents;

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

	public BigDecimal getDuration() {
		return duration;
	}

	public void setDuration(BigDecimal duration) {
		this.duration = duration;
	}

	public Long getOrigBytes() {
		return origBytes;
	}

	public void setOrigBytes(Long origBytes) {
		this.origBytes = origBytes;
	}

	public Long getRespBytes() {
		return respBytes;
	}

	public void setRespBytes(Long respBytes) {
		this.respBytes = respBytes;
	}

	public String getConnState() {
		return connState;
	}

	public void setConnState(String connState) {
		this.connState = connState;
	}

	public Boolean getLocalOrig() {
		return localOrig;
	}

	public void setLocalOrig(Boolean localOrig) {
		this.localOrig = localOrig;
	}

	public Boolean getLocalResp() {
		return localResp;
	}

	public void setLocalResp(Boolean localResp) {
		this.localResp = localResp;
	}

	public Long getMissedBytes() {
		return missedBytes;
	}

	public void setMissedBytes(Long missedBytes) {
		this.missedBytes = missedBytes;
	}

	public String getHistory() {
		return history;
	}

	public void setHistory(String history) {
		this.history = history;
	}

	public Integer getOrigPkts() {
		return origPkts;
	}

	public void setOrigPkts(Integer origPkts) {
		this.origPkts = origPkts;
	}

	public Long getOrigIpBytes() {
		return origIpBytes;
	}

	public void setOrigIpBytes(Long origIpBytes) {
		this.origIpBytes = origIpBytes;
	}

	public Integer getRespPkts() {
		return respPkts;
	}

	public void setRespPkts(Integer respPkts) {
		this.respPkts = respPkts;
	}

	public Long getRespIpBytes() {
		return respIpBytes;
	}

	public void setRespIpBytes(Long respIpBytes) {
		this.respIpBytes = respIpBytes;
	}

	public String getTunnelParents() {
		return tunnelParents;
	}

	public void setTunnelParents(String tunnelParents) {
		this.tunnelParents = tunnelParents;
	}

	public String getAmtTasksList() {
		return amtTasksList;
	}

	public void setAmtTasksList(String amtTasksList) {
		this.amtTasksList = amtTasksList;
	}
}
