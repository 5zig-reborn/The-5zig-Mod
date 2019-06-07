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

package eu.the5zig.teamspeak.request;

import eu.the5zig.teamspeak.event.*;

public class ClientNotifyUnregisterRequest extends Request
{
    public ClientNotifyUnregisterRequest(final EventType eventType) {
        this(0, eventType);
    }
    
    public ClientNotifyUnregisterRequest(final int id, final EventType eventType) {
        super("clientnotifyunregister", new Parameter[] { Request.value("schandlerid", id), Request.value("event", eventType.getName()) });
    }
}
