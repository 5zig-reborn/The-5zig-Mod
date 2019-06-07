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

package eu.the5zig.teamspeak.event;

import eu.the5zig.teamspeak.api.*;

public class ClientMovedEvent extends Event
{
    public final Client client;
    public final Channel from;
    public final Channel to;
    public final Reason reason;
    public final Client invoker;
    public final String reasonMessage;
    
    public ClientMovedEvent(final ServerTab tab, final Client client, final Channel from, final Channel to, final Reason reason, final Client invoker, final String reasonMessage) {
        super(tab);
        this.client = client;
        this.from = from;
        this.to = to;
        this.reason = reason;
        this.invoker = invoker;
        this.reasonMessage = reasonMessage;
    }
    
    public enum Reason
    {
        MOVED_SELF(0), 
        MOVED_BY_OTHER(1), 
        KICKED(4);
        
        private int id;
        
        private Reason(final int id) {
            this.id = id;
        }
        
        public static Reason byId(final int id) {
            for (final Reason reason : values()) {
                if (reason.id == id) {
                    return reason;
                }
            }
            return Reason.MOVED_SELF;
        }
    }
}
