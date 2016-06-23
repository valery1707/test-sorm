package name.valery1707.megatel.sorm.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@SuppressWarnings("unused")
public class TaskFilterValue extends ABaseEntity {
	@NotNull
	@ManyToOne
	private TaskFilter taskFilter;

	@NotNull
	private String value;

	public TaskFilter getTaskFilter() {
		return taskFilter;
	}

	public void setTaskFilter(TaskFilter taskFilter) {
		this.taskFilter = taskFilter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public static final class TaskFilterValueBuilder {
		private TaskFilter taskFilter;
		private String value;
		private Long id;

		private TaskFilterValueBuilder() {
		}

		public static TaskFilterValueBuilder aTaskFilterValue() {
			return new TaskFilterValueBuilder();
		}

		public TaskFilterValueBuilder withTaskFilter(TaskFilter taskFilter) {
			this.taskFilter = taskFilter;
			return this;
		}

		public TaskFilterValueBuilder withValue(String value) {
			this.value = value;
			return this;
		}

		public TaskFilterValueBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public TaskFilterValue build() {
			TaskFilterValue taskFilterValue = new TaskFilterValue();
			taskFilterValue.setTaskFilter(taskFilter);
			taskFilterValue.setValue(value);
			taskFilterValue.setId(id);
			return taskFilterValue;
		}
	}
}
