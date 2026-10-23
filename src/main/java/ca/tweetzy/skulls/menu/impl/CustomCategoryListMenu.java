package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.enums.ViewMode;
import ca.tweetzy.skulls.api.interfaces.Category;
import ca.tweetzy.skulls.impl.SkullCategory;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.MenuClick;
import ca.tweetzy.skulls.menu.PagedMenu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.menu.input.InputDialogs;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;

public final class CustomCategoryListMenu extends PagedMenu<Category> {

	public CustomCategoryListMenu(Player viewer, Menu parent) {
		super(parent, viewer, "custom-category-list", TranslationManager.string(viewer, Translations.GUI_CUSTOM_CATEGORY_LIST_TITLE), 6);
	}

	@Override
	protected void draw() {
		setItems(Skulls.getCategoryManager().getCustomCategories());
		super.draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		final ItemStack fallback = QuickItem.of(CompMaterial.WRITTEN_BOOK)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_CATEGORY_NAME, "category_name", category.getName()))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_CATEGORY_LORE, "category_size", category.getSkulls().size()))
				.hideTags(true)
				.make();

		return MenuConfig.item(getPlayer(), "custom-category-list.item", fallback, Map.of(
				"category_name", category.getName(),
				"category_size", String.valueOf(category.getSkulls().size())
		));
	}

	@Override
	protected void drawFixed() {
		applyBackExit();

		if (!getPlayer().hasPermission("skulls.admin")) return;

		final int newSlot = MenuConfig.slot("custom-category-list.new-category", 40);
		if (!MenuConfig.enabled("custom-category-list.new-category", true) || newSlot < 0) return;

		final ItemStack fallback = QuickItem.of(CompMaterial.SLIME_BALL)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_NAME))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_LORE))
				.make();

		setButton(newSlot, MenuConfig.item(getPlayer(), "custom-category-list.new-category", fallback), click -> InputDialogs.text(click.player,
				TranslationManager.string(click.player, Translations.INPUT_CATEGORY_CREATE_TITLE),
				TranslationManager.string(click.player, Translations.INPUT_CATEGORY_CREATE_SUBTITLE),
				"", 32, input -> {
			final String name = ChatColor.stripColor(input).trim();

			if (name.isEmpty()) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_EMPTY));
				return;
			}

			if (Skulls.getCategoryManager().findCategory(name) != null) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.ID_TAKEN));
				return;
			}

			final Category toCreate = new SkullCategory(name.toLowerCase(), name, true, Collections.emptyList());

			Skulls.getDataManager().insertCategory(toCreate, (error, created) -> {
				if (error != null) {
					Skulls.getTaskScheduler().runTask(click.player, () ->
							Common.tell(click.player, TranslationManager.string(click.player, Translations.GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_ERROR)));
					return;
				}

				Skulls.getCategoryManager().addCategory(created);
				Skulls.getTaskScheduler().runTask(click.player, CustomCategoryListMenu.this::open);
			});
		}));
	}

	@Override
	protected void onClick(Category category, MenuClick click) {
		if (!Settings.GENERAL_USAGE_REQUIRES_NO_PERM.getBoolean()) {
			if (!click.player.hasPermission("skulls.customcategory." + category.getId().toLowerCase())) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.CATEGORY_PERMISSION));
				return;
			}
		}

		new SkullsListMenu(this, click.player, Skulls.getPlayerManager().findOrCreate(click.player), category.getId(), ViewMode.LIST).open();
	}
}
