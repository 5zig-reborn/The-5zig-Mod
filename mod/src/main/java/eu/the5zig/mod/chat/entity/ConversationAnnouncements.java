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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ConversationAnnouncements extends Conversation {

	/**
	 * Creates a new conversation instance of an announcement
	 *
	 * @param conversationId The id of the conversation
	 * @param lastUsed       The time when it has been last used.
	 * @param read           If the announcement has been read.
	 */
	public ConversationAnnouncements(int conversationId, long lastUsed, boolean read, Behaviour behaviour) {
		super(conversationId, lastUsed, read, Message.MessageStatus.PENDING, behaviour);
	}

	@Override
	public int getLineHeight() {
		return 18;
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().drawString((isRead() ? "" : ChatColor.BOLD) + I18n.translate("announcement.short_desc"), x + 2, y + 2);
	}

}
