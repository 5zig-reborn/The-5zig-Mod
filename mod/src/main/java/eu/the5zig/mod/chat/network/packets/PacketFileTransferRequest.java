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
import eu.the5zig.mod.chat.network.util.PacketUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class PacketFileTransferRequest implements Packet {

	private UUID uuid;
	private int fileId;
	private PacketFileTransferStart.Type type;
	private long length;

	public PacketFileTransferRequest(UUID uuid, int fileId, PacketFileTransferStart.Type type, long length) {
		this.uuid = uuid;
		this.fileId = fileId;
		this.type = type;
		this.length = length;
	}

	public PacketFileTransferRequest() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.uuid = PacketBuffer.readUUID(buffer);
		this.fileId = PacketBuffer.readVarIntFromBuffer(buffer);
		this.type = PacketBuffer.readEnum(buffer, PacketFileTransferStart.Type.class);
		this.length = buffer.readLong();
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeUUID(buffer, uuid);
		PacketBuffer.writeVarIntToBuffer(buffer, fileId);
		PacketBuffer.writeEnum(buffer, type);
		buffer.writeLong(length);
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		if (The5zigMod.getConfig().getBool("playMessageSounds"))
			The5zigMod.getVars().playSound("the5zigmod", "chat.message.receive", 1);

		The5zigMod.getConversationManager().handleFileRequest(uuid, fileId, type, length);
	}

}
