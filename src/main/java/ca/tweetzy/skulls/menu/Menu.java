package ca.tweetzy.skulls.menu;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.menu.config.MenuConfig;
import ca.tweetzy.skulls.settings.Translations;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Base class for every menu in the plugin.
 *
 * <p>State is kept in {@link #items} and {@link #actions}; the Bukkit inventory is only
 * touched from the player's own thread by {@link MenuManager}.</p>
 */
public abstract class Menu {

	public static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
			.character('\u00a7')
			.hexColors()
			.useUnusualXRepeatedCharacterHexFormat()
			.build();

	private final Menu parent;
	private final Player player;
	private final String configKey;
	private final String title;
	private final int rows;
	private final Map<Integer, ItemStack> items = new HashMap<>();
	private final Map<Integer, Consumer<MenuClick>> actions = new HashMap<>();

	private Inventory inventory;
	private ItemStack defaultItem;

	protected Menu(Menu parent, Player player, String configKey, String title, int rows) {
		if (player == null) throw new IllegalArgumentException("player cannot be null");
		if (title == null) throw new IllegalArgumentException("title cannot be null");
		if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be between 1 and 6");

		this.parent = parent;
		this.player = player;
		this.configKey = configKey;
		this.title = PlaceholderAPIHook.tryReplace(player, MenuConfig.title(configKey + ".title", title));
		this.rows = MenuConfig.rows(configKey + ".rows", rows);

		final ItemStack fallback = QuickItem.of(CompMaterial.BLACK_STAINED_GLASS_PANE).name("").make();
		final ItemStack globalBackground = MenuConfig.item(player, "background", fallback);
		this.defaultItem = MenuConfig.hasSection(configKey + ".background")
				? MenuConfig.item(player, configKey + ".background", globalBackground)
				: globalBackground;
	}

	protected abstract void draw();

	protected void onClose() {
	}

	public final void open() {
		Skulls.getMenuManager().open(this);
	}

	public final void refresh() {
		Skulls.getMenuManager().refresh(this);
	}

	public final void close() {
		Skulls.getMenuManager().close(this);
	}

	final void drawMenu() {
		this.items.clear();
		this.actions.clear();
		draw();

		if (this.inventory == null) {
			this.inventory = Bukkit.createInventory(new MenuHolder(this), this.rows * 9, LEGACY.deserialize(Common.colorize(this.title)));
		}

		render();
	}

	final void render() {
		if (this.inventory == null) return;
		this.inventory.clear();
		this.items.forEach((slot, item) -> {
			if (item != null && slot >= 0 && slot < this.inventory.getSize()) {
				this.inventory.setItem(slot, item);
			}
		});
	}

	protected final void reset() {
		this.items.clear();
		this.actions.clear();
		if (this.defaultItem != null) fill(this.defaultItem);
	}

	public final void fill(ItemStack item) {
		if (item == null) return;
		for (int slot = 0; slot < this.rows * 9; slot++) {
			this.items.put(slot, item.clone());
		}
	}

	public final void setItem(int slot, ItemStack item) {
		if (slot < 0 || slot >= this.rows * 9) return;
		this.actions.remove(slot);
		this.items.put(slot, item);
	}

	public final void setButton(int slot, ItemStack item, Consumer<MenuClick> action) {
		if (slot < 0 || slot >= this.rows * 9) return;
		this.items.put(slot, item);
		if (action == null) this.actions.remove(slot);
		else this.actions.put(slot, action);
	}

	public final void setButton(int row, int column, ItemStack item, Consumer<MenuClick> action) {
		setButton(row * 9 + column, item, action);
	}

	public final void setItem(int row, int column, ItemStack item) {
		setItem(row * 9 + column, item);
	}

	public final ItemStack getItem(int slot) {
		return this.items.get(slot);
	}

	public final Consumer<MenuClick> getAction(int slot) {
		return this.actions.get(slot);
	}

	public final Player getPlayer() {
		return this.player;
	}

	public final Menu getParent() {
		return this.parent;
	}

	public final int getRows() {
		return this.rows;
	}

	public final String getTitle() {
		return this.title;
	}

	public final Inventory getInventory() {
		return this.inventory;
	}

	public final void setDefaultItem(ItemStack defaultItem) {
		this.defaultItem = defaultItem;
	}

	public final ItemStack getDefaultItem() {
		return this.defaultItem;
	}

	public final int getBackExitButtonSlot() {
		return this.rows * 9 - 5;
	}

	protected final void applyBackExit() {
		final int slot = this.parent != null
				? MenuConfig.slot("buttons.back", getBackExitButtonSlot())
				: MenuConfig.slot("buttons.exit", getBackExitButtonSlot());

		if (this.parent != null) {
			setButton(slot, getBackButton(), click -> this.parent.open());
		} else {
			setButton(slot, getExitButton(), click -> close());
		}
	}

	protected final ItemStack getBackButton() {
		final ItemStack fallback = QuickItem.of(CompMaterial.DARK_OAK_DOOR)
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_LORE))
				.make();

		return MenuConfig.item(this.player, "buttons.back", fallback);
	}

	protected final ItemStack getExitButton() {
		final ItemStack fallback = QuickItem.of(CompMaterial.BARRIER)
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_LORE))
				.make();

		return MenuConfig.item(this.player, "buttons.exit", fallback);
	}

	protected final ItemStack getPreviousButton() {
		final ItemStack fallback = QuickItem.of(CompMaterial.ARROW)
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE))
				.make();

		return MenuConfig.item(this.player, "buttons.previous", fallback);
	}

	protected final ItemStack getNextButton() {
		final ItemStack fallback = QuickItem.of(CompMaterial.ARROW)
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_LORE))
				.make();

		return MenuConfig.item(this.player, "buttons.next", fallback);
	}
}
