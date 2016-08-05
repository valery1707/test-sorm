package name.valery1707.core.db.h2;

import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static name.valery1707.core.db.h2.TimeFunctions.TimeDiff;
import static name.valery1707.core.db.h2.TimeFunctions.TimeToSec;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeFunctionsTest {

	@Test
	public void testTimeDiff_null() throws Exception {
		assertThat(TimeDiff(Timestamp.valueOf(LocalDateTime.now()), null)).isNull();
		assertThat(TimeDiff(null, Timestamp.valueOf(LocalDateTime.now()))).isNull();
	}

	@Test
	@SuppressWarnings({"PointlessArithmeticExpression", "deprecation"})
	public void testTimeDiff_value() throws Exception {
		Timestamp diff = TimeDiff(Timestamp.valueOf("2016-06-29 11:24:58"), Timestamp.valueOf("2016-06-29 10:57:06"));
		int timezoneOffset = diff.getTimezoneOffset();
		int timezoneOffsetMin = timezoneOffset % 60;
		int timezoneOffsetHour = (timezoneOffset - timezoneOffsetMin) / 60;
		assertThat(diff)
				.hasHourOfDay(0 - timezoneOffsetHour)
				.hasMinute(27 - timezoneOffsetMin)
				.hasSecond(52)
				.hasMillisecond(0)
		;
	}

	@Test
	public void testTimeToSec_null() throws Exception {
		assertThat(TimeToSec(null)).isNull();
	}

	@Test
	public void testTimeToSec() throws Exception {
		assertThat(TimeToSec(TimeDiff(Timestamp.valueOf("2016-06-29 11:24:58"), Timestamp.valueOf("2016-06-29 10:57:06")))).isEqualTo(1672);
	}
}
