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

package eu.the5zig.mod.server.timolia;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.mod.util.Vector3f;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

public class TimoliaAdventListener extends AbstractGameListener<ServerTimolia.Advent> {

	@Override
	public Class<ServerTimolia.Advent> getGameMode() {
		return ServerTimolia.Advent.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("advent");
	}

	@Override
	public void onMatch(ServerTimolia.Advent gameMode, String key, IPatternResult match) {
		if (key.equals("advent.start") || key.equals("advent.restart")) {
			reset(gameMode, match.get(0));
		}
		if ((gameMode.getState() == GameState.GAME || gameMode.getState() == GameState.FINISHED) && key.equals("advent.checkpoint")) {
			gameMode.setCurrentCheckpoint(gameMode.getCurrentCheckpoint() + 1);
			gameMode.setCurrentCheckpointCoordinates(new Vector3f((float) The5zigMod.getVars().getPlayerPosX(), (float) The5zigMod.getVars().getPlayerPosY(),
					(float) The5zigMod.getVars().getPlayerPosZ()));
		}
		if (key.equals("advent.checkpoint.time")) {
			if (gameMode.getState() == GameState.GAME) {
				gameMode.setTime(System.currentTimeMillis() - Utils.parseTimeFormatToMillis(match.get(0), "mm:ss.SSS"));
			} else if (gameMode.getState() == GameState.FINISHED) {
				gameMode.setTime(Utils.parseTimeFormatToMillis(match.get(0), "mm:ss"));
			}
		}
		if (gameMode.getState() == GameState.GAME && (key.equals("advent.finished") || key.equals("advent.already_finished"))) {
			gameMode.setState(GameState.FINISHED);
		}
		if (key.equals("advent.to_spawn")) {
			gameMode.setState(GameState.LOBBY);
		}
	}

	@Override
	public boolean onServerChat(ServerTimolia.Advent gameMode, String message) {
		if (matches(message, ChatColor.GOLD)) {
			gameMode.setTimeGold(parseTime(message));
		} else if (matches(message, ChatColor.GRAY)) {
			gameMode.setTimeSilver(parseTime(message));
		} else if (matches(message, ChatColor.DARK_AQUA)) {
			gameMode.setTimeBronze(parseTime(message));
		}
		return false;
	}

	@Override
	public void onTeleport(ServerTimolia.Advent gameMode, double x, double y, double z, float yaw, float pitch) {
		if (gameMode.getStartCoordinates().distanceSquared((float) x, (float) y, (float) z) <= 1.0f) {
			reset(gameMode, gameMode.getParkourName());
		}
		if (gameMode.getState() == GameState.GAME) {
			if (gameMode.getCurrentCheckpointCoordinates().distanceSquared((float) x, (float) y, (float) z) <= 1.0f) {
				gameMode.setFails(gameMode.getFails() + 1);
			}
		}
	}

	private void reset(ServerTimolia.Advent gameMode, String parkourName) {
		gameMode.setState(GameState.GAME);
		gameMode.setParkourName(parkourName);
		gameMode.setCurrentCheckpoint(1);
		gameMode.setTime(System.currentTimeMillis());
		gameMode.setFails(0);
		gameMode.setCurrentCheckpointCoordinates(new Vector3f((float) The5zigMod.getVars().getPlayerPosX(), (float) The5zigMod.getVars().getPlayerPosY(),
				(float) The5zigMod.getVars().getPlayerPosZ()));
		gameMode.setStartCoordinates(gameMode.getCurrentCheckpointCoordinates());
	}

	private boolean matches(String message, ChatColor chatColor) {
		return message.startsWith(ChatColor.DARK_BLUE + "\u2502" + ChatColor.GRAY + " " + chatColor + "\u2726:");
	}

	private long parseTime(String message) {
		String time = ChatColor.stripColor(message).split(": | Minuten")[1];
		return Utils.parseTimeFormatToMillis(time, "mm:ss.SSS");
	}
}
