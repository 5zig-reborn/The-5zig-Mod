/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

import com.google.gson.JsonObject;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.GuiSettings;

public class BoolItem extends ConfigItem<Boolean> {

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public BoolItem(String key, String category, Boolean defaultValue) {
		super(key, category, defaultValue);
	}

	@Override
	public void deserialize(JsonObject object) {
		set(object.get(getKey()).getAsBoolean());
	}

	@Override
	public void serialize(JsonObject object) {
		object.addProperty(getKey(), get());
	}

	@Override
	public void next() {
		set(!get());
	}

	@Override
	public String translateValue() {
		return The5zigMod.toBoolean(get());
	}
}