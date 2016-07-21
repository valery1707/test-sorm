package name.valery1707.megatel.sorm.app.db;

import javaslang.CheckedFunction2;
import javaslang.Tuple2;
import javaslang.collection.LinkedHashMap;
import javaslang.collection.Map;
import javaslang.collection.Seq;
import javaslang.collection.Vector;
import name.valery1707.megatel.sorm.domain.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class DatabaseMerger implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseMerger.class);

	public static final String CREATED_AT = "created_at";
	public static final String CREATED_BY_ID = "created_by_id";
	private static final int BATCH_LIMIT = 100;
	private static final int RUN_LIMIT = 1000;

	private final Server server;
	private final String tableName;
	private final JdbcTemplate jdbcSrc;
	private final JdbcTemplate jdbcDst;

	private Timestamp lastTime = new Timestamp(0);
	private Map<String, ValueGetter> colTypes = LinkedHashMap.empty();
	private Seq<Tuple2<Tuple2<String, ValueGetter>, Long>> colTypesWithIndex;
	private String select;
	private String insert;

	public DatabaseMerger(Server server, String tableName, JdbcTemplate jdbcSrc, JdbcTemplate jdbcDst) {
		this.server = server;
		this.tableName = tableName;
		this.jdbcSrc = jdbcSrc;
		this.jdbcDst = jdbcDst;
		init();
	}

	private void init() {
		mdc();
		LOG.info("Init");

		String quote;
		try (Connection conn = jdbcSrc.getDataSource().getConnection()) {
			quote = conn.getMetaData().getIdentifierQuoteString().trim();
		} catch (SQLException e) {
			throw new IllegalStateException("Could not get metadata: ", e);
		}

		//todo Это не эффективно, но требует минимум изменений в структуре
		//Можно хранить в таблице с серверами, но тогда нужно, после каждого цикла переноса данных, актуализировать это значение в таблице сервером.
		// Что бы после перезапуска мы не наделали дубликатов
		lastTime = jdbcDst.queryForObject(format("SELECT MAX(T.%s) FROM %s AS T WHERE (T.%s = ?)", CREATED_AT, tableName, CREATED_BY_ID), Timestamp.class, server.getId());
		if (lastTime == null) {
			lastTime = new Timestamp(0);
		}

		//Структура таблицы: список полей и их типы
		SqlRowSet rs = jdbcSrc.queryForRowSet(format("SELECT * FROM %s WHERE 1 = 2", tableName));
		SqlRowSetMetaData metaData = rs.getMetaData();
		for (int colIndex = 1; colIndex <= metaData.getColumnCount(); colIndex++) {
			colTypes = colTypes.put(
					metaData.getColumnName(colIndex),
					ValueGetter.detect(metaData.getColumnType(colIndex))
			);
		}
		colTypesWithIndex = colTypes.zipWithIndex();
		Vector<String> colNames = colTypes.iterator().map(Tuple2::_1).toVector();

		select = format("SELECT %s FROM %s WHERE (%s > ?) ORDER BY %s",
				colNames.mkString(quote, quote + ", " + quote, quote),
				tableName,
				CREATED_AT, CREATED_AT
		);
		insert = format("INSERT INTO %s (%s) VALUES (%s)",
				tableName,
				colNames.mkString(quote, quote + ", " + quote, quote),
				colNames.map(name -> "?").mkString(", ")
		);

		LOG.info("Resume merge from {}", lastTime);
	}

	@Override
	public void run() {
		mdc();
		long count = 0;
		Timestamp prevTime = lastTime;
		List<PreparedStatementSetter> batch = new ArrayList<>(BATCH_LIMIT);
		LOG.debug("Search from {}", lastTime);
		try (
				Connection conn = jdbcSrc.getDataSource().getConnection();
				PreparedStatement select = select(conn);
				ResultSet rs = select.executeQuery();
		) {
			while (rs.next()) {
				count++;
				try {
					lastTime = rs.getTimestamp(CREATED_AT);
					batch.add(parseRow(rs));
				} catch (IllegalStateException e) {
					LOG.warn("Failed to parse row", e);
				}
				if (batch.size() >= BATCH_LIMIT) {
					batchUpdate(batch);
					batch.clear();
				}
				if (count < RUN_LIMIT) {
					//В рамках лимита: сохраняем метку времени
					prevTime = lastTime;
				} else {
					//За лимитом: ждём когда сменится метка времени и выходим
					//Это нужно что бы в следующий раз не потерять записи которые мы не обработали из-за лимита по количеству, но они имели ту же метку времени что и уже обработанные
					if (!prevTime.equals(lastTime)) {
						break;
					}
				}
			}
			batchUpdate(batch);
		} catch (Throwable e) {
			LOG.warn("Catch error while merge:", e);
		}
	}

	private PreparedStatement select(Connection conn) throws SQLException {
		PreparedStatement select = conn.prepareStatement(this.select, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
		select.setTimestamp(1, lastTime);
		return select;
	}

	private PreparedStatementSetter parseRow(ResultSet rs) {
		SqlParameterValue[] values = new SqlParameterValue[colTypesWithIndex.size()];
		colTypesWithIndex.forEach(t -> {
			values[t._2.intValue()] = parseCol(rs, t._1._1, t._1._2, t._2.intValue() + 1);
		});
		return new ArgumentPreparedStatementSetter(values);
	}

	private SqlParameterValue parseCol(ResultSet rs, String name, ValueGetter getter, int index) {
		if (CREATED_BY_ID.equals(name)) {
			return new SqlParameterValue(Types.BIGINT, server.getId());
		}
		try {
			return new SqlParameterValue(rs.getMetaData().getColumnType(index), getter.get(rs, index));
		} catch (Throwable throwable) {
			throw new IllegalStateException("Could not extract value from");
		}
	}

	private void batchUpdate(List<PreparedStatementSetter> batch) {
		if (batch.isEmpty()) {
			return;
		}
		LOG.info("batchUpdate[{}]()", batch.size());
		jdbcDst.batchUpdate(insert, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				batch.get(i).setValues(ps);
			}

			@Override
			public int getBatchSize() {
				return batch.size();
			}
		});
	}

	private void mdc() {
		MDC.clear();
		MDC.put("server", format("[%s/%s] ", server.getDbName(), tableName));
	}

	private enum ValueGetter {
		STRING(new Integer[]{Types.LONGVARCHAR, Types.VARCHAR}, ResultSet::getString),
		INTEGER(new Integer[]{Types.INTEGER}, ResultSet::getInt),
		DECIMAL(new Integer[]{Types.DECIMAL}, ResultSet::getBigDecimal),
		BIGINT(new Integer[]{Types.BIGINT}, ResultSet::getLong),
		BIT(new Integer[]{Types.BIT}, ResultSet::getByte),
		BOOLEAN(new Integer[]{Types.BOOLEAN}, ResultSet::getBoolean),
		TIMESTAMP(new Integer[]{Types.TIMESTAMP}, ResultSet::getTimestamp),
		UNKNOWN(new Integer[0], ValueGetter::unknown);

		private final List<Integer> type;
		private final CheckedFunction2<ResultSet, Integer, Object> mapper;

		ValueGetter(Integer[] type, CheckedFunction2<ResultSet, Integer, Object> mapper) {
			this.type = Arrays.asList(type);
			this.mapper = mapper;
		}

		public static ValueGetter detect(int type) {
			for (ValueGetter getter : ValueGetter.values()) {
				if (getter.type.contains(type)) {
					return getter;
				}
			}
			return UNKNOWN;
		}

		public Object get(ResultSet rs, int index) throws Throwable {
			return mapper.apply(rs, index);
		}

		private static Object unknown(ResultSet rs, int index) throws SQLException {
			ResultSetMetaData meta = rs.getMetaData();
			LOG.warn("Unknown type {} ({}) in column '{}'", meta.getColumnType(index), meta.getColumnTypeName(index), meta.getColumnName(index));
			return rs.getObject(index);
		}
	}
}
