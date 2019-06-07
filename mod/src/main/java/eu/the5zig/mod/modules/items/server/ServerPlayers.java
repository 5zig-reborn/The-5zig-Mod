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

package eu.the5zig.mod.modules.items.server;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.StringItem;

public class ServerPlayers extends StringItem {

	private long lastChecked;
	private int serverPlayers;

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return 64;
		}
		return The5zigMod.getDataManager().getServer() == null ? null : getServerPlayers();
	}

	private int getServerPlayers() {
		if (System.currentTimeMillis() - lastChecked < 500) {
			return serverPlayers;
		}
		lastChecked = System.currentTimeMillis();
		return serverPlayers = The5zigMod.getVars().getServerPlayers().size();
	}

	@Override
	public String getTranslation() {
		return "ingame.players";
	}
}
