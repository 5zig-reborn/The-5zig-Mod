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

package eu.the5zig.mod.server.hypixel.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HypixelAPIResponse {

	private JsonObject response;
	private String raw;

	private boolean success;
	private String cause;

	public HypixelAPIResponse(String response) {
		if (response == null) {
			success = false;
			return;
		}
		this.raw = response;
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);
		if (!element.isJsonObject()) {
			throw new IllegalArgumentException();
		}
		this.response = element.getAsJsonObject();

		success = this.response.get("success").getAsBoolean();
		if (!success)
			cause = this.response.get("cause").getAsString();
	}

	public boolean isSuccess() {
		return success;
	}

	public String getCause() {
		return cause;
	}

	public JsonObject data() {
		return response;
	}

	public String getRaw() {
		return raw;
	}

	public JsonElement getElement(String path) {
		return getElement(path, response);
	}

	public static JsonElement getElement(String path, JsonElement element) {
		String[] tree = path.split("\\.");
		JsonElement current = element;
		for (String part : tree) {
			if (current.isJsonNull())
				return null;
			if (current.isJsonArray() || current.isJsonPrimitive())
				return current;
			current = current.getAsJsonObject().get(part);
		}
		return current;
	}

}