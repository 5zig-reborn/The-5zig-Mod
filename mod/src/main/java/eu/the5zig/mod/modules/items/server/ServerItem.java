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
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.server.GameState;

public abstract class ServerItem extends StringItem {

	private GameState[] state;

	public ServerItem(GameState... state) {
		this.state = state;
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		if (dummy) {
			return true;
		}
		if (The5zigMod.getDataManager().getServer() instanceof GameServer) {
			if (state != null && state.length != 0) {
				for (GameState gameState : state) {
					if (getServer().getGameMode().getState() == gameState) {
						return getValue(false) != null;
					}
				}
				return false;
			}
			return getValue(false) != null;
		}
		return false;
	}

	protected GameServer getServer() {
		return !(The5zigMod.getDataManager().getServer() instanceof GameServer) ? null : (GameServer) The5zigMod.getDataManager().getServer();
	}
}
