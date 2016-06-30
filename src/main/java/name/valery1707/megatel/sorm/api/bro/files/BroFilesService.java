package name.valery1707.megatel.sorm.api.bro.files;

import name.valery1707.megatel.sorm.api.bro.http.BroHttpRepo;
import name.valery1707.megatel.sorm.configuration.MimeRepository;
import name.valery1707.megatel.sorm.domain.BroFiles;
import name.valery1707.megatel.sorm.domain.BroHttp;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.trimToNull;

@Service
@Singleton
public class BroFilesService {
	private static final Logger LOG = LoggerFactory.getLogger(BroFilesService.class);

	@Inject
	private BroHttpRepo httpRepo;

	public BroFiles fixFilename(BroFiles files) {
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
								LOG.warn("Incorrect URI in HTTP (ts: {}, uid: {}, uri: {}): ", http.getTs(), http.getUid(), http.getUri(), ignored);
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

		return files;
	}

	@Inject
	private MimeRepository mime;

	@Nonnull
	private String mimeToExt(@Nullable String mimeType) {
		return mime.mimeToExt(mimeType, "bin");
	}
}
