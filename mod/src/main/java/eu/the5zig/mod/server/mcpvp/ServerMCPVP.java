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

package eu.the5zig.mod.server.mcpvp;

import eu.the5zig.mod.server.Server;

public class ServerMCPVP extends Server {

	public ServerMCPVP(String host, int port) {
		super(host, port);
	}

	public enum Server {

		HUB("hub.mcpvp.com"), VIP_HUB("vip.mcpvp.com"), MVP_HUB("mvp.mcpvp.com"), PRO_HUB("pro.mcpvp.com"), HARDCORE_GAMES("mc-hg.com"), HARDCORE_GAMES_NO_SOUP("nosoup.mc-hg.com"), RAID(
				"raid" + ".mcpvp.com"), CAPTURE_THE_FLAG("mcctf.com"), KITPVP("kitpvp.us"), SABOTAGE("mc-sabotage.com"), HEADSHOT("mcheadshot.com"), MAZERUNNER("mc-maze.com"),
		MINECRAFT_BUILD("minecraftbuild.com"), PARKOUR("parkour.mcpvp.com"), PVPDOJO("hub.pvpdojo.com"), SIEGE("mcsiege.com");

		private String ip;

		Server(String ip) {
			this.ip = ip;
		}

		public String getIp() {
			return ip;
		}

		@Override
		public String toString() {
			return ip;
		}
	}

}