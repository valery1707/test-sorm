package name.valery1707.megatel.sorm.api.task.permit;

import javaslang.Value;
import name.valery1707.megatel.sorm.api.account.AccountDto;
import name.valery1707.megatel.sorm.api.auth.AccountRepo;
import name.valery1707.megatel.sorm.api.task.TaskDto;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static name.valery1707.megatel.sorm.DateUtils.parseDateTime;
import static name.valery1707.megatel.sorm.db.SingularAttributeGetter.field;

@RestController
@RequestMapping("/api/task/permit")
public class TaskPermitController {
	@Inject
	private TaskPermitRepo repo;

	@Inject
	private TaskRepo taskRepo;

	@Inject
	private AccountRepo accountRepo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<TaskPermit, TaskPermitFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<TaskPermit, TaskPermitFilter>(SpecificationMode.AND)
				.withNumber(TaskPermitFilter::getId, TaskPermit_.id)
				.withString(TaskPermitFilter::getAgency, TaskPermit_.agency)
				.withNumber(TaskPermitFilter::getTaskId, field(TaskPermit_.task).nest(Task_.id))
				.withNumber(TaskPermitFilter::getAccountId, field(TaskPermit_.account).nest(Account_.id))
				.withDateTime(TaskPermitFilter::getPeriodStart, TaskPermit_.periodStart)
				.withDateTime(TaskPermitFilter::getPeriodFinish, TaskPermit_.periodFinish)
				.withEquals(TaskPermitFilter::getShowOnlyActive, TaskPermit_.isActive)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<TaskPermitDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) TaskPermitFilter filter
	) {
		accountService.requireAnyRole(Account.Role.ADMIN);
		filter.setShowOnlyActive(true);
		Specification<TaskPermit> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(TaskPermitDto::new);
	}

	@RequestMapping
	public TaskPermitDto get(@RequestParam long id) {
		accountService.requireAnyRole(Account.Role.ADMIN);
		TaskPermit entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", "TaskPermit", id));
		}
		return new TaskPermitDto(entity);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@Transactional
	public void delete(@RequestParam long id) {
		accountService.requireAnyRole(Account.Role.ADMIN);
		TaskPermit entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", "TaskPermit", id));
		}
		entity.setActive(false);
		repo.save(entity);
	}

	private void validate(TaskPermitDto dto, BindingResult validation) {
		Task task = taskRepo.findOne(dto.getTaskId());
		if (task == null) {
			validation.rejectValue("taskId", "{TaskPermit.taskId.constraint.exists}", "Task must exists");
		}

		Account account = accountRepo.findOne(dto.getAccountId());
		if (account == null || !Account.Role.OPERATOR.equals(account.getRole())) {
			validation.rejectValue("taskId", "{TaskPermit.accountId.constraint.exists}", "Operator account must exists");
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid TaskPermitDto dto, BindingResult validation) {
		accountService.requireAnyRole(Account.Role.ADMIN);

		validate(dto, validation);

		if (validation.getErrorCount() > 0) {
			javaslang.collection.Map<String, List<FieldError>> fieldErrorMap = javaslang.collection.List
					.ofAll(validation.getFieldErrors())
					.groupBy(FieldError::getField)
					.mapValues(Value::toJavaList);
			return ResponseEntity.badRequest().body(fieldErrorMap.toJavaMap());
		}

		TaskPermit entity = null;
		if (dto.getId() != 0) {
			entity = repo.findOne(dto.getId());
		}
		if (entity == null) {
			entity = new TaskPermit();
			//defaults
			entity.setActive(true);
		}

		entity.setAgency(dto.getAgency());
		entity.setTask(taskRepo.getOne(dto.getTaskId()));
		entity.setAccount(accountRepo.getOne(dto.getAccountId()));
		entity.setPeriodStart(parseDateTime(dto.getPeriodStart()));
		entity.setPeriodFinish(parseDateTime(dto.getPeriodFinish()));

		repo.save(entity);

		return ResponseEntity.ok(Collections.emptyMap());
	}

	@RequestMapping(path = "comboTask")
	public List<TaskDto> comboTask() {
		//todo Only Active
		return taskRepo.findAll()
				.stream().map(TaskDto::new)
				.collect(toList());
	}

	@RequestMapping(path = "comboAccount")
	public List<AccountDto> comboAccount() {
		//todo Only Active
		return accountRepo.findAll()
				.stream().map(AccountDto::new)
				.collect(toList());
	}
}
