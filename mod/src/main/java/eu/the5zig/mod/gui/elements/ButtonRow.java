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

package eu.the5zig.mod.gui.elements;

import eu.the5zig.mod.MinecraftFactory;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ButtonRow implements RowExtended {

	public IButton button1;
	public IButton button2;

	public ButtonRow(IButton button1, IButton button2) {
		this.button1 = button1;
		this.button2 = button2;
	}

	@Override
	public void draw(int x, int y) {
	}

	@Override
	public void draw(int x, int y, int slotHeight, int mouseX, int mouseY) {
		if (button1 != null) {
			button1.setY(y + 2);
			button1.draw(mouseX, mouseY);
		}
		if (button2 != null) {
			button2.setY(y + 2);
			button2.draw(mouseX, mouseY);
		}
	}

	@Override
	public IButton mousePressed(int mouseX, int mouseY) {
		if (button1 != null) {
			if (button1.mouseClicked(mouseX, mouseY)) {
				button1.playClickSound();
				MinecraftFactory.getVars().getCurrentScreen().actionPerformed0(button1);
				return button1;
			}
		}
		if (button2 != null) {
			if (button2.mouseClicked(mouseX, mouseY)) {
				button2.playClickSound();
				MinecraftFactory.getVars().getCurrentScreen().actionPerformed0(button2);
				return button2;
			}
		}
		return null;
	}

	@Override
	public int getLineHeight() {
		return 24;
	}
}
