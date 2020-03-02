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

package eu.the5zig.mod.manager;

import eu.the5zig.mod.util.Vector2i;

public class CoordinateClipboard implements eu.the5zig.mod.util.CoordinateClipboard {

	private Vector2i location;
	private CoordinateClipboard previous;
	private CoordinateClipboard next;

	public CoordinateClipboard(CoordinateClipboard previous) {
		this.previous = previous;
	}

	public Vector2i getLocation() {
		return location;
	}

	public void setLocation(Vector2i location) {
		this.location = location;
	}

	@Override
	public int getLocationX() {
		return location.getX();
	}

	@Override
	public int getLocationZ() {
		return location.getY();
	}

	@Override
	public void setLocation(int x, int z) {
		setLocation(new Vector2i(x, z));
	}

	public CoordinateClipboard getPrevious() {
		return previous;
	}

	public void setPrevious(CoordinateClipboard previous) {
		this.previous = previous;
	}

	public CoordinateClipboard getNext() {
		return next;
	}

	@Override
	public CoordinateClipboard makeNext() {
		return new CoordinateClipboard(this);
	}

	public void setNext(CoordinateClipboard next) {
		this.next = next;
	}
}
