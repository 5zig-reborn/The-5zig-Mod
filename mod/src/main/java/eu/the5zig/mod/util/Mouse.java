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

public class Mouse {

	public static int BUTTON_LEFT = 0;
	public static int BUTTON_RIGHT = 1;
	public static int BUTTON_MIDDLE = 2;

	private static MouseHandler handler;

	public static void init(MouseHandler handler) {
		Mouse.handler = handler;
	}

	public static boolean isButtonDown(int button) {
		return handler.isButtonDown(button);
	}

	public static int getX() {
		return handler.getX();
	}

	public static int getY() {
		return handler.getY();
	}

	public interface MouseHandler {
		boolean isButtonDown(int button);

		int getX();

		int getY();
	}

}
