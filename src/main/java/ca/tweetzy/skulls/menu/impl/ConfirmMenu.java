package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public final class ConfirmMenu extends Menu {

	private final Consumer<Boolean> confirmed;

	public ConfirmMenu(Menu parent, Player player, Consumer<Boolean> confirmed) {
		super(parent, player, "confirm", TranslationManager.string(player, Translations.GUI_CONFIRM_ACTION_TITLE), 3);
		this.confirmed = confirmed;
	}

	@Override
	protected void draw() {
		reset();

		final List<Integer> yesSlots = MenuConfig.slots("confirm.items.confirm", List.of(10, 11, 12));
		final List<Integer> noSlots = MenuConfig.slots("confirm.items.cancel", List.of(14, 15, 16));

		final ItemStack yesFallback = QuickItem.of(CompMaterial.LIME_STAINED_GLASS_PANE)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_CONFIRM_ACTION_ITEMS_YES_NAME))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_CONFIRM_ACTION_ITEMS_YES_LORE))
				.make();

		final ItemStack noFallback = QuickItem.of(CompMaterial.RED_STAINED_GLASS_PANE)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_CONFIRM_ACTION_ITEMS_NO_NAME))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_CONFIRM_ACTION_ITEMS_NO_LORE))
				.make();

		final ItemStack yesItem = MenuConfig.item(getPlayer(), "confirm.items.confirm", yesFallback);
		final ItemStack noItem = MenuConfig.item(getPlayer(), "confirm.items.cancel", noFallback);

		for (int slot : yesSlots) {
			setButton(slot, yesItem, click -> {
				click.close();
				this.confirmed.accept(true);
			});
		}

		for (int slot : noSlots) {
			setButton(slot, noItem, click -> {
				click.close();
				this.confirmed.accept(false);
			});
		}
	}
}
