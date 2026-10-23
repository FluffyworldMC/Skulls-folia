package ca.tweetzy.skulls.utils;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * Paper-native skull helpers.
 *
 * <p>Replaces Flight's XSkull/Profileable usage, which depends on
 * GameProfile#getProperties() and breaks on newer authlib versions.</p>
 */
public final class SkullItems {

	private SkullItems() {
	}

	public static ItemStack applyTexture(ItemStack item, String textureUrl) {
		if (item == null || textureUrl == null || textureUrl.isBlank()) return item;
		if (!(item.getItemMeta() instanceof SkullMeta meta)) return item;

		final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
		final String json = "{\"textures\":{\"SKIN\":{\"url\":\"" + textureUrl + "\"}}}";
		final String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
		profile.setProperty(new ProfileProperty("textures", base64));

		meta.setPlayerProfile(profile);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack applyTexture(String textureUrl) {
		return applyTexture(new ItemStack(Material.PLAYER_HEAD), textureUrl);
	}

	public static ItemStack applyPlayer(ItemStack item, OfflinePlayer player) {
		if (item == null || player == null) return item;
		if (!(item.getItemMeta() instanceof SkullMeta meta)) return item;

		meta.setOwningPlayer(player);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack playerHead(OfflinePlayer player) {
		return applyPlayer(new ItemStack(Material.PLAYER_HEAD), player);
	}
}
