package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.enums.BaseCategory;
import ca.tweetzy.skulls.api.enums.ViewMode;
import ca.tweetzy.skulls.api.interfaces.Skull;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.menu.input.InputDialogs;
import ca.tweetzy.skulls.model.SkullItem;
import ca.tweetzy.skulls.model.StringHelper;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import ca.tweetzy.skulls.utils.SkullItems;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class MainMenu extends Menu {

	public MainMenu(@NonNull final Player player) {
		super(null, player, "main", TranslationManager.string(player, Translations.GUI_MAIN_TITLE), 6);
	}

	@Override
	protected void draw() {
		reset();

		for (BaseCategory baseCategory : BaseCategory.values()) {
			if (!baseCategory.isEnabled()) continue;

			final int slot = MenuConfig.categorySlot(baseCategory.getId(), baseCategory.getSlot());
			if (slot < 0) continue;

			final long categoryCount = Skulls.getSkullManager().getSkullCount(baseCategory.getId());

			final ItemStack fallback = QuickItem
					.of(Skulls.getSkullManager().getSkullItem(baseCategory.getTexture()))
					.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_CATEGORY_NAME, "category_name", baseCategory.getName()))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_CATEGORY_LORE, "category_size", categoryCount))
					.make();

			final ItemStack item = MenuConfig.item(getPlayer(), "main.items.category", fallback, Map.of(
					"category_name", baseCategory.getName(),
					"category_size", String.valueOf(categoryCount)
			));

			setButton(slot, item, click -> {

				if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean()) {
					if (!click.player.hasPermission("skulls.category." + baseCategory.getId().toLowerCase().replace(" ", "").replace("&", ""))) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.CATEGORY_PERMISSION));
						return;
					}
				}

				new SkullsListMenu(this, click.player, Skulls.getPlayerManager().findOrCreate(click.player), baseCategory.getId(), ViewMode.LIST).open();
			});
		}

		final int searchSlot = MenuConfig.slot("main.items.search", 41);
		if (MenuConfig.enabled("main.items.search", true) && searchSlot >= 0) {
			final ItemStack fallback = QuickItem.of(SkullItem.get("skulls:5650"))
					.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_SEARCH_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_SEARCH_LORE))
					.make();

			setButton(searchSlot, MenuConfig.item(getPlayer(), "main.items.search", fallback), click -> {

				if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean()) {
					if (!click.player.hasPermission("skulls.search")) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.ERROR_NO_PERMISSION));
						return;
					}
				}

				InputDialogs.text(click.player,
						TranslationManager.string(click.player, Translations.INPUT_SKULL_SEARCH_TITLE),
						TranslationManager.string(click.player, Translations.INPUT_SKULL_SEARCH_SUBTITLE),
						"", 100, input -> {
							if (input.isBlank()) return;
							final String query = StringHelper.escapeRegex(input).trim();
							new SkullsListMenu(MainMenu.this, click.player, Skulls.getPlayerManager().findOrCreate(click.player), query, ViewMode.SEARCH).open();
						});
			});
		}

		final int customCategoriesSlot = MenuConfig.slot("main.items.custom-categories", 38);
		if (MenuConfig.enabled("main.items.custom-categories", true) && customCategoriesSlot >= 0) {
			final ItemStack fallback = QuickItem.of(SkullItem.get("skulls:25001"))
					.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_CUSTOM_CATEGORIES_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_CUSTOM_CATEGORIES_LORE))
					.make();

			setButton(customCategoriesSlot, MenuConfig.item(getPlayer(), "main.items.custom-categories", fallback),
					click -> new CustomCategoryListMenu(click.player, this).open());
		}

		if (Settings.CATEGORIES_PLAYER_HEADS_ENABLED.getBoolean()) {
			final int playerHeadsSlot = MenuConfig.slot("main.items.player-heads", 39);
			if (MenuConfig.enabled("main.items.player-heads", true) && playerHeadsSlot >= 0) {
				final int totalPlayers = Bukkit.getOfflinePlayers().length;
				final ItemStack fallback = SkullItems.applyPlayer(QuickItem.of(CompMaterial.PLAYER_HEAD)
						.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_PLAYERS_NAME))
						.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_PLAYERS_LORE, "category_size", totalPlayers))
						.make(), getPlayer());

				final ItemStack item = MenuConfig.item(getPlayer(), "main.items.player-heads", fallback, Map.of(
						"category_size", String.valueOf(totalPlayers)
				));

				setButton(playerHeadsSlot, item, click -> {

					if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean()) {
						if (!click.player.hasPermission("skulls.category.playerheads")) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.ERROR_NO_PERMISSION));
							return;
						}
					}

					new PlayerHeadsMenu(this, click.player).open();
				});
			}
		}

		if (Settings.RANDOM_HEAD_BUTTON_ENABLED.getBoolean()) {
			final int randomHeadSlot = MenuConfig.slot("main.items.random-head", 40);
			if (MenuConfig.enabled("main.items.random-head", true) && randomHeadSlot >= 0) {
				final String priceText = String.format("%,.2f", Settings.RANDOM_HEAD_BUTTON_PRICE.getDouble());
				final ItemStack fallback = QuickItem.of(SkullItem.get("skulls:5171"))
						.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_RANDOM_HEAD_NAME))
						.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_RANDOM_HEAD_LORE, "price", priceText))
						.make();

				final ItemStack item = MenuConfig.item(getPlayer(), "main.items.random-head", fallback, Map.of("price", priceText));

				setButton(randomHeadSlot, item, click -> {

					if (!Skulls.getPlayerManager().playerCanClaim(click.player)) {
						return;
					}

					final double price = click.player.hasPermission("skulls.freeskulls") ? 0 : Settings.RANDOM_HEAD_BUTTON_PRICE.getDouble();
					final Skull skull;

					try {
						skull = Skulls.getSkullManager().getRandomAllowedSkull(click.player);
					} catch (IllegalArgumentException exception) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.SKULL_NOT_FOUND));
						return;
					}

					if (price <= 0) {
						click.player.getInventory().addItem(skull.getItemStack());
						Common.tell(click.player, TranslationManager.string(click.player, Translations.RECEIVED_RANDOM_SKULL, "skull_name", skull.getName()));
						return;
					}

					if (!Skulls.getEconomyManager().has(click.player, price)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.ERROR_NO_MONEY));
						return;
					}

					Skulls.getEconomyManager().withdraw(click.player, price);
					click.player.getInventory().addItem(skull.getItemStack());
					Common.tell(click.player, TranslationManager.string(click.player, Translations.RECEIVED_RANDOM_SKULL, "skull_name", skull.getName()));
				});
			}
		}

		final int favouritesSlot = MenuConfig.slot("main.items.favourites", 42);
		if (MenuConfig.enabled("main.items.favourites", true) && favouritesSlot >= 0) {
			final ItemStack fallback = QuickItem.of(SkullItem.get("skulls:39696"))
					.name(TranslationManager.string(getPlayer(), Translations.GUI_MAIN_ITEMS_FAVOURITES_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_MAIN_ITEMS_FAVOURITES_LORE))
					.make();

			setButton(favouritesSlot, MenuConfig.item(getPlayer(), "main.items.favourites", fallback), click -> {

				if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean()) {
					if (!click.player.hasPermission("skulls.favourites")) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.ERROR_NO_PERMISSION));
						return;
					}
				}

				new SkullsListMenu(this, click.player, Skulls.getPlayerManager().findOrCreate(click.player), "", ViewMode.FAVOURITE).open();
			});
		}
	}
}
