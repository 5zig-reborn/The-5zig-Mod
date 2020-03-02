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

package eu.the5zig.mod.modules.items.server.timolia;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.timolia.ServerTimolia;
import eu.the5zig.mod.util.Vector3f;

public class JumpWorldLastCheckpoint extends GameModeItem<ServerTimolia.JumpWorld> {

	public JumpWorldLastCheckpoint() {
		super(ServerTimolia.JumpWorld.class, GameState.GAME);
	}

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return shorten(10.0) + " m";
		}
		Vector3f lastCheckpoint = getGameMode().getLastCheckpoint();
		if (lastCheckpoint == null) {
			return null;
		}
		float distance = lastCheckpoint.distanceSquared((float) The5zigMod.getVars().getPlayerPosX(), (float) The5zigMod.getVars().getPlayerPosY(), (float) The5zigMod.getVars().getPlayerPosZ());
		return shorten(Math.sqrt(distance)) + " m";
	}

	@Override
	public String getTranslation() {
		return "ingame.last_checkpoint";
	}
}
