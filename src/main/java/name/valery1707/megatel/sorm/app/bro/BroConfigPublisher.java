package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Service
@Singleton
public class BroConfigPublisher {
	private static final Logger LOG = LoggerFactory.getLogger(BroConfigPublisher.class);

	@Inject
	private TaskRepo taskRepo;

	private final ReentrantLock lock = new ReentrantLock();

	private Config sshConfig;
	private ExecutorService executor;
	private final Map<Server, String> hashByServer = new HashMap<>();
	private Template templateInit;
	private Template templateTask;

	@PostConstruct
	public void init() {
		sshConfig = new DefaultConfig();

		hashByServer.put(new Server("127.0.0.1", 22, "username", "password", "/opt/bro", "/home/user/megatel-bro-scripts"), "");//todo Описание серверов Bro

		ThreadFactory threadFactory = new BasicThreadFactory.Builder()
				.daemon(true)
				.namingPattern("bro-publisher-%d")
				.build();
		executor = Executors.newFixedThreadPool(hashByServer.size(), threadFactory);//todo Configure?

		Mustache.Compiler compiler = Mustache.compiler();
		templateInit = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_init.bro"), StandardCharsets.UTF_8));
		templateTask = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_task_00.bro"), StandardCharsets.UTF_8));
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

				Map<String, String> files = taskRepo.findAll(hash.keySet()).stream()
						.collect(toMap(task -> "amt_task_" + task.getId() + ".bro", this::drawTaskTemplate, (v1, v2) -> v1, TreeMap::new));
				if (!files.isEmpty()) {
					files.put("amt_init.bro", drawInitTemplate());
				}
				files.put("amt.bro", drawRunnerTemplate(files.keySet()));

				Map<Server, Future<Boolean>> publish = hashByServer.entrySet().stream()
						.filter(entry -> !entry.getValue().equals(hashValue))
						.collect(toMap(Map.Entry::getKey, entry -> Future.of(executor, () -> publish(entry.getKey(), files, hashValue))));
				publish.values().forEach(Future::await);
				publish.forEach((server, result) -> {
					if (result.get()) {
						hashByServer.put(server, hashValue);
					}
				});
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
			File conf = new File(server.getConfPath());
			File hashPath = new File(conf, "task.hash");
			helper.mkdir(conf);
			if (!helper.hasSameContent(hashPath, hashValue)) {
				TreeMap<File, String> fileWithPath = files
						.entrySet().stream()
						.collect(toMap(e -> new File(conf, e.getKey()), Map.Entry::getValue, (v1, v2) -> v1, TreeMap::new));
				fileWithPath.put(hashPath, hashValue);
				helper.cleanTmp(conf);
				helper.upload(fileWithPath);
				helper.clean(conf, file -> file.isRegularFile() && !fileWithPath.containsKey(new File(file.getPath())));
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

	private String drawRunnerTemplate(Set<String> fileNames) {
		return fileNames.stream()
				.map(name -> "@load ./" + name)
				.collect(joining("\n"));
	}

	private String drawInitTemplate() {
		return templateInit.execute(null);
	}

	private String drawTaskTemplate(Task task) {
		return templateTask.execute(task);
	}
}
