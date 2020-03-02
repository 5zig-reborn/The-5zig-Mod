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

package eu.the5zig.mod.gui.ts.menu;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ts.GuiTeamSpeak;
import eu.the5zig.mod.gui.ts.GuiTeamSpeakCreateChannel;
import eu.the5zig.mod.gui.ts.entries.GuiTeamSpeakEntry;
import eu.the5zig.mod.util.Vector2i;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.ServerTab;

public class GuiTeamSpeakContextMenuServer extends GuiTeamSpeakContextMenu {

	public GuiTeamSpeakContextMenuServer() {
		entries.add(new GuiTeamSpeakContextMenuEntry(new Vector2i(9, 1), "server.create_channel") {
			@Override
			public void onClick(GuiTeamSpeakEntry entry) {
				final ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
				if (selectedTab == null) {
					return;
				}
				The5zigMod.getVars().displayScreen(new GuiTeamSpeakCreateChannel(The5zigMod.getVars().getCurrentScreen(), null) {
					@Override
					protected void onDone(ChannelResult result) {
						selectedTab.createChannel(result.name, result.password, result.topic, result.description, result.lifespan, result.defaultChannel, null, result.orderChannel,
								result.bottomPosition, result.neededTalkPower, result.codec, result.codecQuality, result.maxClients);
					}
				});
			}
		});
		entries.add(new GuiTeamSpeakContextMenuEntry(new Vector2i(7, 1), "server.collapse_all") {
			@Override
			public void onClick(GuiTeamSpeakEntry entry) {
				GuiTeamSpeak.collapseAllChannels = true;
			}
		});
		entries.add(new GuiTeamSpeakContextMenuEntry(new Vector2i(14, 1), "server.uncollapse_all") {
			@Override
			public void onClick(GuiTeamSpeakEntry entry) {
				GuiTeamSpeak.collapsedChannels.clear();
			}
		});
	}
}
