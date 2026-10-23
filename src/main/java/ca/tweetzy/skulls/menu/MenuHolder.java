package ca.tweetzy.skulls.menu;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class MenuHolder implements InventoryHolder {

	private final Menu menu;

	public MenuHolder(Menu menu) {
		this.menu = menu;
	}

	public Menu getMenu() {
		return this.menu;
	}

	@Override
	public Inventory getInventory() {
		return this.menu.getInventory();
	}
}
