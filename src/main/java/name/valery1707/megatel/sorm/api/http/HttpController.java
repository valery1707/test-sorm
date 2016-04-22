package name.valery1707.megatel.sorm.api.http;

import name.valery1707.megatel.sorm.api.conn.ConnFilter;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Conn;
import name.valery1707.megatel.sorm.domain.Http;
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
@RequestMapping("/api/http")
public class HttpController {
	@Inject
	private HttpRepo repo;

	private SpecificationBuilder<Http, HttpFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Http, HttpFilter>(SpecificationMode.AND)
//				.withDateTime("ts", HttpFilter::getTs)
//				.withIp("idOrigHost", HttpFilter::getIdOrigHost)
//				.withIp("idRespHost", HttpFilter::getIdRespHost)
				.withNumber("idOrigPort", HttpFilter::getIdOrigPort)
				.withNumber("idRespPort", HttpFilter::getIdRespPort)
				.withString("method", HttpFilter::getMethod)
				.withString("host", HttpFilter::getHost)
				.withString("uri", HttpFilter::getUri)
				.withString("referrer", HttpFilter::getReferrer)
				.withString("userAgent", HttpFilter::getUserAgent)
				.withNumber("requestBodyLen", HttpFilter::getRequestBodyLen)
				.withNumber("responseBodyLen", HttpFilter::getResponseBodyLen)
				.withNumber("statusCode", HttpFilter::getStatusCode)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<HttpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) HttpFilter filter
	) {
		Specification<Http> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(HttpDto::new);
	}
}
