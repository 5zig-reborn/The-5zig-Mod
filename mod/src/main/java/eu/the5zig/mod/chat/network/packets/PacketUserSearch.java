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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import eu.the5zig.mod.gui.GuiAddFriend;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PacketUserSearch implements Packet {

	private Type type;
	private User[] users;

	public PacketUserSearch(Type type, User[] users) {
		this.type = type;
		this.users = users;
	}

	public PacketUserSearch() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		type = PacketBuffer.readEnum(buffer, Type.class);
		users = new User[PacketBuffer.readVarIntFromBuffer(buffer)];
		for (int i = 0; i < users.length; i++) {
			users[i] = PacketBuffer.readUser(buffer);
		}
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeEnum(buffer, type);
		PacketBuffer.writeVarIntToBuffer(buffer, users.length);
		for (User user : users) {
			PacketBuffer.writeString(buffer, user.getUsername());
		}
	}

	@Override
	public void handle() {
		PacketUtil.ensureMainThread(this);

		if (type == Type.KEYWORD) {
			if (!(The5zigMod.getVars().getCurrentScreen() instanceof GuiAddFriend)) {
				return;
			}

			GuiAddFriend gui = (GuiAddFriend) The5zigMod.getVars().getCurrentScreen();
			if (gui.getTextfieldById(1).callGetText().length() >= 3) {
				gui.rows.clear();
				for (User user : users) {
					gui.rows.add(new GuiAddFriend.ProfileRow(user));
				}
			}
		} else if (type == Type.FRIEND_LIST) {
			int newSuggestions = The5zigMod.getFriendManager().addSuggestions(users);
			if (newSuggestions > 0) {
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("friend.invite.suggestions.new", users.length));
			}
		}
	}

	public enum Type {
		KEYWORD, FRIEND_LIST
	}
}
