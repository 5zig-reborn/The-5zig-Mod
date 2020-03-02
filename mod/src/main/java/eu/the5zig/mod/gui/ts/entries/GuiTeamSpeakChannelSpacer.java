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

package eu.the5zig.mod.gui.ts.entries;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.teamspeak.api.Channel;

public class GuiTeamSpeakChannelSpacer extends GuiTeamSpeakChannel {

	private final String channelName;

	public GuiTeamSpeakChannelSpacer(Channel channel, String channelName) {
		super(channel);
		this.channelName = channelName;
	}

	@Override
	public void render(int x, int y, int width, int height) {
		String group = channelName;
		while (The5zigMod.getVars().getStringWidth(group + group.charAt(0)) < width) {
			group += group.charAt(0);
		}
		The5zigMod.getVars().drawString(group, x, y + 2);
	}

	@Override
	public void renderIcons(int x, int y, int width, int height) {
	}

	@Override
	public void renderDragging(int x, int y, int width, int height) {
		render(x, y, width, height);
	}

	@Override
	public void renderDraggingIcons(int x, int y, int width, int height) {
	}

	@Override
	public boolean canBeCollapsed() {
		return false;
	}
}
