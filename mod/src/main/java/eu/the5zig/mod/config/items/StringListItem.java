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

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class StringListItem extends ListItem<String> {

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link eu.the5zig.mod.gui.GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public StringListItem(String key, String category, List<String> defaultValue) {
		super(key, category, defaultValue);
	}

	@Override
	public void deserialize(JsonObject object) {
		List<String> list = Lists.newArrayList();
		JsonArray array = object.getAsJsonArray(getKey());
		for (JsonElement jsonElement : array) {
			if (jsonElement.isJsonPrimitive()) {
				list.add(jsonElement.getAsString());
			}
		}
		set(list);
	}

	@Override
	public void serialize(JsonObject object) {
		if (get().isEmpty()) {
			object.remove(getKey());
		} else {
			JsonArray array = new JsonArray();
			for (String element : get()) {
				array.add(new JsonPrimitive(element));
			}
			object.add(getKey(), array);
		}
	}

	@Override
	public void setSafely(List<Object> objects) {
		List<String> entries = new ArrayList<String>(objects.size());
		for (Object object : objects) {
			entries.add(String.valueOf(object));
		}
		set(entries);
	}
}
