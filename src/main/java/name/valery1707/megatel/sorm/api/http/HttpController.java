package name.valery1707.megatel.sorm.api.http;

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

import javax.inject.Inject;

@RestController
@RequestMapping("/api/http")
public class HttpController {
	@Inject
	private HttpRepo repo;

	@RequestMapping(method = RequestMethod.POST)
	public Page<HttpDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) HttpFilter filter
	) {
		Specification<Http> spec = null;
		return repo.findAll(spec, pageable)
				.map(HttpDto::new);
	}
}
