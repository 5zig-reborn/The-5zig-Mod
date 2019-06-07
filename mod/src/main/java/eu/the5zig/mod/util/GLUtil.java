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

package eu.the5zig.mod.util;

public class GLUtil {

	private static final IGLUtil handle;

	static {
		try {
			handle = (IGLUtil) Class.forName("GLUtilHandle").newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void enableBlend() {
		handle.enableBlend();
	}

	public static void disableBlend() {
		handle.disableBlend();
	}

	public static void scale(float x, float y, float z) {
		handle.scale(x, y, z);
	}

	public static void translate(float x, float y, float z) {
		handle.translate(x, y, z);
	}

	public static void color(float r, float g, float b, float a) {
		handle.color(r, g, b, a);
	}

	public static void color(float r, float g, float b) {
		handle.color(r, g, b);
	}

	public static void pushMatrix() {
		handle.pushMatrix();
	}

	public static void popMatrix() {
		handle.popMatrix();
	}

	public static void matrixMode(int mode) {
		handle.matrixMode(mode);
	}

	public static void loadIdentity() {
		handle.loadIdentity();
	}

	public static void clear(int i) {
		handle.clear(i);
	}

	public static void disableDepth() {
		handle.disableDepth();
	}

	public static void enableDepth() {
		handle.enableDepth();
	}

	public static void depthMask(boolean b) {
		handle.depthMask(b);
	}

	public static void disableLighting() {
		handle.disableLighting();
	}

	public static void enableLighting() {
		handle.enableLighting();
	}

	public static void disableFog() {
		handle.disableFog();
	}

	public static void tryBlendFuncSeparate(int i, int i1, int i2, int i3) {
		handle.tryBlendFuncSeparate(i, i1, i2, i3);
	}

	public static void disableAlpha() {
		handle.disableAlpha();
	}

}
