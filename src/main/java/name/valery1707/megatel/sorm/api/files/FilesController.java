package name.valery1707.megatel.sorm.api.files;

import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Files;
import name.valery1707.megatel.sorm.domain.Files_;
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
@RequestMapping("/api/files")
public class FilesController {
	@Inject
	private FilesRepo repo;

	private SpecificationBuilder<Files, FilesFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Files, FilesFilter>(SpecificationMode.AND)
//				.withDateTime(Files_.ts, FilesFilter::getTs)
				.withNumber(Files_.seenBytes, FilesFilter::getSize)
				.withString(Files_.mimeType, FilesFilter::getMimeType)
				.withString(Files_.filename, FilesFilter::getFilename)
				.withString(Files_.extracted, FilesFilter::getExtracted)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<FilesDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) FilesFilter filter
	) {
		Specification<Files> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(FilesDto::new);
	}
}
