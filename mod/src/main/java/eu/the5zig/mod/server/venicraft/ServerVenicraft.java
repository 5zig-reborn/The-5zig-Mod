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

package eu.the5zig.mod.server.venicraft;

import eu.the5zig.mod.server.GameMode;

public class ServerVenicraft {

	public static class Mineathlon extends GameMode {

		private String discipline;
		private int round;

		public String getDiscipline() {
			return discipline;
		}

		public void setDiscipline(String discipline) {
			this.discipline = discipline;
		}

		public int getRound() {
			return round;
		}

		public void setRound(int round) {
			this.round = round;
		}

		@Override
		public String getName() {
			return "Mineathlon";
		}
	}

	public static class CrystalDefense extends GameMode {

		@Override
		public String getName() {
			return "CrystalDefense";
		}
	}

	public static class SurvivalGames extends GameMode {

		@Override
		public String getName() {
			return "SurvivalGames";
		}
	}

}
