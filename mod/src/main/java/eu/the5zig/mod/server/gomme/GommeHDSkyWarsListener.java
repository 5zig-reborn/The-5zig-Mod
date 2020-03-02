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
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Map;

public class GommeHDSkyWarsListener extends AbstractGameListener<ServerGommeHD.SkyWars> {

	@Override
	public Class<ServerGommeHD.SkyWars> getGameMode() {
		return ServerGommeHD.SkyWars.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("SkyWars");
	}

	@Override
	public void onGameModeJoin(ServerGommeHD.SkyWars gameMode) {
//		getGameListener().sendAndIgnore("/coins", "sw.coins");
	}

	@Override
	public void onMatch(ServerGommeHD.SkyWars gameMode, String key, IPatternResult match) {
		if (key.equals("sw.coins")) {
			gameMode.setCoins(match.get(0));
		}
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("sw.lobby.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("sw.lobby.start")) {
				gameMode.setState(GameState.STARTING);
				gameMode.setTime(System.currentTimeMillis() + 3000);
			}
			if (key.equals("sw.team.added")) {
				gameMode.setTeam(Integer.parseInt(match.get(0)));
			}
			if (key.equals("sw.kit")) {
				gameMode.setKit(match.get(0));
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (key.equals("sw.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("sw.start")) {
				gameMode.setState(GameState.PREGAME);
				gameMode.setTime(System.currentTimeMillis() + 1000 * 30);
				Scoreboard scoreboard = The5zigMod.getVars().getScoreboard();
				for (Map.Entry<String, Integer> entry : scoreboard.getLines().entrySet()) {
					if (entry.getValue() == 1) {
						gameMode.setKit(ChatColor.stripColor(entry.getKey()));
					}
				}
			}
		}
		if (gameMode.getState() == GameState.PREGAME) {
			if (key.equals("sw.invincibility")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("sw.invincibility_off")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis() - 1000 * 30);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("sw.win")) {
				gameMode.setWinner(match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
		}
		if (gameMode.getState() == GameState.STARTING || gameMode.getState() == GameState.PREGAME || gameMode.getState() == GameState.GAME) {
			if (key.equals("sw.team")) {
				gameMode.setTeam(Integer.parseInt(match.get(0)));
			}
		}
	}

}
