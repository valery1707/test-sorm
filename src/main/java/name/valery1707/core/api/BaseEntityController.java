package name.valery1707.core.api;

import javaslang.Value;
import javaslang.collection.Seq;
import name.valery1707.core.app.AccountService;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.LogicRemovableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static name.valery1707.core.utils.ReflectionUtils.findConverter;
import static name.valery1707.core.utils.ReflectionUtils.getGenericType;

/**
 * @param <D>   Domain class
 * @param <R>   Repo class
 * @param <F>   Filter class
 * @param <DTO> Dto class
 */
public abstract class BaseEntityController<D, R extends JpaRepository<D, Long> & JpaSpecificationExecutor<D>, F, DTO extends BaseDto> {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private R repo;

	@Inject
	private AccountService accountService;

	private final Class<D> domainClass;
	private final Class<DTO> dtoClass;
	private final Supplier<D> domainBuilder;
	private final Seq<Account.Role> canCreate;
	private final Seq<Account.Role> canFind;
	private final Seq<Account.Role> canRead;
	private final Seq<Account.Role> canUpdate;
	private final Seq<Account.Role> canDelete;

	private SpecificationBuilder<D, F> userFilter;
	private Function<D, DTO> domain2dto;

	public BaseEntityController(String rightPrefix) {
		domainClass = getGenericType(this.getClass(), BaseEntityController.class, "D");
		dtoClass = getGenericType(this.getClass(), BaseEntityController.class, "DTO");
		domain2dto = findConverter(domainClass, dtoClass);
		domainBuilder = () -> {
			try {
				return domainClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(String.format("Could not create '%s'", domainClass.getName()));
			}
		};
		canCreate = Account.Role.hasRights(rightPrefix + ".create");
		canFind = Account.Role.hasRights(rightPrefix + ".list");
		canRead = Account.Role.hasRights(rightPrefix + ".update");
		canUpdate = Account.Role.hasRights(rightPrefix + ".update");
		canDelete = Account.Role.hasRights(rightPrefix + ".delete");
	}

	@PostConstruct
	public void init() {
		userFilter = buildUserFilter();
	}

	protected R repo() {
		return repo;
	}

	protected AccountService accountService() {
		return accountService;
	}

	protected abstract SpecificationBuilder<D, F> buildUserFilter();

	protected void canCreate() {
		accountService.requireAnyRole(canCreate);
	}

	protected void canFind() {
		accountService.requireAnyRole(canFind);
	}

	protected void canRead() {
		accountService.requireAnyRole(canRead);
	}

	protected void canUpdate() {
		accountService.requireAnyRole(canUpdate);
	}

	protected void canDelete() {
		accountService.requireAnyRole(canDelete);
	}

	protected DTO domain2dto(D src) {
		return domain2dto.apply(src);
	}

	/**
	 * Fill default fields
	 *
	 * @param src
	 * @return
	 */
	protected D domainInit(D src) {
		if (src instanceof LogicRemovableEntity) {
			((LogicRemovableEntity) src).setActive(true);
		}
		return src;
	}

	protected abstract void dto2domain(DTO dto, D entity);

	protected void applyPermanentFilter(F filter) {
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<DTO> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) F filter
	) {
		canFind();
		applyPermanentFilter(filter);
		Specification<D> spec = userFilter.build(filter);
		return repo.findAll(spec, pageable)
				.map(this::domain2dto);
	}

	@RequestMapping
	public DTO get(@RequestParam long id) {
		canRead();
		D entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", domainClass.getName(), id));
		}
		return domain2dto(entity);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	@Transactional
	public void delete(@RequestParam long id) {
		canDelete();
		D entity = repo.findOne(id);
		if (entity == null) {
			throw new AccessDeniedException(String.format("Entity '%s' with id %d not found", domainClass.getName(), id));
		}
		if (entity instanceof LogicRemovableEntity) {
			((LogicRemovableEntity) entity).setActive(false);
			repo.save(entity);
		} else {
			repo.delete(entity);
		}
	}

	protected void validate(DTO dto, BindingResult validation) {
	}

	@RequestMapping(method = RequestMethod.PUT)
	@Transactional
	public ResponseEntity<Map<String, ?>> save(@RequestBody @Valid DTO dto, BindingResult validation) {
		validate(dto, validation);

		if (validation.getErrorCount() > 0) {
			javaslang.collection.Map<String, List<FieldError>> fieldErrorMap = javaslang.collection.List
					.ofAll(validation.getFieldErrors())
					.groupBy(FieldError::getField)
					.mapValues(Value::toJavaList);
			return ResponseEntity.badRequest().body(fieldErrorMap.toJavaMap());
		}

		D entity = null;
		if (dto.getId() != 0) {
			canUpdate();
			entity = repo.findOne(dto.getId());
		}
		if (entity == null) {
			canCreate();
			entity = domainInit(domainBuilder.get());
		}

		dto2domain(dto, entity);
		repo.save(entity);

		return ResponseEntity.ok(Collections.emptyMap());
	}
}
