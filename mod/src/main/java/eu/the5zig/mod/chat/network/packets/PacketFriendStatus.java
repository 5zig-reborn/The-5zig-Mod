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

package eu.the5zig.mod.chat.network.packets;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.LocaleUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PacketFriendStatus implements Packet {

	private UUID uuid;
	private FriendStatus friendStatus;
	private boolean enabled;
	private String status;
	private Rank rank;
	private long time;
	private Friend.OnlineStatus onlineStatus;

	public PacketFriendStatus(FriendStatus friendStatus, String status) {
		this.friendStatus = friendStatus;
		this.status = status;
	}

	public PacketFriendStatus(FriendStatus friendStatus, Friend.OnlineStatus status) {
		this.friendStatus = friendStatus;
		this.onlineStatus = status;
	}

	public PacketFriendStatus(FriendStatus friendStatus, boolean enabled) {
		this.friendStatus = friendStatus;
		this.enabled = enabled;
	}

	public PacketFriendStatus(UUID friend, boolean enabled) {
		this.friendStatus = FriendStatus.FAVORITE;
		this.uuid = friend;
		this.enabled = enabled;
	}

	public PacketFriendStatus() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		int i = buffer.readInt();
		if (i < 0 || i >= FriendStatus.values().length)
			throw new IllegalArgumentException("Received Integer is out of enum range");
		this.friendStatus = FriendStatus.values()[i];
		this.uuid = PacketBuffer.readUUID(buffer);
		if (friendStatus == FriendStatus.ONLINE_STATUS) {
			int ordinal = PacketBuffer.readVarIntFromBuffer(buffer);
			if (ordinal < 0 || ordinal >= Friend.OnlineStatus.values().length)
				throw new IllegalArgumentException("Received Integer is out of enum range");
			this.onlineStatus = Friend.OnlineStatus.values()[ordinal];
			if (onlineStatus == Friend.OnlineStatus.OFFLINE) {
				this.time = buffer.readLong();
			}
		}
		if (friendStatus == FriendStatus.PROFILE_MESSAGE || friendStatus == FriendStatus.SERVER || friendStatus == FriendStatus.LOBBY || friendStatus == FriendStatus.LOCALE) {
			this.status = PacketBuffer.readString(buffer);
		}
		if (friendStatus == FriendStatus.FAVORITE) {
			this.enabled = buffer.readBoolean();
		}
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		buffer.writeInt(friendStatus.ordinal());
		if (friendStatus == FriendStatus.SERVER || friendStatus == FriendStatus.LOBBY) {
			PacketBuffer.writeString(buffer, status);
		}
		if (friendStatus == FriendStatus.FAVORITE) {
			PacketBuffer.writeUUID(buffer, uuid);
			buffer.writeBoolean(enabled);
		}
		if (friendStatus == FriendStatus.ONLINE_STATUS) {
			PacketBuffer.writeVarIntToBuffer(buffer, onlineStatus.ordinal());
		}
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		Friend friend = The5zigMod.getFriendManager().getFriend(uuid);
		if (friend == null)
			return;
		if (friendStatus == FriendStatus.ONLINE_STATUS) {
			if (The5zigMod.getConfig().getBool("showOnlineMessages")) {
				if (friend.getStatus() == Friend.OnlineStatus.AWAY && onlineStatus == Friend.OnlineStatus.ONLINE) {
					String theStatus = Friend.OnlineStatus.AWAY.getDisplayName().toLowerCase(Locale.ROOT);
					The5zigMod.getOverlayMessage().displayMessageAndSplit(
							ChatColor.YELLOW + I18n.translate("friend.online_message.no_longer", friend.getUsername(), theStatus + ChatColor.YELLOW));
				} else {
					String theStatus = onlineStatus.getDisplayName().toLowerCase(Locale.ROOT) + ChatColor.YELLOW;
					The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("friend.online_message", friend.getUsername(), theStatus));
					if (onlineStatus == Friend.OnlineStatus.OFFLINE && The5zigMod.getDataManager().getChatTypingManager().isTyping(uuid))
						The5zigMod.getDataManager().getChatTypingManager().removeFromTyping(uuid);
				}
			}
			friend.setStatus(onlineStatus);
			if (onlineStatus == Friend.OnlineStatus.OFFLINE) {
				friend.setLastOnline(System.currentTimeMillis());
			}
			The5zigMod.getFriendManager().sortFriends();
		}
		if (friendStatus == FriendStatus.PROFILE_MESSAGE) {
			friend.setStatusMessage(status);
		}
		if (friendStatus == FriendStatus.SERVER) {
			friend.setServer(status.isEmpty() ? null : status);
		}
		if (friendStatus == FriendStatus.LOBBY) {
			friend.setLobby(status.isEmpty() ? null : status);
		}
		if (friendStatus == FriendStatus.CHANGE_NAME) {
			friend.setUsername(status);
			The5zigMod.getConversationManager().updateConversationNames(friend);
		}
		if (friendStatus == FriendStatus.LOCALE) {
			friend.setLocale(status.isEmpty() ? null : LocaleUtils.toLocale(status));
		}
		if (friendStatus == FriendStatus.FAVORITE) {
			friend.setFavorite(enabled);
			The5zigMod.getFriendManager().sortFriends();
		}
	}

	public enum FriendStatus {
		ONLINE_STATUS, PROFILE_MESSAGE, SERVER, LOBBY, CHANGE_NAME, FAVORITE, LOCALE
	}

}
