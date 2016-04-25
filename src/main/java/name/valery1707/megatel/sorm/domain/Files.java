package name.valery1707.megatel.sorm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@SuppressWarnings("unused")
public class Files {
	@Id
	@NotNull
	private BigDecimal ts;

	@NotNull
	private String fuid;

	@Lob
	private String txHosts;
	@Lob
	private String rxHosts;

	@Lob
	private String connUids;
	@Lob
	private String source;
	private Integer depth;

	private String analyzers;

	private String mimeType;
	@Lob
	private String filename;

	private BigDecimal duration;
	private Boolean localOrig;
	private Boolean isOrig;

	private Integer seenBytes;
	private Integer totalBytes;
	private Integer missingBytes;
	private Integer overflowBytes;

	private Boolean timedout;

	private String parentFuid;

	@Column(name = "md5")
	private String md5;
	@Column(name = "sha1")
	private String sha1;
	@Column(name = "sha256")
	private String sha256;
	@Lob
	private String extracted;

	public BigDecimal getTs() {
		return ts;
	}

	public void setTs(BigDecimal ts) {
		this.ts = ts;
	}

	public String getFuid() {
		return fuid;
	}

	public void setFuid(String fuid) {
		this.fuid = fuid;
	}

	public String getTxHosts() {
		return txHosts;
	}

	public void setTxHosts(String txHosts) {
		this.txHosts = txHosts;
	}

	public String getRxHosts() {
		return rxHosts;
	}

	public void setRxHosts(String rxHosts) {
		this.rxHosts = rxHosts;
	}

	public String getConnUids() {
		return connUids;
	}

	public void setConnUids(String connUids) {
		this.connUids = connUids;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Integer getDepth() {
		return depth;
	}

	public void setDepth(Integer depth) {
		this.depth = depth;
	}

	public String getAnalyzers() {
		return analyzers;
	}

	public void setAnalyzers(String analyzers) {
		this.analyzers = analyzers;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public BigDecimal getDuration() {
		return duration;
	}

	public void setDuration(BigDecimal duration) {
		this.duration = duration;
	}

	public Boolean getLocalOrig() {
		return localOrig;
	}

	public void setLocalOrig(Boolean localOrig) {
		this.localOrig = localOrig;
	}

	public Boolean getOrig() {
		return isOrig;
	}

	public void setOrig(Boolean orig) {
		isOrig = orig;
	}

	public Integer getSeenBytes() {
		return seenBytes;
	}

	public void setSeenBytes(Integer seenBytes) {
		this.seenBytes = seenBytes;
	}

	public Integer getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(Integer totalBytes) {
		this.totalBytes = totalBytes;
	}

	public Integer getMissingBytes() {
		return missingBytes;
	}

	public void setMissingBytes(Integer missingBytes) {
		this.missingBytes = missingBytes;
	}

	public Integer getOverflowBytes() {
		return overflowBytes;
	}

	public void setOverflowBytes(Integer overflowBytes) {
		this.overflowBytes = overflowBytes;
	}

	public Boolean getTimedout() {
		return timedout;
	}

	public void setTimedout(Boolean timedout) {
		this.timedout = timedout;
	}

	public String getParentFuid() {
		return parentFuid;
	}

	public void setParentFuid(String parentFuid) {
		this.parentFuid = parentFuid;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getSha256() {
		return sha256;
	}

	public void setSha256(String sha256) {
		this.sha256 = sha256;
	}

	public String getExtracted() {
		return extracted;
	}

	public void setExtracted(String extracted) {
		this.extracted = extracted;
	}
}
