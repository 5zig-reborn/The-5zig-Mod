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

package eu.the5zig.mod.server.timolia;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class TimoliaDNAListener extends AbstractGameListener<ServerTimolia.DNA> {

	@Override
	public Class<ServerTimolia.DNA> getGameMode() {
		return ServerTimolia.DNA.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("dna");
	}

	@Override
	public void onMatch(ServerTimolia.DNA gameMode, String key, IPatternResult match) {
		if (key.equals("starting.actionbar")) {
			gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
		}
		if (key.equals("start")) {
			gameMode.setState(GameState.STARTING);
			gameMode.setTime(System.currentTimeMillis() + 1000 * 7);
		}
	}

	@Override
	public void onTick(ServerTimolia.DNA gameMode) {
		if (gameMode.getStartHeight() > 0) {
			gameMode.setHeight(The5zigMod.getVars().getPlayerPosY() - gameMode.getStartHeight());
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (System.currentTimeMillis() - gameMode.getTime() > 0) {
				gameMode.setStartHeight((int) Math.floor(The5zigMod.getVars().getPlayerPosY()));
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
	}

}
