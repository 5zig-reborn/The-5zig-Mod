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
import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import eu.the5zig.mod.chat.party.Party;
import eu.the5zig.mod.gui.GuiParty;
import eu.the5zig.mod.gui.list.GuiArrayList;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

public class PacketPartyInviteResponse implements Packet {

	private UUID partyOwnerId;
	private boolean flag;

	private Party party;

	public PacketPartyInviteResponse(UUID partyOwnerId, boolean accepted) {
		this.partyOwnerId = partyOwnerId;
		this.flag = accepted;
	}

	public PacketPartyInviteResponse() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		long created = buffer.readLong();
		String server = PacketBuffer.readString(buffer);
		User owner = null;
		int size = PacketBuffer.readVarIntFromBuffer(buffer);
		GuiArrayList<GroupMember> members = new GuiArrayList<>(size);
		for (int i = 0; i < size; i++) {
			User member = PacketBuffer.readUser(buffer);
			int type = PacketBuffer.readVarIntFromBuffer(buffer);
			if (type == GroupMember.OWNER)
				owner = member;
			members.add(new GroupMember(member, type));
		}
		if (owner == null)
			owner = new User("unknown", UUID.randomUUID());
		party = new Party(owner, created, server, members);
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeUUID(buffer, partyOwnerId);
		buffer.writeBoolean(flag);
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		The5zigMod.getPartyManager().getPartyInvitations().clear();
		The5zigMod.getPartyManager().setParty(party);
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty) {
			The5zigMod.getVars().getCurrentScreen().initGui0();
		}
	}

	@Override
	public String toString() {
		return "PacketPartyInviteResponse{" + "partyOwnerId=" + partyOwnerId + ", flag=" + flag + ", party=" + party + '}';
	}
}
