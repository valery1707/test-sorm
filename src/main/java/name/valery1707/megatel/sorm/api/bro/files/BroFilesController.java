package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.api.auth.AccountService;
import name.valery1707.megatel.sorm.api.bro.http.BroHttpRepo;
import name.valery1707.megatel.sorm.configuration.MimeRepository;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroFiles_;
import name.valery1707.megatel.sorm.domain.BroHttp;
import org.apache.commons.io.FilenameUtils;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToNull;

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
		Specification<BroFiles> spec = specificationBuilder.build(filter);
		return repo.findAll(spec, pageable)
				.map(BroFilesDto::new);
	}

	@Value("${files.extract_dir}")
	private File extractDir;

	@Inject
	private BroHttpRepo httpRepo;

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

		if (files.getFilename() == null) {
			if (files.getSource().equalsIgnoreCase("HTTP")) {
				String[] connUids = files.getConnUids().split(",");
				for (int c = 0; c < connUids.length && files.getFilename() == null; c++) {
					String connUid = connUids[c];
					List<BroHttp> https = httpRepo.findByUidAndFuid(connUid, files.getFuid());
					for (int h = 0; h < https.size() && files.getFilename() == null; h++) {
						BroHttp http = https.get(h);
						if (http.getUri() != null) {
							try {
								URI uri = new URI(http.getUri().replace(" ", "%20").replace("|", "%7C"));
								files.setFilename(trimToNull(FilenameUtils.getName(uri.getPath())));
							} catch (URISyntaxException ignored) {
							}
						}
					}
				}
			}
		}
		if (files.getFilename() == null) {
			files.setFilename(files.getConnUids());
		}

		if (files.getMimeType() == null) {
			files.setMimeType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		}
		if (!files.getFilename().contains(".")) {
			files.setFilename(files.getFilename() + "." + mimeToExt(files.getMimeType()));
		}

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
		return ResponseEntity
				.status(HttpStatus.OK)
				.headers(headers)
				.header("X-Filename", files.getFilename())
				.body(isr);
	}

	@Inject
	private MimeRepository mime;

	@Nonnull
	private String mimeToExt(@Nullable String mimeType) {
		return mime.mimeToExt(mimeType, "bin");
	}
}
