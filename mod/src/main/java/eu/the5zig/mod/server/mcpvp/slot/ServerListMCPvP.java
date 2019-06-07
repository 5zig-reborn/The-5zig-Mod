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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiMCPVPServers;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.server.mcpvp.MCPVPServer;
import eu.the5zig.mod.server.mcpvp.ServerMCPVP;

import java.util.List;
import java.util.Set;

public class ServerListMCPvP extends GuiMCPVPServers {

	public ServerListMCPvP(Gui lastScreen) {
		super(lastScreen);
	}

	protected void addServers(Set<MCPVPServer> servers) {
	}

	protected void sort(List<MCPVPServer> servers) {
	}

	public void initGui() {
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 175, 50, 50, 20, "Raid"));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 100, 50, 50, 20, "HG"));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2 - 35, 50, 70, 20, "HG NoSoup"));
		addButton(The5zigMod.getVars().createButton(4, getWidth() / 2 + 50, 50, 50, 20, "CTF"));
		addButton(The5zigMod.getVars().createButton(5, getWidth() / 2 + 125, 50, 50, 20, "KitPvP"));

		addButton(The5zigMod.getVars().createButton(10, getWidth() / 2 - 175, 80, 50, 20, "Sabotage"));
		addButton(The5zigMod.getVars().createButton(11, getWidth() / 2 - 100, 80, 50, 20, "Headshot"));
		addButton(The5zigMod.getVars().createButton(12, getWidth() / 2 - 35, 80, 70, 20, "Maze Runner"));
		addButton(The5zigMod.getVars().createButton(13, getWidth() / 2 + 50, 80, 50, 20, "MC Build"));
		addButton(The5zigMod.getVars().createButton(14, getWidth() / 2 + 125, 80, 50, 20, "Parkour"));

		addButton(The5zigMod.getVars().createButton(20, getWidth() / 2 - 75, 110, 70, 20, "PvPDojo"));
		addButton(The5zigMod.getVars().createButton(21, getWidth() / 2 + 5, 110, 70, 20, "Siege"));

		addButton(The5zigMod.getVars().createButton(30, getWidth() / 2 - 115, 160, 50, 20, "Hub"));
		addButton(The5zigMod.getVars().createButton(31, getWidth() / 2 - 55, 160, 50, 20, "VIP Hub"));
		addButton(The5zigMod.getVars().createButton(32, getWidth() / 2 + 5, 160, 50, 20, "MVP Hub"));
		addButton(The5zigMod.getVars().createButton(33, getWidth() / 2 + 65, 160, 50, 20, "PRO Hub"));

		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, The5zigMod.getVars().translate("gui.done")));
	}

	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			joinServer(ServerMCPVP.Server.RAID.getIp());
		}
		if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new ServerListHG(this));
		}
		if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new ServerListNoSoup(this));
		}
		if (button.getId() == 4) {
			The5zigMod.getVars().displayScreen(new ServerListCTF(this));
		}
		if (button.getId() == 5) {
			The5zigMod.getVars().displayScreen(new ServerListKitPvP(this));
		}
		if (button.getId() == 10) {
			The5zigMod.getVars().displayScreen(new ServerListSabotage(this));
		}
		if (button.getId() == 11) {
			The5zigMod.getVars().displayScreen(new ServerListHeadshot(this));
		}
		if (button.getId() == 12) {
			The5zigMod.getVars().displayScreen(new ServerListMaze(this));
		}
		if (button.getId() == 13) {
			joinServer(ServerMCPVP.Server.MINECRAFT_BUILD.getIp());
		}
		if (button.getId() == 14) {
			joinServer(ServerMCPVP.Server.PARKOUR.getIp());
		}
		if (button.getId() == 20) {
			The5zigMod.getVars().displayScreen(new ServerListPvPDojo(this));
		}
		if (button.getId() == 21) {
			The5zigMod.getVars().displayScreen(new ServerListSiege(this));
		}
		if (button.getId() == 30) {
			joinServer(ServerMCPVP.Server.HUB.getIp());
		}
		if (button.getId() == 31) {
			joinServer(ServerMCPVP.Server.VIP_HUB.getIp());
		}
		if (button.getId() == 32) {
			joinServer(ServerMCPVP.Server.MVP_HUB.getIp());
		}
		if (button.getId() == 33) {
			joinServer(ServerMCPVP.Server.PRO_HUB.getIp());
		}
	}

}