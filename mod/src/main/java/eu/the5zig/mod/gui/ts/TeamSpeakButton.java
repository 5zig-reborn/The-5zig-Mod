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

package eu.the5zig.mod.gui.ts;

import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.util.GLUtil;

public abstract class TeamSpeakButton {

	private int x;
	private int y;
	private final int WIDTH = 16;
	private final int HEIGHT = 16;
	private int u;
	private int v;

	public TeamSpeakButton(int x, int y, int u, int v) {
		this.x = x;
		this.y = y;
		this.u = u;
		this.v = v;
	}

	public void draw() {
		if (!isEnabled()) {
			GLUtil.color(0.2f, 0.2f, 0.2f, 1);
			Gui.drawModalRectWithCustomSizedTexture(x, y, u * 128 / 8, v * 128 / 8, 128 / 8, 128 / 8, 2048 / 8, 2048 / 8);
			return;
		} else if (isSelected()) {
			Gui.drawRect(x, y, x + WIDTH, y + HEIGHT, 0x885599fa);
		}
		GLUtil.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x, y, u * 128 / 8, v * 128 / 8, 128 / 8, 128 / 8, 2048 / 8, 2048 / 8);
	}

	public void mouseClicked(int mouseX, int mouseY) {
		if (isEnabled() && mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT) {
			onClick();
		}
	}

	protected abstract void onClick();

	protected abstract boolean isSelected();

	protected boolean isEnabled() {
		return true;
	}

}
