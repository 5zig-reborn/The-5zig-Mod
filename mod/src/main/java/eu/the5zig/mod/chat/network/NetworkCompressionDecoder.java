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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class NetworkCompressionDecoder extends MessageToMessageDecoder<ByteBuf> {

	private final Inflater inflater;
	private int threshold;

	public NetworkCompressionDecoder(int threshold) {
		this.threshold = threshold;
		this.inflater = new Inflater();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		this.inflater.end();
	}

	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws DataFormatException {
		if (in.readableBytes() != 0) {
			int packetLength = PacketBuffer.readVarIntFromBuffer(in);

			if (packetLength == 0) {
				out.add(in.readBytes(in.readableBytes()));
			} else {
				if (packetLength < this.threshold) {
					throw new DecoderException("Badly compressed packet - size of " + packetLength + " is below server threshold of " + this.threshold);
				}

				if (packetLength > 2097152) {
					throw new DecoderException("Badly compressed packet - size of " + packetLength + " is larger than protocol maximum of " + 2097152);
				}

				byte[] compressedData = new byte[in.readableBytes()];
				in.readBytes(compressedData);
				this.inflater.setInput(compressedData);
				byte[] decompressedData = new byte[packetLength];
				this.inflater.inflate(decompressedData);
				out.add(Unpooled.wrappedBuffer(decompressedData));
				this.inflater.reset();
			}
		}
	}

	public void setCompressionTreshold(int treshold) {
		this.threshold = treshold;
	}

}
