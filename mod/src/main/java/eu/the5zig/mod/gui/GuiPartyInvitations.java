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
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.packets.PacketPartyInviteResponse;
import eu.the5zig.mod.chat.party.PartyManager;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;

public class GuiPartyInvitations extends Gui {

	private IGuiList<PartyManager.PartyOwner> invitationList;

	public GuiPartyInvitations(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(MinecraftFactory.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 32, 150, 20, MinecraftFactory.getVars().translate("gui.done")));

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 + 5, getHeight() - 32, 68, 20, I18n.translate("party.invitations.accept")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 77, getHeight() - 32, 68, 20, I18n.translate("party.invitations.deny")));

		invitationList = The5zigMod.getVars().createGuiList(new Clickable<PartyManager.PartyOwner>() {
			@Override
			public void onSelect(int id, PartyManager.PartyOwner row, boolean doubleClick) {
				if (doubleClick) {
					actionPerformed0(getButtonById(1));
				}
			}
		}, getWidth(), getHeight(), 32, getHeight() - 48, 0, getWidth(), The5zigMod.getPartyManager().getPartyInvitations());
		invitationList.setRowWidth(200);
		addGuiList(invitationList);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1 || button.getId() == 2) {
			PartyManager.PartyOwner selectedRow = invitationList.getSelectedRow();
			if (selectedRow != null) {
				The5zigMod.getNetworkManager().sendPacket(new PacketPartyInviteResponse(selectedRow.getUniqueId(), button.getId() == 1));
				The5zigMod.getPartyManager().getPartyInvitations().remove(selectedRow);
				The5zigMod.getVars().displayScreen(lastScreen);
			}
		}
	}

	@Override
	protected void tick() {
		getButtonById(1).setEnabled(invitationList.getSelectedRow() != null);
		getButtonById(2).setEnabled(invitationList.getSelectedRow() != null);
	}

	@Override
	public String getTitleKey() {
		return "party.invitations.title";
	}
}
