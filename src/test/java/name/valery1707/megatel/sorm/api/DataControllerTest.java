package name.valery1707.megatel.sorm.api;

import name.valery1707.megatel.sorm.Launcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Launcher.class)
@WebAppConfiguration
public class DataControllerTest {
	public static final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON;
	public static final String ENCODING = "UTF-8";

	@Inject
	private WebApplicationContext context;

	private MockMvc mvc;

	@Before
	public void setUp() {
		this.mvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.build();
	}

	@Test
	public void testFilter() throws Exception {
		mvc.perform(get("/data"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(CONTENT_TYPE))
				.andExpect(content().encoding(ENCODING))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.first").isBoolean())
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").isBoolean())
				.andExpect(jsonPath("$.last").value(false))
				.andExpect(jsonPath("$.totalElements").isNumber())
				.andExpect(jsonPath("$.totalElements").value(3706))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(186))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(20))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
		;
	}

	@Test
	public void testFilter_protocol() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'protocol':'uDp'}".replace('\'', '"'))
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(CONTENT_TYPE))
				.andExpect(content().encoding(ENCODING))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.first").isBoolean())
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").isBoolean())
				.andExpect(jsonPath("$.last").value(true))
				.andExpect(jsonPath("$.totalElements").isNumber())
				.andExpect(jsonPath("$.totalElements").value(3))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(3))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(3)))
		;
	}

	@Test
	public void testFilter_srcIp() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'srcIp':'192.168.2.17'}".replace('\'', '"'))
		)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(CONTENT_TYPE))
				.andExpect(content().encoding(ENCODING))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.first").isBoolean())
				.andExpect(jsonPath("$.first").value(true))
				.andExpect(jsonPath("$.last").isBoolean())
				.andExpect(jsonPath("$.last").value(false))
				.andExpect(jsonPath("$.totalElements").isNumber())
				.andExpect(jsonPath("$.totalElements").value(1541))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(78))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(20))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
		;
	}
}
