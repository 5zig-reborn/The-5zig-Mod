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

package eu.the5zig.mod.installer;

public abstract class ProcessCallback {

	private int currentStage = 0;

	public void setStage(Stage stage) {
		message(Stage.values()[currentStage = stage.ordinal()].getMessage() + " (" + (currentStage + 1) + "/" + Stage.values().length + ")");
	}

	public void setProgress(float progress) {
		float startPercentage = Stage.values()[currentStage].getStartPercentage();
		float endPercentage = currentStage == Stage.values().length - 1 ? 1 : Stage.values()[currentStage + 1].getStartPercentage();
		progress(startPercentage + (endPercentage - startPercentage) * progress);
	}

	protected abstract void progress(float percentage);

	protected abstract void message(String message);

	public void log(String message) {
		System.out.println(message);
	}

}
