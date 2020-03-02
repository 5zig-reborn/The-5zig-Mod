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

package eu.the5zig.mod.server.cytooxien;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class CytooxienBedwarsListener extends AbstractGameListener<ServerCytooxien.Bedwars> {

	public Class<ServerCytooxien.Bedwars> getGameMode() {
		return ServerCytooxien.Bedwars.class;
	}

	public boolean matchLobby(String lobby) {
		return lobby.contains("Bedwars");
	}

	@Override
	public void onMatch(ServerCytooxien.Bedwars gameMode, String key, IPatternResult match) {
		if (key.equals("bedwars.team")) {
			gameMode.setTeam(match.get(0));
			return;
		}
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("bedwars.gamestart")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		} else if (gameMode.getState() == GameState.GAME) {
			if (key.equals("bedwars.death")) {
				if (match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.setDeaths(gameMode.getDeaths() + 1);
				}
			} else if (key.equals("bedwars.eliminated")) {
				if (match.get(1).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.realkills++;
				}
			} else if (key.equals("bedwars.kill")) {
				gameMode.setKills(gameMode.getKills() + 1);
			} else if (key.equals("bedwars.died")) {
				gameMode.setDeaths(gameMode.getDeaths() + 1);
			} else if (key.equals("bedwars.beddestroyed")) {
				if (match.get(1).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.bedsdestroyed++;
				}
				if (match.get(0).equals(gameMode.getTeam())) {
					gameMode.setCanRespawn(false);
				}
			}
		}
	}
}