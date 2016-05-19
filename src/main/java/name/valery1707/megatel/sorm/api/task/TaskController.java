package name.valery1707.megatel.sorm.api.task;

import name.valery1707.megatel.sorm.api.auth.AccountService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
				.withNumber(Task_.id, TaskFilter::getId)
				.withDateTime(Task_.periodStart, TaskFilter::getPeriodStart)
				.withDateTime(Task_.periodFinish, TaskFilter::getPeriodFinish)
				.withString(Task_.agency, TaskFilter::getAgency)
				.withString(Task_.clientAlias, TaskFilter::getClientAlias)
				.withString(Task_.note, TaskFilter::getNote)
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
}
