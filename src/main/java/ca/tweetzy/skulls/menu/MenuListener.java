package ca.tweetzy.skulls.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public final class MenuListener implements Listener {

	private final MenuManager manager;

	public MenuListener(MenuManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(InventoryClickEvent event) {
		final Inventory top = event.getView().getTopInventory();
		if (!(top.getHolder() instanceof MenuHolder holder)) return;

		event.setCancelled(true);

		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (event.getClickedInventory() == null) return;
		if (!event.getClickedInventory().equals(top)) return;

		final Menu menu = holder.getMenu();
		if (!menu.getPlayer().getUniqueId().equals(player.getUniqueId())) return;

		this.manager.click(menu, player, event.getRawSlot(), event.getClick(), event);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDrag(InventoryDragEvent event) {
		final Inventory top = event.getView().getTopInventory();
		if (!(top.getHolder() instanceof MenuHolder)) return;

		for (int slot : event.getRawSlots()) {
			if (slot < top.getSize()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!holder.getMenu().getPlayer().getUniqueId().equals(player.getUniqueId())) return;

		this.manager.handleClose(holder.getMenu());
	}
}
