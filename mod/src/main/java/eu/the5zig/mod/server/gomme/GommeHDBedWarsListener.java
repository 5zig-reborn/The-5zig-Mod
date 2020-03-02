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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class GommeHDBedWarsListener extends AbstractGameListener<ServerGommeHD.BedWars> {

	@Override
	public Class<ServerGommeHD.BedWars> getGameMode() {
		return ServerGommeHD.BedWars.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.equals("BedWars") || lobby.equals("BW") || lobby.equals("QBW");
	}

	@Override
	public void onGameModeJoin(ServerGommeHD.BedWars gameMode) {
		gameMode.setGoldTime(((GameServer) The5zigMod.getDataManager().getServer()).getLobby().equals("QBW") ? 15 : 30);
	}

	@Override
	public void onMatch(ServerGommeHD.BedWars gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("bw.lobby.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("bw.start")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("bw.bed.self")) {
				gameMode.setCanRespawn(false);
			}
			if (key.equals("bw.bed.other") && match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
				gameMode.setBeds(gameMode.getBeds() + 1);
			}
			if (key.equals("bw.win")) {
				gameMode.setWinner(match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
		}
		if (key.equals("bw.team")) {
			gameMode.setTeam(match.get(0));
		}
	}

}
