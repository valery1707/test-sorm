package name.valery1707.megatel.sorm;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.*;

public class DateUtilsTest {
	@Test
	public void testBigDecimal_ZonedDateTime() throws Exception {
		check("1461750541.915725", "2016-04-27T15:49:01.000915725");
	}

	private void check(String raw, String converted) {
		BigDecimal bigDecimal = new BigDecimal(raw);
		LocalDateTime localDateTime = LocalDateTime.parse(converted, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
		assertEquals(String.format("raw(%s) -> converted(%s)", raw, converted), DateUtils.bigDecimalToZonedDateTime(bigDecimal), zonedDateTime);
		assertEquals(String.format("converted(%s) -> raw(%s)", converted, raw), DateUtils.zonedDateTime2BigDecimal(zonedDateTime), bigDecimal);
	}
}
