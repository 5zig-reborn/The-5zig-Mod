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
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Profile;
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PacketProfile implements Packet {

	private int id;
	private Rank rank;
	private long firstConnectTime;
	private String profileMessage;
	private Friend.OnlineStatus onlineStatus;
	private boolean showServer;
	private boolean showMessageRead;
	private boolean showFriendRequests;
	private boolean showCape;
	private boolean showCountry;
	private ChatColor displayColor;

	private ProfileType profileType;
	private boolean show;

	public PacketProfile(String profileMessage) {
		this.profileType = ProfileType.PROFILE_MESSAGE;
		this.profileMessage = profileMessage;
	}

	public PacketProfile(Friend.OnlineStatus onlineStatus) {
		this.profileType = ProfileType.ONLINE_STATUS;
		this.onlineStatus = onlineStatus;
	}

	public PacketProfile(ProfileType profileType, boolean show) {
		this.profileType = profileType;
		this.show = show;
	}

	public PacketProfile(ProfileType profileType, ChatColor displayColor) {
		this.profileType = profileType;
		this.displayColor = displayColor;
	}

	public PacketProfile() {
	}

	@Override
	public void read(ByteBuf buffer) throws IOException {
		this.id = PacketBuffer.readVarIntFromBuffer(buffer);
		this.rank = PacketBuffer.readRank(buffer);
		this.firstConnectTime = buffer.readLong();
		this.profileMessage = PacketBuffer.readString(buffer);
		int ordinal = PacketBuffer.readVarIntFromBuffer(buffer);
		if (ordinal < 0 || ordinal >= Friend.OnlineStatus.values().length)
			throw new IllegalArgumentException("Received Integer is out of enum range");
		this.onlineStatus = Friend.OnlineStatus.values()[ordinal];
		this.showServer = buffer.readBoolean();
		this.showMessageRead = buffer.readBoolean();
		this.showFriendRequests = buffer.readBoolean();
		this.showCape = buffer.readBoolean();
		this.showCountry = buffer.readBoolean();
		this.displayColor = ChatColor.getByChar(buffer.readChar());
	}

	@Override
	public void write(ByteBuf buffer) throws IOException {
		PacketBuffer.writeVarIntToBuffer(buffer, profileType.ordinal());
		if (profileType == ProfileType.PROFILE_MESSAGE) {
			PacketBuffer.writeString(buffer, profileMessage);
		} else if (profileType == ProfileType.ONLINE_STATUS) {
			PacketBuffer.writeVarIntToBuffer(buffer, onlineStatus.ordinal());
		} else if (profileType == ProfileType.DISPLAY_COLOR) {
			buffer.writeChar(displayColor.getCode());
		} else {
			buffer.writeBoolean(show);
		}
	}

	@Override
	public void handle() {
		The5zigMod.getDataManager().setProfile(new Profile(id, rank, firstConnectTime, profileMessage, onlineStatus, showServer, showMessageRead, showFriendRequests, showCape, showCountry,
				displayColor));
	}

	public enum ProfileType {

		PROFILE_MESSAGE, ONLINE_STATUS, SHOW_SERVER, SHOW_MESSAGE_READ, SHOW_FRIEND_REQUESTS, SHOW_COUNTRY, DISPLAY_COLOR

	}
}
