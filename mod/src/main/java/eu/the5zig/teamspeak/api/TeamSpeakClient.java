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

import eu.the5zig.teamspeak.listener.*;
import java.util.*;

public interface TeamSpeakClient
{
    void connect();
    
    void connect(final String p0);
    
    void disconnect();
    
    boolean isConnected();
    
    void addConnectListener(final ConnectListener p0);
    
    void removeConnectListener(final ConnectListener p0);
    
    void addDisconnectListener(final DisconnectListener p0);
    
    void removeDisconnectListener(final DisconnectListener p0);
    
    void setAutoReconnect(final boolean p0);
    
    boolean isAutoReconnect();
    
    List<? extends ServerTab> getServerTabs();
    
    ServerTab getServerTab(final int p0);
    
    ServerTab getSelectedTab();
}
