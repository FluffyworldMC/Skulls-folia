package ca.tweetzy.skulls.flight.comp.enums;

/**
 * Compatibility replacement for Flight's ServerVersion enum.
 *
 * <p>Flight parses the server version string and compares it against hard-coded
 * versions up to 1.23. Canvas 26.x is reported as {@code 26.2...}, so Flight falls
 * back to UNKNOWN and wrongly treats the server as pre-1.13. This replacement
 * reports a modern version so the modern item/data paths are used.</p>
 */
public enum ServerVersion {

	UNKNOWN("Unknown"),
	V1_7("1.7"),
	V1_8("1.8"),
	V1_9("1.9"),
	V1_10("1.10"),
	V1_11("1.11"),
	V1_12("1.12"),
	V1_13("1.13"),
	V1_14("1.14"),
	V1_15("1.15"),
	V1_16("1.16"),
	V1_17("1.17"),
	V1_18("1.18"),
	V1_19("1.19"),
	V1_20("1.20"),
	V1_21("1.21"),
	V1_22("1.22"),
	V1_23("1.23");

	private final String versionName;

	ServerVersion(String versionName) {
		this.versionName = versionName;
	}

	public static ServerVersion getVersion() {
		return V1_21;
	}

	public boolean isLessThan(ServerVersion other) {
		return this.ordinal() < other.ordinal();
	}

	public static boolean isServerVersion(ServerVersion version) {
		return getVersion() == version;
	}

	public static boolean isServerVersion(ServerVersion... versions) {
		final ServerVersion current = getVersion();
		for (ServerVersion version : versions) {
			if (current == version) return true;
		}
		return false;
	}

	public static boolean isServerVersionAbove(ServerVersion version) {
		return getVersion().ordinal() > version.ordinal();
	}

	public static boolean isServerVersionAtLeast(ServerVersion version) {
		return getVersion().ordinal() >= version.ordinal();
	}

	public static boolean isServerVersionAtOrBelow(ServerVersion version) {
		return getVersion().ordinal() <= version.ordinal();
	}

	public static boolean isServerVersionBelow(ServerVersion version) {
		return getVersion().ordinal() < version.ordinal();
	}

	public String getVersionName() {
		return this.versionName;
	}
}
