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

package eu.the5zig.mod.util;

public class PreciseCounter {

	protected int MEASURE_INTERVAL = 1000;

	private double currentCount;
	private Counter[] timers;

	public PreciseCounter() {
		currentCount = 0;
		timers = new Counter[20];
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < timers.length; i++) {
			long plus = startTime + ((long) i * 1000 / timers.length);
			timers[i] = new Counter(MEASURE_INTERVAL, plus);
		}
	}

	public double getCurrentCount() {
		return currentCount;
	}

	public void incrementCount() {
		incrementCount(1);
	}

	public void incrementCount(double add) {
		for (Counter counter : timers) {
			counter.updateCount(add);
		}
	}

	public void update() {
		for (Counter counter : timers) {
			if (counter.isOver()) {
				this.currentCount = (counter.getCount() / ((double) MEASURE_INTERVAL / 1000f));
				counter.updateStartTime();
			}
		}
	}

}
