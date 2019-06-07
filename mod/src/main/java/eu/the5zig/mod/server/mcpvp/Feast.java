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

package eu.the5zig.mod.server.mcpvp;

import java.util.concurrent.TimeUnit;

public class Feast {

	private int x, z;
	private long millisStarted;
	private int feastTime;

	public Feast(int x, int z) {
		this.x = x;
		this.z = z;
		millisStarted = System.currentTimeMillis();
		feastTime = 5 * 1000 * 60;
		feastTime += 2000;
	}

	public Feast(int x, int z, int feastTime) {
		this.x = x;
		this.z = z;
		millisStarted = System.currentTimeMillis();
		this.feastTime = feastTime;
		this.feastTime += 2000;
	}

	public String getRemainingTime() {
		long millis = (millisStarted + feastTime) - System.currentTimeMillis();
		if (millis > 999) {
			return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
					TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		}
		return null;
	}

	public String getCoordinates() {
		return x + ", " + z;
	}

}