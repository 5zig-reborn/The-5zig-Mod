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

package eu.the5zig.mod.server.mineplex;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;

public class MineplexListener extends AbstractGameListener<GameMode> {

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
		if (key.equals("lobby")) {
			String lobby = match.get(0);
			getGameListener().switchLobby(lobby);
		}
	}

	@Override
	public void onPlayerListHeaderFooter(GameMode gameMode, String header, String footer) {
		// §lMineplex Network   §aLobby-22
		// Visit §awww.mineplex.com for News, Forums and Shop
		if (header.startsWith(ChatColor.BOLD + "Mineplex Network   " + ChatColor.GREEN + "Lobby-") || header.startsWith(ChatColor.GOLD.toString() + ChatColor.BOLD.toString())) {
			getGameListener().sendAndIgnore("/server", "lobby");
			String gameType = header.split(ChatColor.GREEN.toString() + "|" + ChatColor.GOLD.toString() + ChatColor.BOLD.toString())[1];
			getGameListener().switchLobby(gameType);
		}
	}

}
