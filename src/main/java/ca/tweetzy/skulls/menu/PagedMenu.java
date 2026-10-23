package ca.tweetzy.skulls.menu;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Translations;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PagedMenu<T> extends Menu {

	private List<T> items = new ArrayList<>();
	private int page = 1;
	private int pages = 1;
	private boolean loading;

	protected PagedMenu(Menu parent, Player player, String configKey, String title, int rows) {
		super(parent, player, configKey, title, rows);
	}

	protected final void setItems(Collection<T> items) {
		this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
		updatePages();
	}

	protected final List<T> getItems() {
		return this.items;
	}

	protected final void setLoading(boolean loading) {
		this.loading = loading;
	}

	protected final boolean isLoading() {
		return this.loading;
	}

	protected final int getPage() {
		return this.page;
	}

	protected final int getPages() {
		return this.pages;
	}

	protected List<Integer> contentSlots() {
		final List<Integer> slots = new ArrayList<>();
		final int size = Math.max(0, (getRows() - 1) * 9);
		for (int slot = 0; slot < size; slot++) slots.add(slot);
		return slots;
	}

	protected int getPreviousButtonSlot() {
		return MenuConfig.slot("buttons.previous", 48);
	}

	protected int getNextButtonSlot() {
		return MenuConfig.slot("buttons.next", 50);
	}

	@Override
	protected void draw() {
		reset();

		if (this.loading) {
			contentSlots().forEach(slot -> setItem(slot, getLoadingItem()));
		} else {
			drawPage();
		}

		drawFixed();
	}

	private void drawPage() {
		final List<Integer> slots = contentSlots();
		updatePages();

		if (slots.isEmpty()) return;

		final int start = (this.page - 1) * slots.size();
		final int end = Math.min(start + slots.size(), this.items.size());

		for (int index = start; index < end; index++) {
			final T item = this.items.get(index);
			final int slot = slots.get(index - start);
			setButton(slot, makeDisplayItem(item), click -> onClick(item, click));
		}

		if (this.pages > 1) {
			setButton(getPreviousButtonSlot(), getPreviousButton(), click -> {
				if (this.page > 1) {
					this.page--;
					refresh();
				}
			});

			setButton(getNextButtonSlot(), getNextButton(), click -> {
				if (this.page < this.pages) {
					this.page++;
					refresh();
				}
			});
		}
	}

	protected void drawFixed() {
		applyBackExit();
	}

	private void updatePages() {
		final int perPage = Math.max(1, contentSlots().size());
		this.pages = Math.max(1, (int) Math.ceil(this.items.size() / (double) perPage));

		if (this.page < 1) this.page = 1;
		if (this.page > this.pages) this.page = this.pages;
	}

	private ItemStack getLoadingItem() {
		final ItemStack fallback = QuickItem.of(CompMaterial.CLOCK)
				.name(TranslationManager.string(getPlayer(), Translations.GUI_LOADING_NAME))
				.lore(TranslationManager.list(getPlayer(), Translations.GUI_LOADING_LORE))
				.make();

		return MenuConfig.item(getPlayer(), "loading", fallback);
	}

	protected abstract ItemStack makeDisplayItem(T item);

	protected abstract void onClick(T item, MenuClick click);
}
