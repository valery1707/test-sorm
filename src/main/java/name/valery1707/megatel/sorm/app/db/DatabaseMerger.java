package name.valery1707.megatel.sorm.app.db;

import name.valery1707.megatel.sorm.domain.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;

import static java.lang.String.format;

public class DatabaseMerger implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(DatabaseMerger.class);

	private final Server server;
	private final String tableName;
	private final JdbcTemplate jdbcSrc;
	private final JdbcTemplate jdbcDst;

	private Timestamp lastTime = new Timestamp(0);

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
		//todo Это не эффективно, но требует минимум изменений в структуре
		//Можно хранить в таблице с серверами, но тогда нужно, после каждого цикла переноса данных, актуализировать это значение в таблице сервером.
		// Что бы после перезапуска мы не наделали дубликатов
		lastTime = jdbcDst.queryForObject(format("SELECT MAX(T.created_at) FROM %s AS T WHERE (T.created_by_id = ?)", tableName), Timestamp.class, server.getId());
		if (lastTime == null) {
			lastTime = new Timestamp(0);
		}
		LOG.info("Resume merge from {}", lastTime);
	}

	@Override
	public void run() {
		mdc();
		try {
			Long cnt = jdbcSrc.queryForObject(format("SELECT COUNT(*) AS cnt FROM %s AS T WHERE (T.created_at > ?)", tableName), Long.class, lastTime);
			LOG.info("Count: {}", cnt);
		} catch (Throwable e) {
			LOG.warn("Catch error while merge:", e);
		}
	}

	private void mdc() {
		MDC.clear();
		MDC.put("server", format("[%s/%s] ", server.getDbName(), tableName));
	}
}
