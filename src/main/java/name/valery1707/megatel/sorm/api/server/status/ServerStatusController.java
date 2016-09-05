package name.valery1707.megatel.sorm.api.server.status;

import name.valery1707.core.api.BaseEntityController;
import name.valery1707.core.db.SpecificationBuilder;
import name.valery1707.core.db.SpecificationMode;
import name.valery1707.megatel.sorm.app.monitor.ServerStatusRepo;
import name.valery1707.megatel.sorm.domain.ServerStatus;
import name.valery1707.megatel.sorm.domain.ServerStatus_;
import name.valery1707.megatel.sorm.domain.Server_;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/server/status")
public class ServerStatusController extends BaseEntityController<ServerStatus, ServerStatusRepo, ServerStatusFilter, ServerStatusDto> {

	public ServerStatusController() {
		super("serverStatus");
	}

	@Override
	protected SpecificationBuilder<ServerStatus, ServerStatusFilter> buildUserFilter() {
		return new SpecificationBuilder<ServerStatus, ServerStatusFilter>(SpecificationMode.AND)
				.withNumber(ServerStatusFilter::getId, ServerStatus_.id)
				.withString(ServerStatusFilter::getServerName, ServerStatus_.server, Server_.name)//todo Пульт управления
				.withDateTime(ServerStatusFilter::getModifiedAt, ServerStatus_.modifiedAt)
				.withEquals(ServerStatusFilter::getHostStatus, ServerStatus_.hostStatus)
				.withEquals(ServerStatusFilter::getDbStatus, ServerStatus_.dbStatus)
				;
	}

	@Override
	@RequestMapping(method = RequestMethod.POST)
	public Page<ServerStatusDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("id") Pageable pageable,
			@RequestBody(required = false) ServerStatusFilter filter
	) {
		return super.findByFilter(pageable, filter);
	}

	@Override
	protected void dto2domain(ServerStatusDto dto, ServerStatus entity) {
		throw new UnsupportedOperationException();
	}
}
