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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PacketMessageFriendStatus implements Packet {

	private UUID friend;
	private Message.MessageStatus messageStatus;

	public PacketMessageFriendStatus(UUID friend, Message.MessageStatus messageStatus) {
		this.friend = friend;
		this.messageStatus = messageStatus;
	}

	public PacketMessageFriendStatus() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.friend = PacketBuffer.readUUID(buffer);
		this.messageStatus = Message.MessageStatus.values()[PacketBuffer.readVarIntFromBuffer(buffer)];
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeUUID(buffer, friend);
		PacketBuffer.writeVarIntToBuffer(buffer, messageStatus.ordinal());
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);
		Friend f = The5zigMod.getFriendManager().getFriend(friend);
		if (f == null || !The5zigMod.getConversationManager().conversationExists(f))
			return;
		The5zigMod.getConversationManager().setConversationStatus(The5zigMod.getConversationManager().getConversation(f), messageStatus);
	}
}
