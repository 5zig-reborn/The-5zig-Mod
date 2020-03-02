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

package eu.the5zig.mod.server.gomme;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.Teamable;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ServerGommeHD {

	public static class SurvivalGames extends GameMode implements Teamable {

		private long deathmatchTime;
		private boolean teamsAllowed;

		public SurvivalGames() {
			super();
			deathmatchTime = -1;
		}

		public long getDeathmatchTime() {
			return deathmatchTime;
		}

		public void setDeathmatchTime(long deathmatchTime) {
			this.deathmatchTime = deathmatchTime;
		}

		@Override
		public boolean isTeamsAllowed() {
			return teamsAllowed;
		}

		@Override
		public void setTeamsAllowed(boolean teamsAllowed) {
			this.teamsAllowed = teamsAllowed;
		}

		@Override
		public String getName() {
			return "SurvivalGames";
		}
	}

	public static class BedWars extends GameMode {

		private String team;
		private int beds;
		private boolean canRespawn;
		private boolean teamsAllowed;
		private long goldTime;

		public BedWars() {
			super();
			setRespawnable(true);
			canRespawn = true;
			teamsAllowed = false;
		}

		public String getTeam() {
			return team == null ? I18n.translate("ingame.kit.none") : team;
		}

		public void setTeam(String team) {
			this.team = team;
		}

		public int getBeds() {
			return beds;
		}

		public void setBeds(int beds) {
			this.beds = beds;
		}

		public boolean isCanRespawn() {
			return canRespawn;
		}

		public void setCanRespawn(boolean canRespawn) {
			this.canRespawn = canRespawn;
		}

		public boolean isTeamsAllowed() {
			return teamsAllowed;
		}

		public long getGoldTime() {
			return goldTime;
		}

		public void setGoldTime(long goldTime) {
			this.goldTime = goldTime;
		}

		@Override
		public String getName() {
			return "BedWars";
		}
	}

	public static class EnderGames extends GameMode {

		private String kit;
		private String coins;

		public EnderGames() {
			super();
		}

		public String getKit() {
			return kit == null ? I18n.translate("ingame.kit.none") : kit;
		}

		public void setKit(String kit) {
			this.kit = kit;
		}

		public void setCoins(String coins) {
			this.coins = coins;
		}

		public String getCoins() {
			return coins;
		}

		@Override
		public String getName() {
			return "EnderGames";
		}
	}

	public static class PvP extends GameMode {

		public PvP() {
			super();
		}

		@Override
		public String getName() {
			return "PvP";
		}
	}

	public static class PvPMatch extends GameMode {

		public PvPMatch() {
			super();
		}

		@Override
		public String getName() {
			return "PvP";
		}
	}

	public static class FFA extends GameMode {

		public FFA() {
			super();
		}

		@Override
		public String getName() {
			return "FFA";
		}
	}

	public static class SkyWars extends GameMode {

		private String kit;
		private int team;
		private String coins;

		public String getKit() {
			return kit;
		}

		public void setKit(String kit) {
			this.kit = kit;
		}

		public int getTeam() {
			return team;
		}

		public void setTeam(int team) {
			this.team = team;
		}

		public String getCoins() {
			return coins;
		}

		public void setCoins(String coins) {
			this.coins = coins;
		}

		@Override
		public String getName() {
			return "SkyWars";
		}
	}

	public static class RageMode extends GameMode {

		private int emeralds;

		public RageMode() {
			setRespawnable(true);
			killstreakDuration = 1000 * 7;
		}

		public int getEmeralds() {
			return emeralds;
		}

		public void setEmeralds(int emeralds) {
			this.emeralds = emeralds;
		}

		@Override
		public String getName() {
			return "RageMode";
		}
	}

}
