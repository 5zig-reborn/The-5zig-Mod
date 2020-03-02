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

package eu.the5zig.mod.manager;

import com.google.common.collect.Lists;
import eu.the5zig.mod.listener.Listener;

import java.util.List;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ChatTypingManager extends Listener {

	private UUID typingTo = null;
	private final List<UUID> typingFrom = Lists.newArrayList();

	/**
	 * Returns the Friend the Player is currently typing to or {@code null} if there is not Friend the Player is typing to.
	 *
	 * @return if the Friend the Player is currently typing to or {@code null} if there is not Friend the Player is typing to.
	 */
	public UUID getTypingTo() {
		return typingTo;
	}

	/**
	 * Sets the Friend the Player is currently typing to.
	 *
	 * @param typingTo The Friend the Player is currently typing to.
	 */
	public void setTypingTo(UUID typingTo) {
		this.typingTo = typingTo;
	}

	/**
	 * Adds a Friend to the typing List.
	 *
	 * @param friend The Friend that should be added.
	 * @return If the Friend already existed in the typing List and if not so, if it has been successfully been inserted.
	 */
	public boolean addToTyping(UUID friend) {
		synchronized (typingFrom) {
			return !typingFrom.contains(friend) && typingFrom.add(friend);
		}
	}

	/**
	 * Removes a Friend from the typing List.
	 *
	 * @param friend The Friend that should be removed.
	 * @return If the Friend has been successfully removed from the List.
	 */
	public boolean removeFromTyping(UUID friend) {
		synchronized (typingFrom) {
			return typingFrom.remove(friend);
		}
	}

	/**
	 * Returns if the Friend is currently typing to the Player.
	 *
	 * @param friend The Friend of the Player.
	 * @return If the Friend is currently typing to the Player.
	 */
	public boolean isTyping(UUID friend) {
		synchronized (typingFrom) {
			return typingFrom.contains(friend);
		}
	}
}
