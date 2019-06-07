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

import java.awt.image.*;
import java.util.*;
import eu.the5zig.teamspeak.util.*;

public interface Client extends Comparable<Client>
{
    int getId();
    
    int getDatabaseId();
    
    String getUniqueId();
    
    String getName();
    
    String getDisplayName();
    
    ClientType getType();
    
    Channel getChannel();
    
    BufferedImage getIcon();
    
    int getIconId();
    
    BufferedImage getAvatar();
    
    boolean isTalking();
    
    boolean isWhispering();
    
    boolean isInputMuted();
    
    boolean isOutputMuted();
    
    boolean hasInputHardware();
    
    boolean hasOutputHardware();
    
    int getTalkPower();
    
    boolean isTalker();
    
    boolean isPrioritySpeaker();
    
    boolean isRecording();
    
    boolean isChannelCommander();
    
    boolean isMuted();
    
    boolean isAway();
    
    String getAwayMessage();
    
    List<? extends Group> getServerGroups();
    
    Group getChannelGroup();
    
    void joinChannel(final Channel p0);
    
    void joinChannel(final Channel p0, final String p1);
    
    void joinChannel(final Channel p0, final Callback<Integer> p1);
    
    void joinChannel(final Channel p0, final String p1, final Callback<Integer> p2);
    
    void addToServerGroup(final Group p0);
    
    void removeFromServerGroup(final Group p0);
    
    void setChannelGroup(final Channel p0, final Group p1);
    
    void poke(final String p0);
    
    void kickFromChannel(final String p0);
    
    void kickFromServer(final String p0);
    
    void banFromServer(final String p0, final int p1);
    
    void mute();
    
    void unMute();
}
