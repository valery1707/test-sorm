package name.valery1707.megatel.sorm.app.db;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.HashMap;
import javaslang.collection.List;
import javaslang.collection.Map;
import javaslang.concurrent.Future;
import name.valery1707.megatel.sorm.app.bro.ServerRepo;
import name.valery1707.megatel.sorm.domain.Server;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.*;

import static java.lang.String.format;

@Service
@Singleton
public class DatabaseMergeScheduler {
	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private DataSourceProperties dataSourceProperties;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private JdbcTemplate center;

	@Inject
	@SuppressWarnings("SpringJavaAutowiringInspection")
	private ServerRepo serverRepo;

	private ScheduledExecutorService executor;
	private List<String> tableNames = List.empty();
	/**
	 * Server / DataSource
	 */
	private Map<Server, DataSource> connectionPool = HashMap.empty();
	/**
	 * (Server, Table name) / Merger
	 */
	private Map<Tuple2<Server, String>, Future<DatabaseMerger>> mergerInfo = HashMap.empty();
	/**
	 * Merger / Future
	 */
	private java.util.Map<DatabaseMerger, ScheduledFuture<?>> mergerFuture = new ConcurrentHashMap<>();

	@PostConstruct
	public void init() {
		String catalog;
		try (Connection conn = center.getDataSource().getConnection()) {
			catalog = conn.getCatalog();
			ResultSet rs = conn.getMetaData().getTables(conn.getCatalog(), conn.getSchema(), "bro_%", new String[]{"TABLE"});
			while (rs.next()) {
				tableNames = tableNames.append(rs.getString("TABLE_NAME"));
			}
			rs.close();
		} catch (SQLException e) {
			throw new IllegalStateException("Could not get connection to 'center':", e);
		}
		String URL = dataSourceProperties.getUrl().replace(catalog, "%s");

		executor = Executors.newScheduledThreadPool(10, new BasicThreadFactory.Builder()
				.daemon(true)
				.namingPattern("db-merger-%d")
				.priority(Thread.NORM_PRIORITY - 2)
				.build()
		);

		//todo Информация о серверах Bro собирается только при запуске - это не совсем корректно
		serverRepo.findAll().forEach(server -> {
			long id = server.getId();
			String dbName = server.getDbName();
			if (catalog.equals(dbName)) {
				//Если сервер работает на той же БД что и Центр, то копировать от туда нам не нужно
				return;
			}

			PoolProperties poolProperties = new PoolProperties();
			poolProperties.setName(format("replica-for-server-%d", id));
			poolProperties.setUrl(format(URL, dbName));
			poolProperties.setDriverClassName(dataSourceProperties.getDriverClassName());
			poolProperties.setUsername(dataSourceProperties.getUsername());
			poolProperties.setPassword(dataSourceProperties.getPassword());
			poolProperties.setValidationQuery("SELECT 1;");
			poolProperties.setTestWhileIdle(true);
			connectionPool = connectionPool.put(server, new DataSource(poolProperties));
		});

		//todo Период запуска сделать настраиваемым?
		Duration delay = Duration.ofSeconds(10);
		mergerInfo = connectionPool
				//(server, dataSource) -> (server, dataSource, tableName)
				.flatMap(t -> tableNames.map(name -> Tuple.of(t._1, t._2, name)))
				//(server, dataSource, tableName) -> (server, tableName) / Future(merger)
				.toMap(t -> Tuple.of(
						Tuple.of(t._1, t._3),
						Future.of(executor, () -> new DatabaseMerger(t._1, t._3, new JdbcTemplate(t._2, true), center))
								//todo LOG
								.onFailure(Throwable::printStackTrace)
								.onSuccess(merger -> mergerFuture.put(
										merger,
										executor.scheduleWithFixedDelay(merger, TimeUnit.SECONDS.toMillis(1), delay.toMillis(), TimeUnit.MILLISECONDS)
								))
				))
		;
	}
}
