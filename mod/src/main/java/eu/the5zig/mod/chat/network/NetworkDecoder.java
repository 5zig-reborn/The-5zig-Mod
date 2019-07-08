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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.packets.Packet;
import eu.the5zig.mod.chat.network.packets.PacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class NetworkDecoder extends ByteToMessageDecoder {

	private NetworkManager networkManager;
	private NettyEncryptionTranslator enc;

	public NetworkDecoder(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception {
		if(enc != null) {
			byteBuf = enc.decipher(channelHandlerContext, byteBuf);
		}
		if (byteBuf.readableBytes() < 1) {
			return;
		}
		The5zigMod.getDataManager().getNetworkStats().onPacketReceive(byteBuf);
		int packetSize = byteBuf.readableBytes();
		int id = PacketBuffer.readVarIntFromBuffer(byteBuf);
		Packet packet = networkManager.getProtocol().getPacket(id);
		packet.read(byteBuf);
		The5zigMod.logger.debug(The5zigMod.networkMarker, "IN | {} ({} bytes)", packet.toString(), packetSize);

		if (byteBuf.readableBytes() > 0) {
			throw new IOException("Packet " + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, " +
					"found " + byteBuf.readableBytes() + " bytes extra whilst reading packet " + packet);
		} else {
			objects.add(packet);
		}
	}

	public void setEnc(NettyEncryptionTranslator enc) {
		this.enc = enc;
	}
}
