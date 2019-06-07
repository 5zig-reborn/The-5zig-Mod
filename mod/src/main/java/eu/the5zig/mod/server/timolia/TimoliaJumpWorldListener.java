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
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.mod.util.Vector3f;
import eu.the5zig.util.minecraft.ChatColor;

public class TimoliaJumpWorldListener extends AbstractGameListener<ServerTimolia.JumpWorld> {

	@Override
	public Class<ServerTimolia.JumpWorld> getGameMode() {
		return ServerTimolia.JumpWorld.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("jumpworld");
	}

	@Override
	public void onMatch(ServerTimolia.JumpWorld gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("jumpworld.checkpoint")) {
				gameMode.setCheckpoints(gameMode.getCheckpoints() + 1);
				float x = Math.round(The5zigMod.getVars().getPlayerPosX()) + 0.5f;
				float y = Math.round(The5zigMod.getVars().getPlayerPosY());
				float z = Math.round(The5zigMod.getVars().getPlayerPosZ()) + 0.5f;
				gameMode.setLastCheckpoint(new Vector3f(x, y, z));
			} if (key.equals("jumpworld.finished")) {
				gameMode.setState(GameState.FINISHED);
			}
		}
	}

	@Override
	public void onTitle(ServerTimolia.JumpWorld gameMode, String title, String subTitle) {
		if ((ChatColor.WHITE.toString() + ChatColor.GRAY.toString() + "\u2588 \u2588 \u2588" + ChatColor.RESET).equals(title) && subTitle == null) {
			gameMode.setState(GameState.STARTING);
			gameMode.setTime(System.currentTimeMillis() + 1000 * 4);
		}
		if ((ChatColor.WHITE.toString() + ChatColor.RED.toString() + "\u2588 " + ChatColor.GRAY + "\u2588 \u2588" + ChatColor.RESET).equals(title) && subTitle == null) {
			gameMode.setState(GameState.STARTING);
			gameMode.setTime(System.currentTimeMillis() + 1000 * 3);
		}
		if ((ChatColor.WHITE.toString() + ChatColor.RED.toString() + "\u2588 \u2588 " + ChatColor.GRAY + "\u2588" + ChatColor.RESET).equals(title) && subTitle == null) {
			gameMode.setState(GameState.STARTING);
			gameMode.setTime(System.currentTimeMillis() + 1000 * 2);
		}
		if ((ChatColor.WHITE.toString() + ChatColor.RED.toString() + "\u2588 \u2588 \u2588" + ChatColor.RESET).equals(title) && subTitle == null) {
			gameMode.setState(GameState.STARTING);
			gameMode.setTime(System.currentTimeMillis() + 1000);
		}
		if ((ChatColor.WHITE.toString() + ChatColor.GREEN.toString() + "\u2588 \u2588 \u2588" + ChatColor.RESET).equals(title) && subTitle == null) {
			gameMode.setState(GameState.GAME);
			gameMode.setCheckpoints(0);
			gameMode.setLastCheckpoint(
					new Vector3f((float) The5zigMod.getVars().getPlayerPosX(), (float) The5zigMod.getVars().getPlayerPosY(), (float) The5zigMod.getVars().getPlayerPosZ()));
			gameMode.setTime(System.currentTimeMillis());
		}
	}

	@Override
	public void onTick(ServerTimolia.JumpWorld gameMode) {
		Scoreboard scoreboard = The5zigMod.getVars().getScoreboard();
		if (scoreboard == null) {
			return;
		}
		if (gameMode.getState() == GameState.GAME) {
			if (!ChatColor.stripColor(scoreboard.getTitle()).equals("Timolia JumpWorld")) {
				gameMode.setFails(Integer.parseInt(ChatColor.stripColor(scoreboard.getTitle()).split(" - ")[1]));
			}
		}
		if (gameMode.getState() == GameState.GAME || gameMode.getState() == GameState.FINISHED) {
			if (ChatColor.stripColor(scoreboard.getTitle()).equals("Timolia JumpWorld")) {
				gameMode.setState(GameState.LOBBY);
			}
		}
	}
}
