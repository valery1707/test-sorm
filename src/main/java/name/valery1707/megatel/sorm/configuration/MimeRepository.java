package name.valery1707.megatel.sorm.configuration;


import com.github.amr.mimetypes.MimeType;
import com.github.amr.mimetypes.MimeTypes;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Singleton
@SuppressWarnings("unused")
public class MimeRepository {
	private static final Logger LOG = LoggerFactory.getLogger(MimeRepository.class);

	private MimeTypes mimeTypes;

	@PostConstruct
	public void init() {
		mimeTypes = MimeTypes.blank();
		load(mimeTypes.getClass().getClassLoader(), "mime.types");
		mimeTypes.loadOne("text/json json");
	}

	private void load(ClassLoader classLoader, String path) {
		try (InputStream stream = classLoader.getResourceAsStream(path)) {
			if (stream != null) {
				for (String line : IOUtils.readLines(stream, StandardCharsets.US_ASCII)) {
					mimeTypes.loadOne(line);
				}
			} else {
				LOG.warn("Could not load mime types from {} in {}", path, classLoader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String mimeToExt(String mimeType, String def) {
		MimeType type = mimeTypes.getByType(mimeType);
		return type != null ? type.getExtension() : def;
	}
}
