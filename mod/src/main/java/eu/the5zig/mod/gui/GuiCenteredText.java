/*
 * Copyright (c) 2019-2020 5zig Reborn
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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiCenteredText extends Gui {

	private final String text;

	public GuiCenteredText(Gui lastScreen, String text) {
		super(lastScreen);
		this.text = text;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168,  The5zigMod.getVars().translate("gui.done")));
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(text, getWidth() / 2, getHeight() / 4);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}
}
