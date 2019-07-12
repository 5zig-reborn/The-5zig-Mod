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

package eu.the5zig.mod.gui.elements;

public interface IButton {

	int getId();

	String getLabel();

	void setLabel(String label);

	int callGetWidth();

	void callSetWidth(int width);

	int callGetHeight();

	void callSetHeight(int height);

	boolean isEnabled();

	void setEnabled(boolean enabled);

	boolean isVisible();

	void setVisible(boolean visible);

	boolean isHovered();

	void setHovered(boolean hovered);

	int getX();

	void setX(int x);

	int getY();

	void setY(int y);

	void draw(int mouseX, int mouseY);

	void tick();

	boolean mouseClicked(int mouseX, int mouseY);

	void callMouseReleased(int mouseX, int mouseY);

	void playClickSound();

	void setTicksDisabled(int ticks);

	void guiClosed();

}
