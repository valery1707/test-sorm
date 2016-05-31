package name.valery1707.megatel.sorm;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Contract;

import javax.annotation.RegEx;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;
import java.util.regex.Pattern;

public class DateUtils {
	public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneId.systemDefault().getRules().getOffset(ZonedDateTime.now().toInstant());
	public static final ZoneId DEFAULT_ZONE_ID_NORM = DEFAULT_ZONE_OFFSET.normalized();
	public static final String DEFAULT_ZONE_OFFSET_NAME = DEFAULT_ZONE_OFFSET.toString();
	public static final DateTimeFormatter LOCAL_DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	public static final DateTimeFormatter ZONED_DATE_TIME_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;
	public static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
	@RegEx
	@RegExp
	public static final String LOCAL_DATE_PATTERN = "(\\d{4})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";
	@RegEx
	@RegExp
	public static final String LOCAL_TIME_PATTERN = "([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])(\\.\\d+)?Z?";
	@RegEx
	@RegExp
	public static final String LOCAL_DATE_TIME_PATTERN = LOCAL_DATE_PATTERN + "T" + LOCAL_TIME_PATTERN;
	@RegEx
	@RegExp
	public static final String ZONED_DATE_TIME_PATTERN = LOCAL_DATE_TIME_PATTERN + "([+-]\\d\\d:\\d\\d)?";

	public static ZonedDateTime bigDecimalToZonedDateTime(BigDecimal src) {
		LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(
				src.longValue(),
				src.remainder(BigDecimal.ONE).movePointRight(src.scale()).abs().intValue(),
				DEFAULT_ZONE_OFFSET
		);
		return ZonedDateTime.of(localDateTime, DEFAULT_ZONE_ID_NORM);
	}

	public static BigDecimal zonedDateTime2BigDecimal(ZonedDateTime src) {
		BigDecimal nanos = new BigDecimal(src.getNano())
				.movePointLeft(6);
		BigDecimal seconds = new BigDecimal(src.toEpochSecond())
				.add(nanos);
		return seconds;
	}

	@Contract("!null -> !null; null -> null")
	public static String formatDate(LocalDate src) {
		return src != null ? src.format(LOCAL_DATE_FORMAT) : null;
	}

	@Contract("!null -> !null; null -> null")
	public static String formatDateTime(ZonedDateTime src) {
		return src != null ? src.format(LOCAL_DATE_TIME_FORMAT) : null;
	}

	@Contract("!null -> !null; null -> null")
	public static String formatDateTimeZoned(ZonedDateTime src) {
		return src != null ? src.format(ZONED_DATE_TIME_FORMAT) : null;
	}

	private static <T extends Temporal> T parse(String src, String pattern, DateTimeFormatter formatter, Function<TemporalAccessor, T> mapper) {
		if (src == null) {
			return null;
		}
		//todo Cache compiled patterns
		if (!Pattern.compile(pattern).matcher(src).matches()) {
			return null;
		}
		TemporalAccessor accessor = formatter.parse(src);
		return mapper.apply(accessor);
	}

	public static LocalDate parseDate(String src) {
		return parse(src, LOCAL_DATE_PATTERN, LOCAL_DATE_FORMAT, LocalDate::from);
	}

	public static ZonedDateTime parseDateTime(String src) {
		return parse(src, LOCAL_DATE_TIME_PATTERN, LOCAL_DATE_TIME_FORMAT, temporalToZonedDateTime);
	}

	public static ZonedDateTime parseDateTimeZoned(String src) {
		return parse(src, ZONED_DATE_TIME_PATTERN, ZONED_DATE_TIME_FORMAT, temporalToZonedDateTime);
	}

	private static final Function<TemporalAccessor, ZonedDateTime> temporalToZonedDateTime = temporalAccessor -> {
		if (temporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) {
			return ZonedDateTime.from(temporalAccessor);
		} else {
			return LocalDateTime.from(temporalAccessor).atZone(DEFAULT_ZONE_ID_NORM);
		}
	};
}
