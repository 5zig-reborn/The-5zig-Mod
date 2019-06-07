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

package eu.the5zig.teamspeak.tslogs;

import java.io.*;
import javax.swing.text.html.*;
import eu.the5zig.teamspeak.impl.*;
import eu.the5zig.teamspeak.*;
import org.apache.logging.log4j.*;

public class ChannelLogFileParser extends LogFileParser
{
    public ChannelLogFileParser(final ServerInfoImpl serverInfo, final File logDirectory) {
        super(serverInfo, new File(logDirectory, "channel.html"));
    }
    
    @Override
    protected HTMLEditorKit.ParserCallback createParserCallback() {
        return new Parser();
    }
    
    private class Parser extends HTMLEditorKit.ParserCallback
    {
        private StringBuilder builder;
        
        private Parser() {
            this.builder = new StringBuilder();
        }
        
        @Override
        public void handleText(final char[] data, final int pos) {
            this.builder.append(data);
        }
        
        @Override
        public void handleEndOfLineString(final String eol) {
            final String message = this.builder.toString();
            ChannelLogFileParser.this.serverInfo.getServerTab().getChannelChat().addMessage(new MessageImpl(message, System.currentTimeMillis()));
            if (TeamSpeak.isDebugMode()) {
                LogManager.getLogger().info(LogFileParseManager.logMarkerTail, message);
            }
        }
    }
}
