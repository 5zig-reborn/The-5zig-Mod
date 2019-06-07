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
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.FriendSuggestion;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketFriendRequestResponse;
import eu.the5zig.mod.chat.sql.FriendSuggestionEntity;
import eu.the5zig.mod.gui.GuiFriends;
import eu.the5zig.util.db.Database;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class FriendManager {

	private List<Friend> friends = Lists.newArrayList();
	private List<User> friendRequests = Lists.newArrayList();
	private List<User> blockedUsers = Lists.newArrayList();

	private List<FriendSuggestion> suggestions = Lists.newArrayList();

	public FriendManager() {
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiFriends) {
			this.friends.clear();
			this.friends.addAll(friends);
			for (Friend friend : this.friends) {
				The5zigMod.getConversationManager().updateConversationNames(friend);
			}
			sortFriends();
			return;
		}
		this.friends.clear();
		this.friends.addAll(friends);
		for (Friend friend : this.friends) {
			The5zigMod.getConversationManager().updateConversationNames(friend);
		}
		sortFriends();
	}

	public List<User> getFriendRequests() {
		return friendRequests;
	}

	public void setFriendRequests(List<User> friendRequests) {
		this.friendRequests.clear();
		this.friendRequests.addAll(friendRequests);
	}

	public List<User> getBlockedUsers() {
		return blockedUsers;
	}

	public void setBlockedUsers(List<User> blockedUsers) {
		this.blockedUsers.clear();
		this.blockedUsers.addAll(blockedUsers);
	}

	public Friend getFriend(UUID uuid) {
		for (Friend friend : friends) {
			if (friend.getUniqueId().equals(uuid))
				return friend;
		}
		return null;
	}

	public User getBlocked(UUID friendUUID) {
		for (User blockedUser : blockedUsers) {
			if (friendUUID.equals(blockedUser.getUniqueId()))
				return blockedUser;
		}
		return null;
	}

	public void handleFriendRequestResponse(UUID friend, boolean accepted) {
		The5zigMod.getNetworkManager().sendPacket(new PacketFriendRequestResponse(friend, accepted));
		for (Iterator<User> iterator = friendRequests.iterator(); iterator.hasNext(); ) {
			User user = iterator.next();
			if (user.getUniqueId().equals(friend))
				iterator.remove();
		}
	}

	public void addFriend(Friend friend) {
		friends.add(friend);
		if (isSuggested(friend.getUniqueId())) {
			removeSuggestion(friend.getUniqueId());
		}
		The5zigMod.getConversationManager().updateConversationNames(friend);
		sortFriends();
	}

	public void addFriendRequest(User friendRequest) {
		friendRequests.add(friendRequest);
		if (isSuggested(friendRequest.getUniqueId())) {
			removeSuggestion(friendRequest.getUniqueId());
		}
	}

	public void removeBlockedUser(UUID blockedUser) {
		for (Iterator<User> iterator = blockedUsers.iterator(); iterator.hasNext(); ) {
			User user = iterator.next();
			if (user.getUniqueId().equals(blockedUser))
				iterator.remove();
		}
	}

	public void addBlockedUser(User blockedUser) {
		blockedUsers.add(blockedUser);
	}

	public void removeFriend(UUID friend) {
		for (Iterator<Friend> iterator = friends.iterator(); iterator.hasNext(); ) {
			Friend f = iterator.next();
			if (f.getUniqueId().equals(friend)) {
				iterator.remove();
				break;
			}
		}
		sortFriends();
	}

	public boolean isBlocked(UUID user) {
		return getBlocked(user) != null;
	}

	public boolean isFriend(UUID friendUUID) {
		return getFriend(friendUUID) != null;
	}

	public boolean isFriend(String name) {
		for (Friend friend : friends) {
			if (friend.getUsername().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void sortFriends() {
		Collections.sort(friends);
	}

	public void loadSuggestions(Database sql) {
		sql.update("CREATE TABLE IF NOT EXISTS friend_suggestions (id INT AUTO_INCREMENT PRIMARY KEY, uuid VARCHAR(36) UNIQUE, name VARCHAR(16), hide BOOLEAN)");
		List<FriendSuggestionEntity> all = sql.get(FriendSuggestionEntity.class).query("SELECT * FROM friend_suggestions").getAll();
		for (FriendSuggestionEntity entity : all) {
			suggestions.add(new FriendSuggestion(entity.getName(), UUID.fromString(entity.getUuid()), entity.isHide()));
		}
	}

	public List<User> getShownSuggestions() {
		List<User> users = Lists.newArrayList();
		for (FriendSuggestion suggestion : suggestions) {
			if (!suggestion.isHidden()) {
				users.add(suggestion);
			}
		}
		return users;
	}

	public int addSuggestions(User[] users) {
		int newSuggestions = 0;
		for (User user : users) {
			if (isSuggested(user.getUniqueId()) || isFriend(user.getUniqueId()))
				continue;
			newSuggestions++;
			suggestions.add(new FriendSuggestion(user.getUsername(), user.getUniqueId(), false));
			The5zigMod.getConversationManager().queueStatement("INSERT INTO friend_suggestions (uuid, name, hide) VALUES (?, ?, 0)", user.getUniqueId().toString(), user.getUsername());
		}
		return newSuggestions;
	}

	public boolean isSuggested(String name) {
		for (User suggestion : suggestions) {
			if (suggestion.getUsername().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSuggested(UUID uuid) {
		for (User suggestion : suggestions) {
			if (suggestion.getUniqueId().equals(uuid)) {
				return true;
			}
		}
		return false;
	}

	public void hideSuggestion(UUID uuid) {
		for (FriendSuggestion suggestion : suggestions) {
			if (suggestion.getUniqueId().equals(uuid)) {
				suggestion.setHidden(true);
				The5zigMod.getConversationManager().queueStatement("UPDATE friend_suggestions SET hide=1 WHERE uuid=?", uuid.toString());
				break;
			}
		}
	}

	public void removeSuggestion(UUID uuid) {
		for (Iterator<FriendSuggestion> iterator = suggestions.iterator(); iterator.hasNext(); ) {
			FriendSuggestion next = iterator.next();
			if (next.getUniqueId().equals(uuid)) {
				iterator.remove();
				The5zigMod.getConversationManager().queueStatement("DELETE FROM friend_suggestions WHERE uuid=?", uuid.toString());
				break;
			}
		}
	}
}
