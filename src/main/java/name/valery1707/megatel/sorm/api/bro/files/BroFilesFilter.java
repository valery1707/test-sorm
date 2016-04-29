package name.valery1707.megatel.sorm.api.bro.files;

import java.time.ZonedDateTime;
import java.util.List;

public class BroFilesFilter {
	private List<ZonedDateTime> ts;
	private String mimeType;
	private String filename;
	private String seenBytes;
	private String totalBytes;
	private String missingBytes;
	private String extracted;

	public List<ZonedDateTime> getTs() {
		return ts;
	}

	public void setTs(List<ZonedDateTime> ts) {
		this.ts = ts;
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

	public String getSeenBytes() {
		return seenBytes;
	}

	public void setSeenBytes(String seenBytes) {
		this.seenBytes = seenBytes;
	}

	public String getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(String totalBytes) {
		this.totalBytes = totalBytes;
	}

	public String getMissingBytes() {
		return missingBytes;
	}

	public void setMissingBytes(String missingBytes) {
		this.missingBytes = missingBytes;
	}

	public String getExtracted() {
		return extracted;
	}

	public void setExtracted(String extracted) {
		this.extracted = extracted;
	}
}
