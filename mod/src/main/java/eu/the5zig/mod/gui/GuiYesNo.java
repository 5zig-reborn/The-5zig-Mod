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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiYesNo extends Gui {

	private final YesNoCallback callback;

	public GuiYesNo(Gui lastScreen, YesNoCallback callback) {
		super(lastScreen);
		this.callback = callback;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 152, getHeight() / 6 + 140, 150, 20, The5zigMod.getVars().translate("gui.yes")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 2, getHeight() / 6 + 140, 150, 20, The5zigMod.getVars().translate("gui.no")));
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int maxStringWidth = getWidth() / 4 * 3;
		List<String> strings = The5zigMod.getVars().splitStringToWidth(callback.title(), maxStringWidth);
		int yOff = 0;
		for (String string : strings) {
			drawCenteredString(string, getWidth() / 2, getHeight() / 6 + (yOff += 12));
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1 || button.getId() == 2) {
			The5zigMod.getVars().displayScreen(lastScreen);
			callback.onDone(button.getId() == 1);
		}
	}

	@Override
	public String getTitleName() {
		return "";
	}
}
