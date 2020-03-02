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

package eu.the5zig.mod.modules.items.system;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.StringItem;
import eu.the5zig.mod.util.ISystemPowerStatus;
import eu.the5zig.util.Utils;

public class Battery extends StringItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("chargeStatus", false);
	}

	@Override
	protected Object getValue(boolean dummy) {
		ISystemPowerStatus batteryStatus = The5zigMod.getVars().getBatteryStatus();
		if (batteryStatus == null) {
			return "100%";
		}

		String string = batteryStatus.getBatteryPercentage() + "%";
		if ((Boolean) getProperties().getSetting("chargeStatus").get()) {
			string += " (" + (batteryStatus.isPluggedIn() ? I18n.translate("modules.item.battery.plugged_in") : I18n.translate("modules.item.battery.un_plugged")) +
					(batteryStatus.getRemainingLifeTime() > 0 ? ": " + Utils.convertToTime(batteryStatus.getRemainingLifeTime() * 1000, false) : "") + ")";
		}

		return string;
	}

	@Override
	public String getTranslation() {
		return "ingame.battery";
	}
}
