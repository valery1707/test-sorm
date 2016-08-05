package name.valery1707.megatel.sorm.api.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Stream;
import name.valery1707.core.api.BaseDto;
import name.valery1707.core.utils.DateUtils;
import name.valery1707.megatel.sorm.domain.Task;
import name.valery1707.megatel.sorm.domain.TaskFilter;
import name.valery1707.megatel.sorm.domain.TaskFilterValue;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static name.valery1707.core.utils.DateUtils.formatDateTime;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto implements BaseDto {
	public static final javaslang.collection.Map<String, List<String>> DEFAULT_FILTERS = Stream
			.of(TaskFilter.TaskFilterType.values())
			.map(Enum::name)
			.map(String::toLowerCase)
			.toMap(name -> Tuple.of(name, Collections.emptyList()));

	private long id;
	private String createdBy;
	private String createdAt;
	@NotNull
	@Size(min = 3)
	private String agency;
	@NotNull
	@Size(min = 3)
	private String clientAlias;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodStart;
	@NotNull
	@Pattern(regexp = DateUtils.LOCAL_DATE_TIME_PATTERN)
	private String periodFinish;
	private Map<String, List<String>> filter;
	@NotNull
	@Size(min = 3)
	private String note;
	private Boolean isActive;

	public TaskDto() {
	}

	public TaskDto(Task src) {
		this();
		setId(src.getId());
		setCreatedBy(src.getCreatedBy().getUsername());
		setCreatedAt(formatDateTime(src.getCreatedAt()));
		setAgency(src.getAgency());
		setClientAlias(src.getClientAlias());
		setPeriodStart(formatDateTime(src.getPeriodStart()));
		setPeriodFinish(formatDateTime(src.getPeriodFinish()));
		setNote(src.getNote());
		setActive(src.isActive());
		setFilter(HashMap.ofAll(src.getFilter())
				.mapValues(TaskFilter::getValue)
				.mapValues(v -> Stream.ofAll(v).map(TaskFilterValue::getValue).toJavaList())
				.merge(DEFAULT_FILTERS, (real, def) -> real)
				.toJavaMap()
		);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
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

	public Map<String, List<String>> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, List<String>> filter) {
		this.filter = filter;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getActive() {
		return isActive;
	}

	public void setActive(Boolean active) {
		isActive = active;
	}
}
