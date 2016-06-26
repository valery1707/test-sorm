package name.valery1707.megatel.sorm.app.bro;

import javaslang.collection.List;
import javaslang.concurrent.Future;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.app.ssh.SshClientHelper;
import name.valery1707.megatel.sorm.domain.IBaseEntity;
import name.valery1707.megatel.sorm.domain.Server;
import name.valery1707.megatel.sorm.domain.Task;
import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Service
@Singleton
public class BroConfigPublisher {
	private static final Logger LOG = LoggerFactory.getLogger(BroConfigPublisher.class);

	@Inject
	private ServerRepo serverRepo;

	@Inject
	private TaskRepo taskRepo;

	@Inject
	private BroConfigWriter configWriter;

	private final ReentrantLock lock = new ReentrantLock();

	private Config sshConfig;
	private ExecutorService executor;
	private final Map<Long, String> hashByServer = new HashMap<>();

	@PostConstruct
	public void init() {
		sshConfig = new DefaultConfig();

		//todo Информация о количестве серверов Bro собирается только при запуске - это не совсем корректно
		serverRepo.findAll().forEach(server -> hashByServer.put(server.getId(), ""));

		ThreadFactory threadFactory = new BasicThreadFactory.Builder()
				.daemon(true)
				.namingPattern("bro-publisher-%d")
				.build();
		executor = Executors.newFixedThreadPool(Math.max(1, hashByServer.size()), threadFactory);
	}

	@Async
	public void publishIfNeeded() {
		if (isNeedPublish()) {
			publish();
		}
	}

	public boolean isNeedPublish() {
		if (executor == null) {
			return false;
		}
		String hashValue = calcHashValue(calcHash());
		return !hashByServer.values().stream().allMatch(hashValue::equals);
	}

	@Async
	public void publish() {
		if (lock.tryLock()) {
			LOG.debug("Start publish");
			try {
				Map<Long, ZonedDateTime> hash = calcHash();
				String hashValue = calcHashValue(hash);

				Map<Long, Server> servers = serverRepo.findAll().stream().collect(toMap(IBaseEntity::getId, Function.identity()));

				Map<String, String> files = configWriter.writeConf(hash.keySet());

				Map<Long, Future<Boolean>> publish = hashByServer.entrySet().stream()
						.filter(entry -> !entry.getValue().equals(hashValue))
						.collect(toMap(Map.Entry::getKey, entry -> Future.of(executor, () -> publish(servers.get(entry.getKey()), files, hashValue))));
				publish.values().forEach(Future::await);
				publish.entrySet()
						.stream()
						.filter(entry -> entry.getValue().isSuccess())
						.filter(entry -> entry.getValue().get())
						.forEach(entry -> hashByServer.put(entry.getKey(), hashValue));
			} finally {
				lock.unlock();
			}
			LOG.debug("Complete publish");
		}
	}

	private boolean publish(Server server, Map<String, String> files, String hashValue) {
		MDC.put("server", String.format("[%s@%s:%d] ", server.getUsername(), server.getHost(), server.getPort()));
		SshClientHelper helper = new SshClientHelper(server, sshConfig);
		try {
			helper.connect();
			File bro = new File(server.getBroPath());
			helper.checkConfig(bro);
			File conf = new File(server.getConfPath());
			File hashPath = new File(conf, "task.hash");
			helper.mkdir(conf);
			LOG.info("Task hash: checking");
			if (!helper.hasSameContent(hashPath, hashValue)) {
				TreeMap<File, String> fileWithPath = files
						.entrySet().stream()
						.collect(toMap(e -> new File(conf, e.getKey()), Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));
				fileWithPath.put(hashPath, hashValue);
				LOG.info("Clean tmp files");
				helper.cleanTmp(conf);
				LOG.info("Upload new configuration");
				helper.upload(fileWithPath);
				LOG.info("Remove old configuration");
				helper.clean(conf, file -> file.isRegularFile() && !fileWithPath.containsKey(new File(file.getPath())));
				LOG.info("Include AMT configuration into Bro");
				helper.includeIntoBro(new File(bro, "share/bro/site/local.bro"), new File(conf, "amt.bro"));//todo Configure `broConfPath`?
				LOG.info("Bro deploy...");
				List<String> broCtlDeploy = helper.execute(String.format("sudo %s/bin/broctl deploy", server.getBroPath()));
				LOG.info("Bro deploy: {}", broCtlDeploy.mkString(" "));
			} else {
				LOG.info("Task hash: already actual");
			}
			return true;
		} catch (IOException e) {
			LOG.warn("Catch exception: ", e);
			return false;
		} finally {
			helper.disconnect();
			MDC.clear();
		}
	}

	private Map<Long, ZonedDateTime> calcHash() {
		return taskRepo.findActive().stream()
				.collect(toMap(IBaseEntity::getId, Task::getModifiedAt, (v1, v2) -> v1, TreeMap::new));
	}

	private static String calcHashValue(Map<Long, ZonedDateTime> hash) {
		return hash.entrySet().stream()
				.map(entry -> "{id: " + entry.getKey() + ", modifiedAt: '" + entry.getValue() + "'}")
				.collect(joining(", ", "[", "]"));
	}
}
