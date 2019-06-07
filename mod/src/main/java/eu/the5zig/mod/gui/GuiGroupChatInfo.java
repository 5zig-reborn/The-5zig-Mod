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
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.Group;
import eu.the5zig.mod.chat.network.packets.PacketGroupChatStatus;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved ? 2015
 */
public class GuiGroupChatInfo extends Gui {

	private Group group;
	private IGuiList<GroupMember> groupMemberList;
	private int lastSelected;

	public GuiGroupChatInfo(Gui lastScreen, Group group) {
		super(lastScreen);
		this.group = group;
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		List split = The5zigMod.getVars().splitStringToWidth(I18n.translate("group.info.title", group.getName(), group.getOwner().getUsername()), getWidth() / 3 * 2);
		int y = 5;
		for (Object o : split) {
			drawCenteredString(String.valueOf(o), getWidth() / 2, y += 10);
		}
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));

		groupMemberList = The5zigMod.getVars().createGuiList(new Clickable<GroupMember>() {
			@Override
			public void onSelect(int id, GroupMember row, boolean doubleClick) {
				lastSelected = group.getMembers().indexOf(row);
			}
		}, getWidth(), getHeight(), 50, getHeight() - 50, 0, getWidth(), group.getMembers());
		groupMemberList.setRowWidth(200);
		groupMemberList.setScrollX(getWidth() - 15);
		addGuiList(groupMemberList);

		groupMemberList.setSelectedId(lastSelected);
		GroupMember selected = (GroupMember) groupMemberList.getSelectedRow();
		groupMemberList.onSelect(group.getMembers().indexOf(selected), groupMemberList.getSelectedRow(), false);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 202, getHeight() - 38, 98, 20, I18n.translate("group.info.kick_player")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 100, getHeight() - 38, 98, 20, I18n.translate("group.info.admin.set")));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2 + 2, getHeight() - 38, 98, 20, I18n.translate("group.info.transfer_owner")));
		addButton(The5zigMod.getVars().createButton(4, getWidth() / 2 + 104, getHeight() - 38, 98, 20, I18n.translate("group.info.settings")));
	}

	@Override
	protected void tick() {
		GroupMember selectedRow = (GroupMember) groupMemberList.getSelectedRow();
		boolean enableAdmin = group.isAdmin(The5zigMod.getDataManager().getUniqueId());
		boolean enableOwner = group.getOwner().getUniqueId().equals(The5zigMod.getDataManager().getUniqueId());
		boolean isNotSelf = selectedRow != null && !selectedRow.getUniqueId().equals(The5zigMod.getDataManager().getUniqueId());

		getButtonById(1).setEnabled(enableAdmin && isNotSelf && selectedRow.getType() == GroupMember.MEMBER);
		getButtonById(2).setEnabled(isNotSelf && enableOwner);
		getButtonById(2).setLabel(selectedRow != null && (selectedRow.isAdmin() || selectedRow.getType() == GroupMember.OWNER) ? I18n.translate("group.info.admin.remove") :
				I18n.translate("group.info.admin.set"));
		getButtonById(3).setEnabled(isNotSelf && enableOwner);
		getButtonById(4).setEnabled(enableAdmin || enableOwner);
	}

	@Override
	public String getTitleName() {
		return "";
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 4) {
			The5zigMod.getVars().displayScreen(new GuiGroupChatSettings(this, group));
		}

		final GroupMember selectedRow = (GroupMember) groupMemberList.getSelectedRow();
		if (selectedRow == null)
			return;
		if (button.getId() == 1) {
			The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatStatus(group.getId(), PacketGroupChatStatus.GroupAction.REMOVE_PLAYER, selectedRow.getUniqueId()));
		}
		if (button.getId() == 2) {
			The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatStatus(group.getId(), PacketGroupChatStatus.GroupAction.ADMIN, selectedRow.getUniqueId(), !selectedRow.isAdmin()));
		}
		if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiYesNo(this, new YesNoCallback() {
				@Override
				public void onDone(boolean yes) {
					if (yes) {
						The5zigMod.getNetworkManager().sendPacket(new PacketGroupChatStatus(group.getId(), PacketGroupChatStatus.GroupAction.OWNER, selectedRow.getUniqueId()));
						The5zigMod.getVars().displayScreen(lastScreen);
					}
				}

				@Override
				public String title() {
					return I18n.translate("group.info.confirm.owner");
				}
			}));
		}
	}

}
