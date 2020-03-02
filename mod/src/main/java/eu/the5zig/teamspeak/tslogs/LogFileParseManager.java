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

package eu.the5zig.teamspeak.tslogs;

import eu.the5zig.teamspeak.impl.*;
import eu.the5zig.teamspeak.util.*;
import java.io.*;
import com.google.common.base.*;
import org.apache.commons.codec.binary.*;
import org.apache.logging.log4j.*;

public class LogFileParseManager
{
    public static final Marker logMarkerTail;
    private LogFileParser serverLogParser;
    private LogFileParser channelLogParser;
    
    public LogFileParseManager(final ServerInfoImpl serverInfo) {
        final File dir = new File(new File(Utils.getTeamspeakDirectory(), "chats"), Base64.encodeBase64String(serverInfo.getUniqueId().getBytes(Charsets.UTF_8)));
        this.serverLogParser = new ServerLogFileParser(serverInfo, dir);
        this.channelLogParser = new ChannelLogFileParser(serverInfo, dir);
    }
    
    public void stop() {
        this.serverLogParser.stop();
        this.channelLogParser.stop();
    }
    
    static {
        logMarkerTail = MarkerManager.getMarker("CHAT_TAIL");
    }
}
