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

public class RadioCheckBox {

	private final int x;
	private final int y;
	private final int width;
	private final int rowHeight;
	private final String[] elements;

	private boolean enabled = true;
	private int selectedIndex = 0;

	public RadioCheckBox(int x, int y, int width, int rowHeight, String... elements) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.rowHeight = rowHeight;
		this.elements = elements;
	}

	public void mouseClicked(int mouseX, int mouseY) {
		if (enabled && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + elements.length * rowHeight) {
			int index = (int) Math.floor((double) (mouseY - y) / (double) rowHeight);
			if (index >= 0 && index < elements.length) {
				this.selectedIndex = index;
			}
		}
	}

	public void draw() {
		for (int i = 0, elementsLength = elements.length; i < elementsLength; i++) {
			String element = elements[i];
			MinecraftFactory.getVars().drawString(MinecraftFactory.getVars().shortenToWidth(element, width - 16), x + 14, y + i * rowHeight + 1, enabled ? 0xffffff : 0xbbbbbb);
			Gui.drawRect(x, y + i * rowHeight, x + 10, y + i * rowHeight + 10, enabled ? 0xffffffff : 0xffbbbbbb);
			if (i == selectedIndex) {
				Gui.drawRect(x + 2, y + i * rowHeight + 2, x + 8, y + i * rowHeight + 8, 0xff000000);
			}
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex >= 0 && selectedIndex < elements.length) {
			this.selectedIndex = selectedIndex;
		}
	}
}
