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

import eu.the5zig.mod.chat.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NetworkPrepender extends ByteToMessageDecoder {

	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> objects) {
		buffer.markReaderIndex();
		byte[] var4 = new byte[3];

		for (int var5 = 0; var5 < var4.length; ++var5) {
			if (!buffer.isReadable()) {
				buffer.resetReaderIndex();
				return;
			}

			var4[var5] = buffer.readByte();

			if (var4[var5] >= 0) {
				ByteBuf var6 = Unpooled.wrappedBuffer(var4);

				try {
					int var7 = PacketBuffer.readVarIntFromBuffer(var6);

					if (buffer.readableBytes() < var7) {
						buffer.resetReaderIndex();
						return;
					}

					objects.add(buffer.readBytes(var7));
				} finally {
					var6.release();
				}

				return;
			}
		}

		throw new CorruptedFrameException("length wider than 21-bit");
	}

}
