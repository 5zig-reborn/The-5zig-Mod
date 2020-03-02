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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiCreateGroupChat extends Gui {

	public GuiCreateGroupChat(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 + 5, getHeight() - 30, 150, 20, I18n.translate("gui.continue"), false));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 30, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addTextField(The5zigMod.getVars().createTextfield(301, getWidth() / 2 - 100, getHeight() / 6 + 40, 200, 20, 30));
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("group.create.name"), getWidth() / 2 - 100, getHeight() / 6 + 28);
		if (getTextfieldById(301).callGetText().length() < 3)
			drawCenteredString(ChatColor.RED + I18n.translate("group.create.name.min_length"), getWidth() / 2, getHeight() / 6 + 70);
	}

	@Override
	protected void onKeyType(char character, int key) {
		getButtonById(1).setEnabled(getTextfieldById(301).callGetText().length() > 2);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (getTextfieldById(301).callGetText().length() < 3)
				return;
			The5zigMod.getVars().displayScreen(new GuiCreateGroupChatSelectFriends(this, getTextfieldById(301).callGetText()));
		}
	}

	@Override
	public String getTitleKey() {
		return "group.create.title";
	}
}
