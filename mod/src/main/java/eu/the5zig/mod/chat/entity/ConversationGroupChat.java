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

package eu.the5zig.mod.chat.entity;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ConversationGroupChat extends Conversation {

	private final int groupId;
	private String name;

	/**
	 * Creates a new conversation instance of a group chat
	 *
	 * @param conversationId The id of the conversation
	 * @param groupId        The id of the group
	 * @param lastUsed       The time when it has been last used.
	 * @param read           If the conversation has been read.
	 */
	public ConversationGroupChat(int conversationId, int groupId, String name, long lastUsed, boolean read, Message.MessageStatus status, Behaviour behaviour) {
		super(conversationId, lastUsed, read, status, behaviour);
		this.groupId = groupId;
		this.name = name;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getLineHeight() {
		return 18;
	}

	@Override
	public void draw(int x, int y) {
		String displayName = name;
		boolean changed = false;
		while (The5zigMod.getVars().getStringWidth((!isRead() ? ChatColor.BOLD : "") + displayName) > 92 - The5zigMod.getVars().getStringWidth("...")) {
			displayName = displayName.substring(0, displayName.length() - 1);
			changed = true;
		}
		if (changed)
			displayName += "...";
		The5zigMod.getVars().drawString((isRead() ? "" : ChatColor.BOLD) + displayName, x + 2, y + 2);
	}
}
