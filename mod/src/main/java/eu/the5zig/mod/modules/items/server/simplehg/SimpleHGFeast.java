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

package eu.the5zig.mod.modules.items.server.simplehg;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.simplehg.ServerSimpleHG;
import eu.the5zig.mod.util.Vector3i;

public class SimpleHGFeast extends GameModeItem<ServerSimpleHG.SimpleHG> {

	public SimpleHGFeast() {
		super(ServerSimpleHG.SimpleHG.class, GameState.GAME);
	}

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return "48, 66, -70 (" + shorten(300.0) + ")";
		}
		ServerSimpleHG.Feast feast = getGameMode().getFeast();
		if (feast == null) {
			return null;
		}

		Vector3i location = feast.getLocation();
		long time = feast.getTime() - System.currentTimeMillis();
		String result = location.getX() + ", " + location.getY() + ", " + location.getZ();
		if (time > 0) {
			result += " (" + shorten((double) time / 1000.0) + ")";
		}
		return result;
	}

	@Override
	public String getTranslation() {
		return "ingame.feast";
	}
}
