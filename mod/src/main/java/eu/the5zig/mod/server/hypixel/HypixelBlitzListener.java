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

package eu.the5zig.mod.server.hypixel;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class HypixelBlitzListener extends AbstractGameListener<ServerHypixel.Blitz> {

	@Override
	public Class<ServerHypixel.Blitz> getGameMode() {
		return ServerHypixel.Blitz.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("blitzlobby");
	}

	@Override
	public void onMatch(ServerHypixel.Blitz gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (key.equals("blitz.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
			if (key.equals("blitz.kit.select")) {
				gameMode.setKit(match.get(0));
			}
		}
		if (gameMode.getState() == GameState.PREGAME) {
			if (key.equals("blitz.no_kit")) {
				gameMode.setKit(match.get(0));
			}
			if (key.equals("blitz.items")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
			if (key.equals("blitz.kit")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis() - 1000 * 60);
			}
		}
		if (gameMode.getState() == GameState.PREGAME || gameMode.getState() == GameState.GAME) {
			if (key.equals("blitz.star.min")) {
				gameMode.setStar(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000 * 60);
			}
			if (key.equals("blitz.star.sec")) {
				gameMode.setStar(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
			if (key.equals("blitz.kill")) {
				if (match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.setKillStreak(0);
				}
				if (match.get(1).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.setKills(gameMode.getKills() + 1);
					gameMode.setKillStreak(gameMode.getKillStreak() + 1);
				}
			}
			if (key.equals("blitz.deathmatch.min")) {
				gameMode.setStar(-1);
				gameMode.setDeathmatch(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000 * 60);
			}
			if (key.equals("blitz.deathmatch.sec")) {
				gameMode.setStar(-1);
				gameMode.setDeathmatch(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
		}
	}

	@Override
	public void onTick(ServerHypixel.Blitz gameMode) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (gameMode.getTime() != -1 && gameMode.getTime() - System.currentTimeMillis() < 0) {
				gameMode.setState(GameState.STARTING);
				gameMode.setTime(System.currentTimeMillis() + 30 * 1000);
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (gameMode.getTime() != -1 && gameMode.getTime() - System.currentTimeMillis() < 0) {
				gameMode.setState(GameState.PREGAME);
				gameMode.setTime(System.currentTimeMillis() + 60 * 1000);
			}
		}
		if (gameMode.getState() == GameState.PREGAME) {
			if (gameMode.getTime() != -1 && gameMode.getTime() - System.currentTimeMillis() < 0) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis() - 60 * 1000);
			}
		}
		if (gameMode.getDeathmatch() - System.currentTimeMillis() < 0) {
			gameMode.setDeathmatch(-1);
		}
	}
}
