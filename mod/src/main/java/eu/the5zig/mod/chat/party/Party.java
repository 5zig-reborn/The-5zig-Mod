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

import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.Conversation;
import eu.the5zig.mod.chat.entity.ConversationGroupChat;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.chat.entity.User;

import java.util.List;

public class Party {

	private User owner;
	private long created;
	private String server;
	private List<GroupMember> members;

	private final Conversation partyConversation;

	public Party(User owner, long created, String server, List<GroupMember> members) {
		this.owner = owner;
		this.created = created;
		this.server = server;
		this.members = members;
		this.partyConversation = new ConversationGroupChat(0, 0, owner.getUsername(), created, true, Message.MessageStatus.PENDING, Conversation.Behaviour.DEFAULT);
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public List<GroupMember> getMembers() {
		return members;
	}

	public void setMembers(List<GroupMember> members) {
		this.members = members;
	}

	public Conversation getPartyConversation() {
		return partyConversation;
	}
}
