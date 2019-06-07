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

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {

	private final Cipher cipher;
	private byte[] field_150505_b = new byte[0];
	private byte[] field_150506_c = new byte[0];

	protected NettyEncryptionTranslator(Cipher cipherIn) {
		this.cipher = cipherIn;
	}

	private byte[] func_150502_a(ByteBuf buf) {
		int i = buf.readableBytes();

		if (this.field_150505_b.length < i) {
			this.field_150505_b = new byte[i];
		}

		buf.readBytes(this.field_150505_b, 0, i);
		return this.field_150505_b;
	}

	protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
		int i = buffer.readableBytes();
		byte[] abyte = this.func_150502_a(buffer);
		ByteBuf bytebuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
		bytebuf.writerIndex(this.cipher.update(abyte, 0, i, bytebuf.array(), bytebuf.arrayOffset()));
		return bytebuf;
	}

	protected void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException {
		int i = in.readableBytes();
		byte[] abyte = this.func_150502_a(in);
		int j = this.cipher.getOutputSize(i);

		if (this.field_150506_c.length < j) {
			this.field_150506_c = new byte[j];
		}

		out.writeBytes(this.field_150506_c, 0, this.cipher.update(abyte, 0, i, this.field_150506_c));
	}
}
