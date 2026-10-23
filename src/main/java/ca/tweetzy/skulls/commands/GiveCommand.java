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
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.interfaces.Skull;
import ca.tweetzy.skulls.model.StringHelper;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class GiveCommand extends Command {

	public GiveCommand() {
		super(AllowedExecutor.BOTH, "give");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length < 2) return ReturnType.FAIL;

		final boolean isGiveAll = args[0].equals("*");

		Player target = null;

		if (!isGiveAll) {
			target = Bukkit.getPlayerExact(args[0]);
		}

		if (target == null && !isGiveAll) {
			Common.tell(sender, TranslationManager.string(Translations.ERROR_PLAYER_OFFLINE, "value", args[0]));
			return ReturnType.FAIL;
		}

		final boolean isRandomHead = args[1].equalsIgnoreCase("random");

		if (!isRandomHead && !StringHelper.isInt(args[1])) {
			Common.tell(sender, TranslationManager.string(Translations.ERROR_NOT_A_NUMBER, "value", args[1]));
			return ReturnType.FAIL;
		}

		int amount = args.length == 3 ? StringHelper.tryInt(args[2], 1) : 1;
		if (amount > 36) amount = 36;

		Skull skull = isRandomHead ? Skulls.getSkullManager().getRandomSkull() : Skulls.getSkullManager().getSkull(Integer.parseInt(args[1]));

		if (skull == null) {
			Common.tell(sender, TranslationManager.string(Translations.SKULL_NOT_FOUND));
			return ReturnType.FAIL;
		}

		final List<Player> targets = isGiveAll
				? List.copyOf(Bukkit.getOnlinePlayers())
				: List.of(target);

		for (int i = 0; i < amount; i++) {
			final Skull selected = isRandomHead ? Skulls.getSkullManager().getRandomSkull() : skull;
			if (selected == null) continue;

			for (Player player : targets) {
				Skulls.getTaskScheduler().runTask(player, () -> giveHead(player, selected));
			}
		}

		return ReturnType.SUCCESS;
	}

	private void giveHead(Player target, Skull skull) {
		if (!target.isOnline()) return;

		if (target.getInventory().firstEmpty() == -1) {
			target.getWorld().dropItemNaturally(target.getLocation(), skull.getItemStack());
		} else {
			target.getInventory().addItem(skull.getItemStack());
		}
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "skulls.command.give";
	}

	@Override
	public String getSyntax() {
		return "skulls give <*/player> <random/id> [#]";
	}

	@Override
	public String getDescription() {
		return "มอบหัวที่ระบุหรือหัวสุ่มให้ผู้เล่น";
	}
}
