/*
 * Copyright (c) 2019-2020 5zig Reborn
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

package eu.the5zig.mod.modules.items.player;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.modules.ModuleItemProperties;
import eu.the5zig.mod.modules.StringItem;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalDateTime;

public class Uptime extends StringItem {
    @Override
    protected Object getValue(boolean dummy) {
        Duration uptime = Duration.between(The5zigMod.getStartDate(), LocalDateTime.now());
        return format(uptime);
    }

    @Override
    public String getTranslation() {
        return "ingame.uptime";
    }

    @Override
    public void registerSettings() {
        getProperties().addSetting("extended", false);
    }

    private String format(Duration duration) {
        ModuleItemProperties settings = getProperties();
        boolean extended = (boolean) settings.getSetting("extended").get();
        String format = extended ? String.format("H '%s', m '%s'", I18n.translate("friend.profile.last_seen.hours"),
                I18n.translate("friend.profile.last_seen.minutes")) : "HH:mm";
        return DurationFormatUtils.formatDuration(duration.toMillis(), format);
    }
}
