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

package eu.the5zig.mod.server.gomme;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class GommeHDRageModeListener extends AbstractGameListener<ServerGommeHD.RageMode> {

	@Override
	public Class<ServerGommeHD.RageMode> getGameMode() {
		return ServerGommeHD.RageMode.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.equals("RageMode");
	}

	@Override
	public void onMatch(ServerGommeHD.RageMode gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("rm.lobby.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("rm.lobby.start")) {
				gameMode.setState(GameState.STARTING);
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (key.equals("rm.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("rm.start")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("rm.kill")) {
				gameMode.setKills(gameMode.getKills() + 1);
				gameMode.setKillStreak(gameMode.getKillStreak() + 1);
				gameMode.setEmeralds(gameMode.getEmeralds() + 1);
			}
			if (key.equals("rm.kill.nemesis")) {
				gameMode.setEmeralds(gameMode.getEmeralds() + 1);
			}
			if (key.equals("rm.death.axe") || key.equals("rm.death.suicide")) {
				gameMode.setDeaths(gameMode.getDeaths() + 1);
				gameMode.setKillStreak(0);
			}
			if (key.equals("rm.shop")) {
				String item = match.get(0);
				if (item.equals("Mine")) {
					gameMode.setEmeralds(gameMode.getEmeralds() - 5);
				} else if (item.equals("Hundestaffel") || item.equals("Rüstung")) {
					gameMode.setEmeralds(gameMode.getEmeralds() - 10);
				} else if (item.equals("Geschwindigkeit")) {
					gameMode.setEmeralds(gameMode.getEmeralds() - 15);
				} else if (item.equals("Airstrike")) {
					gameMode.setEmeralds(gameMode.getEmeralds() - 25);
				} else if (item.equals("Gottes Licht")) {
					gameMode.setEmeralds(gameMode.getEmeralds() - 30);
				}
			}
			if (key.equals("rm.win")) {
				gameMode.setWinner(match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
		}
	}
}
