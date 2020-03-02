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

/**
 * A two-dimensional vector.
 */
public class Vector3i {

	/**
	 * The x-coordinate of the vector.
	 */
	private int x;
	/**
	 * The y-coordinate of the vector.
	 */
	private int y;
	/**
	 * The z-coordinate of the vector.
	 */
	private int z;

	/**
	 * Creates a new instance of the vector.
	 *
	 * @param x the x-coordinate of the vector.
	 * @param y the y-coordinate of the vector.
	 */
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return the x-coordinate of the vector.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets a new x-coordinate.
	 *
	 * @param x the new x-coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y-coordinate of the vector.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets a new y-coordinate.
	 *
	 * @param y the new y-coordinate.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the z-coordinate of the vector.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Sets a new z-coordinate.
	 *
	 * @param z the new z-coordinate.
	 */
	public void setZ(int z) {
		this.z = z;
	}

	public double distanceSquared(int x, int y, int z) {
		return (double) (x - this.x) * (double) (x - this.x) + (double) (y - this.y) * (double) (y - this.y) + (double) (z - this.z) * (double) (z - this.z);
	}

	@Override
	public String toString() {
		return "Vector3i{" +
				"x=" + x +
				", y=" + y +
				", z=" + z +
				'}';
	}
}
