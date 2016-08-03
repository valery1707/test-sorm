package name.valery1707.megatel.sorm.app.bro;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

@Service
@Singleton
public class BroConfigPublishScheduler {
	@Inject
	private BroConfigPublisher publisher;

	@PostConstruct
	public void init() {
		publisher.publish();
	}

	@Scheduled(fixedDelay = 10 * 1000)
	public void publishIfNeeded() {
		publisher.publishIfNeeded();
	}
}
