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

package eu.the5zig.mod.modules.items.server.timolia;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.timolia.ServerTimolia;
import eu.the5zig.util.minecraft.ChatColor;

public class AdventMedal extends GameModeItem<ServerTimolia.Advent> {

	public AdventMedal() {
		super(ServerTimolia.Advent.class, GameState.GAME, GameState.FINISHED);
	}

	@Override
	protected Object getValue(boolean dummy) {
		String medal = null;
		long medalTime = 0;
		if (dummy) {
			return "\u2726";
		}
		long currentTime = System.currentTimeMillis();
		long l = getGameMode().getState() == GameState.GAME ? currentTime - getGameMode().getTime() : getGameMode().getTime();
		if (l <= getGameMode().getTimeGold()) {
			medal = ChatColor.GOLD.toString();
			medalTime = getGameMode().getTimeGold();
		} else if (l <= getGameMode().getTimeSilver()) {
			medal = ChatColor.GRAY.toString();
			medalTime = getGameMode().getTimeSilver();
		} else if (l <= getGameMode().getTimeBronze()) {
			medal = ChatColor.DARK_AQUA.toString();
			medalTime = getGameMode().getTimeBronze();
		}
		if (medal != null) {
			int millis = (int) (medalTime % 1000);
			medalTime /= 1000;
			int seconds = (int) (medalTime % 60);
			medalTime /= 60;
			int minutes = (int) medalTime;
			medal += "\u2726 " + ChatColor.RESET + "(" + String.format("%d:%02d.%03d", minutes, seconds, millis) + ")";
			return medal;
		}
		return null;
	}

	@Override
	public String getTranslation() {
		return "ingame.medal";
	}
}
