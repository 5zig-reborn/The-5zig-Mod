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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.StringItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timer extends StringItem {

	private static final DateFormat formatterSecond = new SimpleDateFormat("ss.SSS");
	private static final DateFormat formatterMinute = new SimpleDateFormat("mm:ss.SSS");
	private static final DateFormat formatterHour = new SimpleDateFormat("HH:mm:ss.SSS");

	@Override
	protected Object getValue(boolean dummy) {
		long time = dummy ? 61500 : The5zigMod.getDataManager().getTimerManager().getTime();
		Date date = new Date(time);
		return time == 0 ? null : time < 60000 ? formatterSecond.format(date) : time < 3600000 ? formatterMinute.format(date) : formatterHour.format(date);
	}

	@Override
	public String getTranslation() {
		return "ingame.timer";
	}
}
