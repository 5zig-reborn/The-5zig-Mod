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

package eu.the5zig.mod.chat;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;

import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GroupMember extends User implements Comparable<GroupMember> {

	public static final int MEMBER = 0, ADMIN = 1, OWNER = 2;

	private int type;
	private int maxWidth = 220;

	public GroupMember(String username, UUID uuid, int type) {
		super(username, uuid);
		this.type = type;
	}

	public GroupMember(User user, int type) {
		this(user.getUsername(), user.getUniqueId(), type);
	}

	public boolean isAdmin() {
		return type == ADMIN;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public void draw(int x, int y) {
		String displayName = getUsername();
		if (maxWidth >= 150) {
			switch (getType()) {
				case ADMIN:
					displayName += String.format(" (%s)", I18n.translate("group.admin"));
					break;
				case OWNER:
					displayName += String.format(" (%s)", I18n.translate("group.owner"));
					break;
			}
		}
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(displayName, maxWidth - 4), x + 2, y + 2);
	}

	@Override
	public int compareTo(GroupMember other) {
		if (getType() == OWNER && other.getType() != OWNER)
			return -1;
		if (getType() != OWNER && other.getType() == OWNER)
			return 1;
		if (getType() == ADMIN && other.getType() != ADMIN)
			return -1;
		if (getType() != ADMIN && other.getType() == ADMIN)
			return 1;
		return getUsername().compareTo(other.getUsername());
	}
}
