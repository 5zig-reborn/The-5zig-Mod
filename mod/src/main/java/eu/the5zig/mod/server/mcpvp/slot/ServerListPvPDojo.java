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

package eu.the5zig.mod.server.mcpvp.slot;

import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiMCPVPServersScroll;
import eu.the5zig.mod.server.mcpvp.MCPVPServer;
import eu.the5zig.mod.server.mcpvp.ServerMCPVP;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ServerListPvPDojo extends GuiMCPVPServersScroll {

	public ServerListPvPDojo(Gui lastScreen) {
		super(lastScreen);
	}

	protected void addServers(Set<MCPVPServer> set) {
		for (MCPVPServer server : set) {
			if ((server.isOnline()) && (server.isAcceptingPlayers()) && (server.getServerType().equals(ServerMCPVP.Server.PVPDOJO.getIp()))) {
				this.servers.add(server);
			}
		}
	}

	protected void sort(List<MCPVPServer> servers) {
		Collections.sort(servers, new Comparator<MCPVPServer>() {
			public int compare(MCPVPServer s1, MCPVPServer s2) {
				return s2.getPlayers() - s1.getPlayers();
			}
		});
	}
}