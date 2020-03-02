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

package eu.the5zig.mod.server.bergwerk;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class BergwerkDuelListener extends AbstractGameListener<ServerBergwerk.Duel> {

	@Override
	public Class<ServerBergwerk.Duel> getGameMode() {
		return ServerBergwerk.Duel.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("DUEL_");
	}

	@Override
	public void onMatch(ServerBergwerk.Duel gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("start")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
			if (key.equals("duel.team")) {
				gameMode.setTeam(match.get(0));
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("duel.bed_destroy")) {
				if (match.get(0).equals(gameMode.getTeam())) {
					gameMode.setCanRespawn(false);
				}
			}
			if (key.equals("duel.teleporter")) {
				gameMode.setTeleporterTimer(System.currentTimeMillis() + 5000);
			}
			if (key.equals("duel.win")) {
				gameMode.setWinner(match.get(0));
			}
		}
	}

	@Override
	public void onTick(ServerBergwerk.Duel gameMode) {
		if (gameMode.getTeleporterTimer() != -1) {
			if (System.currentTimeMillis() - gameMode.getTeleporterTimer() > 0)
				gameMode.setTeleporterTimer(-1);
		}
	}
}
