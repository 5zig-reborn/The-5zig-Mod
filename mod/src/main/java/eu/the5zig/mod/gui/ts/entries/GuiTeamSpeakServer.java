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

package eu.the5zig.mod.gui.ts.entries;

import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.teamspeak.api.ServerInfo;
import eu.the5zig.util.Utils;

import java.util.List;

public class GuiTeamSpeakServer extends GuiTeamSpeakEntry {

	private final ServerInfo serverInfo;
	private final int channelCount;
	private final int clientCount;

	public GuiTeamSpeakServer(ServerInfo serverInfo, int channelCount, int clientCount) {
		this.serverInfo = serverInfo;
		this.channelCount = channelCount;
		this.clientCount = clientCount;
	}

	@Override
	public void render(int x, int y, int width, int height) {
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(serverInfo.getName(), width - 14), x + 14, y + 2);
	}

	@Override
	public void renderIcons(int x, int y, int width, int height) {
		Gui.drawModalRectWithCustomSizedTexture(x, y, 8 * 128 / 12, 8 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
	}

	@Override
	public void onClick(boolean doubleClick) {
	}

	@Override
	public List<? extends Row> getDescription(int width) {
		return ImmutableList.of(
				row(width, "server.name", serverInfo.getName()),
				row(width, "server.ip", serverInfo.getIp() + ":" + serverInfo.getPort()),
				row(width, "server.version", serverInfo.getVersion(), serverInfo.getPlatform()),
				row(width, "server.created", Utils.convertToDate(serverInfo.getTimeCreated() * 1000)),
				row(width, "server.channels", channelCount),
				row(width, "server.clients", clientCount)
		);
	}

	@Override
	public int getXOffset() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof GuiTeamSpeakServer && ((GuiTeamSpeakServer) obj).serverInfo.equals(serverInfo);
	}
}
