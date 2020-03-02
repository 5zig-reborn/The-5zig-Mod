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

package eu.the5zig.mod.manager;

public class FPSCalculator {

	private int currentFPS;
	private FPS[] timers;

	public FPSCalculator() {
		currentFPS = 0;
		timers = new FPS[20];
		long startTime = System.nanoTime();
		for (int i = 0; i < timers.length; i++) {
			long plus = startTime + ((long) i * 1000000000 / timers.length);
			timers[i] = new FPS(plus);
		}
	}

	public int getCurrentFPS() {
		return currentFPS;
	}

	public void render() {
		for (FPS fps : timers) {
			fps.updateFPSCount();
			if (fps.isOver()) {
				this.currentFPS = fps.getFpsCount();
				fps.updateStartTime();
			}
		}
	}

	public class FPS {

		private long startTime;
		private int fpsCount;

		public FPS(long startTime) {
			this.startTime = startTime;
			fpsCount = 0;
		}

		public void updateFPSCount() {
			fpsCount++;
		}

		public int getFpsCount() {
			return fpsCount;
		}

		public boolean isOver() {
			return System.nanoTime() - startTime >= 1000000000;
		}

		public void updateStartTime() {
			while (isOver()) {
				startTime += 1000000000;
			}
			fpsCount = 0;
		}

	}

}
