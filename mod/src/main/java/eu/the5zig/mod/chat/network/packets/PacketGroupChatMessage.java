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

package eu.the5zig.mod.chat.network.packets;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Group;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PacketGroupChatMessage implements Packet {

	private int id;
	private String username;
	private String message;
	private long time;

	public PacketGroupChatMessage(int id, String message, long time) {
		this.id = id;
		this.message = message;
		this.time = time;
	}

	public PacketGroupChatMessage() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.id = PacketBuffer.readVarIntFromBuffer(buffer);
		this.username = PacketBuffer.readString(buffer);
		this.message = PacketBuffer.readString(buffer);
		this.time = buffer.readLong();
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeVarIntToBuffer(buffer, id);
		PacketBuffer.writeString(buffer, message);
		buffer.writeLong(time);
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		Group group = The5zigMod.getGroupChatManager().getGroup(id);
		if (group == null)
			return;
		if (The5zigMod.getConfig().getBool("playMessageSounds"))
			The5zigMod.getVars().playSound("the5zigmod", "chat.message.receive", 1);

		The5zigMod.getConversationManager().handleGroupChatMessage(group, username, message, time);
	}
}
