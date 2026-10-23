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

package ca.tweetzy.skulls.database;

import ca.tweetzy.flight.database.Callback;
import ca.tweetzy.flight.database.DataManagerAbstract;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.UpdateCallback;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.SerializeUtil;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.enums.BaseCategory;
import ca.tweetzy.skulls.api.interfaces.PlacedSkull;
import ca.tweetzy.skulls.api.interfaces.Skull;
import ca.tweetzy.skulls.api.interfaces.SkullUser;
import ca.tweetzy.skulls.impl.PlacedSkullLocation;
import ca.tweetzy.skulls.impl.SkullPlayer;
import ca.tweetzy.skulls.impl.TexturedSkull;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Date Created: April 01 2022
 * Time Created: 11:23 p.m.
 *
 * @author Kiran Hart
 */
public final class DataManager extends DataManagerAbstract {

	private final boolean mariaDb;

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
		this.mariaDb = databaseConnector instanceof MariaDbConnector;
	}

	private String insertIgnore() {
		return this.mariaDb ? "INSERT IGNORE" : "INSERT OR IGNORE";
	}

	/*
	 * Flight's DataManagerAbstract uses Bukkit.getScheduler() internally, which does not
	 * exist on Folia. Database work is executed on the inherited single-thread executor
	 * instead, and callbacks are moved to the global region thread.
	 */

	@Override
	public void async(Runnable runnable) {
		this.asyncPool.execute(runnable);
	}

	@Override
	public void sync(Runnable runnable) {
		final TaskScheduler scheduler = Skulls.getTaskScheduler();
		if (scheduler != null) scheduler.runTask(runnable);
	}

	@Override
	public void runAsync(Runnable runnable) {
		this.asyncPool.execute(runnable);
	}

	@Override
	public void runAsync(Runnable runnable, Consumer<Throwable> onError) {
		this.asyncPool.execute(() -> {
			try {
				runnable.run();
			} catch (Throwable throwable) {
				if (onError != null) onError.accept(throwable);
			}
		});
	}

	public void insertSkulls(Collection<Skull> skulls) {
		final int batchSize = 1000;
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final boolean autoCommit = connection.getAutoCommit();

			try (PreparedStatement statement = connection.prepareStatement(
					insertIgnore() + " INTO " + this.getTablePrefix() + "skull(id, name, category, texture, tags, price, blocked) VALUES(?, ?, ?, ?, ?, ?, ?)"
			)) {
				connection.setAutoCommit(false);

				int count = 0;
				for (Skull skull : skulls) {
					statement.setInt(1, skull.getId());
					statement.setString(2, skull.getName());
					statement.setString(3, skull.getCategory());
					statement.setString(4, skull.getTexture());
					statement.setString(5, String.join(",", skull.getTags()));
					statement.setDouble(6, skull.getPrice());
					statement.setBoolean(7, skull.isBlocked());
					statement.addBatch();

					if (++count % batchSize == 0) {
						statement.executeBatch();
					}
				}

				statement.executeBatch();
				connection.commit();
			} catch (SQLException exception) {
				try {
					connection.rollback();
				} catch (SQLException ignored) {
				}
				throw exception;
			} finally {
				try {
					connection.setAutoCommit(autoCommit);
				} catch (SQLException ignored) {
				}
			}

			Skulls.getSkullManager().setDownloading(false);
			Common.log("&r&aFinished inserting all heads into the data file!");
		}));
	}

	public void insertPlacedSkull(@NonNull final PlacedSkull placedSkull, Callback<PlacedSkull> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + this.getTablePrefix() + "placed_skull (id, skull_id, location) VALUES(?, ?, ?)")) {
				PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "placed_skull WHERE id = ?");

				fetch.setString(1, placedSkull.getId().toString());
				statement.setString(1, placedSkull.getId().toString());
				statement.setInt(2, placedSkull.getSkullId());
				statement.setString(3, SerializeUtil.serializeLocation(placedSkull.getLocation()));
				statement.executeUpdate();

				if (callback != null) {
					ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractPlacedSkull(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void getPlacedSkulls(Callback<ArrayList<PlacedSkull>> callback) {
		ArrayList<PlacedSkull> placedSkulls = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "placed_skull")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					placedSkulls.add(extractPlacedSkull(resultSet));
				}

				callback.accept(null, placedSkulls);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void deletePlacedSkull(final UUID id, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "placed_skull WHERE id = ?")) {
				statement.setString(1, id.toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	//id, name, category, tags, price, blocked
	public void getSkulls(Callback<ArrayList<Skull>> callback) {
		ArrayList<Skull> skulls = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "skull")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					skulls.add(extractSkull(resultSet));
				}
				this.sync(() -> callback.accept(null, skulls));
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void syncSkullPricesByCategory(Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "skull SET price = ? WHERE category = ?");

			for (BaseCategory value : BaseCategory.values()) {
				statement.setDouble(1, value.getDefaultPrice());
				statement.setString(2, value.getId());
				statement.addBatch();
			}

			int[] updated = statement.executeBatch();
			Common.log("updated" + updated.length);

			if (callback != null)
				this.sync(() -> callback.accept(null, updated.length > 0));
		}));
	}

	public void getPlayers(Callback<ArrayList<SkullUser>> callback) {
		ArrayList<SkullUser> skulls = new ArrayList<>();
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "players")) {
				ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					skulls.add(extractSkullPlayer(resultSet));
				}

				this.sync(() -> callback.accept(null, skulls));
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void insertPlayer(@NonNull final SkullUser user, Callback<SkullUser> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement(insertIgnore() + " INTO " + this.getTablePrefix() + "players (uuid, favourites) VALUES(?, ?)");
			     PreparedStatement fetch = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "players WHERE uuid = ?")) {

				fetch.setString(1, user.getUUID().toString());
				statement.setString(1, user.getUUID().toString());
				statement.setString(2, user.getFavourites().stream().map(String::valueOf).collect(Collectors.joining(",")));
				statement.executeUpdate();

				if (callback != null) {
					try (ResultSet res = fetch.executeQuery()) {
						if (res.next()) {
							callback.accept(null, extractSkullPlayer(res));
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateSkull(@NonNull final Skull skull, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "skull SET name = ?, price = ?, blocked = ? WHERE id = ?")) {

				statement.setString(1, skull.getName());
				statement.setDouble(2, skull.getPrice());
				statement.setBoolean(3, skull.isBlocked());
				statement.setInt(4, skull.getId());

				int result = statement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void updatePlayer(@NonNull final SkullUser user, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("UPDATE " + this.getTablePrefix() + "players SET favourites = ? WHERE uuid = ?")) {

				statement.setString(1, user.getFavourites().stream().map(String::valueOf).collect(Collectors.joining(",")));
				statement.setString(2, user.getUUID().toString());

				int result = statement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public Skull extractSkull(@NonNull final ResultSet resultSet) throws SQLException {
		return new TexturedSkull(
				resultSet.getInt("id"),
				resultSet.getString("name"),
				resultSet.getString("category"),
				Arrays.asList(resultSet.getString("tags").split(",")),
				resultSet.getString("texture"),
				resultSet.getDouble("price"),
				resultSet.getBoolean("blocked")
		);
	}

	public PlacedSkull extractPlacedSkull(@NonNull final ResultSet resultSet) throws SQLException {
		return new PlacedSkullLocation(
				UUID.fromString(resultSet.getString("id")),
				resultSet.getInt("skull_id"),
				SerializeUtil.deserializeLocation(resultSet.getString("location"))
		);
	}


	public SkullUser extractSkullPlayer(@NonNull final ResultSet resultSet) throws SQLException {
		final String favs = resultSet.getString("favourites");

		if (favs == null || favs.isBlank()) {
			return new SkullPlayer(UUID.fromString(resultSet.getString("uuid")), new ArrayList<>());
		}

		final String[] split = favs.split(",");
		return new SkullPlayer(
				UUID.fromString(resultSet.getString("uuid")),
				Arrays.stream(split).filter(value -> !value.isBlank()).map(Integer::parseInt).collect(Collectors.toList())
		);
	}

	private void resolveUpdateCallback(@Nullable UpdateCallback callback, @Nullable Exception ex) {
		if (callback != null) {
			callback.accept(ex);
		} else if (ex != null) {
			ex.printStackTrace();
		}
	}

	private void resolveCallback(@Nullable Callback<?> callback, @NotNull Exception ex) {
		if (callback != null) {
			callback.accept(ex, null);
		} else {
			ex.printStackTrace();
		}
	}

	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
