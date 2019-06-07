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

package eu.the5zig.mod.api;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PayloadUtils {

	public static final String API_CHANNEL = "5zig";
	public static final String API_CHANNEL_REGISTER = "5zig:reg";

	public static final String SETTING_CHANNEL = "5zig:set";

	public static void sendPayload(String message) {
		sendPayload(API_CHANNEL, message);
	}

	public static void sendPayload(ByteBuf buf) {
		sendPayload(API_CHANNEL, buf);
	}

	public static void sendPayload(String channel, String message) {
		sendPayload(channel, Unpooled.buffer().writeBytes(message.getBytes()));
	}

	public static void sendPayload(String channel, ByteBuf buf) {
		if (The5zigMod.getVars().hasNetworkManager()) {
			The5zigMod.getVars().sendCustomPayload(channel, buf);
		}
	}

	public static ByteBuf writeString(ByteBuf byteBuf, String string) {
		byte[] bytes = string.getBytes(org.apache.commons.codec.Charsets.UTF_8);
		byteBuf.writeBytes(bytes);
		return byteBuf;
	}

	public static String readString(ByteBuf byteBuf, int maxLength) {
		int length = PacketBuffer.readVarIntFromBuffer(byteBuf);
		if (length > maxLength * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + length + " > " + maxLength * 4 + ")");
		} else if (length < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String var2 = byteBuf.toString(byteBuf.readerIndex(), length, org.apache.commons.codec.Charsets.UTF_8);
			byteBuf.readerIndex(byteBuf.readerIndex() + length);
			if (var2.length() > maxLength) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + length + " > " + maxLength + ")");
			} else {
				return var2;
			}
		}
	}

}
