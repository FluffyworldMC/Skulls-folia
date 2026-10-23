/*
 * Skulls
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.skulls.settings;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.skulls.Skulls;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Date Created: April 04 2022
 * Time Created: 9:22 p.m.
 *
 * @author Kiran Hart
 */
public final class Settings extends FlightSettings {

	public static final ConfigEntry LANG = create("language", "th_th").withComment("ภาษาหลัก (th_th / en_us)");
	public static final ConfigEntry PREFIX = create("prefix", "<GRADIENT:DD5E89>&lSkulls</GRADIENT:fbc7d4>&r &8»").withComment("คำนำหน้าข้อความในแชท");
	public static final ConfigEntry CHARGE_FOR_HEADS = create("charge-for-heads", true).withComment("เก็บเงินเมื่อผู้เล่นซื้อหัว");
	public static final ConfigEntry SKULLS_DATA_SOURCE_URL = create("data-url", "https://raw.githubusercontent.com/Tweetzy/Data-Files/main/Skulls/skulls.json").withComment("URL ฐานข้อมูลหัว อย่าแก้ไขหากไม่แน่ใจ");
	public static final ConfigEntry DATABASE_TYPE = create("database.type", "sqlite", "sqlite หรือ mariadb");
	public static final ConfigEntry DATABASE_HOST = create("database.mariadb.host", "127.0.0.1");
	public static final ConfigEntry DATABASE_PORT = create("database.mariadb.port", 3306);
	public static final ConfigEntry DATABASE_NAME = create("database.mariadb.database", "skulls");
	public static final ConfigEntry DATABASE_USER = create("database.mariadb.username", "root");
	public static final ConfigEntry DATABASE_PASSWORD = create("database.mariadb.password", "");
	public static final ConfigEntry DATABASE_SSL = create("database.mariadb.use-ssl", false);
	public static final ConfigEntry DATABASE_POOL_SIZE = create("database.mariadb.pool-size", 5);
	public static final ConfigEntry DATABASE_CONNECTION_TIMEOUT = create("database.mariadb.connection-timeout", 10000);
	public static final ConfigEntry DATABASE_MAX_LIFETIME = create("database.mariadb.max-lifetime", 1800000);
	public static final ConfigEntry DATABASE_SQLITE_BUSY_TIMEOUT = create("database.sqlite.busy-timeout", 10000);
	public static final ConfigEntry ECONOMY = create("economy", "Vault").withComment("ระบบเศรษฐกิจ: Vault หรือ Item");
	public static final ConfigEntry ITEM_ECONOMY_ITEM = create("item-economy-item", CompMaterial.GOLD_INGOT.name());
	public static final ConfigEntry MAIN_MENU_REQUIRES_NO_PERM = create("main-menu-requires-no-permission", true);
	public static final ConfigEntry GENERAL_USAGE_REQUIRES_NO_PERM = create("general-usage-requires-no-permission", false, "ถ้า true ผู้เล่นทั่วไปไม่ต้องมีสิทธิ์ใช้งานเมนู");
	public static final ConfigEntry TELL_OP_PATREON_LINK = create("tell-ops-patron-on-join", true);
	public static final ConfigEntry SKULL_TRACKING = create("track-skull-placement", true, "ถ้าปิด หัวจะไม่ดรอปในโหมดสร้างสรรค์");
	public static final ConfigEntry RANDOM_HEAD_BUTTON_ENABLED = create("random-head-button.enabled", false, "เปิดปุ่มสุ่มหัวในเมนูหลัก");
	public static final ConfigEntry RANDOM_HEAD_BUTTON_PRICE = create("random-head-button.price", 1.0, "ราคาของการสุ่มหัว");
	public static final ConfigEntry ASK_FOR_BUY_CONFIRM = create("ask-for-purchase-confirmation", false, "ถามยืนยันก่อนซื้อหัว");

	public static final ConfigEntry CATEGORIES_ALPHABET_ENABLED = create("enabled-categories.alphabet", true);
	public static final ConfigEntry CATEGORIES_ANIMALS_ENABLED = create("enabled-categories.animals", true);
	public static final ConfigEntry CATEGORIES_BLOCKS_ENABLED = create("enabled-categories.blocks", true);
	public static final ConfigEntry CATEGORIES_DECORATION_ENABLED = create("enabled-categories.decoration", true);
	public static final ConfigEntry CATEGORIES_FOOD_AND_DRINKS_ENABLED = create("enabled-categories.food-and-drinks", true);
	public static final ConfigEntry CATEGORIES_HUMANS_ENABLED = create("enabled-categories.humans", true);
	public static final ConfigEntry CATEGORIES_HUMANOID_ENABLED = create("enabled-categories.humanoids", true);
	public static final ConfigEntry CATEGORIES_MISC_ENABLED = create("enabled-categories.misc", true);
	public static final ConfigEntry CATEGORIES_MONSTERS_ENABLED = create("enabled-categories.monsters", true);
	public static final ConfigEntry CATEGORIES_PLANTS_ENABLED = create("enabled-categories.plants", true);
	public static final ConfigEntry CATEGORIES_PLAYER_HEADS_ENABLED = create("enabled-categories.player-heads", false);


	public static final ConfigEntry PLAYER_HEAD_NAME = create("player-head.name", "&e%player_name%");
	public static final ConfigEntry PLAYER_HEAD_DROP = create("player-head.drop-enabled", true);
	public static final ConfigEntry PLAYER_HEAD_DROP_CHANCE = create("player-head.drop-chance", 50);


	public static final ConfigEntry CLAIM_DELAY_ENABLED = create("claim-delay.enabled", false, "เปิดการจำกัดเวลารับหัว");
	public static final ConfigEntry CLAIM_DELAY_PERMS = create("claim-delay.permission-times", List.of(
			"basic:30"
	), "รูปแบบ ชื่อสิทธิ์:วินาที เช่น basic:30");

	public static final ConfigEntry DEFAULT_PRICES_ALPHABET = create("default-prices.alphabet", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_ANIMALS = create("default-prices.animals", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_BLOCKS = create("default-prices.blocks", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_DECORATION = create("default-prices.decoration", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_FOOD_AND_DRINKS = create("default-prices.food-and-drinks", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_HUMANS = create("default-prices.humans", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_HUMANOID = create("default-prices.humanoids", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_MISC = create("default-prices.misc", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_MONSTERS = create("default-prices.monsters", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_PLANTS = create("default-prices.plants", 1.0);
	public static final ConfigEntry DEFAULT_PRICES_PLAYER_HEADS = create("default-prices.player-heads", 1.0);

	@SneakyThrows
	public static void setup() {
		Skulls.getCoreConfig().init();
	}
}
