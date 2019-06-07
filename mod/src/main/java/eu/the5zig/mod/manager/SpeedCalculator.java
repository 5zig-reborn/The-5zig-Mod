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

package eu.the5zig.mod.manager;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.util.Counter;

public class SpeedCalculator {

	private double currentSpeed;
	private SpeedCounter[] timers;

	public SpeedCalculator() {
		currentSpeed = 0;
		timers = new SpeedCounter[20];
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < timers.length; i++) {
			long plus = startTime + ((long) i * 1000 / timers.length);
			timers[i] = new SpeedCounter(1000, plus);
		}
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void update() {
		for (SpeedCounter counter : timers) {
			if (!The5zigMod.getVars().isPlayerNull() && !The5zigMod.getVars().isTerrainLoading()) {
				if (counter.isOver()) {
					double x = The5zigMod.getVars().getPlayerPosX();
					double y = The5zigMod.getVars().getPlayerPosY();
					double z = The5zigMod.getVars().getPlayerPosZ();

					if (counter.lastX != null && counter.lastY != null && counter.lastZ != null) {
						currentSpeed = Math.sqrt((counter.lastX - x) * (counter.lastX - x) + (counter.lastY - y) * (counter.lastY - y) + (counter.lastZ - z) * (counter.lastZ - z));
						if (currentSpeed > 100) {
							currentSpeed = 0;
						}
					} else {
						currentSpeed = 0;
					}
					counter.lastX = x;
					counter.lastY = y;
					counter.lastZ = z;

					counter.updateStartTime();
				}
			} else {
				counter.lastX = null;
				counter.lastY = null;
				counter.lastZ = null;
			}
		}
	}

	public class SpeedCounter extends Counter {

		private Double lastX, lastY, lastZ;

		public SpeedCounter(int MEASURE_INTERVAL, long startTime) {
			super(MEASURE_INTERVAL, startTime);
		}
	}
}
