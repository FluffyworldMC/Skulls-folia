package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.MenuClick;
import ca.tweetzy.skulls.menu.PagedMenu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import ca.tweetzy.skulls.utils.SkullItems;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class PlayerHeadsMenu extends PagedMenu<OfflinePlayer> {

	public PlayerHeadsMenu(Menu parent, Player player) {
		super(parent, player, "player-heads", TranslationManager.string(player, Translations.GUI_PLAYER_HEADS_TITLE), 6);
		setLoading(true);
		loadPlayers();
	}

	private void loadPlayers() {
		if (Skulls.getTaskScheduler() == null) return;

		Skulls.getTaskScheduler().runTask(() -> {
			final List<OfflinePlayer> players = Skulls.getSkullManager().getOnlineOfflinePlayers();

			Skulls.getTaskScheduler().runTask(getPlayer(), () -> {
				if (!getPlayer().isOnline()) return;
				setItems(players);
				setLoading(false);
				refresh();
			});
		});
	}

	@Override
	protected ItemStack makeDisplayItem(OfflinePlayer target) {
		if (target == null || !target.hasPlayedBefore()) {
			final ItemStack fallback = QuickItem.of(CompMaterial.PLAYER_HEAD)
					.name(TranslationManager.string(getPlayer(), Translations.GUI_PLAYER_HEADS_UNKNOWN_NAME))
					.make();

			return MenuConfig.item(getPlayer(), "player-heads.unknown", fallback);
		}

		final String priceText = String.format("%,.2f", Settings.DEFAULT_PRICES_PLAYER_HEADS.getDouble());
		final QuickItem item = QuickItem.of(CompMaterial.PLAYER_HEAD)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_PLAYER_HEADS_ITEMS_HEAD_NAME, "player_name", target.getName()));

		if (Settings.CHARGE_FOR_HEADS.getBoolean() && Settings.DEFAULT_PRICES_PLAYER_HEADS.getDouble() > 0) {
			item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_PRICE, "skull_price", priceText));
		}

		item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_TAKE));

		final ItemStack head = SkullItems.applyPlayer(item.make(), target);

		return MenuConfig.item(getPlayer(), "player-heads.item", head, Map.of(
				"player_name", target.getName() == null ? "" : target.getName(),
				"skull_price", priceText
		));
	}

	@Override
	protected void onClick(OfflinePlayer target, MenuClick click) {
		if (click.clickType != ClickType.LEFT) return;

		if (Settings.ASK_FOR_BUY_CONFIRM.getBoolean()) {
			new ConfirmMenu(this, click.player, confirmed -> {
				if (confirmed) handleBuy(click.player, target);
				PlayerHeadsMenu.this.open();
			}).open();
			return;
		}

		handleBuy(click.player, target);
	}

	private void handleBuy(Player player, OfflinePlayer target) {
		if (!Settings.CHARGE_FOR_HEADS.getBoolean()) {
			PlayerUtil.giveItem(player, buildHead(target));
			return;
		}

		final double price = player.hasPermission("skulls.freeskulls") ? 0 : Settings.DEFAULT_PRICES_PLAYER_HEADS.getDouble();

		if (price <= 0) {
			PlayerUtil.giveItem(player, buildHead(target));
			return;
		}

		if (!Skulls.getEconomyManager().has(player, price)) {
			Common.tell(player, TranslationManager.string(player, Translations.ERROR_NO_MONEY));
			return;
		}

		Skulls.getEconomyManager().withdraw(player, price);
		PlayerUtil.giveItem(player, buildHead(target));
		Common.tell(player, TranslationManager.string(player, Translations.PURCHASE_SUCCESS));
	}

	private ItemStack buildHead(OfflinePlayer target) {
		final ItemStack item = QuickItem.of(CompMaterial.PLAYER_HEAD)
				.name(TranslationManager.string(getPlayer(), Translations.PLAYER_HEAD, "player_name", target.getName()))
				.make();

		return SkullItems.applyPlayer(item, target);
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}
}
