package name.valery1707.megatel.sorm.api.conn;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Conn;
import name.valery1707.megatel.sorm.domain.Conn_;
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
@RequestMapping("/api/conn")
public class ConnController {
	@Inject
	private ConnRepo repo;

	private SpecificationBuilder<Conn, ConnFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Conn, ConnFilter>(SpecificationMode.AND)
//				.withDateTime(Conn_.ts, ConnFilter::getTs)
				.withIp(Conn_.idOrigIp, ConnFilter::getIdOrigHost)
				.withIp(Conn_.idRespIp, ConnFilter::getIdRespHost)
				.withNumber(Conn_.idOrigPort, ConnFilter::getIdOrigPort)
				.withNumber(Conn_.idRespPort, ConnFilter::getIdRespPort)
				.withString(Conn_.proto, ConnFilter::getProto)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<ConnDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) ConnFilter filter
	) {
		Specification<Conn> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(ConnDto::new);
	}
}
