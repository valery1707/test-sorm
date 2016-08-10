package name.valery1707.megatel.sorm.api.task.permit;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.api.account.AccountDto;
import name.valery1707.core.api.auth.AccountRepo;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Account_;
import name.valery1707.megatel.sorm.api.agency.AgencyRepo;
import name.valery1707.megatel.sorm.api.task.TaskDto;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.domain.*;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static name.valery1707.core.db.SingularAttributeGetter.field;
import static name.valery1707.core.utils.DateUtils.parseDateTime;

@RestController
@RequestMapping("/api/task/permit")
public class TaskPermitController extends BaseEntityController<TaskPermit, TaskPermitRepo, TaskPermitFilter, TaskPermitDto> {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private TaskRepo taskRepo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AgencyRepo agencyRepo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private AccountRepo accountRepo;

	public TaskPermitController() {
		super("taskPermit");
	}

	@Override
	protected SpecificationBuilder<TaskPermit, TaskPermitFilter> buildUserFilter() {
		return new SpecificationBuilder<TaskPermit, TaskPermitFilter>(SpecificationMode.AND)
				.withNumber(TaskPermitFilter::getId, TaskPermit_.id)
				.withString(TaskPermitFilter::getAgencyName, TaskPermit_.agency, Agency_.name)
				.withNumber(TaskPermitFilter::getTaskId, field(TaskPermit_.task).nest(Task_.id))
				.withNumber(TaskPermitFilter::getAccountId, field(TaskPermit_.account).nest(Account_.id))
				.withDateTime(TaskPermitFilter::getPeriodStart, TaskPermit_.periodStart)
				.withDateTime(TaskPermitFilter::getPeriodFinish, TaskPermit_.periodFinish)
				.withEquals(TaskPermitFilter::getShowOnlyActive, TaskPermit_.isActive)
				.withEquals(TaskPermitFilter::getAgency, TaskPermit_.agency)
				;
	}

	@Override
	protected void applyPermanentFilter(TaskPermitFilter filter) {
		super.applyPermanentFilter(filter);
		filter.setShowOnlyActive(true);
		filter.setAgency(accountService().getAccount().get().getAgency());
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<TaskPermitDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) TaskPermitFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}

	@Override
	protected void validate(TaskPermitDto dto, BindingResult validation) {
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
		return super.save(dto, validation);
	}

	@Override
	protected void dto2domain(TaskPermitDto dto, TaskPermit entity) {
		entity.setAgency(agencyRepo.getOne(dto.getAgencyId()));
		entity.setTask(taskRepo.getOne(dto.getTaskId()));
		entity.setAccount(accountRepo.getOne(dto.getAccountId()));
		entity.setPeriodStart(parseDateTime(dto.getPeriodStart()));
		entity.setPeriodFinish(parseDateTime(dto.getPeriodFinish()));
	}

	@RequestMapping(path = "comboTask")
	public List<TaskDto> comboTask() {
		return taskRepo.findActive()
				.stream().map(TaskDto::new)
				.collect(toList());
	}

	/**
	 * WHERE (isActive = :isActive) AND (activeUntil IS NULL OR activeUntil <= now()) AND (role = :role)
	 */
	private static final SpecificationBuilder<Account, AccountDto> comboAccountFilter = new SpecificationBuilder<Account, AccountDto>(SpecificationMode.AND)
			.withEquals(AccountDto::isActive, Account_.isActive)
			.withCustom(AccountDto::getActiveUntil, Account_.activeUntil, cb -> (field, filter) -> cb.or(cb.isNull(field), cb.lessThanOrEqualTo(field, LocalDate.now())))
			.withEquals(AccountDto::getRole, Account_.role);

	@RequestMapping(path = "comboAccount")
	public List<AccountDto> comboAccount() {
		AccountDto filter = new AccountDto();
		filter.setActive(true);
		filter.setActiveUntil("now");
		filter.setRole(Account.Role.OPERATOR);
		return accountRepo.findAll(comboAccountFilter.build(filter))
				.stream().map(AccountDto::new)
				.collect(toList());
	}
}
