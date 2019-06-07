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

package eu.the5zig.mod.server.hypixel;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Locale;

public class HypixelPaintballListener extends AbstractGameListener<ServerHypixel.Paintball> {

	@Override
	public Class<ServerHypixel.Paintball> getGameMode() {
		return ServerHypixel.Paintball.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("paintballlobby");
	}

	@Override
	public void onMatch(ServerHypixel.Paintball gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("paintball.kill")) {
				gameMode.setKills(gameMode.getKills() + 1);
			}
			if (key.equals("paintball.death")) {
				gameMode.setDeaths(gameMode.getDeaths() + 1);
			}
		}
		if (key.equals("paintball.team")) {
			gameMode.setTeam(WordUtils.capitalize(match.get(0).toLowerCase(Locale.ROOT)));
		}
	}

	@Override
	public void onTick(ServerHypixel.Paintball gameMode) {
		if (gameMode.getState() == GameState.LOBBY) {
			if (gameMode.getTime() != -1 && gameMode.getTime() - System.currentTimeMillis() < 0) {
				gameMode.setState(GameState.GAME);
				gameMode.setTime(System.currentTimeMillis());
			}
		}
	}
}
