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

public class TimoliaBrainBowListener extends AbstractGameListener<ServerTimolia.BrainBow> {

	@Override
	public Class<ServerTimolia.BrainBow> getGameMode() {
		return ServerTimolia.BrainBow.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("brainbow");
	}

	@Override
	public void onMatch(ServerTimolia.BrainBow gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY || gameMode.getState() == GameState.STARTING) {
			if (key.equals("brainbow.team")) {
				gameMode.setTeam(match.get(0));
			}
		}
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("start")) {
				gameMode.setState(GameState.STARTING);
				gameMode.setTime(System.currentTimeMillis() + 4890);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("brainbow.win")) {
				gameMode.setWinner("Team " + match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
			if (key.equals("brainbow.score") && match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
				gameMode.setScore(gameMode.getScore() + 1);
			}
		}
	}

	@Override
	public void onTick(ServerTimolia.BrainBow gameMode) {
		if (gameMode.getState() == GameState.STARTING) {
			if (System.currentTimeMillis() - gameMode.getTime() > 0) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
	}

}
