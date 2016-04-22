package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Data;
import name.valery1707.megatel.sorm.dto.DataDto;
import name.valery1707.megatel.sorm.dto.DataFilter;
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
@RequestMapping("/data")
public class DataController {
	@Inject
	private DataRepo repo;

	private SpecificationBuilder<Data, DataFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Data, DataFilter>(SpecificationMode.AND)
				.withString("protocol", DataFilter::getProtocol)
				.withIp("srcIp", DataFilter::getSrcIp)
				.withIp("dstIp", DataFilter::getDstIp)
				.withNumber("srcPort", DataFilter::getSrcPort)
				.withNumber("dstPort", DataFilter::getDstPort)
				.withDateTime("dateTime", DataFilter::getDateTime)
		;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Page<DataDto> findAll(
			@PageableDefault(size = 20) @SortDefault("dateTime") Pageable pageable
	) {
		return findByFilter(pageable, null);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<DataDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("dateTime") Pageable pageable,
			@RequestBody(required = false) DataFilter filter
	) {
		Specification<Data> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(DataDto::fromEntity);
	}
}
