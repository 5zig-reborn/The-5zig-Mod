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

import eu.the5zig.teamspeak.impl.*;
import org.apache.commons.io.input.*;
import com.google.common.collect.*;
import org.apache.commons.io.*;
import java.util.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;

public abstract class LogFileParser extends TailerListenerAdapter
{
    protected final ServerInfoImpl serverInfo;
    private final File logFile;
    private Tailer tailer;
    
    public LogFileParser(final ServerInfoImpl serverInfo, final File logFile) {
        this.serverInfo = serverInfo;
        this.logFile = logFile;
        this.readTail();
        this.tailer = new Tailer(logFile, this, 500L, true);
        final Thread thread = new Thread(this.tailer, "Tail " + logFile.getName());
        thread.setDaemon(true);
        thread.start();
    }
    
    private void readTail() {
        ReversedLinesFileReader reader = null;
        try {
            reader = new ReversedLinesFileReader(this.logFile, 4096, Charsets.UTF_8);
            final List<String> lines = new ArrayList<>();
            for (int i = 0; i < 100; ++i) {
                final String line = reader.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
            Collections.reverse(lines);
            final Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                final String line = iterator.next();
                this.parse(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }
    
    public void stop() {
        this.tailer.stop();
    }
    
    protected abstract HTMLEditorKit.ParserCallback createParserCallback();
    
    public void handle(final String line) {
        this.parse(this.rebuildUTF8String(line));
    }
    
    private void parse(final String line) {
        Reader reader = null;
        try {
            String replace = line.replace("&nbsp;", " ");
            replace = replace.replace("&apos;", "'");
            reader = new StringReader(replace);
            final HTMLEditorKit.Parser parser = new ParserDelegator();
            parser.parse(reader, this.createParserCallback(), true);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(reader);
        }
    }
    
    private String rebuildUTF8String(final String line) {
        final int len = line.length();
        final byte[] bytes = new byte[len];
        for (int i = 0; i < len; ++i) {
            bytes[i] = (byte)line.charAt(i);
        }
        return new String(bytes, Charsets.UTF_8);
    }
}
