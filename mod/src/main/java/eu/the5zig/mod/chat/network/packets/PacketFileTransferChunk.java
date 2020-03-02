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
import eu.the5zig.mod.chat.network.filetransfer.FileUploadTask;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PacketFileTransferChunk implements Packet {

	private int transferId;
	private int partId;
	private byte[] data;
	private int length;

	public PacketFileTransferChunk(int transferId, int partId, FileUploadTask.Chunk chunk) {
		this.transferId = transferId;
		this.partId = partId;
		this.data = chunk.getData();
		this.length = chunk.getLength();
	}

	public PacketFileTransferChunk() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.transferId = PacketBuffer.readVarIntFromBuffer(buffer);
		this.partId = PacketBuffer.readVarIntFromBuffer(buffer);
		this.data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeVarIntToBuffer(buffer, transferId);
		PacketBuffer.writeVarIntToBuffer(buffer, partId);
		buffer.writeBytes(data, 0, length);
	}

	@Override
	public void handle() {
		The5zigMod.getConversationManager().handleFileChunk(transferId, partId, data);
	}
}
