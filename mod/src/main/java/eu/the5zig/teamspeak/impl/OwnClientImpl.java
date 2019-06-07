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

import eu.the5zig.teamspeak.api.*;
import eu.the5zig.teamspeak.net.*;
import eu.the5zig.teamspeak.response.*;
import eu.the5zig.teamspeak.request.*;
import eu.the5zig.teamspeak.util.*;

public class OwnClientImpl extends ClientImpl implements OwnClient
{
    public OwnClientImpl(final TeamSpeakNetworkManager networkManager, final int id, final int databaseId, final String uniqueId, final String nickName, final ChannelImpl channel) {
        super(networkManager, id, databaseId, uniqueId, nickName, channel);
    }
    
    @Override
    public void setNickName(final String nickname) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.NICKNAME, nickname), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void setAway(final boolean away) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.AWAY, away), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void setAwayMessage(final String awayMessage) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.AWAY_MESSAGE, awayMessage), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void setInputMuted(final boolean muted) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.INPUT_MUTED, muted), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void setOutputMuted(final boolean muted) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.OUTPUT_MUTED, muted), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void setInputDeactivated(final boolean deactivated) {
        this.networkManager.sendRequest(new ClientUpdateRequest(ClientUpdateRequest.Ident.INPUT_DEACTIVATED, deactivated), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public void poke(final String message) {
        throw new UnsupportedOperationException("Cannot poke yourself!");
    }
    
    @Override
    public void kickFromChannel(final String reason) {
        throw new UnsupportedOperationException("Cannot kick yourself!");
    }
    
    @Override
    public void kickFromServer(final String reason) {
        throw new UnsupportedOperationException("Cannot kick yourself!");
    }
    
    @Override
    public void banFromServer(final String reason, final int time) {
        throw new UnsupportedOperationException("Cannot ban yourself!");
    }
    
    @Override
    public void mute() {
        throw new UnsupportedOperationException("Cannot mute yourself!");
    }
    
    @Override
    public void unMute() {
        throw new UnsupportedOperationException("Cannot un-mute yourself!");
    }
}
