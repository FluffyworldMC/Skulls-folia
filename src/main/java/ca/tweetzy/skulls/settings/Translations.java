/*
 * Skulls
 * Copyright 2023 Kiran Hart
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

import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.skulls.Skulls;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public final class Translations extends TranslationManager {

	private static JavaPlugin languagePlugin;

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
		this.mainLanguage = Settings.LANG.getString();
	}

	@Override
	protected void registerLanguages() {
		if (languagePlugin == null) languagePlugin = Skulls.getInstance();
		if (languagePlugin == null) return;

		registerLanguage(languagePlugin, "en_us");
		registerLanguage(languagePlugin, "th_th");
	}

	public static final TranslationEntry MISC_IS_TRUE = create("conditionals.is-true", "&aใช่");
	public static final TranslationEntry MISC_IS_FALSE = create("conditionals.is-false", "&cไม่ใช่");
	public static final TranslationEntry MISC_IS_ALLOWED = create("conditionals.is-allowed", "&aอนุญาต");
	public static final TranslationEntry MISC_IS_DISALLOWED = create("conditionals.is-disallowed", "&cไม่อนุญาต");
	public static final TranslationEntry CATEGORY_PERMISSION = create("misc.category-permission", "&cคุณไม่มีสิทธิ์เข้าถึงหมวดหมู่นี้");
	public static final TranslationEntry SKULL_NOT_FOUND = create("misc.skull-not-found", "&cไม่พบหัวที่มีรหัสนี้");
	public static final TranslationEntry NO_SKULL_INFO = create("misc.no-skull-info", "&cไม่สามารถระบุรหัสของหัวนี้ได้");
	public static final TranslationEntry SKULL_TITLE = create("skull.name", "&e%skull_name%");
	public static final TranslationEntry ID_TAKEN = create("misc.id-taken", "&cรหัสหมวดหมู่นี้ถูกใช้งานแล้ว");
	public static final TranslationEntry CLAIM_DELAY = create("misc.claim-delay", "&cคุณสามารถรับหัวได้อีกครั้งใน &7(&e%time_difference%&7)");
	public static final TranslationEntry RECEIVED_RANDOM_SKULL = create("misc.received-random-skull", "&aคุณได้รับหัวสุ่ม &f(&e%skull_name%&f)");
	public static final TranslationEntry PURCHASE_SUCCESS = create("misc.purchase-success", "&aซื้อสำเร็จ");
	public static final TranslationEntry RELOAD_SUCCESS = create("misc.reload-success", "&aโหลดการตั้งค่าใหม่เรียบร้อยแล้ว");
	public static final TranslationEntry PLAYER_HEAD_GIVEN = create("misc.player-head-given", "&aมอบหัวให้ &e%player_name% &aเรียบร้อยแล้ว");

	public static final TranslationEntry ERROR_NO_MONEY = create("skulls.error.no-money", "&cคุณมีเงินไม่พอ");
	public static final TranslationEntry ERROR_NO_PERMISSION = create("skulls.error.no-permission", "&cคุณไม่มีสิทธิ์ใช้งานส่วนนี้");
	public static final TranslationEntry ERROR_NOT_A_NUMBER = create("skulls.error.not-a-number", "&c%value% ไม่ใช่ตัวเลขที่ถูกต้อง");
	public static final TranslationEntry ERROR_PLAYER_OFFLINE = create("skulls.error.player-offline", "&c%value% ไม่ได้ออนไลน์อยู่");
	public static final TranslationEntry ERROR_PLAYER_NOT_FOUND = create("skulls.error.player-not-found", "&cไม่พบผู้เล่น %value%");

	public static final TranslationEntry ALPHABET = create("categories.alphabet", "ตัวอักษร");
	public static final TranslationEntry ANIMALS = create("categories.animals", "สัตว์");
	public static final TranslationEntry BLOCKS = create("categories.blocks", "บล็อก");
	public static final TranslationEntry DECORATION = create("categories.decoration", "ของตกแต่ง");
	public static final TranslationEntry FOOD_AND_DRINKS = create("categories.food-and-drinks", "อาหารและเครื่องดื่ม");
	public static final TranslationEntry HUMANS = create("categories.humans", "มนุษย์");
	public static final TranslationEntry HUMANOID = create("categories.humanoid", "สิ่งมีชีวิตคล้ายมนุษย์");
	public static final TranslationEntry MISC = create("categories.misc", "เบ็ดเตล็ด");
	public static final TranslationEntry MONSTERS = create("categories.monsters", "มอนสเตอร์");
	public static final TranslationEntry PLANTS = create("categories.plants", "พืช");
	public static final TranslationEntry PLAYER_HEAD = create("categories.player-head", "&eหัวของ %player_name%");

	public static final TranslationEntry INPUT_SKULL_EDIT_TITLE = create("input.skull-edit.title", "&eแก้ไขหัว");
	public static final TranslationEntry INPUT_SKULL_EDIT_NAME = create("input.skull-edit.name", "&fพิมพ์ชื่อใหม่ของหัวในแชท");
	public static final TranslationEntry INPUT_SKULL_EDIT_PRICE = create("input.skull-edit.price", "&fพิมพ์ราคาใหม่ของหัวในแชท");

	public static final TranslationEntry INPUT_SKULL_SEARCH_TITLE = create("input.skull-search.title", "&eค้นหาหัว");
	public static final TranslationEntry INPUT_SKULL_SEARCH_SUBTITLE = create("input.skull-search.subtitle", "&fพิมพ์คำค้นหาในแชท");

	public static final TranslationEntry INPUT_CATEGORY_CREATE_TITLE = create("input.category-create.title", "&eสร้างหมวดหมู่");
	public static final TranslationEntry INPUT_CATEGORY_CREATE_SUBTITLE = create("input.category-create.subtitle", "&fพิมพ์รหัสของหมวดหมู่ใหม่ในแชท");

	public static final TranslationEntry INSPECT_INFO = create("inspect-info",
			"&8&m-----------------------------------------------------",
			"",
			"&eรหัสหัว&f: &a%skull_id%",
			"&eลิงก์เท็กซ์เจอร์&f: &a%skull_texture%",
			"",
			"&8&m-----------------------------------------------------"
	);

	// GUIS

	public static TranslationEntry GUI_LOADING_NAME = create("gui.loading.name", "&e&lกำลังโหลด...");
	public static TranslationEntry GUI_LOADING_LORE = create("gui.loading.lore", "&7กรุณารอสักครู่");

	public static TranslationEntry DIALOG_CONFIRM = create("dialog.confirm", "&a&lยืนยัน");

	public static TranslationEntry GUI_CONFIRM_ACTION_TITLE = create("gui.confirm-action.title", "%pl_name% &f- &7ยืนยันหรือไม่?");
	public static TranslationEntry GUI_CONFIRM_ACTION_ITEMS_YES_NAME = create("gui.confirm-action.items.confirm.name", "&a&lยืนยัน");
	public static TranslationEntry GUI_CONFIRM_ACTION_ITEMS_YES_LORE = create("gui.confirm-action.items.confirm.lore", "&e&lคลิก &7เพื่อยืนยัน");

	public static TranslationEntry GUI_CONFIRM_ACTION_ITEMS_NO_NAME = create("gui.confirm-action.items.cancel.name", "&c&lยกเลิก");
	public static TranslationEntry GUI_CONFIRM_ACTION_ITEMS_NO_LORE = create("gui.confirm-action.items.cancel.lore", "&e&lคลิก &7เพื่อยกเลิก");

	public static TranslationEntry GUI_SHARED_ITEMS_BACK_BUTTON_NAME = create("gui.shared-buttons.back-button.name", "&e&lย้อนกลับ");
	public static TranslationEntry GUI_SHARED_ITEMS_BACK_BUTTON_LORE = create("gui.shared-buttons.back-button.lore",
			"&e&lคลิก &8» &7กลับไปเมนูก่อนหน้า"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_EXIT_BUTTON_NAME = create("gui.shared-buttons.exit-button.name", "&c&lปิดเมนู");
	public static TranslationEntry GUI_SHARED_ITEMS_EXIT_BUTTON_LORE = create("gui.shared-buttons.exit-button.lore",
			"&e&lคลิก &8» &7ปิดเมนู"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME = create("gui.shared-buttons.previous-button.name", "&e&lหน้าก่อนหน้า");
	public static TranslationEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE = create("gui.shared-buttons.previous-button.lore",
			"&e&lคลิก &8» &7ไปหน้าก่อนหน้า"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_NEXT_BUTTON_NAME = create("gui.shared-buttons.next-button.name", "&e&lหน้าถัดไป");
	public static TranslationEntry GUI_SHARED_ITEMS_NEXT_BUTTON_LORE = create("gui.shared-buttons.next-button.lore",
			"&e&lคลิก &8» &7ไปหน้าถัดไป"
	);

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_SELECTOR_TITLE = create("gui.custom-category-selector.title", "&eSkulls &8» &7เลือกหมวดหมู่");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_SELECTOR_ITEMS_CATEGORY_NAME = create("gui.custom-category-selector.items.category.name", "&e%category_name%");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_SELECTOR_ITEMS_CATEGORY_LORE = create("gui.custom-category-selector.items.category.lore",
			"&e&lคลิก &8» &7เลือกหมวดหมู่นี้"
	);

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_TITLE = create("gui.custom-category-list.title", "&eSkulls &8» &7หมวดหมู่ที่สร้างเอง");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_CATEGORY_NAME = create("gui.custom-category-list.items.category.name", "&e%category_name%");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_CATEGORY_LORE = create("gui.custom-category-list.items.category.lore",
			"&7ดูหัวทั้งหมดในหมวดหมู่นี้",
			"&7จำนวนหัว&f: &b%category_size%",
			"",
			"&e&lคลิก &8» &7เปิดหมวดหมู่"
	);

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_NAME = create("gui.custom-category-list.items.new.name", "&a&lสร้างหมวดหมู่ใหม่");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_LORE = create("gui.custom-category-list.items.new.lore", "", "&e&lคลิก &8» &7สร้างหมวดหมู่ใหม่");

	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_ERROR = create("gui.custom-category-list.items.new.error", "&cไม่สามารถสร้างหมวดหมู่ได้ กรุณาดูข้อมูลในคอนโซล");
	public static final TranslationEntry GUI_CUSTOM_CATEGORY_LIST_ITEMS_NEW_EMPTY = create("gui.custom-category-list.items.new.empty", "&cกรุณากรอกรหัสหมวดหมู่");

	public static final TranslationEntry GUI_PLAYER_HEADS_TITLE = create("gui.player-heads.title", "&eSkulls &8» &7หัวผู้เล่น");
	public static final TranslationEntry GUI_PLAYER_HEADS_ITEMS_HEAD_NAME = create("gui.player-heads.items.head.name", "&e%player_name%");
	public static final TranslationEntry GUI_PLAYER_HEADS_UNKNOWN_NAME = create("gui.player-heads.items.unknown.name", "&eผู้เล่นที่ไม่รู้จัก");

	public static final TranslationEntry GUI_EDIT_TITLE = create("gui.edit.title", "&eSkulls &8» &7แก้ไข &8» &7%skull_id%");

	public static final TranslationEntry GUI_EDIT_ITEMS_NAME_NAME = create("gui.edit.items.name.name", "&e&lชื่อหัว");

	public static final TranslationEntry GUI_EDIT_ITEMS_NAME_LORE = create("gui.edit.items.name.lore", "", "&e&lคลิก &8» &7เปลี่ยนชื่อหัว");

	public static final TranslationEntry GUI_EDIT_ITEMS_PRICE_NAME = create("gui.edit.items.price.name", "&e&lราคาหัว");

	public static final TranslationEntry GUI_EDIT_ITEMS_PRICE_LORE = create("gui.edit.items.price.lore", "", "&e&lคลิก &8» &7แก้ไขราคาหัว");

	public static final TranslationEntry GUI_EDIT_ITEMS_ADD_CATEGORY_NAME = create("gui.edit.items.add-category.name", "&e&lเพิ่มเข้าหมวดหมู่");

	public static final TranslationEntry GUI_EDIT_ITEMS_ADD_CATEGORY_LORE = create("gui.edit.items.add-category.lore", "", "&e&lคลิก &8» &7เพิ่มหัวนี้เข้าหมวดหมู่อื่น");

	public static final TranslationEntry GUI_EDIT_ITEMS_BLOCKED_NAME = create("gui.edit.items.blocked.name", "&e&lสถานะการบล็อก");

	public static final TranslationEntry GUI_EDIT_ITEMS_BLOCKED_LORE = create("gui.edit.items.blocked.lore", "", "&7ถูกบล็อก&f: %is_true%", "", "&e&lคลิก &8» &7สลับสถานะการบล็อก");

	public static final TranslationEntry GUI_MAIN_TITLE = create("gui.main.title", "&eSkulls &8» &7เมนูหลัก");

	public static final TranslationEntry GUI_MAIN_ITEMS_CATEGORY_NAME = create("gui.main.items.category.name", "&e%category_name%");

	public static final TranslationEntry GUI_MAIN_ITEMS_CATEGORY_LORE = create("gui.main.items.category.lore",
			"&7ดูหัวทั้งหมดในหมวดหมู่นี้",
			"&7จำนวนหัว&f: &b%category_size%",
			"",
			"&e&lคลิก &8» &7เปิดหมวดหมู่"
	);

	public static final TranslationEntry GUI_MAIN_ITEMS_PLAYERS_NAME = create("gui.main.items.players.name", "&e&lหัวผู้เล่น");

	public static final TranslationEntry GUI_MAIN_ITEMS_PLAYERS_LORE = create("gui.main.items.players.lore",
			"&7ดูหัวของผู้เล่นทั้งหมด",
			"&7จำนวนหัว&f: &b%category_size%",
			"",
			"&e&lคลิก &8» &7เปิดหัวผู้เล่น"
	);

	public static final TranslationEntry GUI_MAIN_ITEMS_FAVOURITES_NAME = create("gui.main.items.favourites.name", "&e&lรายการโปรด");

	public static final TranslationEntry GUI_MAIN_ITEMS_FAVOURITES_LORE = create("gui.main.items.favourites.lore",
			"&7รวมหัวทั้งหมดที่คุณกดถูกใจไว้",
			"",
			"&e&lคลิก &8» &7ดูรายการโปรด"
	);

	public static final TranslationEntry GUI_MAIN_ITEMS_RANDOM_HEAD_NAME = create("gui.main.items.random-head.name", "&e&lสุ่มหัว");

	public static final TranslationEntry GUI_MAIN_ITEMS_RANDOM_HEAD_LORE = create("gui.main.items.random-head.lore",
			"&7รับหัวแบบสุ่มจากหมวดหมู่ที่คุณเข้าถึงได้",
			"",
			"&7ราคา&f: &a$%price%",
			"",
			"&e&lคลิก &8» &7รับหัวสุ่ม"
	);

	public static final TranslationEntry GUI_MAIN_ITEMS_SEARCH_NAME = create("gui.main.items.search.name", "&e&lค้นหาหัว");

	public static final TranslationEntry GUI_MAIN_ITEMS_SEARCH_LORE = create("gui.main.items.search.lore",
			"",
			"&e&lคลิก &8» &7ค้นหาหัว"
	);

	public static final TranslationEntry GUI_MAIN_ITEMS_CUSTOM_CATEGORIES_NAME = create("gui.main.items.custom-categories.name", "&e&lหมวดหมู่เพิ่มเติม");

	public static final TranslationEntry GUI_MAIN_ITEMS_CUSTOM_CATEGORIES_LORE = create("gui.main.items.custom-categories.lore",
			"",
			"&e&lคลิก &8» &7ดูหมวดหมู่ที่สร้างเอง"
	);

	public static final TranslationEntry GUI_SKULLS_LIST_TITLE_CATEGORY = create("gui.skulls-list.title.category", "&eSkulls &8» &7%category_name%");

	public static final TranslationEntry GUI_SKULLS_LIST_TITLE_FAVOURITES = create("gui.skulls-list.title.favourites", "&eSkulls &8» &7รายการโปรด");

	public static final TranslationEntry GUI_SKULLS_LIST_TITLE_SEARCH = create("gui.skulls-list.title.search", "&eSkulls &8» &7ค้นหา: %search_phrase%");

	public static final TranslationEntry GUI_SKULLS_LIST_TITLE_ALL = create("gui.skulls-list.title.all", "&eSkulls &8» &7ทั้งหมด");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_NAME = create("gui.skulls-list.items.skull.name", "&e%skull_name%");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_ID = create("gui.skulls-list.items.skull.lore.id", "&7รหัส&f: &e%skull_id%");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_TAGS = create("gui.skulls-list.items.skull.lore.tags", "&7แท็ก&f: &e%skull_tags%");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_PRICE = create("gui.skulls-list.items.skull.lore.price", "&7ราคา&f: &a$%skull_price%");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_TAKE = create("gui.skulls-list.items.skull.lore.take", "&e&lคลิกซ้าย &8» &7รับหัวนี้");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_FAVOURITE = create("gui.skulls-list.items.skull.lore.favourite", "&e&lคลิกขวา &8» &7เพิ่มในรายการโปรด");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_UN_FAVOURITE = create("gui.skulls-list.items.skull.lore.un-favourite", "&e&lคลิกขวา &8» &7ลบออกจากรายการโปรด");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_EDIT = create("gui.skulls-list.items.skull.lore.edit", "&b&lกดปุ่ม 1 &8» &7แก้ไขหัวนี้");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_FAVOURITED = create("gui.skulls-list.items.skull.lore.favourited", "&e&lอยู่ในรายการโปรด");

	public static final TranslationEntry GUI_SKULLS_LIST_ITEMS_SKULL_LORE_BLOCKED = create("gui.skulls-list.items.skull.lore.blocked", "&c&oไม่สามารถซื้อหัวนี้ได้");

	public static void init() {
		languagePlugin = Skulls.getInstance();
		new Translations(languagePlugin).setup(languagePlugin);
	}
}
