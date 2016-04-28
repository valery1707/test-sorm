package name.valery1707.megatel.sorm;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {
	@Test
	public void testBigDecimal_ZonedDateTime() throws Exception {
		check("1461750541.915725", "2016-04-27T09:49:01.000915725" + DateUtils.DEFAULT_ZONE_OFFSET_NAME);
	}

	private void check(String raw, String converted) {
		BigDecimal bigDecimal = new BigDecimal(raw);
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(converted, DateTimeFormatter.ISO_DATE_TIME)
				.plusSeconds(DateUtils.DEFAULT_ZONE_OFFSET.getTotalSeconds());
		assertEquals(String.format("raw(%s) -> converted(%s)", raw, converted), zonedDateTime, DateUtils.bigDecimalToZonedDateTime(bigDecimal));
		assertEquals(String.format("converted(%s) -> raw(%s)", converted, raw), bigDecimal, DateUtils.zonedDateTime2BigDecimal(zonedDateTime));
	}
}
