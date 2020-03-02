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

package eu.the5zig.mod.server.bergwerk;

import eu.the5zig.mod.server.GameMode;

public class ServerBergwerk {

	public static class Flash extends GameMode {

		@Override
		public String getName() {
			return "Flash";
		}
	}

	public static class Duel extends GameMode {

		private String team;
		private boolean canRespawn;
		private long teleporterTimer;


		public Duel() {
			super();
			setRespawnable(true);
			canRespawn = true;
			teleporterTimer = -1;
		}

		public String getTeam() {
			return team;
		}

		public void setTeam(String team) {
			this.team = team;
		}

		public boolean isCanRespawn() {
			return canRespawn;
		}

		public void setCanRespawn(boolean canRespawn) {
			this.canRespawn = canRespawn;
		}

		public long getTeleporterTimer() {
			return teleporterTimer;
		}

		public void setTeleporterTimer(long teleporterTimer) {
			this.teleporterTimer = teleporterTimer;
		}

		@Override
		public String getName() {
			return "BedWars Duel";
		}
	}

}
