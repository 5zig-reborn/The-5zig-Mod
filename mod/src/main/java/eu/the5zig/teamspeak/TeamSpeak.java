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

package eu.the5zig.teamspeak;

import eu.the5zig.teamspeak.api.*;
import java.awt.*;
import java.net.*;

public class TeamSpeak
{
    private static final TeamSpeakClient CLIENT;
    private static boolean debugMode;
    
    public static TeamSpeakClient getClient() {
        return TeamSpeak.CLIENT;
    }
    
    public static void setDebugMode(final boolean debugMode) {
        TeamSpeak.debugMode = debugMode;
    }
    
    public static boolean isDebugMode() {
        return TeamSpeak.debugMode;
    }
    
    public static void startClient(final String server, final String nickname) {
        try {
            Desktop.getDesktop().browse(new URI("ts3server://" + server + "?nickname=" + nickname));
        }
        catch (Throwable throwable) {
            throw new TeamSpeakException(throwable);
        }
    }
    
    static {
        CLIENT = new TeamSpeakClientImpl();
        TeamSpeak.debugMode = false;
    }
}
