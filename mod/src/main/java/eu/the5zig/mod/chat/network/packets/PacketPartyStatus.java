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

package eu.the5zig.mod.chat.network.packets;


import com.google.common.base.Strings;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import eu.the5zig.mod.chat.party.Party;
import eu.the5zig.mod.chat.party.PartyManager;
import eu.the5zig.mod.chat.party.handler.PartyServerHandler;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiParty;
import eu.the5zig.mod.gui.GuiPartyInviteMembers;
import eu.the5zig.mod.gui.GuiPartyManageMembers;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

public class PacketPartyStatus implements Packet {

	private Action action;

	private UUID memberId;

	private String server;

	private ChatMessageType chatMessageType;
	private String chatUsername;
	private String chatMessage;

	private User user;

	private boolean flag;

	public PacketPartyStatus(Action action) {
		this.action = action;
	}

	public PacketPartyStatus(Action action, UUID memberId) {
		this.action = action;
		this.memberId = memberId;
	}

	public PacketPartyStatus(Action action, String chatMessage) {
		this.action = action;
		this.chatMessage = chatMessage;
	}

	public PacketPartyStatus(String server) {
		this.action = Action.SWITCH_SERVER;
		this.server = server;
	}

	public PacketPartyStatus(UUID memberId, boolean promote) {
		this.action = Action.ADMIN;
		this.memberId = memberId;
		this.flag = promote;
	}

	public PacketPartyStatus() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		action = PacketBuffer.readEnum(buffer, Action.class);
		switch (action) {
			case MEMBER_ADD:
				user = PacketBuffer.readUser(buffer);
				break;
			case MEMBER_REMOVE:
			case TRANSFER_OWNER:
				memberId = PacketBuffer.readUUID(buffer);
				break;
			case ADMIN:
				memberId = PacketBuffer.readUUID(buffer);
				flag = buffer.readBoolean();
				break;
			case SWITCH_SERVER:
				server = PacketBuffer.readString(buffer);
				break;
			case SWITCH_SERVER_COMPLETE:
				memberId = PacketBuffer.readUUID(buffer);
				break;
			case CHAT:
				chatMessageType = PacketBuffer.readEnum(buffer, ChatMessageType.class);
				switch (chatMessageType) {
					case NORMAL:
						chatUsername = PacketBuffer.readString(buffer);
						chatMessage = PacketBuffer.readString(buffer);
						break;
					case BROADCAST:
						chatMessage = PacketBuffer.readString(buffer);
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeEnum(buffer, action);
		switch (action) {
			case MEMBER_REMOVE:
			case TRANSFER_OWNER:
			case SWITCH_SERVER_COMPLETE_RESPONSE:
				PacketBuffer.writeUUID(buffer, memberId);
				break;
			case ADMIN:
				PacketBuffer.writeUUID(buffer, memberId);
				buffer.writeBoolean(flag);
				break;
			case CHAT:
				PacketBuffer.writeString(buffer, chatMessage);
				break;
			default:
				break;
		}
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		PartyManager partyManager = The5zigMod.getPartyManager();
		Party party = partyManager.getParty();
		PartyServerHandler serverHandler = partyManager.getCurrentServerHandler();
		switch (action) {
			case MEMBER_ADD:
				party.getMembers().add(new GroupMember(user, GroupMember.MEMBER));
				Collections.sort(party.getMembers());
				partyManager.addBroadcast("party.messages.member_joined", user.getUsername());
				break;
			case MEMBER_REMOVE:
				GroupMember member = null;
				for (Iterator<GroupMember> iterator = party.getMembers().iterator(); iterator.hasNext(); ) {
					GroupMember next = iterator.next();
					if (next.getUniqueId().equals(memberId)) {
						iterator.remove();
						member = next;
					}
				}
				Collections.sort(party.getMembers());
				if (member != null) {
					partyManager.addBroadcast("party.messages.member_left", member.getUsername());
				}
				break;
			case TRANSFER_OWNER:
				UUID previousOwner = party.getOwner().getUniqueId();
				for (GroupMember groupMember : party.getMembers()) {
					if (groupMember.equals(party.getOwner())) {
						groupMember.setType(GroupMember.ADMIN);
					}
					if (groupMember.getUniqueId().equals(memberId)) {
						groupMember.setType(GroupMember.OWNER);
						party.setOwner(groupMember);
						partyManager.addBroadcast("party.messages.new_owner", groupMember.getUsername());
					}
				}
				Collections.sort(party.getMembers());
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty && (previousOwner.equals(The5zigMod.getDataManager().getUniqueId()) || memberId.equals(
						The5zigMod.getDataManager().getUniqueId()))) {
					The5zigMod.getVars().getCurrentScreen().initGui0();
				}
				if (previousOwner.equals(The5zigMod.getDataManager().getUniqueId()) && serverHandler != null) {
					String command = serverHandler.getPromoteCommand(party.getOwner().getUsername());
					if (command != null) {
						The5zigMod.getVars().sendMessage(command);
					}
				}
				break;
			case ADMIN:
				for (GroupMember groupMember : party.getMembers()) {
					if (groupMember.getUniqueId().equals(memberId)) {
						groupMember.setType(flag ? GroupMember.ADMIN : GroupMember.MEMBER);
						if (flag) {
							partyManager.addBroadcast("party.messages.promoted", groupMember.getUsername());
						} else {
							partyManager.addBroadcast("party.messages.demoted", groupMember.getUsername());
						}
					}
				}
				Collections.sort(party.getMembers());
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty && memberId.equals(The5zigMod.getDataManager().getUniqueId())) {
					The5zigMod.getVars().getCurrentScreen().initGui0();
				}
				break;
			case SWITCH_SERVER:
				party.setServer(server);
				if (!Strings.isNullOrEmpty(server)) {
					The5zigMod.getVars().joinServer(server.split(":")[0], Integer.parseInt(server.split(":")[1]));
					partyManager.addBroadcast("party.messages.switch_server", server);
				}
				break;
			case SWITCH_SERVER_COMPLETE:
				if (serverHandler != null) {
					for (GroupMember groupMember : party.getMembers()) {
						if (groupMember.getUniqueId().equals(memberId)) {
							The5zigMod.getVars().sendMessage(serverHandler.getInviteCommand(groupMember.getUsername()));
							The5zigMod.getNetworkManager().sendPacket(new PacketPartyStatus(Action.SWITCH_SERVER_COMPLETE_RESPONSE, memberId));
							break;
						}
					}
				}
				break;
			case SWITCH_SERVER_COMPLETE_RESPONSE:
				if (serverHandler != null) {
					The5zigMod.getVars().sendMessage(serverHandler.getAcceptCommand(party.getOwner().getUsername()));
				}
				break;
			case CHAT:
				Conversation conversation = party.getPartyConversation();
				Message message;
				switch (chatMessageType) {
					case NORMAL:
						message = new Message(conversation, 0, chatUsername, chatMessage, System.currentTimeMillis(), Message.MessageType.LEFT);
						break;
					case BROADCAST:
						message = new Message(conversation, 0, "", chatMessage, System.currentTimeMillis(), Message.MessageType.CENTERED);
						break;
					default:
						return;
				}
				partyManager.addMessage(message);
				if (chatMessageType == ChatMessageType.NORMAL && !(The5zigMod.getVars().getCurrentScreen() instanceof GuiParty)) {
					The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("party.messages.chat", chatUsername));
				}
				break;
			case CHAT_SENT:
				party.getPartyConversation().setStatus(Message.MessageStatus.SENT);
				break;
			case DELETE:
				partyManager.setParty(null);
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("party.messages.deleted"));
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty) {
					The5zigMod.getVars().getCurrentScreen().initGui0();
				}
				if (The5zigMod.getVars().getCurrentScreen() instanceof GuiPartyInviteMembers || The5zigMod.getVars().getCurrentScreen() instanceof GuiPartyManageMembers) {
					Gui gui = The5zigMod.getVars().getCurrentScreen();
					while (!(gui instanceof GuiParty)) {
						if (gui.lastScreen == null) {
							break;
						}
						gui = gui.lastScreen;
					}
					The5zigMod.getVars().displayScreen(gui);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public String toString() {
		return "PacketPartyStatus{" + "action=" + action + ", memberId=" + memberId + ", server='" + server + '\'' + ", chatMessageType=" + chatMessageType + ", chatUsername='" +
				chatUsername + '\'' + ", chatMessage='" + chatMessage + '\'' + ", user=" + user + ", flag=" + flag + '}';
	}

	public enum Action {
		MEMBER_ADD, MEMBER_REMOVE, ADMIN, TRANSFER_OWNER, SWITCH_SERVER, SWITCH_SERVER_COMPLETE, SWITCH_SERVER_COMPLETE_RESPONSE, CHAT, CHAT_SENT, DELETE
	}

	public enum ChatMessageType {
		NORMAL, BROADCAST
	}
}
