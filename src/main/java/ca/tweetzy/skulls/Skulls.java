/*
 * Skulls
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.skulls;

import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.skulls.api.SkullsAPI;
import ca.tweetzy.skulls.commands.*;
import ca.tweetzy.skulls.database.DataManager;
import ca.tweetzy.skulls.database.MariaDbConnector;
import ca.tweetzy.skulls.database.SqliteConnector;
import ca.tweetzy.skulls.database.migrations._1_InitialMigration;
import ca.tweetzy.skulls.database.migrations._2_PlacedSkullsMigration;
import ca.tweetzy.skulls.database.migrations._3_HistoryRemovalMigration;
import ca.tweetzy.skulls.impl.SkullsAPIImplementation;
import ca.tweetzy.skulls.listeners.PlayerDeathListener;
import ca.tweetzy.skulls.listeners.PlayerJoinQuitListener;
import ca.tweetzy.skulls.listeners.SkullBlockListener;
import ca.tweetzy.skulls.manager.CategoryManager;
import ca.tweetzy.skulls.manager.EconomyManager;
import ca.tweetzy.skulls.manager.PlayerManager;
import ca.tweetzy.skulls.manager.SkullManager;
import ca.tweetzy.skulls.menu.MenuManager;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import org.bukkit.NamespacedKey;

import java.io.File;

/**
 * Date Created: April 04 2022
 * Time Created: 9:46 a.m.
 *
 * @author Kiran Hart
 */
public final class Skulls extends FlightPlugin {

	private static TaskScheduler scheduler;

	private final MenuManager menuManager = new MenuManager(this);
	private final CommandManager commandManager = new CommandManager(this);

	private final SkullManager skullManager = new SkullManager();
	private final CategoryManager categoryManager = new CategoryManager();
	private final PlayerManager playerManager = new PlayerManager();
	private EconomyManager economyManager;

	private final SkullsAPI api = new SkullsAPIImplementation();

	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private final NamespacedKey SKULLS_CLAIM_DELAY = new NamespacedKey(this, "SkullsClaimDelay");
	private final NamespacedKey SKULLS_ID = new NamespacedKey(this, "SkullsID");

	@Override
	protected void onFlight() {
		// The scheduler has to exist before anything else touches the database or the menus.
		scheduler = UniversalScheduler.getScheduler(this);

		// Copy the Thai defaults on first run, then let Flight load them.
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveResource("config.yml", false);
		}

		if (!new File(getDataFolder(), "locales/th_th.yml").exists()) {
			new File(getDataFolder(), "locales").mkdirs();
			saveResource("locales/th_th.yml", false);
		}

		if (!new File(getDataFolder(), "menus.yml").exists()) {
			saveResource("menus.yml", false);
		}

		// settings and locale setup
		Settings.setup();
		Translations.init();
		MenuConfig.load(this);

		Common.setPrefix(Settings.PREFIX.getString());
		Common.setPluginName("<GRADIENT:DD5E89>&lSkulls</GRADIENT:fbc7d4>");

		// setup database
		this.databaseConnector = createDatabaseConnector();
		this.dataManager = new DataManager(this.databaseConnector, this);

		// run idempotent table migrations
		this.runMigrations();

		this.skullManager.load();
		this.playerManager.load();
		this.categoryManager.load();

		this.economyManager = new EconomyManager();
		this.economyManager.init();
		this.menuManager.init();

		// command
		this.commandManager.registerCommandDynamically(new SkullsCommand()).addSubCommands(
				new SearchCommand(),
				new PlayerHeadCommand(),
				new GiveCommand(),
				new InspectCommand(),
				new ReloadCommand()
		);

		// events
		getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
		if (Settings.SKULL_TRACKING.getBoolean()) {
			getServer().getPluginManager().registerEvents(new SkullBlockListener(), this);
		}
	}

	@Override
	protected void onSleep() {
		this.menuManager.closeAll();

		if (this.dataManager != null) {
			shutdownDataManager(this.dataManager);
		}

		if (this.databaseConnector != null) {
			this.databaseConnector.closeConnection();
		}

		if (scheduler != null) {
			scheduler.cancelTasks();
			scheduler = null;
		}
	}

	private DatabaseConnector createDatabaseConnector() {
		final String type = Settings.DATABASE_TYPE.getString();

		if ("mariadb".equalsIgnoreCase(type)) {
			try {
				return new MariaDbConnector(this);
			} catch (Exception exception) {
				Common.log("&cไม่สามารถเชื่อมต่อ MariaDB ได้ จะใช้ SQLite แทน");
				exception.printStackTrace();
			}
		}

		try {
			return new SqliteConnector(this);
		} catch (Exception exception) {
			Common.log("&cไม่สามารถเปิด SQLite ด้วย HikariCP ได้ จะใช้ตัวเชื่อมต่อเดิมแทน");
			exception.printStackTrace();
			return new SQLiteConnector(this);
		}
	}

	private void runMigrations() {
		this.databaseConnector.connect(connection -> {
			final String prefix = this.dataManager.getTablePrefix();
			new _1_InitialMigration().migrate(connection, prefix);
			new _2_PlacedSkullsMigration().migrate(connection, prefix);
			new _3_HistoryRemovalMigration().migrate(connection, prefix);
		});
	}

	public static Skulls getInstance() {
		return (Skulls) FlightPlugin.getInstance();
	}

	public static TaskScheduler getTaskScheduler() {
		return scheduler;
	}

	// menu manager
	public static MenuManager getMenuManager() {
		return getInstance().menuManager;
	}

	// data manager
	public static DataManager getDataManager() {
		return getInstance().dataManager;
	}

	// skull manager
	public static SkullManager getSkullManager() {
		return getInstance().skullManager;
	}

	// category manager
	public static CategoryManager getCategoryManager() {
		return getInstance().categoryManager;
	}

	// player manager
	public static PlayerManager getPlayerManager() {
		return getInstance().playerManager;
	}

	// economy manager
	public static EconomyManager getEconomyManager() {
		return getInstance().economyManager;
	}

	// api
	public static SkullsAPI getAPI() {
		return getInstance().api;
	}

	public static NamespacedKey getClaimDelayKey() {
		return getInstance().SKULLS_CLAIM_DELAY;
	}

	public static NamespacedKey getSkullIdKey() {
		return getInstance().SKULLS_ID;
	}

	@Override
	protected int getBStatsId() {
		return 10616;
	}
}
