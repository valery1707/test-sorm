package name.valery1707.megatel.sorm.api.agency;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.Account;
import name.valery1707.core.domain.Event;
import name.valery1707.megatel.sorm.domain.Agency;
import name.valery1707.megatel.sorm.domain.Agency_;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/agency")
public class AgencyController extends BaseEntityController<Agency, AgencyRepo, AgencyFilter, AgencyDto> {
	public AgencyController() {
		super("agency");
	}

	@Override
	protected SpecificationBuilder<Agency, AgencyFilter> buildUserFilter() {
		return new SpecificationBuilder<Agency, AgencyFilter>(SpecificationMode.AND)
				.withNumber(AgencyFilter::getId, Agency_.id)
				.withString(AgencyFilter::getName, Agency_.name)
				;
	}

	@Override
	protected Event.EventType eventCreate() {
		return null;
	}

	@Override
	protected Event.EventType eventRead() {
		return null;
	}

	@Override
	protected Event.EventType eventUpdate() {
		return null;
	}

	@Override
	protected Event.EventType eventDelete() {
		return null;
	}

	@Override
	protected Event.EventType eventFind() {
		return null;
	}

	@Override
	protected void dto2domain(AgencyDto dto, Agency entity) {

	}

	private static final SpecificationBuilder<Agency, Agency> comboAgencyFilter = new SpecificationBuilder<Agency, Agency>(SpecificationMode.AND)
			.withEquals(Agency::getId, Agency_.id);

	@RequestMapping(path = "comboAgency")
	public List<AgencyDto> comboAgency() {
		accountService().requireAuthorized();
		Account account = accountService().getAccount().get();
		return repo()
				//Отображается только Агенство, которое прописано у текущего пользователя
				.findAll(comboAgencyFilter.build(account.getAgency()))
				.stream().map(AgencyDto::new)
				.collect(toList());
	}
}
