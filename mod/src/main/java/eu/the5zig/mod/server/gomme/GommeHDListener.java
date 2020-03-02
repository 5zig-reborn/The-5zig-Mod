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
import eu.the5zig.mod.server.*;
import eu.the5zig.util.minecraft.ChatColor;

public class GommeHDListener extends AbstractGameListener<GameMode> {

	@Override
	public Class<GameMode> getGameMode() {
		return null;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return false;
	}

	@Override
	public void onMatch(GameMode gameMode, String key, IPatternResult match) {
		if (key.equals("nick.nicked.1") || key.equals("nick.nicked.2")) {
			getGameListener().setCurrentNick(match.get(0));
		}
		if (key.equals("nick.unnicked")) {
			getGameListener().setCurrentNick(null);
		}
		if (gameMode == null)
			return;
		if (gameMode instanceof Teamable) {
			if (key.equals("teams.allowed")) {
				((Teamable) gameMode).setTeamsAllowed(true);
			}
			if (key.equals("teams.not_allowed")) {
				((Teamable) gameMode).setTeamsAllowed(false);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("kill") && match.get(1).equals(The5zigMod.getDataManager().getUsername())) {
				gameMode.setKills(gameMode.getKills() + 1);
				gameMode.setKillStreak(gameMode.getKillStreak() + 1);
			}
			if (key.equals("death") || (key.equals("kill") && match.get(0).equals(The5zigMod.getDataManager().getUsername()))) {
				gameMode.setDeaths(gameMode.getDeaths() + 1);
				gameMode.setKillStreak(0);
			}
		}
	}

	@Override
	public void onPlayerListHeaderFooter(GameMode gameMode, String header, String footer) {
		header = ChatColor.stripColor(header);
		if (header.startsWith("GommeHD.net ")) {
			getGameListener().switchLobby(header.split("GommeHD.net |\n")[1]);
		}
	}

}
