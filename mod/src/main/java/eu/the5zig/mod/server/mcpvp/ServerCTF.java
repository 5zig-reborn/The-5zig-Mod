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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.minecraft.ChatColor;

public class ServerCTF extends ServerMCPVP {

	private String kit;
	private int kills;
	private int killstreak;
	private int deaths;
	private int steals;
	private int captures;
	private int recovers;

	private EnumCTFTeam team;
	private CTFTeam red;
	private CTFTeam blue;

	public ServerCTF(String host, int port) {
		super(host, port);
		this.kit = "Heavy";
		this.kills = 0;
		this.killstreak = 0;
		this.deaths = 0;
		this.steals = 0;
		this.captures = 0;
		this.recovers = 0;
		this.red = new CTFTeam(EnumCTFTeam.RED);
		this.blue = new CTFTeam(EnumCTFTeam.BLUE);
		The5zigMod.getLastServerConfig().getConfigInstance().setLastServer(this);
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public String getKit() {
		return kit;
	}

	public void setKit(String kit) {
		this.kit = kit;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getKillstreak() {
		return killstreak;
	}

	public void setKillstreak(int killstreak) {
		this.killstreak = killstreak;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getSteals() {
		return steals;
	}

	public void setSteals(int steals) {
		this.steals = steals;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getCaptures() {
		return captures;
	}

	public void setCaptures(int captures) {
		this.captures = captures;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public int getRecovers() {
		return recovers;
	}

	public void setRecovers(int recovers) {
		this.recovers = recovers;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public EnumCTFTeam getTeam() {
		return team;
	}

	public void setTeam(EnumCTFTeam team) {
		this.team = team;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public CTFTeam getRedTeam() {
		return red;
	}

	public void setRedTeam(CTFTeam red) {
		this.red = red;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public CTFTeam getBlueTeam() {
		return blue;
	}

	public void setBlueTeam(CTFTeam blue) {
		this.blue = blue;
		The5zigMod.getLastServerConfig().saveConfig();
	}

	public CTFTeam getPlayerTeam() {
		return team == EnumCTFTeam.RED ? getRedTeam() : getBlueTeam();
	}

	public CTFTeam getOtherTeam() {
		return team == EnumCTFTeam.BLUE ? getRedTeam() : getBlueTeam();
	}

	public enum EnumCTFTeam {
		RED(ChatColor.RED + "Red"), BLUE(ChatColor.BLUE + "Blue");

		private String name;

		EnumCTFTeam(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public class CTFTeam {

		private final EnumCTFTeam team;
		private int captures;
		private int maxcaptures;
		private String flag;
		private int players;

		public CTFTeam(EnumCTFTeam team) {
			this.team = team;
		}

		public CTFTeam(EnumCTFTeam team, int captures, int maxcaptures, String flag, int players) {
			this.team = team;
			this.captures = captures;
			this.maxcaptures = maxcaptures;
			this.flag = flag;
			this.players = players;
			The5zigMod.getLastServerConfig().saveConfig();
		}

		public EnumCTFTeam getTeam() {
			return team;
		}

		public int getCaptures() {
			return captures;
		}

		public void setCaptures(int captures) {
			this.captures = captures;
			The5zigMod.getLastServerConfig().saveConfig();
		}

		public int getMaxCaptures() {
			return maxcaptures;
		}

		public void setMaxCaptures(int maxcaptures) {
			this.maxcaptures = maxcaptures;
			The5zigMod.getLastServerConfig().saveConfig();
		}

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
			The5zigMod.getLastServerConfig().saveConfig();
		}

		public int getPlayers() {
			return players;
		}

		public void setPlayers(int players) {
			this.players = players;
			The5zigMod.getLastServerConfig().saveConfig();
		}

	}

}