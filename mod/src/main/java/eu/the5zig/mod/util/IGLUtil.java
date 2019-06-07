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

public interface IGLUtil {

	void enableBlend();

	void disableBlend();

	void scale(float x, float y, float z);

	void translate(float x, float y, float z);

	void color(float r, float g, float b, float a);

	void color(float r, float g, float b);

	void pushMatrix();

	void popMatrix();

	void matrixMode(int mode);

	void loadIdentity();

	void clear(int i);

	void disableDepth();

	void enableDepth();

	void depthMask(boolean b);

	void disableLighting();

	void enableLighting();

	void disableFog();

	void tryBlendFuncSeparate(int i, int i1, int i2, int i3);

	void disableAlpha();

}