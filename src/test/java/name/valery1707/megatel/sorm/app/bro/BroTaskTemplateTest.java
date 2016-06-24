package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import name.valery1707.megatel.sorm.domain.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static name.valery1707.megatel.sorm.domain.Task.TaskBuilder.aTask;
import static name.valery1707.megatel.sorm.domain.TaskFilter.TaskFilterBuilder.aTaskFilter;
import static name.valery1707.megatel.sorm.domain.TaskFilterValue.TaskFilterValueBuilder.aTaskFilterValue;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BroTaskTemplateTest {
	private Template templateTask;

	@Before
	public void setUp() throws Exception {
		Mustache.Compiler compiler = Mustache.compiler()
				.nullValue("")
				.defaultValue("");
		templateTask = compiler.compile(new InputStreamReader(this.getClass().getResourceAsStream("/bro/amt_task_00.bro"), StandardCharsets.UTF_8));
	}

	@Test
	public void testId() throws Exception {
		Task task = aTask()
				.withId(1L)
				.build();
		assertThat(templateTask.execute(task))
				.isNotEmpty()
				.contains("module AMT_TASK_1;")
				.doesNotContain("watch_email");
	}

	@Test
	public void testFilterEmail() throws Exception {
		Task task = aTask()
				.withId(1L)
				.withFilter(Collections.singletonMap("email", aTaskFilter()
						.withName("email")
						.withValue(new HashSet<>(Arrays.asList(
								aTaskFilterValue().withValue("test@mail.ru").build(),
								aTaskFilterValue().withValue("test@gmail.com").build()
						)))
						.build()))
				.build();
		String execute = templateTask.execute(task);
		assertThat(execute)
				.isNotEmpty()
				.contains("module AMT_TASK_1;")
				.contains("AMT::watch_email(\"test@mail.ru\", taskId);")
				.contains("AMT::watch_email(\"test@gmail.com\", taskId);")
		;
	}

	@Test
	public void testFilterIp() throws Exception {
		Task task = aTask()
				.withId(1L)
				.withFilter(Collections.singletonMap("ip", aTaskFilter()
						.withName("ip")
						.withValue(new HashSet<>(Arrays.asList(
								aTaskFilterValue().withValue("127.0.0.1").build(),
								aTaskFilterValue().withValue("192.168.1.1").build()
						)))
						.build()))
				.build();
		String execute = templateTask.execute(task);
		assertThat(execute)
				.isNotEmpty()
				.contains("module AMT_TASK_1;")
				.contains("AMT::watch_ip_addr(127.0.0.1, taskId);")
				.contains("AMT::watch_ip_addr(192.168.1.1, taskId);")
		;
	}
}
