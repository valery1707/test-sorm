package name.valery1707.megatel.sorm.api.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import name.valery1707.megatel.sorm.domain.Task;

import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	private long id;
	private String createdAt;
	private String agency;
	private String clientAlias;
	private String periodStart;
	private String periodFinish;
	private String note;

	public TaskDto() {
	}

	public TaskDto(Task src) {
		this();
		setId(src.getId());
		setCreatedAt(src.getCreatedAt().format(FORMATTER));
		setAgency(src.getAgency());
		setClientAlias(src.getClientAlias());
		setPeriodStart(src.getPeriodStart().format(FORMATTER));
		setPeriodFinish(src.getPeriodFinish().format(FORMATTER));
		setNote(src.getNote());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getAgency() {
		return agency;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public String getClientAlias() {
		return clientAlias;
	}

	public void setClientAlias(String clientAlias) {
		this.clientAlias = clientAlias;
	}

	public String getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(String periodStart) {
		this.periodStart = periodStart;
	}

	public String getPeriodFinish() {
		return periodFinish;
	}

	public void setPeriodFinish(String periodFinish) {
		this.periodFinish = periodFinish;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
