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

package eu.the5zig.mod.modules.items.system;

import eu.the5zig.mod.modules.StringItem;
import eu.the5zig.util.Utils;

public class Memory extends StringItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("style", Style.PERCENTAGE, Style.class);
	}

	@Override
	protected Object getValue(boolean dummy) {
		long maxMemory = dummy ? (long) Math.pow(1024, 3) : Runtime.getRuntime().maxMemory();
		long totalMemory = dummy ? (long) Math.pow(1024, 3) : Runtime.getRuntime().totalMemory();
		long freeMemory = dummy ? (long) Math.pow(1024, 3) / 2 : Runtime.getRuntime().freeMemory();
		long usedMemory = totalMemory - freeMemory;
		double percentageUsed = ((double) usedMemory / (double) maxMemory) * 100.0;
		Style style = (Style) getProperties().getSetting("style").get();

		if (style == Style.PERCENTAGE) {
			return shorten(percentageUsed) + "%";
		} else if (style == Style.BYTES) {
			return Utils.bytesToReadable(usedMemory) + "/" + Utils.bytesToReadable(maxMemory);
		} else {
			return shorten(percentageUsed) + "% (" + Utils.bytesToReadable(usedMemory) + "/" + Utils.bytesToReadable(maxMemory) + ")";
		}
	}

	@Override
	public String getTranslation() {
		return "ingame.memory";
	}

	public enum Style {

		PERCENTAGE, BYTES, BOTH

	}
}
