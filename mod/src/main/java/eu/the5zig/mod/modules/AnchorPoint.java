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

package eu.the5zig.mod.modules;

import eu.the5zig.mod.render.RenderLocation;

public enum AnchorPoint {

	TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

	public AnchorPoint getNext() {
		return values()[(ordinal() + 1) % values().length];
	}

	public RenderLocation toRenderLocation() {
		switch (this) {
			case TOP_LEFT: case CENTER_LEFT: case BOTTOM_LEFT:
				return RenderLocation.LEFT;
			case TOP_CENTER: case CENTER_CENTER: case BOTTOM_CENTER:
				return RenderLocation.CENTERED;
			case TOP_RIGHT: case CENTER_RIGHT: case BOTTOM_RIGHT:
				return RenderLocation.RIGHT;
			default:
				return RenderLocation.LEFT;
		}
	}

}
