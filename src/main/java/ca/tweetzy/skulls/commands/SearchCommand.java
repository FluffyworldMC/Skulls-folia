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

package ca.tweetzy.skulls.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.enums.ViewMode;
import ca.tweetzy.skulls.api.interfaces.SkullUser;
import ca.tweetzy.skulls.impl.SkullPlayer;
import ca.tweetzy.skulls.menu.impl.SkullsListMenu;
import ca.tweetzy.skulls.model.StringHelper;
import ca.tweetzy.skulls.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Date Created: April 20 2022
 * Time Created: 9:42 p.m.
 *
 * @author Kiran Hart
 */
public final class SearchCommand extends Command {

	public SearchCommand() {
		super(AllowedExecutor.PLAYER, "search");
	}

	@Override
	protected ReturnType execute(CommandSender commandSender, String... args) {
		final Player player = (Player) commandSender;
		if (args.length == 0) return ReturnType.INVALID_SYNTAX;

		final StringBuilder builder = new StringBuilder();
		for (String arg : args) builder.append(" ").append(arg);

		SkullUser skullUser = Skulls.getPlayerManager().findOrCreate(player);
		String query = StringHelper.escapeRegex(builder.toString().trim());

		if (skullUser == null)
			Skulls.getDataManager().insertPlayer(new SkullPlayer(player.getUniqueId(), new ArrayList<>()), (createError, created) -> {
				if (createError == null) {
					Skulls.getPlayerManager().addPlayer(created);
					new SkullsListMenu(null, player, created, query, ViewMode.SEARCH).open();
				} else {
					new SkullsListMenu(null, player, new SkullPlayer(player.getUniqueId(), new ArrayList<>()), query, ViewMode.SEARCH).open();
				}
			});
		else
			new SkullsListMenu(null, player, skullUser, query, ViewMode.SEARCH).open();

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender commandSender, String... strings) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean() ? null : "skulls.command.search";
	}

	@Override
	public String getSyntax() {
		return "search <keywords>";
	}

	@Override
	public String getDescription() {
		return "ค้นหาหัวจากคำค้นหา";
	}
}
