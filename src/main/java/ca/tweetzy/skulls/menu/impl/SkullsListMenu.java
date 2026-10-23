package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.enums.ViewMode;
import ca.tweetzy.skulls.api.interfaces.Skull;
import ca.tweetzy.skulls.api.interfaces.SkullUser;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.MenuClick;
import ca.tweetzy.skulls.menu.PagedMenu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class SkullsListMenu extends PagedMenu<Skull> {

	private final SkullUser skullPlayer;
	private final ViewMode viewMode;
	private final String category;

	public SkullsListMenu(Menu parent, Player player, SkullUser skullPlayer, String category, ViewMode viewMode) {
		super(parent, player, "skulls-list", title(player, category, viewMode), 6);
		this.skullPlayer = skullPlayer;
		this.viewMode = viewMode;
		this.category = category;
	}

	private static String title(Player player, String category, ViewMode viewMode) {
		if (viewMode == ViewMode.SEARCH) {
			final String fallback = TranslationManager.string(player, Translations.GUI_SKULLS_LIST_TITLE_SEARCH, "search_phrase", category);
			return MenuConfig.title("skulls-list.title.search", fallback);
		}

		if (viewMode == ViewMode.FAVOURITE) {
			final String fallback = TranslationManager.string(player, Translations.GUI_SKULLS_LIST_TITLE_FAVOURITES);
			return MenuConfig.title("skulls-list.title.favourites", fallback);
		}

		final String fallback = TranslationManager.string(player, Translations.GUI_SKULLS_LIST_TITLE_CATEGORY, "category_name", category);
		return MenuConfig.title("skulls-list.title.category", fallback);
	}

	@Override
	protected void draw() {
		if (this.viewMode == ViewMode.SEARCH) {
			setItems(Skulls.getSkullManager().getSkullsBySearch(getPlayer(), this.category));
		} else if (this.viewMode == ViewMode.FAVOURITE) {
			setItems(Skulls.getSkullManager().getSkulls(this.skullPlayer.getFavourites()));
		} else {
			setItems(Skulls.getSkullManager().getSkulls(this.category));
		}

		super.draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Skull skull) {
		final QuickItem item = QuickItem.of(skull.getItemStack())
				.name(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_NAME, "skull_name", skull.getName()));

		item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_ID, "skull_id", skull.getId()));
		item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_TAGS, "skull_tags", String.join(", ", skull.getTags())));

		if (Settings.CHARGE_FOR_HEADS.getBoolean() && skull.getPrice() > 0) {
			item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_PRICE, "skull_price", String.format("%,.2f", skull.getPrice())));
		}

		if (!skull.isBlocked() && this.skullPlayer.getFavourites().contains(skull.getId())) {
			item.lore(" ");
			item.lore(TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_FAVOURITED));
			item.lore(" ");
		} else {
			item.lore(" ");
		}

		item.lore(skull.isBlocked()
				? TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_BLOCKED)
				: TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_TAKE));

		if (!skull.isBlocked()) {
			item.lore(this.skullPlayer.getFavourites().contains(skull.getId())
					? TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_UN_FAVOURITE)
					: TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_FAVOURITE));
		}

		if (getPlayer().hasPermission("skulls.admin")) {
			item.lore(" ", TranslationManager.string(getPlayer(), Translations.GUI_SKULLS_LIST_ITEMS_SKULL_LORE_EDIT));
		}

		return MenuConfig.item(getPlayer(), "skulls-list.item", item.make(), Map.of(
				"skull_name", skull.getName() == null ? "" : skull.getName(),
				"skull_id", String.valueOf(skull.getId()),
				"skull_tags", String.join(", ", skull.getTags()),
				"skull_price", String.format("%,.2f", skull.getPrice()),
				"search_phrase", this.category,
				"category_name", this.category
		));
	}

	@Override
	protected void onClick(Skull skull, MenuClick click) {
		final Player player = click.player;

		if (click.clickType == ClickType.LEFT) {
			if (skull.isBlocked()) {
				if (!player.isOp() || !player.hasPermission("skulls.buyblocked")) return;
			}

			if (!Skulls.getPlayerManager().playerCanClaim(player)) {
				return;
			}

			if (Settings.ASK_FOR_BUY_CONFIRM.getBoolean()) {
				new ConfirmMenu(this, player, confirmed -> {
					if (confirmed) handleBuy(skull);
					SkullsListMenu.this.open();
				}).open();
				return;
			}

			handleBuy(skull);
		}

		if (click.clickType == ClickType.RIGHT) {
			if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean() && !player.hasPermission("skulls.favourite")) {
				return;
			}

			this.skullPlayer.toggleFavourite(skull.getId());
			this.skullPlayer.sync();
			refresh();
		}

		if (click.clickType == ClickType.NUMBER_KEY && player.hasPermission("skulls.admin")) {
			new SkullEditMenu(this, player, skull, getPage()).open();
		}
	}

	private void handleBuy(Skull skull) {
		final Player player = getPlayer();

		if (!Settings.CHARGE_FOR_HEADS.getBoolean()) {
			PlayerUtil.giveItem(player, skull.getItemStack());
			return;
		}

		final double price = player.hasPermission("skulls.freeskulls") ? 0 : skull.getPrice();

		if (price <= 0) {
			PlayerUtil.giveItem(player, skull.getItemStack());
			return;
		}

		if (!Skulls.getEconomyManager().has(player, price)) {
			Common.tell(player, TranslationManager.string(player, Translations.ERROR_NO_MONEY));
			return;
		}

		Skulls.getEconomyManager().withdraw(player, price);
		PlayerUtil.giveItem(player, skull.getItemStack());
		Common.tell(player, TranslationManager.string(player, Translations.PURCHASE_SUCCESS));
	}

	@Override
	protected void drawFixed() {
		applyBackExit();
	}
}
