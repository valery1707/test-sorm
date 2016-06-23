package name.valery1707.megatel.sorm.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@Entity
@SuppressWarnings("unused")
public class TaskFilter extends ABaseEntity {
	public enum TaskFilterType {
		EMAIL, IP
	}

	@NotNull
	@ManyToOne
	private Task task;

	@NotNull
	@Pattern(regexp = "^(email|ip)$", flags = {Pattern.Flag.CASE_INSENSITIVE})
	private String name;

	//Из-за особенностей работы хибера валидаторы тут ставить нельзя
	@OneToMany(mappedBy = "taskFilter", cascade = {CascadeType.ALL}, orphanRemoval = true)
	private Set<TaskFilterValue> value;

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<TaskFilterValue> getValue() {
		return value;
	}

	public void setValue(Set<TaskFilterValue> value) {
		this.value = value;
		value.forEach(v -> v.setTaskFilter(this));
	}

	public static final class TaskFilterBuilder {
		private Task task;
		private String name;
		private Set<TaskFilterValue> value;
		private Long id;

		private TaskFilterBuilder() {
		}

		public static TaskFilterBuilder aTaskFilter() {
			return new TaskFilterBuilder();
		}

		public TaskFilterBuilder withTask(Task task) {
			this.task = task;
			return this;
		}

		public TaskFilterBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public TaskFilterBuilder withValue(Set<TaskFilterValue> value) {
			this.value = value;
			return this;
		}

		public TaskFilterBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public TaskFilter build() {
			TaskFilter taskFilter = new TaskFilter();
			taskFilter.setTask(task);
			taskFilter.setName(name);
			taskFilter.setValue(value);
			taskFilter.setId(id);
			return taskFilter;
		}
	}
}
