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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketDeleteBlockedUser;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiBlockedUsers extends Gui {

	private int selected = 0;
	private IGuiList guiList;

	public GuiBlockedUsers(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 100, getHeight() - 38, 200, 20, I18n.translate("blocked_users.unblock")));

		initRowList();
	}

	protected void initRowList() {
		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 50, getHeight() - 50, 0, getWidth(), The5zigMod.getFriendManager().getBlockedUsers());
		guiList.setRowWidth(200);
		guiList.setScrollX(getWidth() - 15);
		addGuiList(guiList);

		if (selected < 0)
			selected = 0;
		guiList.setSelectedId(selected);
	}

	@Override
	protected void tick() {
		getButtonById(1).setEnabled(!The5zigMod.getFriendManager().getBlockedUsers().isEmpty());
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (The5zigMod.getFriendManager().getBlockedUsers().isEmpty()) {
			drawCenteredString(I18n.translate("blocked_users.none"), getWidth() / 2, getHeight() / 2 - 20);
		}
	}

	@Override
	public String getTitleKey() {
		return "blocked_users.title";
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (selected < 0 || selected >= The5zigMod.getFriendManager().getBlockedUsers().size())
				return;
			User selectedUser = (User) guiList.getSelectedRow();
			The5zigMod.getFriendManager().removeBlockedUser(selectedUser.getUniqueId());
			The5zigMod.getNetworkManager().sendPacket(new PacketDeleteBlockedUser(selectedUser.getUniqueId()));
		}
	}
}