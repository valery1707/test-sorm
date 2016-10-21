package name.valery1707.core.api.event;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.api.EventRepo;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.core.domain.Account_;
import name.valery1707.core.domain.Event;
import name.valery1707.core.domain.Event_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
public class EventController extends BaseEntityController<Event, EventRepo, EventFilter, EventDto> {
	public EventController() {
		super("event");
	}

	@Override
	protected SpecificationBuilder<Event, EventFilter> buildUserFilter() {
		return new SpecificationBuilder<Event, EventFilter>(SpecificationMode.AND)
				.withNumber(EventFilter::getId, Event_.id)
				.withString(EventFilter::getUsername, Event_.createdBy, Account_.username)
				.withDateTime(EventFilter::getCreatedAt, Event_.createdAt)
				.withEquals(EventFilter::getType, Event_.type)
				.withEquals(EventFilter::getSuccess, Event_.success)
				.withString(EventFilter::getIp, Event_.ip)
				.withString(EventFilter::getExt, Event_.ext)
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
	protected void dto2domain(EventDto dto, Event entity) {

	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public Page<EventDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) EventFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}
}
