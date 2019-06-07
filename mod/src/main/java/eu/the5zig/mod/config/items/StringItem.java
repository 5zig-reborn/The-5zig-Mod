/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
 *
 * This file is part of The 5zig Mod
 * The 5zig Mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The 5zig Mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The 5zig Mod.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.the5zig.mod.config.items;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.the5zig.mod.gui.GuiSettings;

public class StringItem extends ConfigItem<String> {

	private int minLength;
	private int maxLength;

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public StringItem(String key, String category, String defaultValue) {
		this(key, category, defaultValue, 0, 255);
	}

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public StringItem(String key, String category, String defaultValue, int minLength, int maxLength) {
		super(key, category, defaultValue);
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	@Override
	public void deserialize(JsonObject object) {
		JsonElement element = object.get(getKey());
		set(element.isJsonNull() ? null : element.getAsString());
	}

	@Override
	public void serialize(JsonObject object) {
		object.addProperty(getKey(), get());
	}

	@Override
	public void next() {
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}
