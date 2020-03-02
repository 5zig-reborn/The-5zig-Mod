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

package eu.the5zig.mod.chat.network;

import eu.the5zig.mod.chat.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NetworkSplitter extends MessageToByteEncoder<ByteBuf> {

	protected void encode(ChannelHandlerContext ctx, ByteBuf out, ByteBuf byteBuf) {
		int var4 = out.readableBytes();
		int var5 = PacketBuffer.getVarIntSize(var4);

		if (var5 > 3) {
			throw new IllegalArgumentException("unable to fit " + var4 + " into " + 3);
		} else {
			byteBuf.ensureWritable(var5 + var4);
			PacketBuffer.writeVarIntToBuffer(byteBuf, var4);
			byteBuf.writeBytes(out, out.readerIndex(), var4);
		}
	}

}
