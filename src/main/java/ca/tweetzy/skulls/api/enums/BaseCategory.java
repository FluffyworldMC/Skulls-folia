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

package ca.tweetzy.skulls.api.enums;


import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.skulls.settings.Settings;
import ca.tweetzy.skulls.settings.Translations;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Date Created: April 04 2022
 * Time Created: 9:03 p.m.
 *
 * @author Kiran Hart
 */
@AllArgsConstructor
@Getter
public enum BaseCategory {

	ALPHABET("alphabet", TranslationManager.string(Translations.ALPHABET), Settings.DEFAULT_PRICES_ALPHABET.getDouble(), 164, 11, Settings.CATEGORIES_ALPHABET_ENABLED.getBoolean()),
	ANIMALS("animals", TranslationManager.string(Translations.ANIMALS), Settings.DEFAULT_PRICES_ANIMALS.getDouble(), 26960, 12, Settings.CATEGORIES_ANIMALS_ENABLED.getBoolean()),
	BLOCKS("blocks", TranslationManager.string(Translations.BLOCKS), Settings.DEFAULT_PRICES_BLOCKS.getDouble(), 24064, 13, Settings.CATEGORIES_BLOCKS_ENABLED.getBoolean()),
	DECORATION("decoration", TranslationManager.string(Translations.DECORATION), Settings.DEFAULT_PRICES_DECORATION.getDouble(), 46908, 14, Settings.CATEGORIES_DECORATION_ENABLED.getBoolean()),
	FOOD_AND_DRINKS("food & drinks", TranslationManager.string(Translations.FOOD_AND_DRINKS), Settings.DEFAULT_PRICES_FOOD_AND_DRINKS.getDouble(), 2686, 15, Settings.CATEGORIES_FOOD_AND_DRINKS_ENABLED.getBoolean()),
	HUMANS("humans", TranslationManager.string(Translations.HUMANS), Settings.DEFAULT_PRICES_HUMANS.getDouble(), 47833, 20, Settings.CATEGORIES_HUMANS_ENABLED.getBoolean()),
	HUMANOID("humanoid", TranslationManager.string(Translations.HUMANOID), Settings.DEFAULT_PRICES_HUMANOID.getDouble(), 46664, 21, Settings.CATEGORIES_HUMANOID_ENABLED.getBoolean()),
	MISCELLANEOUS("miscellaneous", TranslationManager.string(Translations.MISC), Settings.DEFAULT_PRICES_MISC.getDouble(), 45534, 22, Settings.CATEGORIES_MISC_ENABLED.getBoolean()),
	MONSTERS("monsters", TranslationManager.string(Translations.MONSTERS), Settings.DEFAULT_PRICES_MONSTERS.getDouble(), 47778, 23, Settings.CATEGORIES_MONSTERS_ENABLED.getBoolean()),
	PLANTS("plants", TranslationManager.string(Translations.PLANTS), Settings.DEFAULT_PRICES_PLANTS.getDouble(), 44334, 24, Settings.CATEGORIES_PLANTS_ENABLED.getBoolean());


	private final String id;
	private final String name;
	private final double defaultPrice;
	private final int texture;
	private final int slot;
	private final boolean enabled;


	public static BaseCategory getById(String id) {
		BaseCategory category = null;

		for (BaseCategory value : values()) {
			if (!value.getId().equalsIgnoreCase(id)) continue;
			category = value;
			break;
		}

		return category;
	}
}
