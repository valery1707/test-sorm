package name.valery1707.megatel.sorm.api.files;

import name.valery1707.megatel.sorm.domain.Files;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;

import static name.valery1707.megatel.sorm.DateUtils.bigDecimalToZonedDateTime;

public class FilesDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private String ts;

	private String mimeType;
	private String filename;
	private long seenBytes;
	private String extracted;

	public FilesDto() {
	}

	public FilesDto(Files src) {
		this();
		setTs(bigDecimalToZonedDateTime(src.getTs()).format(FORMATTER));
		setMimeType(src.getMimeType());
		setFilename(src.getFilename());
		setExtracted(src.getExtracted());
		setSeenBytes(
				Stream.of(src.getSeenBytes(), src.getTotalBytes())
						.filter(Objects::nonNull)
						.mapToLong(i -> i)
						.max().orElse(0)
		);
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

	public long getSeenBytes() {
		return seenBytes;
	}

	public void setSeenBytes(long seenBytes) {
		this.seenBytes = seenBytes;
	}

	public String getExtracted() {
		return extracted;
	}

	public void setExtracted(String extracted) {
		this.extracted = extracted;
	}
}
