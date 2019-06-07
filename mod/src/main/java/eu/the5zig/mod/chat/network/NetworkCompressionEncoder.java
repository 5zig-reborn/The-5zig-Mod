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
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.zip.Deflater;

public class NetworkCompressionEncoder extends MessageToByteEncoder<ByteBuf> {

	private final byte[] buffer = new byte[8192];
	private final Deflater deflater;
	private int threshold;

	public NetworkCompressionEncoder(int threshold) {
		this.threshold = threshold;
		this.deflater = new Deflater();
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		this.deflater.end();
	}

	protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
		int length = in.readableBytes();

		if (length < this.threshold) {
			PacketBuffer.writeVarIntToBuffer(out, 0);
			out.writeBytes(in);
		} else {
			byte[] uncompressedData = new byte[length];
			in.readBytes(uncompressedData);
			PacketBuffer.writeVarIntToBuffer(out, uncompressedData.length);
			this.deflater.setInput(uncompressedData, 0, length);
			this.deflater.finish();

			while (!this.deflater.finished()) {
				int compressedLength = this.deflater.deflate(this.buffer);
				out.writeBytes(this.buffer, 0, compressedLength);
			}

			this.deflater.reset();
		}
	}

	public void setCompressionTreshold(int treshold) {
		this.threshold = treshold;
	}

}