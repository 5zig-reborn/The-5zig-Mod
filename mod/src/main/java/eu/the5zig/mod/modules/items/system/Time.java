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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time extends StringItem {

	private static final DateFormat SYSTEM_TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);
	private static final DateFormat SYSTEM_TIME_FORMAT_SECONDS = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	private static final DateFormat TIME_FORMAT_24 = new SimpleDateFormat("HH:mm");
	private static final DateFormat TIME_FORMAT_24_SECONDS = new SimpleDateFormat("HH:mm:ss");
	private static final DateFormat TIME_FORMAT_12 = new SimpleDateFormat("hh:mm a");
	private static final DateFormat TIME_FORMAT_12_SECONDS = new SimpleDateFormat("hh:mm:ss a");

	@Override
	public void registerSettings() {
		getProperties().addSetting("style", Style.SYSTEM, Style.class);
		getProperties().addSetting("showSeconds", true);
	}

	@Override
	protected Object getValue(boolean dummy) {
		DateFormat dateFormat;
		boolean showSeconds = (Boolean) getProperties().getSetting("showSeconds").get();
		switch ((Style) getProperties().getSetting("style").get()) {
			case HOURS_24:
				dateFormat = showSeconds ? TIME_FORMAT_24_SECONDS : TIME_FORMAT_24;
				break;
			case HOURS_12:
				dateFormat = showSeconds ? TIME_FORMAT_12_SECONDS : TIME_FORMAT_12;
				break;
			default:
				dateFormat = showSeconds ? SYSTEM_TIME_FORMAT_SECONDS : SYSTEM_TIME_FORMAT;
				break;
		}
		return dateFormat.format(new Date());
	}

	@Override
	public String getTranslation() {
		return "ingame.time";
	}

	private enum Style {
		SYSTEM, HOURS_24, HOURS_12
	}
}
