package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.interfaces.Category;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.MenuClick;
import ca.tweetzy.skulls.menu.PagedMenu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public final class CategorySelectMenu extends PagedMenu<Category> {

	private final Consumer<Category> selected;

	public CategorySelectMenu(Menu parent, Player player, Consumer<Category> selected) {
		super(parent, player, "category-selector", TranslationManager.string(player, Translations.GUI_CUSTOM_CATEGORY_SELECTOR_TITLE), 6);
		this.selected = selected;
	}

	@Override
	protected void draw() {
		setItems(Skulls.getCategoryManager().getCustomCategories());
		super.draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		final ItemStack fallback = QuickItem.of(CompMaterial.WRITTEN_BOOK)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_SELECTOR_ITEMS_CATEGORY_NAME, "category_name", category.getName()))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_CUSTOM_CATEGORY_SELECTOR_ITEMS_CATEGORY_LORE, "category_size", category.getSkulls().size()))
				.hideTags(true)
				.make();

		return MenuConfig.item(getPlayer(), "category-selector.item", fallback, Map.of(
				"category_name", category.getName(),
				"category_size", String.valueOf(category.getSkulls().size())
		));
	}

	@Override
	protected void onClick(Category category, MenuClick click) {
		click.close();
		this.selected.accept(category);
	}
}
