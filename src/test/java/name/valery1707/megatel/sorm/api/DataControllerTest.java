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
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
	private static final String ZONE_OFFSET_NOW = ZoneId.systemDefault().getRules().getOffset(ZonedDateTime.now().toInstant()).toString();

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
	public void testPage() throws Exception {
		mvc.perform(get("/data?page={page}&size={size}", 1, 30))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(CONTENT_TYPE))
				.andExpect(content().encoding(ENCODING))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.first").isBoolean())
				.andExpect(jsonPath("$.first").value(false))
				.andExpect(jsonPath("$.last").isBoolean())
				.andExpect(jsonPath("$.last").value(false))
				.andExpect(jsonPath("$.totalElements").isNumber())
				.andExpect(jsonPath("$.totalElements").value(3706))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(124))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(30))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(30))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(30)))
		;
	}

	@Test
	public void testSort() throws Exception {
		mvc.perform(get("/data?sort={sort1}&sort={sort2}", "protocol,ASC", "srcIp,DESC"))
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
				.andExpect(jsonPath("$.content[0].dateTime").value("2014-10-31T21:59:12.776838"))
		;
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

	@Test
	public void testFilter_dateTime_empty() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'dateTime':[null, null]}".replace('\'', '"'))
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
	public void testFilter_dateTime_short() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'dateTime':['2016-03-15T13:21:00.000+06:00']}".replace('\'', '"'))
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
	public void testFilter_dateTime_greater() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'dateTime':['2014-10-31T21:59:24.39098+06:00', null]}".replace('\'', '"').replace("+06:00", ZONE_OFFSET_NOW))
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
				.andExpect(jsonPath("$.totalElements").value(6))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(1))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(6))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(6)))
				.andExpect(jsonPath("$.content[1].dateTime").value("2014-10-31T21:59:24.498338"))
		;
	}

	@Test
	public void testFilter_dateTime_less() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'dateTime':[null, '2014-10-31T21:59:10.657163+06:00']}".replace('\'', '"').replace("+06:00", ZONE_OFFSET_NOW))
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
				.andExpect(jsonPath("$.totalElements").value(27))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(2))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(20))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
				.andExpect(jsonPath("$.content[1].dateTime").value("2014-10-31T21:59:10.602255"))
		;
	}

	@Test
	public void testFilter_dateTime_between() throws Exception {
		mvc.perform(post("/data")
				.contentType(CONTENT_TYPE)
				.content("{'dateTime':['2014-10-31T21:59:10.718562+06:00', '2014-10-31T21:59:10.842767+06:00']}".replace('\'', '"').replace("+06:00", ZONE_OFFSET_NOW))
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
				.andExpect(jsonPath("$.totalElements").value(25-1+25+25-3))
				.andExpect(jsonPath("$.totalPages").isNumber())
				.andExpect(jsonPath("$.totalPages").value(4))
				.andExpect(jsonPath("$.size").isNumber())
				.andExpect(jsonPath("$.size").value(20))
				.andExpect(jsonPath("$.numberOfElements").isNumber())
				.andExpect(jsonPath("$.numberOfElements").value(20))
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
				.andExpect(jsonPath("$.content[4].dateTime").value("2014-10-31T21:59:10.718638"))
		;
	}
}
