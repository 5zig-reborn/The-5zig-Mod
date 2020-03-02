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

package eu.the5zig.mod.render;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.PotionEffect;
import eu.the5zig.mod.util.GLUtil;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class PotionIndicatorRenderer {

	public void render() {
		if (!The5zigMod.getConfig().getBool("showPotionIndicator") || (The5zigMod.getDataManager().getServer() != null && !The5zigMod.getDataManager().getServer().isRenderPotionIndicator()))
			return;
		PotionEffect potionEffect = The5zigMod.getVars().getPotionForVignette();
		if (potionEffect == null)
			return;
		if (potionEffect.isGood()) {
			int maxTime = 20 * 60;
			double percent = (double) potionEffect.getTime() / (double) maxTime;
			float intensity = (float) (0.3 + percent);
			GLUtil.color(intensity, 0, intensity, 1);
		} else {
			int maxTime = 20 * 60;
			double percent = (double) potionEffect.getTime() / (double) maxTime;
			float intensity = (float) (0.2 + percent);
			GLUtil.color(0, intensity, intensity, 1);
		}
	}

}
