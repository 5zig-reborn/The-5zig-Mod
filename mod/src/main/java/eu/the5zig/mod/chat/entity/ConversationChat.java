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

package eu.the5zig.mod.chat.entity;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ConversationChat extends Conversation {

	private final UUID friendUUID;
	private String friendName;

	/**
	 * Creates a new conversation instance of a normal chat
	 *
	 * @param conversationId The id of the conversation
	 * @param friendName     The name of the friend
	 * @param friendUUID     The uuid of the friend
	 * @param lastUsed       The time when it has been last used.
	 * @param read           If the announcement has been read.
	 */
	public ConversationChat(int conversationId, String friendName, UUID friendUUID, long lastUsed, boolean read, Message.MessageStatus status, Behaviour behaviour) {
		super(conversationId, lastUsed, read, status, behaviour);
		this.friendName = friendName;
		this.friendUUID = friendUUID;
	}

	public String getFriendName() {
		return friendName;
	}

	public void setFriendName(String friendName) {
		this.friendName = friendName;
	}

	public UUID getFriendUUID() {
		return friendUUID;
	}

	@Override
	public int getLineHeight() {
		return 18;
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().drawString((isRead() ? "" : ChatColor.BOLD) + friendName, x + 2, y + 2);
	}
}
