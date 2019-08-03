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

package eu.the5zig.mod.chat.network;

import com.google.common.collect.Maps;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.chat.network.packets.*;

import java.util.Map;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Protocol {

	public static final int VERSION = Version.PROTOCOL;

	private Map<Integer, Class> packets = Maps.newHashMap();
	private Map<Class, ConnectionState> protocolMap = Maps.newHashMap();

	public Protocol() {
		// 0x0x for general stuff
		register(0x00, PacketHandshake.class, ConnectionState.HANDSHAKE);
		register(0x01, PacketStartLogin.class, ConnectionState.LOGIN);
		register(0x02, PacketLogin.class, ConnectionState.LOGIN);
		register(0x03, PacketWelcome.class, ConnectionState.PLAY);
		register(0x04, PacketOverlay.class, ConnectionState.ALL);
		register(0x05, PacketHeartbeat.class, ConnectionState.ALL);
		register(0x06, PacketBanned.class, ConnectionState.ALL);
		register(0x07, PacketCompression.class, ConnectionState.LOGIN);
		register(0x08, PacketEncryption.class, ConnectionState.LOGIN);
		register(0x09, PacketDisconnect.class, ConnectionState.ALL);

		// 0xax for other general stuff
		register(0xa0, PacketClientStats.class, ConnectionState.PLAY);
		register(0xa1, PacketServerStats.class, ConnectionState.PLAY);
		register(0xa2, PacketProfile.class, ConnectionState.PLAY);
		register(0xa3, PacketAnnouncement.class, ConnectionState.PLAY);
		register(0xa4, PacketAnnouncementList.class, ConnectionState.PLAY);
		register(0xa5, PacketCapeSettings.class, ConnectionState.PLAY);

		// 0x1x for Friend stuff
		register(0x10, PacketFriendList.class, ConnectionState.PLAY);
		register(0x11, PacketMessageFriend.class, ConnectionState.PLAY);
		register(0x12, PacketFriendStatus.class, ConnectionState.PLAY);
		register(0x14, PacketMessageFriendStatus.class, ConnectionState.PLAY);

		// 0x2x for Friend Request stuff
		register(0x20, PacketFriendRequest.class, ConnectionState.PLAY);
		register(0x21, PacketFriendRequestList.class, ConnectionState.PLAY);
		register(0x22, PacketFriendRequestResponse.class, ConnectionState.PLAY);
		register(0x23, PacketNewFriend.class, ConnectionState.PLAY);
		register(0x24, PacketNewFriendRequest.class, ConnectionState.PLAY);
		register(0x25, PacketDeleteFriend.class, ConnectionState.PLAY);
		register(0x26, PacketTyping.class, ConnectionState.PLAY);
		register(0x27, PacketUserSearch.class, ConnectionState.PLAY);

		// 0x3x for Blocked User stuff
		register(0x30, PacketAddBlockedUser.class, ConnectionState.PLAY);
		register(0x31, PacketDeleteBlockedUser.class, ConnectionState.PLAY);
		register(0x32, PacketBlockedUserList.class, ConnectionState.PLAY);

		// 0x4x for Group Chat stuff
		register(0x40, PacketCreateGroupChat.class, ConnectionState.PLAY);
		register(0x41, PacketGroupChatMessage.class, ConnectionState.PLAY);
		register(0x42, PacketGroupChatList.class, ConnectionState.PLAY);
		register(0x43, PacketLeaveGroupChat.class, ConnectionState.PLAY);
		register(0x44, PacketDeleteGroupChat.class, ConnectionState.PLAY);
		register(0x45, PacketGroupChatMessageStatusSent.class, ConnectionState.PLAY);
		register(0x46, PacketGroupChatStatus.class, ConnectionState.PLAY);
		register(0x47, PacketGroupBroadcast.class, ConnectionState.PLAY);

		// 0x5x for File Transfer stuff
		register(0x50, PacketFileTransferRequest.class, ConnectionState.PLAY);
		register(0x51, PacketFileTransferResponse.class, ConnectionState.PLAY);
		register(0x52, PacketFileTransferId.class, ConnectionState.PLAY);
		register(0x53, PacketFileTransferStart.class, ConnectionState.PLAY);
		register(0x54, PacketFileTransferStartResponse.class, ConnectionState.PLAY);
		register(0x55, PacketFileTransferChunk.class, ConnectionState.PLAY);
		register(0x59, PacketFileTransferAbort.class, ConnectionState.PLAY);

		// 0x6x for Party stuff
		register(0x60, PacketPartyInvite.class, ConnectionState.PLAY);
		register(0x61, PacketPartyInviteResponse.class, ConnectionState.PLAY);
		register(0x62, PacketPartyInviteDelete.class, ConnectionState.PLAY);
		register(0x63, PacketPartyStatus.class, ConnectionState.PLAY);

		register(0x90, PacketAuthToken.class, ConnectionState.PLAY);
		register(0x91, PacketNewMessages.class, ConnectionState.PLAY);
	}

	private void register(int id, Class packet, ConnectionState state) {
		if (packets.containsKey(id)) {
			throw new RuntimeException("Packet with id " + id + " is already registered!");
		}

		try {
			packet.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Packet with id " + id + " has no default constructor!");
		}

		packets.put(id, packet);
		protocolMap.put(packet, state);
	}

	public int getPacketId(Packet packet) {
		for (Map.Entry<Integer, Class> entry : packets.entrySet()) {
			Class c = entry.getValue();
			if (c.isInstance(packet))
				return entry.getKey();
		}
		throw new RuntimeException("Packet " + packet + " is not registered!");
	}

	public Packet getPacket(int id) throws IllegalAccessException, InstantiationException {
		if (!packets.containsKey(id)) {
			throw new RuntimeException("Could not get unregistered packet (" + id + ")!");
		}

		return (Packet) packets.get(id).newInstance();
	}

	public ConnectionState getProtocol(int id) throws InstantiationException, IllegalAccessException {
		for (Map.Entry<Class, ConnectionState> entry : protocolMap.entrySet()) {
			if (entry.getKey().equals(getPacket(id).getClass()))
				return entry.getValue();
		}
		throw new RuntimeException("Packet " + id + " is not registered!");
	}

}
