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

package eu.the5zig.mod.server.playminity;

import eu.the5zig.mod.server.GameMode;

public class ServerPlayMinity {

	public static class JumpLeague extends GameMode {

		private int checkPoint;
		private int maxCheckPoints;
		private int fails;
		private int lives;

		public JumpLeague() {
			this.maxCheckPoints = 10;
			this.lives = 3;
		}

		public int getCheckPoint() {
			return checkPoint;
		}

		public void setCheckPoint(int checkPoint) {
			this.checkPoint = checkPoint;
		}

		public int getMaxCheckPoints() {
			return maxCheckPoints;
		}

		public void setMaxCheckPoints(int maxCheckPoints) {
			this.maxCheckPoints = maxCheckPoints;
		}

		public int getFails() {
			return fails;
		}

		public void setFails(int fails) {
			this.fails = fails;
		}

		public int getLives() {
			return lives;
		}

		public void setLives(int lives) {
			this.lives = lives;
		}

		@Override
		public String getName() {
			return "JumpLeague";
		}
	}

}
