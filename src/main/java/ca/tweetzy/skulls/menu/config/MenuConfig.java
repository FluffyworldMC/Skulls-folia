package ca.tweetzy.skulls.menu.config;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.hooks.PlaceholderAPIHook;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.skulls.model.SkullItem;
import ca.tweetzy.skulls.utils.SkullItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Loads menus.yml and lets every menu item be customised without touching the source.
 */
public final class MenuConfig {

	private static Plugin plugin;
	private static YamlConfiguration config = new YamlConfiguration();

	private MenuConfig() {
	}

	public static void load(Plugin plugin) {
		MenuConfig.plugin = plugin;

		final File file = new File(plugin.getDataFolder(), "menus.yml");
		if (!file.exists()) {
			plugin.saveResource("menus.yml", false);
		}

		config = YamlConfiguration.loadConfiguration(file);
	}

	public static void reload() {
		if (plugin != null) load(plugin);
	}

	public static String title(String path, String fallback) {
		return config.isString(path) ? config.getString(path) : fallback;
	}

	public static boolean hasSection(String path) {
		return config.isConfigurationSection(path);
	}

	public static int rows(String path, int fallback) {
		final int rows = config.getInt(path, fallback);
		return Math.max(1, Math.min(6, rows));
	}

	public static boolean enabled(String path, boolean fallback) {
		return config.getBoolean(path + ".enabled", fallback);
	}

	public static int slot(String path, int fallback) {
		if (config.isInt(path + ".slot")) return config.getInt(path + ".slot");

		if (config.isList(path + ".slots")) {
			final List<Integer> slots = config.getIntegerList(path + ".slots");
			if (!slots.isEmpty()) return slots.get(0);
		}

		return fallback;
	}

	public static List<Integer> slots(String path, List<Integer> fallback) {
		if (config.isList(path + ".slots")) {
			return config.getIntegerList(path + ".slots");
		}

		if (config.isInt(path + ".slot")) {
			return Collections.singletonList(config.getInt(path + ".slot"));
		}

		return fallback;
	}

	public static int categorySlot(String categoryId, int fallback) {
		return config.getInt("main.category-slots." + categoryKey(categoryId), fallback);
	}

	private static String categoryKey(String categoryId) {
		return categoryId.toLowerCase().replace("&", "and").replace(" ", "-");
	}

	public static ItemStack item(Player player, String path, ItemStack fallback) {
		return item(player, path, fallback, Collections.emptyMap());
	}

	public static ItemStack item(Player player, String path, ItemStack fallback, Map<String, String> placeholders) {
		final ConfigurationSection section = config.getConfigurationSection(path);
		if (section == null) return fallback;

		ItemStack item = fallback == null ? new ItemStack(Material.STONE) : fallback.clone();

		if (section.getBoolean("player_head", false) && player != null) {
			item = SkullItems.playerHead(player);
		} else if (section.isString("texture")) {
			item = textured(section.getString("texture"));
		} else if (section.isString("material")) {
			final ItemStack materialItem = material(section.getString("material"));
			if (materialItem != null) item = materialItem;
		}

		item.setAmount(Math.max(1, Math.min(64, section.getInt("amount", item.getAmount()))));

		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return item;

		boolean changed = false;

		if (section.isString("display_name")) {
			meta.setDisplayName(color(placeholder(player, replace(section.getString("display_name"), placeholders))));
			changed = true;
		}

		if (section.isList("lore")) {
			final List<String> lore = new ArrayList<>();
			for (Object raw : section.getList("lore")) {
				final String line = raw == null ? "" : String.valueOf(raw);
				lore.add(color(placeholder(player, replace(line, placeholders))));
			}
			meta.setLore(lore);
			changed = true;
		} else if (section.isString("lore")) {
			meta.setLore(Collections.singletonList(color(placeholder(player, replace(section.getString("lore"), placeholders)))));
			changed = true;
		}

		if (section.isString("item_model") && !section.getString("item_model").isBlank()) {
			final String raw = section.getString("item_model").trim();
			NamespacedKey key = null;

			try {
				key = NamespacedKey.fromString(raw);
				if (key == null && !raw.contains(":")) {
					key = NamespacedKey.minecraft(raw.toLowerCase(java.util.Locale.ROOT));
				}
			} catch (IllegalArgumentException ignored) {
			}

			if (key != null) {
				meta.setItemModel(key);
				changed = true;
			}
		}

		if (section.contains("custom_model_data")) {
			try {
				meta.setCustomModelData(Integer.parseInt(String.valueOf(section.get("custom_model_data")).trim()));
				changed = true;
			} catch (NumberFormatException ignored) {
			}
		}

		if (section.getBoolean("glow", false)) {
			meta.addEnchant(Enchantment.UNBREAKING, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			changed = true;
		}

		if (changed) item.setItemMeta(meta);
		return item;
	}

	private static ItemStack material(String name) {
		if (name == null || name.isBlank()) return null;

		final CompMaterial compMaterial = CompMaterial.matchCompMaterial(name).orElse(null);
		if (compMaterial != null) return new ItemStack(compMaterial.parseMaterial());

		final Material material = Material.matchMaterial(name);
		return material == null ? null : new ItemStack(material);
	}

	private static ItemStack textured(String texture) {
		if (texture == null || texture.isBlank()) return new ItemStack(Material.PLAYER_HEAD);

		if (texture.startsWith("skulls:")) {
			return SkullItem.get(texture);
		}

		if (texture.contains("textures.minecraft.net")) {
			return QuickItem.of(texture).make();
		}

		return QuickItem.of(texture).make();
	}

	private static String replace(String input, Map<String, String> placeholders) {
		if (input == null) return "";
		if (placeholders == null || placeholders.isEmpty()) return input;

		String result = input;
		for (Map.Entry<String, String> entry : placeholders.entrySet()) {
			result = result.replace("%" + entry.getKey() + "%", entry.getValue() == null ? "" : entry.getValue());
		}
		return result;
	}

	private static String placeholder(Player player, String input) {
		if (player == null || input == null) return input;
		return PlaceholderAPIHook.tryReplace(player, input);
	}

	private static String color(String input) {
		return Common.colorize(input);
	}
}
