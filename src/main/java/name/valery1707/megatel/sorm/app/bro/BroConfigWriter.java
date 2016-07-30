package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import javaslang.collection.Stream;
import javaslang.control.Try;
import name.valery1707.megatel.sorm.api.task.TaskRepo;
import name.valery1707.megatel.sorm.domain.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
	private Template templateSig;

	@PostConstruct
	public void init() {
		Mustache.Compiler compiler = Mustache.compiler();
		templateInit = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_init.bro"), StandardCharsets.UTF_8));
		templateTask = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_task_00.bro"), StandardCharsets.UTF_8));
		templateSig = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_binary.sig.cfg"), StandardCharsets.UTF_8));
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
			files.put("amt_binary.sig", drawSigTemplate());
		} else {
			//todo Template для полного отключения логирования
		}
		files.put("amt.bro", drawRunnerTemplate(files.keySet()));
		files.keySet().forEach(name -> files.put(name, files.get(name).replace("\r\n", "\n")));
		return files;
	}

	private String drawRunnerTemplate(Set<String> fileNames) {
		return fileNames.stream()
				.filter(name -> !name.endsWith(".sig"))
				.map(name -> "@load ./" + name)
				.collect(joining("\n"));
	}

	private String drawInitTemplate() {
		return templateInit.execute(null);
	}

	private String drawTaskTemplate(Task task) {
		return templateTask.execute(task);
	}

	static final Object SIG_CONTEXT = Collections.singletonMap("loadIP", (Mustache.Lambda) BroConfigWriter::loadIp);

	static void loadIp(Template.Fragment frag, Writer out) throws IOException {
		String[] hosts = frag.execute().split(",");
		Stream<String> ip = Stream
				.of(hosts)
				.map(String::trim)
				.map(host -> Try.of(() -> InetAddress.getAllByName(host)).map(Stream::of))
				.filter(Try::isSuccess)
				.flatMap(Try::get)
				.map(InetAddress::getHostAddress)
				.distinct()
				//IPv6 must be enclosed in square brackets
				.map(s -> s.contains(":") ? "[" + s + "]" : s)
				.sorted();
		out.write(ip.mkString(", "));
	}

	private String drawSigTemplate() {
		return templateSig.execute(SIG_CONTEXT);
	}
}
