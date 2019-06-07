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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.util.Container;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class StatisticRow implements Row {

	private String translation;
	private Container[] containers;
	private int xOff;
	private int yOff;

	public StatisticRow(String translation, Container... containers) {
		this(translation, 0, 0, containers);
	}

	public StatisticRow(String translation, int xOff, int yOff, Container... containers) {
		this.translation = translation;
		this.containers = containers;
		this.xOff = xOff;
		this.yOff = yOff;
	}

	@Override
	public int getLineHeight() {
		return 14;
	}

	@Override
	public void draw(int x, int y) {
		if (!The5zigMod.getNetworkManager().isConnected()) {
			The5zigMod.getVars().drawString(ChatColor.RED + I18n.translate("connection.offline"), x + xOff + 2, y + yOff + 2);
			return;
		}
		Object[] values = new Object[containers.length];
		for (int i = 0; i < containers.length; i++) {
			values[i] = containers[i].getValue();
		}
		The5zigMod.getVars().drawString(I18n.translate(translation, values), x + xOff + 2, y + yOff + 2);
	}
}
