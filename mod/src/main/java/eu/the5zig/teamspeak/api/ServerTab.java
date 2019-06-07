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

import java.util.*;

public interface ServerTab
{
    int getId();
    
    boolean isSelected();
    
    void setSelected();
    
    ServerInfo getServerInfo();
    
    List<? extends Channel> getChannels();
    
    void createChannel(final String p0, final String p1, final String p2, final String p3, final ChannelLifespan p4, final boolean p5, final Channel p6, final Channel p7, final boolean p8, final int p9, final ChannelCodec p10, final int p11, final int p12);
    
    void updateChannelProperties(final Channel p0, final String p1, final String p2, final String p3, final String p4, final ChannelLifespan p5, final boolean p6, final Channel p7, final Channel p8, final boolean p9, final int p10, final ChannelCodec p11, final int p12, final int p13);
    
    void deleteChannel(final Channel p0, final boolean p1);
    
    OwnClient getSelf();
    
    List<? extends Group> getServerGroups();
    
    Group getServerGroup(final int p0);
    
    Group getDefaultServerGroup();
    
    List<? extends Group> getChannelGroups();
    
    Group getChannelGroup(final int p0);
    
    Group getDefaultChannelGroup();
    
    Chat getServerChat();
    
    Chat getChannelChat();
    
    Chat getPokeChat();
    
    void resetPokeChat();
    
    List<? extends PrivateChat> getPrivateChats();
    
    PrivateChat getPrivateChat(final Client p0);
    
    void removePrivateChat(final PrivateChat p0);
}
