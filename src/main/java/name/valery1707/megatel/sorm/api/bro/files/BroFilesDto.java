package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.domain.BroFiles;

import java.time.format.DateTimeFormatter;

import static name.valery1707.megatel.sorm.DateUtils.bigDecimalToZonedDateTime;

public class BroFilesDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private String ts;

	private String mimeType;
	private String filename;
	private Long seenBytes;
	private Long totalBytes;
	private Long missingBytes;
	private String extracted;

	public BroFilesDto() {
	}

	public BroFilesDto(BroFiles src) {
		this();
		setTs(bigDecimalToZonedDateTime(src.getTs()).format(FORMATTER));
		setMimeType(src.getMimeType());
		setFilename(src.getFilename());
		setExtracted(src.getExtracted());
		setSeenBytes(src.getSeenBytes());
		setTotalBytes(src.getTotalBytes());
		setMissingBytes(src.getMissingBytes());
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
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

	public Long getSeenBytes() {
		return seenBytes;
	}

	public void setSeenBytes(Long seenBytes) {
		this.seenBytes = seenBytes;
	}

	public Long getTotalBytes() {
		return totalBytes;
	}

	public void setTotalBytes(Long totalBytes) {
		this.totalBytes = totalBytes;
	}

	public Long getMissingBytes() {
		return missingBytes;
	}

	public void setMissingBytes(Long missingBytes) {
		this.missingBytes = missingBytes;
	}

	public String getExtracted() {
		return extracted;
	}

	public void setExtracted(String extracted) {
		this.extracted = extracted;
	}
}
