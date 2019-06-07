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

package eu.the5zig.teamspeak.api;

import org.apache.logging.log4j.*;
import java.util.regex.*;

public enum ChannelType
{
    NORMAL((Pattern)null), 
    CSPACER(Pattern.compile("\\[cspacer.*\\](.+)")), 
    SPACER(Pattern.compile("\\[\\*[c]?spacer.*\\](.+)")), 
    SPACER_TEXT(Pattern.compile("\\[spacer.*\\](.+)"));
    
    private Pattern pattern;
    
    private ChannelType(final Pattern pattern) {
        this.pattern = pattern;
    }
    
    public String formatName(final String name) {
        if (this.pattern == null) {
            return name;
        }
        final Matcher matcher = this.pattern.matcher(name);
        if (!matcher.matches()) {
            LogManager.getLogger().error("Could not match pattern of channel type " + this.toString() + " for name " + name + "!");
            return name;
        }
        return matcher.group(1);
    }
    
    public static ChannelType byName(final String name) {
        for (final ChannelType channelType : values()) {
            if (channelType.pattern != null) {
                final Matcher matcher = channelType.pattern.matcher(name);
                if (matcher.matches()) {
                    return channelType;
                }
            }
        }
        return ChannelType.NORMAL;
    }
}
