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

package eu.the5zig.mod.installer;

public enum Stage {

	EXTRACT_SOURCES(0, "Extracting Sources to Library File."),
	COPY_MINECRAFT(.25f, "Copying Minecraft Version."),
	APPLY_OPTIFINE_PATCHES(.5f, "Trying to apply Optifine patches."),
	COPY_OTHER_MODS(.55f, "Installing other Mods."),
	UPDATE_LAUNCHER_FILES(.95f, "Updating launcher files.");

	private float startPercentage;
	private String message;

	Stage(float startPercentage, String message) {
		this.startPercentage = startPercentage;
		this.message = message;
	}

	public float getStartPercentage() {
		return startPercentage;
	}

	public void setStartPercentage(float startPercentage) {
		this.startPercentage = startPercentage;
	}

	public String getMessage() {
		return message;
	}
}
