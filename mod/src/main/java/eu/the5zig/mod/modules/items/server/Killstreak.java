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

package eu.the5zig.mod.modules.items.server;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.render.RenderLocation;

public class Killstreak extends ServerItem {

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		super.render(x, y, renderLocation, dummy);
		if (getServer() == null || getServer().getGameMode() == null) {
			return;
		}
		int killstreak = getServer().getGameMode().getKillStreak();
		if (killstreak > 1 && The5zigMod.getConfig().getBool("showLargeKillstreaks")) {
			String text = null;
			if (killstreak == 2)
				text = I18n.translate("ingame.killstreak.double");
			else if (killstreak == 3)
				text = I18n.translate("ingame.killstreak.triple");
			else if (killstreak == 4)
				text = I18n.translate("ingame.killstreak.quadruple");
			else if (killstreak >= 5)
				text = I18n.translate("ingame.killstreak.multi");
			if (text == null)
				return;
			DisplayRenderer.largeTextRenderer.render(The5zigMod.getRenderer().getPrefix() + text);
		}
	}

	@Override
	protected Object getValue(boolean dummy) {
		if (dummy) {
			return 4;
		}
		return getServer().getGameMode() != null && getServer().getGameMode().getKillStreak() > 0 ? getServer().getGameMode().getKillStreak() : null;
	}

	@Override
	public String getTranslation() {
		return "ingame.killstreak";
	}
}
