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

package eu.the5zig.mod.chat;

import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.ConversationGroupChat;
import eu.the5zig.mod.chat.entity.Group;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GroupChatManager {

	private List<Group> groups = Lists.newArrayList();

	public void addGroup(Group group) {
		groups.add(group);
	}

	public void removeGroup(Group group) {
		groups.remove(group);
	}

	public Group getGroup(int id) {
		for (Group group : groups) {
			if (group.getId() == id)
				return group;
		}
		return null;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
		manageGroups();
	}

	private void manageGroups() {
		for (Group group : groups) {
			ConversationGroupChat conversationGroupChat = The5zigMod.getConversationManager().getConversation(group);
			if (group.getName().equals(conversationGroupChat.getName()))
				continue;
			The5zigMod.getConversationManager().setConversationName(conversationGroupChat, group.getName());
		}
	}

}
