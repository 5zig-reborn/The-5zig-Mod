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
import eu.the5zig.mod.config.LastServer;
import eu.the5zig.mod.server.*;

import java.util.UUID;
import java.util.regex.Pattern;

public class HypixelListener extends AbstractGameListener<GameMode> {

	private final Pattern gameModePattern = Pattern.compile("s[0-9]{1,4}|mini[0-9]+.+");

	@Override
	public Class<GameMode> getGameMode() {
		return null;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return false;
	}

	@Override
	public void onServerJoin() {
		LastServer configuration = The5zigMod.getLastServerConfig().getConfigInstance();
		if (configuration.getLastServer() != null && configuration.getLastServer() instanceof GameServer && "hypixel".equals(((GameServer) configuration.getLastServer()).getConfigName())) {
			The5zigMod.getDataManager().setServer(configuration.getLastServer());
		}
	}

	@Override
	public void onMatch(GameMode gameMode, String key, IPatternResult match) {
		if (key.equals("lobby")) {
			String lobby = match.get(0);
			if (gameModePattern.matcher(lobby).matches()) {
				getGameListener().switchLobby(getGameListener().getCurrentLobby());
			} else {
				getGameListener().switchLobby(lobby);
			}
		}
		if (gameMode != null) {
			if (gameMode.getState() == GameState.GAME) {
				if (key.equals("win") && gameMode.getWinner() == null) {
					gameMode.setWinner(match.get(0));
					gameMode.setState(GameState.FINISHED);
				} else if (key.equals("win.team")) {
					gameMode.setWinner("Team " + match.get(0));
					gameMode.setState(GameState.FINISHED);
				}
			}
		}
		if (key.equals("api")) {
			The5zigMod.logger.info("Got new Hypixel API key!");
			The5zigMod.getHypixelAPIManager().setKey(UUID.fromString(match.get(0)));
		}
	}

	@Override
	public void onTick(GameMode gameMode) {
		if (The5zigMod.getHypixelAPIManager().keyRequested) {
			The5zigMod.getHypixelAPIManager().keyRequested = false;
			The5zigMod.logger.info("Requesting new Hypixel API key...");
			getGameListener().sendAndIgnore("/api new", "api");
		}
	}

	@Override
	public void onServerConnect(GameMode gameMode) {
		getGameListener().sendAndIgnore("/whereami", "lobby");
		The5zigMod.getHypixelAPIManager().checkFriendSuggestions();
	}

}
