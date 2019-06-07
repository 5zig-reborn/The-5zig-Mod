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

package eu.the5zig.mod.modules.items.server;

import com.google.common.collect.Sets;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.ArrayList;
import java.util.Set;

public class PartyMembers extends AbstractModuleItem {

	private final Set<String> DUMMY_PARTY = Sets.newHashSet("5zig", "Notch", "jeb_", "Gerrygames");

	public void registerSettings() {
		getProperties().addSetting("length", "", 5.0F, 1.0F, 15.0F, 1);
		getProperties().addSetting("star", true);
	}

	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		ArrayList<String> members = new ArrayList<String>();
		members.addAll(getMembers(dummy));
		String king = "";
		for (String s : members) {
			if (s.startsWith("!")) {
				king = s;
				break;
			}
		}
		if (!king.equals("")) {
			members.remove(king);
			members.add(0, king.replace("!", ""));
		}
		String pre = getProperties().buildPrefix("");
		int index = 0;
		int length = length();
		for (int i = 0; i < members.size(); i++) {
			The5zigMod.getVars().drawString(pre + members.get(i) + (i == 0 &&
							(Boolean) this.getProperties().getSetting("star").get() ? ChatColor.GOLD.toString() + " ★" : ""),
					x, y + index * The5zigMod.getVars().getFontHeight());
			index++;
			if (index >= length) {
				break;
			}
		}
	}

	public boolean shouldRender(boolean dummy) {
		return (dummy) || (The5zigAPI.getAPI().getActiveServer() != null && !getMembers(false).isEmpty());
	}

	public int getWidth(boolean dummy) {
		Set<String> members = getMembers(dummy);
		int maxWidth = 0;
		for (String member : members) {
			int width = The5zigMod.getVars().getStringWidth(member);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	public int getHeight(boolean dummy) {
		return Math.min(length(), getMembers(dummy).size()) * The5zigMod.getVars().getFontHeight();
	}

	private int length() {
		Float length = (Float) getProperties().getSetting("length").get();
		if (length == null) {
			length = 5.0F;
		}
		return length.intValue();
	}

	private Set<String> getMembers(boolean dummy) {
		if (dummy) {
			return this.DUMMY_PARTY;
		}
		return The5zigAPI.getAPI().getActiveServer().getGameListener().getPartyMembers();
	}
}