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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class GommeHDEnderGamesListener extends AbstractGameListener<ServerGommeHD.EnderGames> {

	@Override
	public Class<ServerGommeHD.EnderGames> getGameMode() {
		return ServerGommeHD.EnderGames.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.equals("EnderGames") || lobby.equals("EG");
	}

	@Override
	public void onGameModeJoin(ServerGommeHD.EnderGames gameMode) {
		getGameListener().sendAndIgnore("/coins", "eg.coins");
	}

	@Override
	public void onMatch(ServerGommeHD.EnderGames gameMode, String key, IPatternResult match) {
		if (key.equals("eg.coins")) {
			gameMode.setCoins(match.get(0));
		}
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("eg.lobby.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("eg.lobby.start")) {
				gameMode.setState(GameState.STARTING);
			}
		}
		if (gameMode.getState() == GameState.LOBBY || gameMode.getState() == GameState.STARTING) {
			if (key.equals("eg.kit")) {
				gameMode.setKit(match.get(0));
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (key.equals("eg.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("eg.start")) {
				gameMode.setState(GameState.PREGAME);
				gameMode.setTime(System.currentTimeMillis() + 1000 * 22);
			}
		}
		if (gameMode.getState() == GameState.PREGAME) {
			if (key.equals("eg.invincibility")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("eg.invincibility_off")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis() - 1000 * 74);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("eg.win")) {
				gameMode.setWinner(match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
		}
		if (gameMode.getState() == GameState.PREGAME || gameMode.getState() == GameState.GAME) {
			if (key.equals("eg.track")) {
				if (The5zigMod.getConfig().getBool("showCompassTarget")) {
					The5zigMod.getGuiIngame().showTextAboveHotbar("Tracking: " + match.get(0) + " (" + match.get(1) + ")");
				}
			}
		}
	}

}
