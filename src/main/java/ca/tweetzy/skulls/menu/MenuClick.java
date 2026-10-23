package ca.tweetzy.skulls.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class MenuClick {

	public final Menu menu;
	public final Player player;
	public final int slot;
	public final ClickType clickType;
	public final InventoryClickEvent event;

	public MenuClick(Menu menu, Player player, int slot, ClickType clickType, InventoryClickEvent event) {
		this.menu = menu;
		this.player = player;
		this.slot = slot;
		this.clickType = clickType;
		this.event = event;
	}

	public void open(Menu menu) {
		if (menu != null) menu.open();
	}

	public void back() {
		if (this.menu.getParent() != null) this.menu.getParent().open();
		else this.menu.close();
	}

	public void close() {
		this.menu.close();
	}
}
