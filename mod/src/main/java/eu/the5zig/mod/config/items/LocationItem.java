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
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.mod.modules.ModuleLocation;

public class LocationItem extends EnumItem<ModuleLocation> {

	private float xOffset, yOffset;
	private boolean centered = false;

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public LocationItem(String key, String category, ModuleLocation defaultValue) {
		super(key, category, defaultValue, ModuleLocation.class);
	}

	@Override
	public void serialize(JsonObject object) {
		JsonObject content = new JsonObject();
		content.addProperty("type", get().toString());
		content.addProperty("x", getXOffset());
		content.addProperty("y", getYOffset());
		content.addProperty("centered", isCentered());
		object.add(getKey(), content);
	}

	@Override
	public void deserialize(JsonObject object) {
		JsonObject content = object.get(getKey()).getAsJsonObject();
		ModuleLocation location = ModuleLocation.valueOf(content.get("type").getAsString());
		set(location);
		setXOffset(content.get("x").getAsFloat());
		setYOffset(content.get("y").getAsFloat());
		setCentered(content.get("centered").getAsBoolean());
	}

	public float getXOffset() {
		return xOffset;
	}

	public void setXOffset(float xOffset) {
		this.xOffset = xOffset;
	}

	public float getYOffset() {
		return yOffset;
	}

	public void setYOffset(float yOffset) {
		this.yOffset = yOffset;
	}

	public boolean isCentered() {
		return centered;
	}

	public void setCentered(boolean centered) {
		this.centered = centered;
	}
}
