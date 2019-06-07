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

package eu.the5zig.mod.server.hypixel.api;

import com.google.gson.JsonObject;
import eu.the5zig.mod.server.hypixel.HypixelGameType;

import java.util.Arrays;
import java.util.List;

public class HypixelStatSkyWars extends HypixelStatCategory {

	public HypixelStatSkyWars() {
		super(HypixelGameType.SKYWARS);
	}

	@Override
	public List<String> getStats(JsonObject object) {
		return Arrays.asList(parseInt(object, "coins"), parseInt(object, "kills"), parseInt(object, "assists"), parseInt(object, "deaths"), parseInt(object, "wins"),
				parseInt(object, "win_streak"), parseInt(object, "losses"), parseInt(object, "blocks_placed"), parseInt(object, "blocks_broken"), parseInt(object, "quits"),
				parseInt(object, "soul_well"), parseInt(object, "soul_well_legendaries"), parseInt(object, "soul_well_rares"), parseInt(object, "souls_gathered"),
				parseInt(object, "egg_thrown"), parseInt(object, "enderpearls_thrown"), parseInt(object, "arrows_shot"), parseInt(object, "arrows_hit"), parseInt(object, "items_enchanted"));
	}
}
