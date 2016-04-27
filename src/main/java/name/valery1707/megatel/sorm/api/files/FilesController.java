package name.valery1707.megatel.sorm.api.files;

import name.valery1707.megatel.sorm.api.http.HttpRepo;
import name.valery1707.megatel.sorm.configuration.MimeRepository;
import name.valery1707.megatel.sorm.db.SpecificationBuilder;
import name.valery1707.megatel.sorm.db.SpecificationMode;
import name.valery1707.megatel.sorm.domain.Files;
import name.valery1707.megatel.sorm.domain.Files_;
import name.valery1707.megatel.sorm.domain.Http;
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
@RequestMapping("/api/files")
public class FilesController {
	@Inject
	private FilesRepo repo;

	private SpecificationBuilder<Files, FilesFilter> specificationBuilder;

	@PostConstruct
	public void init() {
		specificationBuilder = new SpecificationBuilder<Files, FilesFilter>(SpecificationMode.AND)
				.withDateTimeDecimal(Files_.ts, FilesFilter::getTs)
				.withNumber(Files_.seenBytes, FilesFilter::getSeenBytes)
				.withNumber(Files_.totalBytes, FilesFilter::getTotalBytes)
				.withNumber(Files_.missingBytes, FilesFilter::getMissingBytes)
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

	@Value("${files.extract_dir}")
	private File extractDir;

	@Inject
	private HttpRepo httpRepo;

	@RequestMapping(path = "download", method = RequestMethod.POST)
	public HttpEntity<InputStreamResource> download(@RequestBody String extracted) {
		Files files = repo.getByExtracted(extracted);
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
					List<Http> https = httpRepo.findByUidAndFuid(connUid, files.getFuid());
					for (int h = 0; h < https.size() && files.getFilename() == null; h++) {
						Http http = https.get(h);
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
