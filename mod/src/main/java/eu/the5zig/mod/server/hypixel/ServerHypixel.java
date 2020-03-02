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

package eu.the5zig.mod.server.hypixel;

import eu.the5zig.mod.server.GameMode;

public class ServerHypixel {

	public static class Quake extends GameMode {

		public Quake() {
			super();
			setRespawnable(true);
		}

		@Override
		public String getName() {
			return "Quakecraft";
		}
	}

	public static class Blitz extends GameMode {

		private String kit;
		private long star;
		private long deathmatch;

		public Blitz() {
			super();
			star = -1;
			deathmatch = -1;
		}

		public String getKit() {
			return kit;
		}

		public void setKit(String kit) {
			this.kit = kit;
		}

		public long getStar() {
			return star;
		}

		public void setStar(long star) {
			this.star = star;
		}

		public long getDeathmatch() {
			return deathmatch;
		}

		public void setDeathmatch(long deathmatch) {
			this.deathmatch = deathmatch;
		}

		@Override
		public String getName() {
			return "BlitzSG";
		}
	}

	public static class Paintball extends GameMode {

		private String team;

		public Paintball() {
			super();
			setRespawnable(true);
		}

		public String getTeam() {
			return team;
		}

		public void setTeam(String team) {
			this.team = team;
		}

		@Override
		public String getName() {
			return "PaintBall";
		}
	}
}
