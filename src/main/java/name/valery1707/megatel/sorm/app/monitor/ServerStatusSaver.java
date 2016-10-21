package name.valery1707.megatel.sorm.app.monitor;

import name.valery1707.core.api.EventRepo;
import name.valery1707.core.domain.Event;
import name.valery1707.megatel.sorm.domain.ServerStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;

import static name.valery1707.megatel.sorm.api.server.status.ServerStatusDto.LOCAL_SERVER_NAME;

@Service
@Singleton
public class ServerStatusSaver {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private ServerStatusRepo repo;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private EventRepo eventRepo;

	@Transactional
	public void save(ServerStatus status) {
		status.setModifiedAt(ZonedDateTime.now());
		repo.save(status);
		eventRepo.save(Event.server(
				Event.EventType.SERVER_STATUS, true,
				null,
				"Server: " + getServerName(status)
		));
	}

	@Transactional
	public void fail(ServerStatus status, Throwable throwable) {
		eventRepo.save(Event.server(
				Event.EventType.SERVER_STATUS, true,
				null,
				"Server: " + getServerName(status) + ". Failure: " + throwable.getMessage()
		));
	}

	private static String getServerName(ServerStatus status) {
		return status.getServer() != null
				? status.getServer().getName()
				: LOCAL_SERVER_NAME;
	}
}
