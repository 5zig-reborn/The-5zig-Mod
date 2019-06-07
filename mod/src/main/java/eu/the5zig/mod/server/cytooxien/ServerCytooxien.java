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

package eu.the5zig.mod.server.cytooxien;

import com.google.common.collect.Lists;
import eu.the5zig.mod.server.GameMode;

import java.util.List;

public class ServerCytooxien {

	public static class MarioParty extends GameMode {

		private final List<String> minigameQueue = Lists.newArrayList();
		private long winTime;
		private String firstPlayer;
		private int place;
		private int remainingFields = -20;

		public boolean inventoryRequested;
		public int fieldAnnounceCount;

		public int minigames, first, second, third;

		public MarioParty() {
			super();
			winTime = -1;
		}

		public List<String> getMinigameQueue() {
			return minigameQueue;
		}

		public long getWinTime() {
			return winTime;
		}

		public void setWinTime(long winTime) {
			this.winTime = winTime;
		}

		public String getFirstPlayer() {
			return firstPlayer;
		}

		public void setFirstPlayer(String firstPlayer) {
			this.firstPlayer = firstPlayer;
		}

		public int getPlace() {
			return place;
		}

		public void setPlace(int place) {
			this.place = place;
		}

		public int getRemainingFields() {
			return remainingFields;
		}

		public void setRemainingFields(int remainingFields) {
			this.remainingFields = remainingFields;
		}

		@Override
		public String getName() {
			return "MarioParty";
		}
	}

	public static class Bedwars extends GameMode {
		public int realkills;
		public int bedsdestroyed;
		private boolean canRespawn;
		private String team;

		public Bedwars() {
			this.respawnable = true;
			this.canRespawn = true;
		}

		public String getName() {
			return "Bedwars";
		}

		public void setCanRespawn(boolean respawn) {
			this.canRespawn = respawn;
		}

		public boolean isCanRespawn() {
			return this.canRespawn;
		}

		public String getTeam() {
			return this.team;
		}

		public void setTeam(String pTeam) {
			this.team = pTeam;
		}
	}

}
