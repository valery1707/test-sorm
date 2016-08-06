package name.valery1707.megatel.sorm.app;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Singleton;

@Service
@Singleton
public class GcScheduler {

	@Scheduled(cron = "0 0 */2 * * *")
	public void run() {
		System.gc();
	}
}
