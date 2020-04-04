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

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.FriendRow;
import eu.the5zig.mod.chat.network.packets.PacketPartyInvite;
import eu.the5zig.mod.chat.party.Party;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.ITextfield;
import eu.the5zig.mod.manager.SearchEntry;
import eu.the5zig.util.Callback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.*;

public class GuiPartyInviteMembers extends Gui {

	private IGuiList<FriendRow> guiListAllFriends;
	private IGuiList<FriendRow> guiListInvitedFriends;
	private List<FriendRow> allFriends = Lists.newArrayList();
	private List<FriendRow> invitedFriends = Lists.newArrayList();

	private ITextfield textfield, textfield1;

	@SuppressWarnings("unlikely-arg-type")
	public GuiPartyInviteMembers(Gui lastScreen) {
		super(lastScreen);
		Party party = The5zigMod.getPartyManager().getParty();
		for (Friend friend : The5zigMod.getFriendManager().getFriends()) {
			if (friend.getStatus() != Friend.OnlineStatus.OFFLINE) {
				//noinspection SuspiciousMethodCalls
				if ((party == null || !party.getMembers().contains(friend)) && !The5zigMod.getPartyManager().getPartyInvitations().contains(friend)) {
					allFriends.add(new FriendRow(friend));
				}
			}
		}
	}

	@Override
	public void initGui() {
		guiListAllFriends = The5zigMod.getVars().createGuiList(null, getWidth() / 2, getHeight(), 68, getHeight() - 40, getWidth() / 2 - 130, getWidth() / 2 - 30, allFriends);
		guiListAllFriends.setLeftbound(true);
		guiListAllFriends.setScrollX(getWidth() / 2 - 35);
		guiListInvitedFriends = The5zigMod.getVars().createGuiList(null, getWidth() / 2, getHeight(), 68, getHeight() - 40, getWidth() / 2 + 30, getWidth() / 2 + 130, invitedFriends);
		guiListInvitedFriends.setLeftbound(true);
		guiListInvitedFriends.setScrollX(getWidth() / 2 + 125);
		addGuiList(guiListAllFriends);
		addGuiList(guiListInvitedFriends);

		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 30, 150, 20, I18n.translate("gui.back")));
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 + 5, getHeight() - 30, 150, 20, I18n.translate("party.invite_members.invite"), false));

		addButton(The5zigMod.getVars().createButton(5, getWidth() / 2 - 20, getHeight() / 2 - 40, 40, 20, ">>"));
		addButton(The5zigMod.getVars().createButton(6, getWidth() / 2 - 20, getHeight() / 2 - 15, 40, 20, "<<"));

		textfield = The5zigMod.getVars().createTextfield(I18n.translate("gui.search"), 9991, getWidth() / 2 - 130, 50, 100, 16);
		SearchEntry<FriendRow> searchEntry = new SearchEntry<FriendRow>(textfield, allFriends) {
			@Override
			public boolean filter(String text, FriendRow o) {
				Friend friend = o.friend;
				return friend.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT));
			}
		};
		searchEntry.setAlwaysVisible(true);
		searchEntry.setEnterCallback(new Callback<FriendRow>() {
			@Override
			public void call(FriendRow callback) {
				actionPerformed0(getButtonById(5));
				textfield.callSetText("");
			}
		});
		textfield1 = The5zigMod.getVars().createTextfield(I18n.translate("gui.search"), 9992, getWidth() / 2 + 30, 50, 100, 16);
		SearchEntry<FriendRow> searchEntry1 = new SearchEntry<FriendRow>(textfield1, invitedFriends) {
			@Override
			public boolean filter(String text, FriendRow o) {
				Friend friend = o.friend;
				return friend.getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT));
			}
		};
		searchEntry1.setAlwaysVisible(true);
		searchEntry1.setEnterCallback(new Callback<FriendRow>() {
			@Override
			public void call(FriendRow callback) {
				actionPerformed0(getButtonById(6));
				textfield1.callSetText("");
			}
		});
		The5zigMod.getDataManager().getSearchManager().addSearch(searchEntry, searchEntry1);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("party.invite_members.friends.online", allFriends.size()), getWidth() / 2 - 130, 36);
		The5zigMod.getVars().drawString(I18n.translate("party.invite_members.friends.invited", ChatColor.RESET.toString() + invitedFriends.size() + ChatColor.RESET), getWidth() / 2 + 30,
				36);
		The5zigMod.getDataManager().getSearchManager().draw();
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		The5zigMod.getDataManager().getSearchManager().mouseClicked(x, y, button);
	}

	@Override
	protected void onKeyType(char character, int key) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(character, key);
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	protected void tick() {
		getButtonById(1).setEnabled(!invitedFriends.isEmpty());

		getButtonById(5).setEnabled(guiListAllFriends.getSelectedRow() != null);
		getButtonById(6).setEnabled(guiListInvitedFriends.getSelectedRow() != null);

		if (textfield.callGetText().isEmpty()) {
			removeOfflineFriends(allFriends);
			for (Friend friend : The5zigMod.getFriendManager().getFriends()) {
				if (friend.getStatus() != Friend.OnlineStatus.OFFLINE) {
					boolean exists = false;
					for (FriendRow friendRow : allFriends) {
						if (friendRow.friend.equals(friend)) {
							exists = true;
							break;
						}
					}
					for (FriendRow friendRow : invitedFriends) {
						if (friendRow.friend.equals(friend)) {
							exists = true;
							break;
						}
					}
					Party party = The5zigMod.getPartyManager().getParty();
					//noinspection SuspiciousMethodCalls
					if (!exists && ((party == null || !party.getMembers().contains(friend)) && !The5zigMod.getPartyManager().getPartyInvitations().contains(friend))) {
						allFriends.add(new FriendRow(friend));
					}
				}
			}
		}
		if (textfield1.callGetText().isEmpty()) {
			removeOfflineFriends(invitedFriends);
		}
	}

	private void removeOfflineFriends(List<FriendRow> list) {
		for (Iterator<FriendRow> iterator = list.iterator(); iterator.hasNext(); ) {
			if (iterator.next().friend.getStatus() == Friend.OnlineStatus.OFFLINE) {
				iterator.remove();
			}
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (invitedFriends.isEmpty())
				return;
			List<UUID> players = Lists.newArrayList();
			for (FriendRow invitedFriend : invitedFriends) {
				players.add(invitedFriend.friend.getUniqueId());
			}

			The5zigMod.getNetworkManager().sendPacket(new PacketPartyInvite(players));

			The5zigMod.getVars().displayScreen(lastScreen);
		}
		if (button.getId() == 5) {
			FriendRow selectedRow = guiListAllFriends.getSelectedRow();
			if (selectedRow != null) {
				allFriends.remove(selectedRow);
				invitedFriends.add(selectedRow);
				Collections.sort(allFriends);
				Collections.sort(invitedFriends);
			}
		}
		if (button.getId() == 6) {
			FriendRow selectedRow = guiListInvitedFriends.getSelectedRow();
			if (selectedRow != null) {
				allFriends.add(selectedRow);
				invitedFriends.remove(selectedRow);
				Collections.sort(allFriends);
				Collections.sort(invitedFriends);
			}
		}
	}

	@Override
	public String getTitleKey() {
		return "party.invite_members.title";
	}
}
