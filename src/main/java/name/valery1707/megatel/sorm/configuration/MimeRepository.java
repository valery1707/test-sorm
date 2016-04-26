package name.valery1707.megatel.sorm.configuration;


import com.github.amr.mimetypes.MimeTypes;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Component
@Singleton
@SuppressWarnings("unused")
public class MimeRepository {
	@PostConstruct
	public void init() {
		MimeTypes.getInstance().loadOne("text/json json");
	}
}
