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

import eu.the5zig.teamspeak.request.*;
import eu.the5zig.teamspeak.util.*;
import eu.the5zig.teamspeak.response.*;
import eu.the5zig.teamspeak.*;

public class RequestChain
{
    private final TeamSpeakNetworkManager networkManager;
    private TeamSpeakRequest request;
    
    public RequestChain(final TeamSpeakNetworkManager networkManager) {
        this.networkManager = networkManager;
    }
    
    RequestChain sendThen(final Request command, final Callback<TeamSpeakCommandResponse> callback) {
        final RequestChain chain = new RequestChain(this.networkManager);
        this.request = new TeamSpeakRequest(command, callback, chain);
        return chain;
    }
    
    void sendNextRequest() throws TeamSpeakException {
        if (this.request != null) {
            this.networkManager.sendRequest(this.request);
        }
    }
}
