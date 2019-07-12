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

import eu.the5zig.mod.chat.GroupMember;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Group;
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.mod.chat.entity.User;
import io.netty.buffer.ByteBuf;
import org.apache.commons.codec.Charsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PacketBuffer {

	private PacketBuffer() {
	}

	/**
	 * Calculates the number of bytes required to fit the supplied int (0-5) if it were to be read/written using
	 * readVarIntFromBuffer or writeVarIntToBuffer
	 */
	public static int getVarIntSize(int input) {
		for (int var1 = 1; var1 < 5; ++var1) {
			if ((input & -1 << var1 * 7) == 0) {
				return var1;
			}
		}

		return 5;
	}

	/**
	 * Reads a compressed int from the buffer. To do so it maximally reads 5 byte-sized chunks whose most significant
	 * bit dictates whether another byte should be read.
	 */
	public static int readVarIntFromBuffer(ByteBuf byteBuf) {
		int var1 = 0;
		int var2 = 0;
		byte var3;

		do {
			var3 = byteBuf.readByte();
			var1 |= (var3 & 127) << var2++ * 7;

			if (var2 > 5) {
				throw new RuntimeException("VarInt too big");
			}
		} while ((var3 & 128) == 128);

		return var1;
	}

	/**
	 * Writes a compressed int to the buffer. The smallest number of bytes to fit the passed int will be written. Of
	 * each such byte only 7 bits will be used to describe the actual value since its most significant bit dictates
	 * whether the next byte is part of that same int. Micro-optimization for int values that are expected to have
	 * values below 128.
	 */
	public static void writeVarIntToBuffer(ByteBuf byteBuf, int input) {
		while ((input & -128) != 0) {
			byteBuf.writeByte(input & 127 | 128);
			input >>>= 7;
		}

		byteBuf.writeByte(input);
	}

	public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
		byteBuf.writeLong(uuid.getMostSignificantBits());
		byteBuf.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(ByteBuf byteBuf) {
		return new UUID(byteBuf.readLong(), byteBuf.readLong());
	}

	public static void writeEnum(ByteBuf byteBuf, Enum e) {
		writeVarIntToBuffer(byteBuf, e.ordinal());
	}

	public static  <T extends Enum> T readEnum(ByteBuf byteBuf, Class<T> classOfT) {
		return classOfT.getEnumConstants()[readVarIntFromBuffer(byteBuf)];
	}

	public static void writeUser(ByteBuf byteBuf, User user) {
		writeString(byteBuf, user.getUsername());
		writeUUID(byteBuf, user.getUniqueId());
	}

	public static User readUser(ByteBuf byteBuf) {
		return new User(readString(byteBuf), readUUID(byteBuf));
	}

	public static ArrayList<Rank> readRank(ByteBuf byteBuf) {
		long bits = byteBuf.readLong();
		ArrayList<Rank> ranks = new ArrayList<>();
		for(Rank r : Rank.values()) {
			r.addIfSet(bits, ranks);
		}
		return ranks;
	}

	public static Friend readFriend(ByteBuf byteBuf) {
		String username = readString(byteBuf);
		UUID uuid = readUUID(byteBuf);
		String status = readString(byteBuf);
		int onlineOrdinal = readVarIntFromBuffer(byteBuf);
		if (onlineOrdinal < 0 || onlineOrdinal >= Friend.OnlineStatus.values().length)
			throw new IllegalArgumentException("Received Integer is out of enum range.");
		Friend.OnlineStatus online = Friend.OnlineStatus.values()[onlineOrdinal];
		long lastOnline = 0;
		if (online == Friend.OnlineStatus.OFFLINE) {
			lastOnline = byteBuf.readLong();
		}
		ArrayList<Rank> rank = readRank(byteBuf);
		long firstOnline = byteBuf.readLong();
		boolean favorite = byteBuf.readBoolean();
		String modVersion = "Unknown";
		String locale = readString(byteBuf);

		Friend friend = new Friend(username, uuid);
		friend.setStatusMessage(status);
		friend.setStatus(online);
		if (online == Friend.OnlineStatus.OFFLINE) {
			friend.setLastOnline(lastOnline);
		}
		friend.setRank(rank);
		friend.setFirstOnline(firstOnline);
		friend.setModVersion(modVersion);
		friend.setLocale(locale.isEmpty() ? null : Locale.forLanguageTag(locale));
		friend.setFavorite(favorite);
		return friend;
	}

	public static Group readGroup(ByteBuf byteBuf) {
		int id = readVarIntFromBuffer(byteBuf);
		String name = readString(byteBuf);
		User owner = null;
		int size = readVarIntFromBuffer(byteBuf);
		List<GroupMember> members = new ArrayList<GroupMember>(size);
		for (int i = 0; i < size; i++) {
			User member = readUser(byteBuf);
			int type = readVarIntFromBuffer(byteBuf);
			if (type == GroupMember.OWNER)
				owner = member;
			members.add(new GroupMember(member, type));
		}
		if (owner == null)
			owner = new User("unknown", UUID.randomUUID());
		return new Group(id, name, owner, members);
	}

	public static void writeString(ByteBuf byteBuf, String string) {
		byte[] bytes = string.getBytes(Charsets.UTF_8);
		byteBuf.writeInt(bytes.length);
		byteBuf.writeBytes(bytes);
	}

	public static String readString(ByteBuf byteBuf) {
		int length = byteBuf.readInt();
		String string = byteBuf.toString(byteBuf.readerIndex(), length, Charsets.UTF_8);
		byteBuf.readerIndex(byteBuf.readerIndex() + length);
		return string;
	}

	public static void writeByteArray(ByteBuf byteBuf, byte[] array) {
		writeVarIntToBuffer(byteBuf, array.length);
		byteBuf.writeBytes(array);
	}

	public static byte[] readByteArray(ByteBuf byteBuf) {
		byte[] array = new byte[readVarIntFromBuffer(byteBuf)];
		byteBuf.readBytes(array);
		return array;
	}
}
