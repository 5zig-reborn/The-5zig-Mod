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

package eu.the5zig.mod.server;

import eu.the5zig.util.minecraft.ChatColor;

import java.util.Iterator;
import java.util.List;

public class MultiPatternResult implements IMultiPatternResult {

	private final RegisteredServerInstance instance;
	private List<String> messages;

	public MultiPatternResult(RegisteredServerInstance instance, List<String> messages) {
		this.instance = instance;
		this.messages = messages;
	}

	@Override
	public PatternResult parseKey(String key) {
		for (Iterator<String> iterator = messages.iterator(); iterator.hasNext(); ) {
			String message = ChatColor.stripColor(iterator.next());
			final List<String> match = instance.match(message, key);
			if (match == null || match.isEmpty())
				continue;
			iterator.remove();
			return new NonIgnoreablePatternResult(match);
		}
		return null;
	}

	@Override
	public int getRemainingMessageCount() {
		return messages.size();
	}

	@Override
	public String getMessage(int index) {
		return messages.remove(index);
	}
}
