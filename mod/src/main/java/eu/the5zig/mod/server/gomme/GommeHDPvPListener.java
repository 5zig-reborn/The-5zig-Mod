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

package eu.the5zig.mod.server.gomme;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class GommeHDPvPListener extends AbstractGameListener<ServerGommeHD.PvPMatch> {

	@Override
	public Class<ServerGommeHD.PvPMatch> getGameMode() {
		return ServerGommeHD.PvPMatch.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("1VS1");
	}

	@Override
	public void onMatch(ServerGommeHD.PvPMatch gameMode, String key, IPatternResult match) {
		if (key.equals("pvp.starting")) {
			gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
		}
		if (key.equals("pvp.start")) {
			gameMode.setState(GameState.GAME);
			gameMode.setTime(System.currentTimeMillis());
		}
		if (key.equals("pvp.win")) {
			gameMode.setWinner(match.get(0));
			gameMode.setState(GameState.FINISHED);
		}
	}

	@Override
	public void onTick(ServerGommeHD.PvPMatch gameMode) {
		if (gameMode.getState() == GameState.LOBBY) {
			gameMode.setState(GameState.STARTING);
		}
	}

}
