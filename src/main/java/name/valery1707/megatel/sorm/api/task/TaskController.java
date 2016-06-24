package name.valery1707.megatel.sorm.api.task;

import javaslang.Tuple;
import javaslang.collection.HashMap;
import javaslang.collection.Stream;
import javaslang.control.Option;
import name.valery1707.megatel.sorm.api.BaseEntityController;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.Task;
import name.valery1707.megatel.sorm.domain.TaskFilter.TaskFilterType;
import name.valery1707.megatel.sorm.domain.TaskFilterValue;
import name.valery1707.megatel.sorm.domain.Task_;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static name.valery1707.megatel.sorm.DateUtils.parseDateTime;
import static name.valery1707.megatel.sorm.domain.TaskFilter.TaskFilterBuilder.aTaskFilter;
import static name.valery1707.megatel.sorm.domain.TaskFilterValue.TaskFilterValueBuilder.aTaskFilterValue;

@RestController
@RequestMapping("/api/task")
public class TaskController extends BaseEntityController<Task, TaskRepo, TaskFilter, TaskDto> {
	@Inject
	private TaskPermitRepo taskPermitRepo;

	@Inject
	private AccountService accountService;

	public TaskController() {
		super("task");
	}

	@Override
	protected SpecificationBuilder<Task, TaskFilter> buildUserFilter() {
		return new SpecificationBuilder<Task, TaskFilter>(SpecificationMode.AND)
				.withNumber(TaskFilter::getId, Task_.id)
				.withDateTime(TaskFilter::getPeriodStart, Task_.periodStart)
				.withDateTime(TaskFilter::getPeriodFinish, Task_.periodFinish)
				.withString(TaskFilter::getAgency, Task_.agency)
				.withString(TaskFilter::getClientAlias, Task_.clientAlias)
				.withString(TaskFilter::getNote, Task_.note)
				.withEquals(TaskFilter::getShowOnlyActive, Task_.isActive)
				.withCustom(TaskFilter::getAllowedIds, Task_.id, cb -> (field, filter) -> filter.isEmpty() ? cb.isNull(field) : field.in(filter))
				;
	}

	@Override
	protected void applyPermanentFilter(TaskFilter filter) {
		super.applyPermanentFilter(filter);
		filter.setShowOnlyActive(accountService.hasAnyRole(Account.Role.SUPERVISOR) ? null : true);
		if (accountService.hasAnyRole(Account.Role.OPERATOR)) {
			//todo Optimize
			filter.setAllowedIds(taskPermitRepo
					.findAllowedTaskAtTime(accountService.getCurrentAuditor(), ZonedDateTime.now()));
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<TaskDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) TaskFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid TaskDto dto, BindingResult validation) {
		return super.save(dto, validation);
	}

	@Override
	protected void validate(TaskDto dto, BindingResult validation) {
		super.validate(dto, validation);
		//todo Можно редактировать только задачу с датой активации в будущем
		// Нужно помнить что в DTO может быть значение дат отличающееся от того что в БД

		//todo Валидация периодов активности

		validateFilter(dto.getFilter() != null ? dto.getFilter() : Collections.emptyMap(), validation);
	}

	private void validateFilter(Map<String, List<String>> filter, BindingResult validation) {
		//Оставляем только известные типы фильтров
		filter.keySet().retainAll(TaskDto.DEFAULT_FILTERS.keySet().toJavaSet());
		//Удаляем пустые фильтры
		filter.entrySet().removeIf(e -> e.getValue() == null);
		filter.values().forEach(f -> f.removeIf(StringUtils::isBlank));
		filter.entrySet().removeIf(e -> e.getValue().isEmpty());
		//Проверка размера
		if (filter.isEmpty()) {
			validation.rejectValue("filter", "{Task.filter.constraint.empty}", "Filters must be non empty");
		}
		//Проверка значений для каждого типа фильтра
		for (TaskFilterType filterType : TaskFilterType.values()) {
			String name = filterType.name().toLowerCase();
			Stream<String> nonValid = Option.of(filter.get(name))
					.map(Stream::ofAll).getOrElse(Stream.empty())
					.filter(filterType.getValidator().negate());
			if (nonValid.nonEmpty()) {
				validation.rejectValue(String.format("filter[%s]", name),
						String.format("{Task.filter.constraint.%s}", name),
						new Object[]{nonValid.mkString(", ")},
						filterType.getMessage());
			}
		}
	}

	@Override
	protected void dto2domain(TaskDto dto, Task entity) {
		entity.setAgency(dto.getAgency());
		entity.setClientAlias(dto.getClientAlias());
		entity.setPeriodStart(parseDateTime(dto.getPeriodStart()));
		entity.setPeriodFinish(parseDateTime(dto.getPeriodFinish()));
		entity.setNote(dto.getNote());

		//region filter
		//Удаляем старые фильтры, которые убрал пользователь
		entity.getFilter().keySet().retainAll(dto.getFilter().keySet());
		//В том что осталось выполняем преобразования так что бы сохранить все ID
		entity.getFilter().forEach((name, filterBase) -> {
			List<String> filterUser = dto.getFilter().get(name);
			//Убираем значения, которые убрал пользователь
			filterBase.getValue().removeIf(v -> !filterUser.contains(v.getValue()));
			//Добавляем новые значения
			Stream<String> filterSaved = Stream.ofAll(filterBase.getValue()).map(TaskFilterValue::getValue);
			Stream<String> filterAdded = Stream.ofAll(filterUser).removeAll(filterSaved);
			filterBase.getValue().addAll(
					filterAdded.map(v -> aTaskFilterValue()
							.withTaskFilter(filterBase)
							.withValue(v)
							.build()
					).toJavaList()
			);
		});
		//Добавляем новые фильтры
		entity.getFilter().putAll(HashMap.ofAll(dto.getFilter())
				//Оставляем только новые фильтры
				.filter(f -> !entity.getFilter().keySet().contains(f._1()))
				//Создаём объекты для БД
				.map((name, list) -> {
					return Tuple.of(
							name,
							aTaskFilter()
									.withTask(entity)
									.withName(name)
									.withValue(Stream.ofAll(list)
											.map(f -> aTaskFilterValue()
													.withValue(f)
													.build())
											.toJavaSet())
									.build()
					);
				})
				.toJavaMap()
		);
		//endregion
	}
}
