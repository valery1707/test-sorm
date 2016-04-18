package name.valery1707.megatel.sorm;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateUtils {
	private static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(ZonedDateTime.now().toInstant());
	private static final String DEFAULT_ZONE_OFFSET_NAME = DEFAULT_ZONE_OFFSET.toString();

	public static ZonedDateTime bigDecimalToZonedDateTime(BigDecimal src) {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
				src.longValue(),
				src.remainder(BigDecimal.ONE).movePointRight(src.scale()).abs().intValue(),
				DEFAULT_ZONE_OFFSET
		);
		return ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
	}
}
