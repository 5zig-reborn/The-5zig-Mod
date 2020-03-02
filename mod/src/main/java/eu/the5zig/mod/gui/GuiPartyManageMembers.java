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
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.network.packets.PacketPartyStatus;
import eu.the5zig.mod.chat.party.Party;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;

public class GuiPartyManageMembers extends Gui {

	private final Party party;
	private IGuiList<GroupMember> guiList;

	public GuiPartyManageMembers(Gui lastScreen, Party party) {
		super(lastScreen);
		this.party = party;
	}

	@Override
	public void initGui() {
		for (GroupMember groupMember : party.getMembers()) {
			groupMember.setMaxWidth(200);
		}

		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 48, 0, getWidth(), party.getMembers());
		guiList.setRowWidth(200);
		addGuiList(guiList);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 205, getHeight() - 32, 80, 20, I18n.translate("party.manage_members.invite")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 123, getHeight() - 32, 80, 20, I18n.translate("party.manage_members.kick")));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2 - 41, getHeight() - 32, 80, 20, I18n.translate("party.manage_members.promote")));
		addButton(The5zigMod.getVars().createButton(4, getWidth() / 2 + 41, getHeight() - 32, 80, 20, I18n.translate("party.manage_members.set_owner")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 123, getHeight() - 32, 80, 20, The5zigMod.getVars().translate("gui.back")));
	}

	@Override
	protected void tick() {
		boolean admin = false;
		for (GroupMember groupMember : party.getMembers()) {
			if (groupMember.getUniqueId().equals(The5zigMod.getDataManager().getUniqueId())) {
				admin = groupMember.isAdmin();
				break;
			}
		}
		admin |= party.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId());

		getButtonById(1).setEnabled(admin);
		getButtonById(2).setEnabled(guiList.getSelectedRow() != null && admin && !guiList.getSelectedRow().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId()) &&
				!guiList.getSelectedRow().getUniqueId().equals(party.getOwner().getUniqueId()));
		getButtonById(3).setEnabled(guiList.getSelectedRow() != null && !guiList.getSelectedRow().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId()) &&
				!guiList.getSelectedRow().equals(party.getOwner()));
		getButtonById(3).setLabel(
				guiList.getSelectedRow() != null && guiList.getSelectedRow().isAdmin() ? I18n.translate("party.manage_members.demote") : I18n.translate("party.manage_members.promote"));
		getButtonById(4).setEnabled(
				guiList.getSelectedRow() != null && !guiList.getSelectedRow().equals(party.getOwner()) && The5zigMod.getDataManager().getUniqueId().equals(party.getOwner().getUniqueId()));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiPartyInviteMembers(this));
		} else if (button.getId() == 2) {
			GroupMember selectedRow = guiList.getSelectedRow();
			if (selectedRow != null) {
				The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(PacketPartyStatus.Action.MEMBER_REMOVE, selectedRow.getUniqueId()));
			}
		} else if (button.getId() == 3) {
			GroupMember selectedRow = guiList.getSelectedRow();
			if (selectedRow != null) {
				The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(selectedRow.getUniqueId(), !selectedRow.isAdmin()));
			}
		} else if (button.getId() == 4) {
			final GroupMember selectedRow = guiList.getSelectedRow();
			if (selectedRow != null) {
				The5zigMod.getVars().displayScreen(new GuiYesNo(this, new YesNoCallback() {
					@Override
					public void onDone(boolean yes) {
						if (yes) {
							The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(PacketPartyStatus.Action.TRANSFER_OWNER, selectedRow.getUniqueId()));
						}
					}

					@Override
					public String title() {
						return I18n.translate("party.manage_members.set_owner.confirm", selectedRow.getUsername());
					}
				}));
			}
		}
	}

	@Override
	public String getTitleKey() {
		return "party.manage_members.title";
	}
}
