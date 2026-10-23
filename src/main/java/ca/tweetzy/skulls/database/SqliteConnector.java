package ca.tweetzy.skulls.database;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.skulls.settings.Settings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class SqliteConnector implements DatabaseConnector {

	private final Plugin plugin;
	private final HikariDataSource dataSource;

	public SqliteConnector(Plugin plugin) {
		this.plugin = plugin;

		final File file = new File(plugin.getDataFolder(), "skulls.db");
		final int busyTimeout = Math.max(1000, Settings.DATABASE_SQLITE_BUSY_TIMEOUT.getInt());

		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath()
				+ "?journal_mode=WAL&busy_timeout=" + busyTimeout + "&synchronous=NORMAL");
		config.setDriverClassName("org.sqlite.JDBC");
		config.setMaximumPoolSize(1);
		config.setMinimumIdle(1);
		config.setConnectionTimeout(10000);
		config.setMaxLifetime(0);
		config.setPoolName("Skulls-SQLite");
		config.setConnectionTestQuery("SELECT 1");

		this.dataSource = new HikariDataSource(config);
		plugin.getLogger().info("Connected to SQLite at " + file.getAbsolutePath());
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
			this.plugin.getLogger().warning("SQLite query failed: " + exception.getMessage());
			exception.printStackTrace();
		}
	}
}
