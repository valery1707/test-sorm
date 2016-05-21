package name.valery1707.megatel.sorm.db.h2;

import org.jetbrains.annotations.Contract;

import java.sql.Timestamp;

/**
 * Новые функции для H2
 *
 * @see <a href="http://h2database.com/html/grammar.html#create_alias">CREATE ALIAS</a>
 */
public final class TimeFunctions {
	/**
	 * @param expr1 First expression
	 * @param expr2 Second expression
	 * @return {@code expr1} − {@code expr2}
	 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html#function_timediff">TIMEDIFF()</a>
	 */
	@Contract("!null, !null -> !null; null, _ -> null; _, null -> null")
	public static Timestamp TimeDiff(Timestamp expr1, Timestamp expr2) {
		if (expr1 == null || expr2 == null) {
			return null;
		}
		return new Timestamp(expr1.getTime() - expr2.getTime());
	}

	/**
	 * @param time Time value
	 * @return {@code time} argument, converted to seconds
	 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/date-and-time-functions.html#function_time-to-sec">TIME_TO_SEC(time)</a>
	 */
	@Contract("!null -> !null; null -> null")
	public static Long TimeToSec(Timestamp time) {
		if (time == null) {
			return null;
		}
		return time.getTime() / 1000L;
	}
}
