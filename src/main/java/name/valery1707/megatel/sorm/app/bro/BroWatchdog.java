package name.valery1707.megatel.sorm.app.bro;

import javaslang.collection.List;
import javaslang.concurrent.Future;
import name.valery1707.megatel.sorm.app.ssh.SshClientHelper;
import name.valery1707.megatel.sorm.domain.Server;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Service
@Singleton
public class BroWatchdog {
	private static final Logger LOG = LoggerFactory.getLogger(BroWatchdog.class);

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private ServerRepo serverRepo;

	@Inject
	private SshConfigurer sshConfigurer;

	@Inject
	private BroLocker locker;

	private ExecutorService executor = Executors.newFixedThreadPool(5,
			new BasicThreadFactory.Builder()
					.daemon(true)
					.namingPattern("bro-watchdog-%d")
					.build()
	);

	@Scheduled(fixedDelay = 60 * 1000/*каждую минуту*/, initialDelay = 30 * 1000 /*30 секунд*/)
	public void checkStatuses() {
		List<Future<Status>> futures = List.ofAll(serverRepo.findAll())
				.map(server -> Future.of(executor, () -> checkStatus(server)));
		futures.forEach(Future::await);
	}

	private static final Pattern STATUS_OK = Pattern.compile("(standalone)\\s+(localhost)\\s+(running)\\s+(\\d+)");
	private static final Predicate<String> STATUS_OK_MATCHER = STATUS_OK.asPredicate();

	private Status checkStatus(Server server) {
		MDC.put("server", String.format("[%s@%s:%d] ", server.getUsername(), server.getHost(), server.getPort()));
		if (!locker.lock(server).tryLock()) {
			LOG.debug("Server used by other thread - maybe updating tasks");
			return Status.DELAY;
		}
		SshClientHelper helper = new SshClientHelper(server, sshConfigurer.getConfig());
		try {
			LOG.debug("Check status");
			helper.connect();
			List<String> status = helper.execute(String.format("sudo %s/bin/broctl status", server.getBroPath()));
			boolean isRunning = status.exists(STATUS_OK_MATCHER);
			if (isRunning) {
				LOG.debug("Bro already running");
				return Status.ALREADY_OK;
			} else {
				LOG.info("Bro server not running: {}", status.mkString("\n"));
				List<String> deploy = helper.execute(String.format("sudo %s/bin/broctl deploy", server.getBroPath()));
				LOG.info("Bro deploy: {}", deploy.mkString("\n"));
				boolean success = deploy.exists(s -> s.contains("starting bro ..."));
				if (!success) {
					LOG.warn("Bro deploy failed");
					return Status.FAIL;
				} else {
					LOG.info("Bro deploy success");
					return Status.RESTART;
				}
			}
		} catch (IOException e) {
			LOG.warn("Catch exception: ", e);
			return Status.FAIL;
		} finally {
			helper.disconnect();
			locker.lock(server).unlock();
			MDC.clear();
		}
	}

	private enum Status {
		ALREADY_OK, RESTART, DELAY, FAIL
	}
}
