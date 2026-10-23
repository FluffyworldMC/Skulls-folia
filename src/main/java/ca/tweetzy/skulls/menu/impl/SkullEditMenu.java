package ca.tweetzy.skulls.menu.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.interfaces.Skull;
import ca.tweetzy.skulls.menu.Menu;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.menu.input.InputDialogs;
import ca.tweetzy.skulls.model.StringHelper;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.entity.Player;

public final class SkullEditMenu extends Menu {

	private final Skull skull;
	private final int page;

	public SkullEditMenu(Menu parent, Player player, Skull skull, int page) {
		super(parent, player, "edit", TranslationManager.string(player, Translations.GUI_EDIT_TITLE, "skull_id", skull.getId()), 6);
		this.skull = skull;
		this.page = page;
	}

	@Override
	protected void draw() {
		reset();

		final int previewSlot = MenuConfig.slot("edit.items.preview", 13);
		if (previewSlot >= 0) setItem(previewSlot, this.skull.getItemStack());

		final int nameSlot = MenuConfig.slot("edit.items.name", 28);
		if (nameSlot >= 0) {
			final ca.tweetzy.flight.utils.QuickItem nameFallback = QuickItem.of(CompMaterial.NAME_TAG)
					.name(TranslationManager.string(getPlayer(), Translations.GUI_EDIT_ITEMS_NAME_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_EDIT_ITEMS_NAME_LORE));

			setButton(nameSlot, MenuConfig.item(getPlayer(), "edit.items.name", nameFallback.make(), java.util.Map.of("skull_id", String.valueOf(this.skull.getId()))), click -> InputDialogs.text(click.player,
					TranslationManager.string(click.player, Translations.INPUT_SKULL_EDIT_TITLE),
					TranslationManager.string(click.player, Translations.INPUT_SKULL_EDIT_NAME),
					this.skull.getName() == null ? "" : this.skull.getName(), 32, input -> {
						final String name = input.trim();
						if (name.isEmpty()) return;

						this.skull.setName(name);
						this.skull.sync();
						open();
					}));
		}

		final int priceSlot = MenuConfig.slot("edit.items.price", 30);
		if (priceSlot >= 0) {
			final ca.tweetzy.flight.utils.QuickItem priceFallback = QuickItem.of(CompMaterial.GOLD_INGOT)
					.name(TranslationManager.string(getPlayer(), Translations.GUI_EDIT_ITEMS_PRICE_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_EDIT_ITEMS_PRICE_LORE));

			setButton(priceSlot, MenuConfig.item(getPlayer(), "edit.items.price", priceFallback.make(), java.util.Map.of("skull_id", String.valueOf(this.skull.getId()))), click -> InputDialogs.text(click.player,
					TranslationManager.string(click.player, Translations.INPUT_SKULL_EDIT_TITLE),
					TranslationManager.string(click.player, Translations.INPUT_SKULL_EDIT_PRICE),
					String.valueOf(this.skull.getPrice()), 32, input -> {
						final String price = input.trim();
						if (price.isEmpty()) return;

						if (!StringHelper.isDouble(price)) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.ERROR_NOT_A_NUMBER, "value", price));
							return;
						}

						this.skull.setPrice(Double.parseDouble(price));
						this.skull.sync();
						open();
					}));
		}

		final int blockedSlot = MenuConfig.slot("edit.items.blocked", 34);
		if (blockedSlot >= 0) {
			final ca.tweetzy.flight.utils.QuickItem blockedFallback = QuickItem.of(this.skull.isBlocked() ? CompMaterial.RED_STAINED_GLASS_PANE : CompMaterial.LIME_STAINED_GLASS_PANE)
					.name(TranslationManager.string(getPlayer(), Translations.GUI_EDIT_ITEMS_BLOCKED_NAME))
					.lore(TranslationManager.list(getPlayer(), Translations.GUI_EDIT_ITEMS_BLOCKED_LORE, "is_true", this.skull.isBlocked()
							? TranslationManager.string(getPlayer(), Translations.MISC_IS_TRUE)
							: TranslationManager.string(getPlayer(), Translations.MISC_IS_FALSE)));

			setButton(blockedSlot, MenuConfig.item(getPlayer(), "edit.items.blocked", blockedFallback.make(), java.util.Map.of(
					"skull_id", String.valueOf(this.skull.getId()),
					"is_true", this.skull.isBlocked()
							? TranslationManager.string(getPlayer(), Translations.MISC_IS_TRUE)
							: TranslationManager.string(getPlayer(), Translations.MISC_IS_FALSE)
			)), click -> {
				this.skull.setBlocked(!this.skull.isBlocked());
				this.skull.sync();
				refresh();
			});
		}

		applyBackExit();
	}
}
