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

package eu.the5zig.mod.modules.items.server;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.util.Utils;

public class Countdown extends ServerItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("showLargeCountdown", true);
	}

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return Utils.convertToClock(90);
		}
		GameMode gameMode = getServer().getGameMode();
		if (gameMode == null || gameMode.getTime() == -1) {
			return null;
		}
		String time;
		if (gameMode.getState() == GameState.LOBBY || gameMode.getState() == GameState.STARTING || gameMode.getState() == GameState.PREGAME) {
			time = shorten((double) (gameMode.getTime() - System.currentTimeMillis()) / 1000.0);
		} else if (gameMode.getState() == GameState.GAME || gameMode.getState() == GameState.ENDGAME) {
			if (System.currentTimeMillis() - gameMode.getTime() > 0) {
				return Utils.convertToClock(System.currentTimeMillis() - gameMode.getTime());
			} else {
				time = shorten((double) (gameMode.getTime() - System.currentTimeMillis()) / 1000.0);
			}
		} else if (gameMode.getState() == GameState.FINISHED) {
			if (gameMode.getTime() > 0) {
				time = Utils.convertToClock(gameMode.getTime());
			} else {
				time = shorten((double) (-gameMode.getTime()) / 1000.0);
			}
		} else {
			throw new AssertionError();
		}
		if ((Boolean) getProperties().getSetting("showLargeCountdown").get()) {
			renderLarge(gameMode.getTime(), time);
		}
		return time;
	}

	private void renderLarge(long time, String timeString) {
		long l = System.currentTimeMillis();
		if (time - l <= 1000 * 15 && time - l > 0) {
			GameState state = getServer().getGameMode().getState();
			String translationKey;
			if (state == GameState.LOBBY || state == GameState.STARTING) {
				translationKey = "ingame.starting_in";
			} else if (state == GameState.PREGAME) {
				translationKey = "ingame.invincibility_wears_off";
			} else if (state == GameState.GAME || state == GameState.ENDGAME || state == GameState.FINISHED) {
				translationKey = "ingame.ending_in";
			} else {
				throw new AssertionError();
			}
			DisplayRenderer.largeTextRenderer.render(The5zigMod.getRenderer().getPrefix() + I18n.translate(translationKey, timeString));
		}
	}

	@Override
	public String getTranslation() {
		if (getServer() == null || getServer().getGameMode() == null) {
			return "ingame.time";
		}
		GameState state = getServer().getGameMode().getState();
		if (state == GameState.LOBBY || state == GameState.STARTING) {
			return "ingame.starting";
		}
		if (state == GameState.PREGAME) {
			return "ingame.invincibility";
		}
		if (state == GameState.GAME || state == GameState.ENDGAME || state == GameState.FINISHED) {
			return "ingame.time";
		}
		return super.getTranslation();
	}
}
