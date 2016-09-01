package name.valery1707.megatel.sorm.app.monitor;

import name.valery1707.megatel.sorm.domain.ServerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;

@Service
@Singleton
public class ServerStatusSaver {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private ServerStatusRepo repo;

	@Transactional
	public void save(ServerStatus status) {
		status.setModifiedAt(ZonedDateTime.now());
		repo.save(status);
	}
}
