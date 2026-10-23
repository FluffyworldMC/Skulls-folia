package ca.tweetzy.skulls.flight.comp.enums;

import org.bukkit.Material;

import java.util.Locale;

/**
 * Compatibility replacement for Flight's CompMaterial$Data.
 *
 * <p>The original class parses the server version with a regex that only understands
 * versions like {@code 1.21}. Canvas/Paper 26.x reports {@code 26.2}, which made the
 * original static initializer throw. The accessors below keep the same contract but
 * are version independent.</p>
 */
@SuppressWarnings("ALL")
final class CompMaterial$Data {

	private CompMaterial$Data() {
	}

	// ISFLAT
	static boolean access$000() {
		return true;
	}

	// getExactMaterial
	static Material access$100(String name) {
		if (name == null || name.isBlank()) return null;

		final Material material = Material.matchMaterial(name);
		if (material != null) return material;

		try {
			return Material.valueOf(name.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	// VERSION
	static int access$200() {
		return 21;
	}
}
