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
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.model.StringHelper;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import ca.tweetzy.skulls.utils.SkullItems;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Date Created: May 02 2022
 * Time Created: 12:58 p.m.
 *
 * @author Kiran Hart
 */
public final class PlayerHeadCommand extends Command {

	public PlayerHeadCommand() {
		super(AllowedExecutor.BOTH, "phead");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length < 3 && !(sender instanceof Player)) {
			return ReturnType.INVALID_SYNTAX;
		}

		final Player executor = sender instanceof Player ? (Player) sender : null;
		final Player receiver = args.length == 3 ? Bukkit.getPlayerExact(args[2]) : executor;

		if (receiver == null) {
			Common.tell(sender, TranslationManager.string(Translations.ERROR_PLAYER_OFFLINE, "value", args[2]));
			return ReturnType.FAIL;
		}

		final OfflinePlayer targetUser;
		if (args.length == 0) {
			targetUser = executor;
		} else {
			final Player onlineTarget = Bukkit.getPlayerExact(args[0]);
			targetUser = onlineTarget != null ? onlineTarget : Bukkit.getOfflinePlayer(args[0]);
		}

		if (targetUser == null) {
			Common.tell(sender, TranslationManager.string(Translations.ERROR_PLAYER_NOT_FOUND, "value", args.length == 0 ? "?" : args[0]));
			return ReturnType.FAIL;
		}

		final int amount = args.length > 1 ? StringHelper.tryInt(args[1], 1) : 1;
		final String targetName = targetUser.getName() == null ? (args.length == 0 ? receiver.getName() : args[0]) : targetUser.getName();

		final ItemStack item = SkullItems.applyPlayer(QuickItem.of(CompMaterial.PLAYER_HEAD)
				.name(Settings.PLAYER_HEAD_NAME.getString().replace("%player_name%", targetName))
				.amount(amount)
				.make(), targetUser);

		Skulls.getTaskScheduler().runTask(receiver, () -> {
			if (!receiver.isOnline()) return;
			receiver.getInventory().addItem(item);
		});

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "skulls.command.phead";
	}

	@Override
	public String getSyntax() {
		return "skulls phead <player> [#] [target]";
	}

	@Override
	public String getDescription() {
		return "มอบหัวผู้เล่นให้ตัวเองหรือผู้เล่นคนอื่น";
	}
}
