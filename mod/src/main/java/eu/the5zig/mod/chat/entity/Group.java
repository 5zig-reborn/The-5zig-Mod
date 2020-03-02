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

package eu.the5zig.mod.chat.entity;

import com.google.common.collect.Lists;
import eu.the5zig.mod.chat.GroupMember;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Group {

	public static final int MAX_MEMBERS = 50;
	private final int id;
	private User owner;
	private String name;
	private List<GroupMember> members = Lists.newArrayList();

	public Group(int id, String name, User owner, List<GroupMember> members) {
		this.id = id;
		this.name = name;
		this.owner = owner;
		this.members = members;
		sort();
	}

	private void sort() {
		Collections.sort(this.members);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<GroupMember> getMembers() {
		return members;
	}

	public GroupMember getMember(UUID uuid) {
		for (GroupMember member : members) {
			if (member.getUniqueId().equals(uuid))
				return member;
		}
		return null;
	}

	public boolean isMember(UUID uuid) {
		return getMember(uuid) != null;
	}

	public boolean isAdmin(UUID uuid) {
		return getMember(uuid) != null && getMember(uuid).isAdmin();
	}

	public void addMember(User user) {
		members.add(new GroupMember(user, GroupMember.MEMBER));
		Collections.sort(this.members);
	}

	public void removeMember(UUID uuid) {
		GroupMember member = getMember(uuid);
		if (member == null)
			return;
		members.remove(member);
		Collections.sort(this.members);
	}

	@Override
	public String toString() {
		return "Group{" +
				"id=" + id +
				", name='" + name + '\'' +
				'}';
	}

}
