package name.valery1707.megatel.sorm.api.task;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.app.AccountService;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Event.EventType;
import name.valery1707.megatel.sorm.api.agency.AgencyController;
import name.valery1707.megatel.sorm.api.agency.AgencyDto;
import name.valery1707.megatel.sorm.api.agency.AgencyRepo;
import name.valery1707.megatel.sorm.api.task.permit.TaskPermitRepo;
import name.valery1707.megatel.sorm.domain.Agency_;
import name.valery1707.megatel.sorm.domain.Task;
import name.valery1707.megatel.sorm.domain.Task_;
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
import java.util.List;
import java.util.Map;

import static name.valery1707.core.utils.DateUtils.parseDateTime;

@RestController
@RequestMapping("/api/task")
public class TaskController extends BaseEntityController<Task, TaskRepo, TaskFilter, TaskDto> {
	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TaskPermitRepo taskPermitRepo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AgencyRepo agencyRepo;

	@Inject
	private AgencyController agencyController;

	public TaskController() {
		super("task");
	}

	@Override
	protected SpecificationBuilder<Task, TaskFilter> buildUserFilter() {
		return new SpecificationBuilder<Task, TaskFilter>(SpecificationMode.AND)
				.withNumber(TaskFilter::getId, Task_.id)
				.withString(TaskFilter::getNumber, Task_.number)
				.withDateTime(TaskFilter::getPeriodStart, Task_.periodStart)
				.withDateTime(TaskFilter::getPeriodFinish, Task_.periodFinish)
				.withString(TaskFilter::getAgencyName, Task_.agency, Agency_.name)
				.withString(TaskFilter::getClientAlias, Task_.clientAlias)
				.withEquals(TaskFilter::getMode, Task_.mode)
				.withString(TaskFilter::getNote, Task_.note)
				.withEquals(TaskFilter::getShowOnlyActive, Task_.isActive)
				.withCustom(TaskFilter::getAllowedIds, Task_.id, cb -> (field, filter) -> filter.isEmpty() ? cb.isNull(field) : field.in(filter))
				.withEquals(TaskFilter::getAgency, Task_.agency)
				;
	}

	@Override
	protected EventType eventCreate() {
		return EventType.ADMIN_TASK_CREATE;
	}

	@Override
	protected EventType eventRead() {
		return EventType.OPERATOR_TASK_VIEW;
	}

	@Override
	protected EventType eventUpdate() {
		//todo Редактирование Заданий не доступно
		return null;
	}

	@Override
	protected EventType eventDelete() {
		return EventType.ADMIN_TASK_DELETE;
	}

	@Override
	protected EventType eventFind() {
		switch (accountService().getCurrentAuditor().getRole()) {
			case ADMIN:
				return EventType.ADMIN_TASK_LIST;
			case OPERATOR:
				return EventType.OPERATOR_TASK_LIST;
			case SUPERVISOR:
				return EventType.SUPERVISOR_TASK_LIST;
			default:
				return EventType.ADMIN_TASK_LIST;
		}
	}

	@Override
	protected void applyPermanentFilter(TaskFilter filter) {
		super.applyPermanentFilter(filter);
		AccountService accountService = accountService();
		filter.setShowOnlyActive(accountService.hasAnyRole(Account.Role.SUPERVISOR) ? null : true);
		if (accountService.hasAnyRole(Account.Role.OPERATOR)) {
			//todo Optimize
			filter.setAllowedIds(taskPermitRepo
					.findAllowedTaskAtTime(accountService.getCurrentAuditor(), ZonedDateTime.now()));
		}
		filter.setAgency(accountService.getAccount().get().getAgency());
	}

	@RequestMapping(method = RequestMethod.POST)
	@Override
	public Page<TaskDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) TaskFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	@Override
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid TaskDto dto, BindingResult validation) {
		return super.save(dto, validation);
	}

	@Override
	protected void validate(TaskDto dto, BindingResult validation) {
		super.validate(dto, validation);
		//todo Можно редактировать только задачу с датой активации в будущем
		// Нужно помнить что в DTO может быть значение дат отличающееся от того что в БД

		//todo Валидация периодов активности

		//Если у пользователя есть привязка к агенству, то при создании он может выбрать только доступные ему агенства
		if (
				dto.getAgency() != null &&
				accountService().getAccount().get().getAgency() != null &&
				dto.getAgency().getId() != accountService().getAccount().get().getAgency().getId()
				) {
			validation.rejectValue("agency", "{Task.agency.constraint.inaccessible}", "Agency must be accessible for current Account");
		}
	}

	@Override
	protected void dto2domain(TaskDto dto, Task entity) {
		entity.setAgency(agencyRepo.getOne(dto.getAgency().getId()));
		entity.setClientAlias(dto.getClientAlias());
		entity.setPeriodStart(parseDateTime(dto.getPeriodStart()));
		entity.setPeriodFinish(parseDateTime(dto.getPeriodFinish()));
		entity.setMode(dto.getMode());
		entity.setNote(dto.getNote());
	}

	@RequestMapping(path = "comboAgency")
	public List<AgencyDto> comboAgency() {
		return agencyController.comboAgency();
	}
}
