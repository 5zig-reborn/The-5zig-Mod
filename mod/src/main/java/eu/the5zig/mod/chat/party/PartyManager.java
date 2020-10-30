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

package eu.the5zig.mod.chat.party;

import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.mod.chat.party.handler.*;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.ServerJoinEvent;
import eu.the5zig.mod.event.ServerQuitEvent;
import eu.the5zig.mod.gui.GuiParty;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PartyManager {

	private Party party;
	private final GuiArrayList<PartyOwner> partyInvitations = new GuiArrayList<>();

	private final List<PartyServerHandler> serverHandlers = ImmutableList.of(new BadLionHandler(), new BergwerglabsHandler(), new CytooxienHandler(), new DustMCHandler(),
			new GommeHDHandler(), new HiveMCHandler(), new HypixelHandler(), new MineplexHandler(), new PlayMinityHandler(), new RewinsideHandler(), new TimoliaHandler());
	private PartyServerHandler currentServerHandler;

	public PartyManager() {
		The5zigMod.getListener().registerListener(this);
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
		if(party != null) {
			Collections.sort(party.getMembers());
		}
	}

	public boolean addPartyInvitation(User user) {
		final PartyOwner partyOwner = new PartyOwner(user.getUsername(), user.getUniqueId());
		if (partyInvitations.contains(partyOwner)) {
			partyInvitations.get(partyInvitations.indexOf(partyOwner)).time = System.currentTimeMillis();
			return false;
		} else {
			partyInvitations.add(partyOwner);
			return true;
		}
	}

	public GuiArrayList<PartyOwner> getPartyInvitations() {
		return partyInvitations;
	}

	@EventHandler
	public void onJoin(ServerJoinEvent event) {
		for (PartyServerHandler serverHandler : serverHandlers) {
			if (serverHandler.match(event.getHost(), event.getPort())) {
				currentServerHandler = serverHandler;
				break;
			}
		}
	}

	@EventHandler
	public void onQuit(ServerQuitEvent event) {
		currentServerHandler = null;
	}

	public PartyServerHandler getCurrentServerHandler() {
		return currentServerHandler;
	}

	public void addBroadcast(String key, Object... values) {
		if (party == null) {
			return;
		}
		if (I18n.has(key + ".broadcast")) {
			addMessage(new Message(party.getPartyConversation(), 0, "", I18n.translate(key + ".broadcast", values), System.currentTimeMillis(), Message.MessageType.CENTERED));
		}
		if (I18n.has(key + ".overlay")) {
			The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate(key + ".overlay", values));
		}
	}

	public void addMessage(Message message) {
		if (party == null) {
			return;
		}
		checkNewDay(party.getPartyConversation(), message);
		party.getPartyConversation().addMessage(message);
		addChatLineToGui(message);

		if (The5zigMod.getConfig().getBool("playMessageSounds")) {
			The5zigMod.getVars().playSound("the5zigmod", message.getMessageType() == Message.MessageType.RIGHT ? "chat.message.send" : "chat.message.receive", 1);
		}
	}

	private void checkNewDay(final Conversation conversation, final Message newMessage) {
		if (!conversation.getMessages().isEmpty() && Utils.isSameDay(newMessage.getTime(), conversation.getMessages().get(conversation.getMessages().size() - 1).getTime()))
			return;
		final long time = newMessage.getTime() - 1;
		final Message dateMessage = new Message(conversation, 0, "", "", time, Message.MessageType.DATE);
		conversation.addMessage(dateMessage);
		addChatLineToGui(dateMessage);
	}

	private void addChatLineToGui(Message message) {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty) {
			GuiParty gui = (GuiParty) The5zigMod.getVars().getCurrentScreen();
			gui.chatLines.add(ChatLine.fromMessage(message));
			gui.chatList.scrollToBottom();
		}
	}

	public class PartyOwner extends User {

		private long time;

		public PartyOwner(String username, UUID uuid) {
			super(username, uuid);
			this.time = System.currentTimeMillis();
		}
	}

}
