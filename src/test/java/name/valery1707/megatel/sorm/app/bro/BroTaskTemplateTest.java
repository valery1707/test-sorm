package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import name.valery1707.megatel.sorm.domain.Task;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static name.valery1707.megatel.sorm.domain.Agency.AgencyBuilder.anAgency;
import static name.valery1707.megatel.sorm.domain.Task.TaskBuilder.aTask;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class BroTaskTemplateTest {
	private Template templateTask;

	@Before
	public void setUp() throws Exception {
		Mustache.Compiler compiler = BroConfigWriter.buildCompiler();
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
	public void testAgency() throws Exception {
		Task task = aTask()
				.withId(1L)
				.withAgency(anAgency()
						.withId(1L)
						.withName("Test agency 'ТОО Рога и Копыта'")
						.build())
				.build();
		String execute = templateTask.execute(task);
		assertThat(execute)
				.isNotEmpty()
				.contains("Agency: " + task.getAgency().getName())
		;
	}
}
