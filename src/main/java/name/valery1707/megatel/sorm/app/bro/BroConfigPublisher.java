package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import javaslang.concurrent.Future;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.domain.IBaseEntity;
import name.valery1707.megatel.sorm.domain.Task;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
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
	private ExecutorService executor;
	private final Map<String, String> hashByServer = new HashMap<>();
	private Template templateInit;
	private Template templateTask;

	@PostConstruct
	public void init() {
		hashByServer.put("out", "");//todo Описание серверов Bro

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
				files.put("amt_init.bro", drawInitTemplate());
				files.put("amt.bro", drawRunnerTemplate(files.keySet()));

				Map<String, Future<String>> publish = hashByServer.entrySet().stream()
						.filter(entry -> !entry.getValue().equals(hashValue))
						.collect(toMap(Map.Entry::getKey, entry -> Future.of(executor, () -> publish(entry.getKey(), files))));
				publish.values().forEach(Future::await);
				publish.forEach((server, result) -> hashByServer.put(server, hashValue));
			} finally {
				lock.unlock();
			}
			LOG.debug("Complete publish");
		}
	}

	private String publish(String server, Map<String, String> files) {
		//todo Работа с файлами на серверах
		files.forEach((name, content) -> {
			LOG.info("[{}]", name);
			LOG.info(content);
		});
		return server;
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
