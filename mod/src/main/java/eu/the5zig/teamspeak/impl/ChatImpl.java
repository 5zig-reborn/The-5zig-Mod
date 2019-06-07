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

package eu.the5zig.teamspeak.impl;

import eu.the5zig.teamspeak.net.*;
import eu.the5zig.teamspeak.api.*;
import com.google.common.collect.*;
import java.util.*;

public abstract class ChatImpl implements Chat
{
    public static final int MAX_MESSAGES = 100;
    protected final TeamSpeakNetworkManager networkManager;
    private final MessageTargetMode type;
    protected final List<MessageImpl> messages;
    
    public ChatImpl(final TeamSpeakNetworkManager networkManager, final MessageTargetMode type) {
        this.messages = new ArrayList<>();
        this.networkManager = networkManager;
        this.type = type;
    }
    
    @Override
    public MessageTargetMode getType() {
        return this.type;
    }
    
    @Override
    public List<? extends Message> getMessages() {
        synchronized (this.messages) {
            return (List<? extends Message>)ImmutableList.copyOf((Collection)this.messages);
        }
    }
    
    public void addMessage(final MessageImpl message) {
        synchronized (this.messages) {
            this.messages.add(message);
            while (this.messages.size() > 100) {
                this.messages.remove(0);
            }
        }
    }
}
