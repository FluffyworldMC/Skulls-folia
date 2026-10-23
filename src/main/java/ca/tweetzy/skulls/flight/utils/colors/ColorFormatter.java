package ca.tweetzy.skulls.flight.utils.colors;

import net.md_5.bungee.api.ChatColor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compatibility replacement for Flight's ColorFormatter.
 *
 * <p>The original implementation parses {@code Bukkit.getVersion()} and crashes on
 * version strings such as {@code 26.2} (Canvas/Paper 26.x). This version keeps the
 * same public API but does not depend on the server version.</p>
 */
public final class ColorFormatter {

	private static final Pattern GRADIENT_PATTERN = Pattern.compile("<GRADIENT:([0-9A-Fa-f]{6})>(.*?)</GRADIENT:([0-9A-Fa-f]{6})>", Pattern.DOTALL);
	private static final Pattern HEX_PATTERN = Pattern.compile("<#([0-9A-Fa-f]{6})>");
	private static final Pattern CENTER_PATTERN = Pattern.compile("<center>", Pattern.CASE_INSENSITIVE);
	private static final Pattern TAG_PATTERN = Pattern.compile("<[^>]+>");
	private static final Pattern CODE_PATTERN = Pattern.compile("[&?][0-9A-FK-ORa-fk-or]");

	public ColorFormatter() {
	}

	public static String process(String input) {
		if (input == null) return null;

		String result = processGradients(input);
		result = HEX_PATTERN.matcher(result).replaceAll(match -> Matcher.quoteReplacement(legacyColor(new Color(Integer.parseInt(match.group(1), 16)))));
		result = CENTER_PATTERN.matcher(result).replaceAll(" ");
		result = TAG_PATTERN.matcher(result).replaceAll("");
		return ChatColor.translateAlternateColorCodes('&', result);
	}

	public static List<String> process(List<String> input) {
		final List<String> result = new ArrayList<>();
		if (input == null) return result;

		for (String line : input) {
			result.add(process(line));
		}

		return result;
	}

	public static String color(String input, Color color) {
		return legacyColor(color) + process(input);
	}

	public static String color(String input, Color start, Color end) {
		return applyGradient(process(input), start, end);
	}

	public static String rainbow(String input, float saturation) {
		final String base = process(input);
		final int length = ChatColor.stripColor(base).length();
		if (length == 0) return base;

		final StringBuilder result = new StringBuilder();
		int visible = 0;

		for (int i = 0; i < base.length(); i++) {
			final char character = base.charAt(i);
			if (character == '\u00a7' && i + 1 < base.length()) {
				result.append(character).append(base.charAt(++i));
				continue;
			}

			final Color color = Color.getHSBColor(visible / (float) length, saturation, 1.0f);
			result.append(legacyColor(color)).append(character);
			visible++;
		}

		return result.toString();
	}

	public static ChatColor getColor(String input) {
		if (input == null || input.isBlank()) return ChatColor.WHITE;

		String value = input.trim();

		if (value.startsWith("#")) {
			value = value.substring(1);
		} else if (value.length() == 1) {
			final ChatColor legacy = ChatColor.getByChar(value.charAt(0));
			if (legacy != null) return legacy;
		}

		try {
			return ChatColor.of(new Color(Integer.parseInt(value, 16)));
		} catch (NumberFormatException exception) {
			return ChatColor.WHITE;
		}
	}

	public static String stripColorFormatting(String input) {
		if (input == null) return null;

		final String withoutTags = TAG_PATTERN.matcher(input).replaceAll("");
		final String withoutCodes = CODE_PATTERN.matcher(withoutTags).replaceAll("");
		return ChatColor.stripColor(withoutCodes);
	}

	private static String processGradients(String input) {
		final Matcher matcher = GRADIENT_PATTERN.matcher(input);
		final StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			final Color start = parseColor(matcher.group(1));
			final Color end = parseColor(matcher.group(3));
			matcher.appendReplacement(buffer, Matcher.quoteReplacement(applyGradient(matcher.group(2), start, end)));
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String applyGradient(String input, Color start, Color end) {
		if (input == null || input.isEmpty()) return "";

		final String stripped = TAG_PATTERN.matcher(input).replaceAll("");
		final String plain = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', stripped));
		if (plain.isEmpty()) return "";

		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < plain.length(); i++) {
			final float ratio = plain.length() == 1 ? 0f : i / (float) (plain.length() - 1);
			result.append(legacyColor(interpolate(start, end, ratio))).append(plain.charAt(i));
		}

		return result.toString();
	}

	private static Color interpolate(Color start, Color end, float ratio) {
		final int red = Math.round(start.getRed() + (end.getRed() - start.getRed()) * ratio);
		final int green = Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
		final int blue = Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
		return new Color(clamp(red), clamp(green), clamp(blue));
	}

	private static int clamp(int value) {
		return Math.max(0, Math.min(255, value));
	}

	private static String legacyColor(Color color) {
		final String hex = String.format(Locale.ROOT, "%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
		final StringBuilder result = new StringBuilder("\u00a7x");

		for (char character : hex.toCharArray()) {
			result.append('\u00a7').append(character);
		}

		return result.toString();
	}

	private static Color parseColor(String hex) {
		return new Color(Integer.parseInt(hex, 16));
	}
}
