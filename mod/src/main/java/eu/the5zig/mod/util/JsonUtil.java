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

package eu.the5zig.mod.util;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class JsonUtil {

	private JsonUtil() {
	}

	public static int getInt(JsonObject object, String name) {
		JsonElement element = object.get(name);
		if (element == null || element.isJsonNull())
			return 0;
		return element.getAsInt();
	}

	public static double getDouble(JsonObject object, String name) {
		JsonElement element = object.get(name);
		if (element == null || element.isJsonNull())
			return 0;
		return element.getAsDouble();
	}

	public static long getLong(JsonObject object, String name) {
		JsonElement element = object.get(name);
		if (element == null || element.isJsonNull())
			return 0;
		return element.getAsLong();
	}

	public static String getString(JsonObject object, String name) {
		JsonElement element = object.get(name);
		if (element == null || element.isJsonNull())
			return null;
		return element.getAsString();
	}

	public static List<String> getList(JsonObject object, String name) {
		List<String> result = Lists.newArrayList();
		JsonElement element = object.get(name);
		if (element == null || element.isJsonNull())
			return result;
		for (JsonElement jsonElement : element.getAsJsonArray()) {
			result.add(jsonElement.getAsString());
		}
		return result;
	}

}
