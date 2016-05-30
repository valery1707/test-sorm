package name.valery1707.megatel.sorm;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DateUtilsTest {
	@Test
	public void testBigDecimal_ZonedDateTime() throws Exception {
		checkBigDecimal_ZonedDateTime("1461750541.915725", "2016-04-27T09:49:01.000915725" + DateUtils.DEFAULT_ZONE_OFFSET_NAME);
	}

	private void checkBigDecimal_ZonedDateTime(String raw, String converted) {
		BigDecimal bigDecimal = new BigDecimal(raw);
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(converted, DateTimeFormatter.ISO_DATE_TIME)
				.plusSeconds(DateUtils.DEFAULT_ZONE_OFFSET.getTotalSeconds());
		assertEquals(String.format("raw(%s) -> converted(%s)", raw, converted), zonedDateTime, DateUtils.bigDecimalToZonedDateTime(bigDecimal));
		assertEquals(String.format("converted(%s) -> raw(%s)", converted, raw), bigDecimal, DateUtils.zonedDateTime2BigDecimal(zonedDateTime));
	}

	@Test
	public void testDateTime() throws Exception {
		assertNull(DateUtils.formatDate(null));
		assertNull(DateUtils.formatDateTime(null));
		assertNull(DateUtils.formatDateTimeZoned(null));

		assertNull(DateUtils.parseDate(null));
		assertNull(DateUtils.parseDate("30/05/2016"));

		ZonedDateTime nowTime = ZonedDateTime.now().withZoneSameLocal(DateUtils.DEFAULT_ZONE_ID_NORM);
		LocalDate nowDate = nowTime.toLocalDate();
		assertEquals("now-date", nowDate, DateUtils.parseDate(DateUtils.formatDate(nowDate)));
		assertEquals("now-time", nowTime, DateUtils.parseDateTime(DateUtils.formatDateTime(nowTime)));
		assertEquals("now-zone", nowTime, DateUtils.parseDateTimeZoned(DateUtils.formatDateTimeZoned(nowTime)));
	}
}
