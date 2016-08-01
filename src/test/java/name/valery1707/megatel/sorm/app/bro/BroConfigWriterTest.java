package name.valery1707.megatel.sorm.app.bro;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import javaslang.collection.List;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BroConfigWriterTest {

	@Test
	public void testLoadIp_127() throws Exception {
		assertThat(loadIp("127.0.0.1")).containsExactly("127.0.0.1");
	}

	@Test
	public void testLoadIp_localhost() throws Exception {
		assertThat(loadIp("localhost")).contains("127.0.0.1");
	}

	@Test
	public void testLoadIp_ya_ru() throws Exception {
		List<String> ipYaRu = loadIp("ya.ru");
		assertThat(ipYaRu).contains("213.180.204.3");
		assertThat(loadIp("ya.ru, ya.ru")).containsOnlyElementsOf(ipYaRu).hasSameSizeAs(ipYaRu);
	}

	@Test
	public void testLoadIp_google_com() throws Exception {
		List<String> ipGoogleCom = loadIp("google.com");
		assertThat(ipGoogleCom).isNotEmpty();
	}

	@Test
	public void testLoadIp_google_com_and_ya_ru() throws Exception {
		List<String> ipYaRu = loadIp("ya.ru");
		List<String> ipGoogleCom = loadIp("google.com");
		List<String> ip = ipYaRu.appendAll(ipGoogleCom);
		assertThat(loadIp("ya.ru, google.com")).containsOnlyElementsOf(ip);
	}

	private List<String> loadIp(String src) throws IOException {
		StringBuilderWriter out = new StringBuilderWriter();
		Template.Fragment frag = mock(Template.Fragment.class);
		when(frag.execute()).thenReturn(src);
		BroConfigWriter.loadIp(frag, out);
		return List.of(out.toString().split(", "));
	}

	@Test
	public void testMustacheLambda() throws Exception {
		Mustache.Compiler compiler = Mustache.compiler();
		Template template = compiler.compile("dst-ip == {{#loadIP}}127.0.0.1{{/loadIP}}");
		assertThat(template.execute(BroConfigWriter.SIG_CONTEXT)).isEqualToIgnoringCase("dst-ip == 127.0.0.1");
	}
}
