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

package eu.the5zig.teamspeak.net;

import io.netty.handler.codec.*;
import java.nio.charset.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.util.*;
import eu.the5zig.teamspeak.*;
import eu.the5zig.teamspeak.event.*;
import eu.the5zig.teamspeak.response.*;

public class TeamSpeakDecoder extends ByteToMessageDecoder
{
    private final Charset charset;
    
    public TeamSpeakDecoder() {
        this(Charset.defaultCharset());
    }
    
    public TeamSpeakDecoder(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    
    protected void decode(final ChannelHandlerContext channelHandlerContext, final ByteBuf byteBuf, final List<Object> out) throws Exception {
        final String line = byteBuf.toString(this.charset);
        if (line.contains("\rerror id=")) {
            final int lastErrorId = line.lastIndexOf("error id=");
            final int lastMsgId = line.lastIndexOf("msg=");
            out.add(new TeamSpeakCommandResponse(line.substring(0, line.lastIndexOf("\rerror id=")).replace("\r", "\n"), Integer.parseInt(line.substring(lastErrorId + "error id=".length(), lastMsgId - 1)), line.substring(lastMsgId + "msg=".length())));
            byteBuf.clear();
            return;
        }
        if (line.contains("\rselected schandlerid=") && line.startsWith("TS3 Client")) {
            out.add(new TeamSpeakServerConnectionResponse(line, Integer.parseInt(line.substring(line.lastIndexOf("=") + 1)), line.contains("Use the \"auth\" command to authenticate yourself")));
            byteBuf.clear();
            return;
        }
        final String replace = line.replace("\r", "");
        final EventType event = EventType.byName(replace.split(" ")[0]);
        if (event != null) {
            out.add(new TeamSpeakEventResponse(line, event));
            byteBuf.clear();
        }
    }
}
