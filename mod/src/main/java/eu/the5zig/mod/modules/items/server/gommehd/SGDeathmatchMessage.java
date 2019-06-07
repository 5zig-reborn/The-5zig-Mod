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

package eu.the5zig.mod.modules.items.server.gommehd;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.modules.LargeTextItem;
import eu.the5zig.mod.server.gomme.ServerGommeHD;

public class SGDeathmatchMessage extends LargeTextItem<ServerGommeHD.SurvivalGames> {

	public SGDeathmatchMessage() {
		super(ServerGommeHD.SurvivalGames.class);
	}

	@Override
	protected String getText() {
		if (getGameMode().getDeathmatchTime() != -1 && getGameMode().getDeathmatchTime() - System.currentTimeMillis() > 0 &&
				getGameMode().getDeathmatchTime() - System.currentTimeMillis() <= 1000 * 15) {
			return I18n.translate("ingame.deathmatch_in", shorten((double) (getGameMode().getDeathmatchTime() - System.currentTimeMillis()) / 1000.0));
		}
		return null;
	}

}
