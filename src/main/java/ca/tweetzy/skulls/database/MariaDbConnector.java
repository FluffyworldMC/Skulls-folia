package ca.tweetzy.skulls.database;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.skulls.settings.Settings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class MariaDbConnector implements DatabaseConnector {

	private final Plugin plugin;
	private final HikariDataSource dataSource;

	public MariaDbConnector(Plugin plugin) {
		this.plugin = plugin;

		final HikariConfig config = new HikariConfig();
		final String host = Settings.DATABASE_HOST.getString();
		final int port = Settings.DATABASE_PORT.getInt();
		final String database = Settings.DATABASE_NAME.getString();
		final boolean ssl = Settings.DATABASE_SSL.getBoolean();

		final String url = "jdbc:mariadb://" + host + ":" + port + "/" + database
				+ "?useUnicode=true&characterEncoding=UTF-8&useServerPrepStmts=true&rewriteBatchedStatements=true"
				+ (ssl ? "&useSsl=true" : "&useSsl=false");

		config.setJdbcUrl(url);
		config.setUsername(Settings.DATABASE_USER.getString());
		config.setPassword(Settings.DATABASE_PASSWORD.getString());
		config.setDriverClassName("org.mariadb.jdbc.Driver");
		config.setMaximumPoolSize(Math.max(1, Settings.DATABASE_POOL_SIZE.getInt()));
		config.setMinimumIdle(1);
		config.setConnectionTimeout(Settings.DATABASE_CONNECTION_TIMEOUT.getInt());
		config.setMaxLifetime(Settings.DATABASE_MAX_LIFETIME.getInt());
		config.setPoolName("Skulls-MariaDB");
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		config.addDataSourceProperty("useServerPrepStmts", "true");
		config.addDataSourceProperty("rewriteBatchedStatements", "true");

		this.dataSource = new HikariDataSource(config);
		plugin.getLogger().info("Connected to MariaDB at " + host + ":" + port + "/" + database);
	}

	@Override
	public boolean isInitialized() {
		return this.dataSource != null && !this.dataSource.isClosed();
	}

	@Override
	public void closeConnection() {
		if (this.dataSource != null && !this.dataSource.isClosed()) {
			this.dataSource.close();
		}
	}

	@Override
	public void connect(ConnectionCallback callback) {
		try (Connection connection = this.dataSource.getConnection()) {
			callback.accept(connection);
		} catch (SQLException exception) {
			this.plugin.getLogger().warning("MariaDB query failed: " + exception.getMessage());
			exception.printStackTrace();
		}
	}
}
