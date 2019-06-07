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

import java.lang.reflect.*;

public class EventListener implements Comparable<EventListener>
{
    private Object listener;
    private Method listenerMethod;
    private EventHandler.Priority priority;
    
    public EventListener(final Object listener, final Method listenerMethod, final EventHandler.Priority priority) {
        this.listener = listener;
        this.listenerMethod = listenerMethod;
        this.priority = priority;
    }
    
    public Object getListener() {
        return this.listener;
    }
    
    public Method getListenerMethod() {
        return this.listenerMethod;
    }
    
    public EventHandler.Priority getPriority() {
        return this.priority;
    }
    
    @Override
    public int compareTo(final EventListener o) {
        return o.getPriority().ordinal() - this.getPriority().ordinal();
    }
}
