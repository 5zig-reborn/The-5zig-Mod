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

import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.gui.Gui;

public class CheckBox {

	private final int x;
	private final int y;
	private final int width;
	private final String string;

	private boolean selected;

	public CheckBox(int x, int y, int width, String string) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.string = string;
	}

	public void mouseClicked(int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 10) {
			selected = !selected;
		}
	}

	public void draw() {
		MinecraftFactory.getVars().drawString(MinecraftFactory.getVars().shortenToWidth(string, width - 14), x + 14, y + 1);
		Gui.drawRect(x, y, x + 10, y + 10, 0xffffffff);
		if (selected) {
			Gui.drawRect(x + 1, y + 1, x + 9, y + 9, 0xff000000);
		}
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}
}
