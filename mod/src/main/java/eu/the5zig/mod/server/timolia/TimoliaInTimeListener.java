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

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;

public class TimoliaInTimeListener extends AbstractGameListener<ServerTimolia.InTime> {

	@Override
	public Class<ServerTimolia.InTime> getGameMode() {
		return ServerTimolia.InTime.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("intime");
	}

	@Override
	public void onMatch(ServerTimolia.InTime gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("starting.actionbar")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("start")) {
				gameMode.setState(GameState.STARTING);
			}
		}
		if (gameMode.getState() == GameState.STARTING) {
			if (key.equals("intime.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
			if (key.equals("intime.start")) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("intime.invincibility")) {
				gameMode.setInvincible(false);
				gameMode.setInvincibleTimer(System.currentTimeMillis() + 3000);
			}
			if (key.equals("intime.loot.min")) {
				gameMode.setLoot(System.currentTimeMillis() + 1000 * 60 * Integer.parseInt(match.get(0)));
			}
			if (key.equals("intime.loot.sec")) {
				gameMode.setLoot(System.currentTimeMillis() + 1000 * Integer.parseInt(match.get(0)));
			}
			if (key.equals("intime.loot.spawned")) {
				gameMode.setLoot(-1);
				gameMode.setLootTimer(System.currentTimeMillis() + 3000);
			}
			if (key.equals("intime.spawn_regeneration")) {
				gameMode.setSpawnRegeneration(true);
				gameMode.setSpawnRegenerationTimer(System.currentTimeMillis() + 3000);
			}
		}
	}

	@Override
	public void onTick(ServerTimolia.InTime gameMode) {
		if (gameMode.getState() == GameState.GAME) {
			if (gameMode.getInvincibleTimer() != -1 && gameMode.getInvincibleTimer() - System.currentTimeMillis() < 0) {
				gameMode.setInvincibleTimer(-1);
			}
			if (gameMode.getSpawnRegenerationTimer() != -1 && gameMode.getSpawnRegenerationTimer() - System.currentTimeMillis() < 0) {
				gameMode.setSpawnRegenerationTimer(-1);
			}
			if (gameMode.getLootTimer() != -1 && gameMode.getLootTimer() - System.currentTimeMillis() < 0) {
				gameMode.setLootTimer(-1);
			}
		}
	}
}
