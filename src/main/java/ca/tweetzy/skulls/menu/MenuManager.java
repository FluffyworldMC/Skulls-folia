package ca.tweetzy.skulls.menu;

import ca.tweetzy.skulls.Skulls;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class MenuManager {

	private final Plugin plugin;
	private final Map<UUID, Menu> openMenus = new ConcurrentHashMap<>();
	private boolean initialized;

	public MenuManager(Plugin plugin) {
		this.plugin = plugin;
	}

	public void init() {
		if (this.initialized) return;

		Bukkit.getPluginManager().registerEvents(new MenuListener(this), this.plugin);
		this.initialized = true;
	}

	public void open(Menu menu) {
		runOnPlayer(menu.getPlayer(), () -> {
			final Player player = menu.getPlayer();
			if (!player.isOnline()) return;

			menu.drawMenu();
			this.openMenus.put(player.getUniqueId(), menu);
			player.openInventory(menu.getInventory());
		});
	}

	public void refresh(Menu menu) {
		runOnPlayer(menu.getPlayer(), () -> {
			final Player player = menu.getPlayer();
			if (!player.isOnline()) return;
			menu.drawMenu();
		});
	}

	public void close(Menu menu) {
		runOnPlayer(menu.getPlayer(), () -> {
			final Player player = menu.getPlayer();
			this.openMenus.remove(player.getUniqueId(), menu);

			if (isTopInventory(player, menu)) {
				player.closeInventory();
			}
		});
	}

	public void closeAll() {
		new ArrayList<>(this.openMenus.values()).forEach(this::close);
		this.openMenus.clear();
	}

	void click(Menu menu, Player player, int slot, ClickType clickType, InventoryClickEvent event) {
		final Consumer<MenuClick> action = menu.getAction(slot);
		if (action == null) return;

		runOnPlayer(player, () -> action.accept(new MenuClick(menu, player, slot, clickType, event)));
	}

	void handleClose(Menu menu) {
		this.openMenus.remove(menu.getPlayer().getUniqueId(), menu);
		menu.onClose();
	}

	public Menu getOpenMenu(Player player) {
		return this.openMenus.get(player.getUniqueId());
	}

	private boolean isTopInventory(Player player, Menu menu) {
		final Inventory top = player.getOpenInventory().getTopInventory();
		return top.getHolder() instanceof MenuHolder holder && holder.getMenu() == menu;
	}

	private void runOnPlayer(Player player, Runnable task) {
		final TaskScheduler scheduler = Skulls.getTaskScheduler();
		if (scheduler == null) return;

		if (scheduler.isEntityThread(player)) {
			task.run();
			return;
		}

		scheduler.runTask(player, task);
	}
}
