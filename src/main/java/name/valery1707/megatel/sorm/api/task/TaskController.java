package name.valery1707.megatel.sorm.api.task;

import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Account;
import name.valery1707.megatel.sorm.domain.Task;
import name.valery1707.megatel.sorm.domain.Task_;
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
import java.util.stream.Collectors;

import static name.valery1707.megatel.sorm.DateUtils.parseDateTime;

@RestController
@RequestMapping("/api/task")
public class TaskController {
	@Inject
	private TaskRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<Task, TaskFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Task, TaskFilter>(SpecificationMode.AND)
				.withNumber(TaskFilter::getId, Task_.id)
				.withDateTime(TaskFilter::getPeriodStart, Task_.periodStart)
				.withDateTime(TaskFilter::getPeriodFinish, Task_.periodFinish)
				.withString(TaskFilter::getAgency, Task_.agency)
				.withString(TaskFilter::getClientAlias, Task_.clientAlias)
				.withString(TaskFilter::getNote, Task_.note)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<TaskDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) TaskFilter filter
	) {
		accountService.requireAnyRole(Account.Role.ADMIN, Account.Role.OPERATOR);
		Specification<Task> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(TaskDto::new);
	}

	@RequestMapping
	public TaskDto get(@RequestParam long id) {
		accountService.requireAnyRole(Account.Role.ADMIN);
		Task entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", "Task", id));
		}
		return new TaskDto(entity);
	}

	private void validate(TaskDto dto, BindingResult validation) {

	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid TaskDto dto, BindingResult validation) {
		accountService.requireAnyRole(Account.Role.ADMIN);

		validate(dto, validation);

		if (validation.getErrorCount() > 0) {
			Map<String, List<FieldError>> fieldErrorMap = validation.getFieldErrors()
					.stream()
					.collect(Collectors.groupingBy(FieldError::getField, Collectors.toList()));
			return ResponseEntity.badRequest().body(fieldErrorMap);
		}

		Task entity = null;
		if (dto.getId() != 0) {
			entity = repo.findOne(dto.getId());
		}
		if (entity == null) {
			entity = new Task();
		}

		entity.setAgency(dto.getAgency());
		entity.setClientAlias(dto.getClientAlias());
		entity.setPeriodStart(parseDateTime(dto.getPeriodStart()));
		entity.setPeriodFinish(parseDateTime(dto.getPeriodFinish()));
		entity.setNote(dto.getNote());

		repo.save(entity);

		return ResponseEntity.ok(Collections.emptyMap());
	}
}
