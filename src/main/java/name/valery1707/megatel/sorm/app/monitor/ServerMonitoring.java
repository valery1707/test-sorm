package name.valery1707.megatel.sorm.app.monitor;

import javaslang.collection.List;
import javaslang.concurrent.Future;
import name.valery1707.megatel.sorm.app.bro.SshConfigurer;
import name.valery1707.megatel.sorm.app.db.DatabaseMergeScheduler;
import name.valery1707.megatel.sorm.app.ssh.SshClientHelper;
import name.valery1707.megatel.sorm.domain.ServerStatus;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.hibernate.type.descriptor.java.ZonedDateTimeJavaDescriptor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Singleton
public class ServerMonitoring {

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private ServerStatusRepo serverStatusRepo;

	@Inject
	private ServerStatusSaver saver;

	@Inject
	private SshConfigurer sshConfigurer;

	@Inject
	private DatabaseMergeScheduler databaseMergeScheduler;

	private ExecutorService executor = Executors.newFixedThreadPool(5,
			new BasicThreadFactory.Builder()
					.daemon(true)
					.namingPattern("server-monitor-%d")
					.build()
	);

	@Scheduled(fixedDelay = 5 * 60 * 1000 /*каждые 5 минут*/, initialDelay = 45 * 1000 /*45 секунд*/)
	@Async
	@Transactional
	public void checkStatuses() {
		List<Future<ServerStatus>> futures = List.ofAll(serverStatusRepo.findAll())
				.map(
						server -> Future.of(executor, () -> checkStatus(server))
								.onSuccess(saver::save)
				);
		futures.forEach(Future::await);
	}

	private ServerStatus checkStatus(ServerStatus status) {
		//Пульт управления/Веб-сервер
		if (status.getServer() == null) {
			status.setDbStatus(true);
			status.setHostStatus(true);
			return status;
		}

		//Съёмник трафика
		SshClientHelper ssh = new SshClientHelper(status.getServer(), sshConfigurer.getConfig());
		try {
			ssh.connect();
			List<String> df = ssh.execute("LANG=C df -h | grep -v -e 'udev ' -e 'tmpfs ' -e 'none '");
			status.setHostStatus(df.size() > 1);
		} catch (Throwable e) {
			status.setHostStatus(false);
		} finally {
			ssh.disconnect();
		}

		//СУБД
		if (status.isHostStatus()) {
			try {
				DataSource source = databaseMergeScheduler.getConnection(status.getServer()).get();
				JdbcTemplate template = new JdbcTemplate(source, true);
				Timestamp serverTimeRaw = template.queryForObject("SELECT NOW()", Timestamp.class);
				ZonedDateTime serverTime = ZonedDateTimeJavaDescriptor.INSTANCE.wrap(serverTimeRaw, null);
				status.setDbStatus(serverTime != null);
			} catch (Throwable e) {
				status.setDbStatus(false);
			}
		}
		return status;
	}
}
