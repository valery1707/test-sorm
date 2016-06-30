package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.app.AccountService;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroFiles_;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/bro/files")
public class BroFilesController {
	@Inject
	private BroFilesRepo repo;

	@Inject
	private AccountService accountService;

	private SpecificationBuilder<BroFiles, BroFilesFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<BroFiles, BroFilesFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(BroFilesFilter::getTs, BroFiles_.ts)
				.withNumber(BroFilesFilter::getSeenBytes, BroFiles_.seenBytes)
				.withNumber(BroFilesFilter::getTotalBytes, BroFiles_.totalBytes)
				.withNumber(BroFilesFilter::getMissingBytes, BroFiles_.missingBytes)
				.withString(BroFilesFilter::getMimeType, BroFiles_.mimeType)
				.withString(BroFilesFilter::getFilename, BroFiles_.filename)
				.withString(BroFilesFilter::getExtracted, BroFiles_.extracted)
		;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Page<BroFilesDto> findByFilter(
			@PageableDefault(size = 20) @SortDefault("ts") Pageable pageable,
			@RequestBody(required = false) BroFilesFilter filter
	) {
		accountService.requireAnyRight("task.view");
		//todo Фильтрация по выбранной задаче
		Specification<BroFiles> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroFilesDto::new);
	}

	@Value("${files.extract_dir}")
	private File extractDir;

	@Inject
	private BroFilesService filesService;

	@RequestMapping(path = "download", method = RequestMethod.POST)
	public HttpEntity<InputStreamResource> download(@RequestBody String extracted) {
		BroFiles files = repo.getByExtracted(extracted);
		if (files == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		File file = new File(extractDir, files.getExtracted());
		if (!file.exists() || !file.canRead()) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}

		files = filesService.fixFilename(files);

		InputStreamResource isr;
		try {
			isr = new InputStreamResource(new FileInputStream(file));
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(files.getMimeType()));
		headers.setContentLength(file.length());
		headers.setContentDispositionFormData("attachment", files.getFilename());
		headers.add("X-Filename", files.getFilename());
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.body(isr);
	}
}
