package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.domain.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Service
@Singleton
public class BroConfigWriter {
	@Inject
	private TaskRepo taskRepo;

	private Template templateInit;
	private Template templateTask;

	@PostConstruct
	public void init() {
		Mustache.Compiler compiler = Mustache.compiler();
		templateInit = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_init.bro"), StandardCharsets.UTF_8));
		templateTask = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_task_00.bro"), StandardCharsets.UTF_8));
	}

	/**
	 * Генерация содержимого файлов с шаблонами для указанных заданий
	 *
	 * @param tasIds Список ID заданий
	 * @return Содержимое файлов, в формате "Имя файла" -> "Содержимое файла"
	 */
	@Transactional(readOnly = true)
	public Map<String, String> writeConf(Iterable<Long> tasIds) {
		Map<String, String> files = taskRepo.findAll(tasIds).stream()
				.collect(toMap(
						task -> "amt_task_" + task.getId() + ".bro",
						this::drawTaskTemplate,
						(v1, v2) -> v1,
						TreeMap::new
				));
		if (!files.isEmpty()) {
			files.put("amt_init.bro", drawInitTemplate());
		}
		files.put("amt.bro", drawRunnerTemplate(files.keySet()));
		return files;
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
