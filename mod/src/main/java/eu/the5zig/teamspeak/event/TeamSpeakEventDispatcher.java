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

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import com.google.common.collect.*;

public class TeamSpeakEventDispatcher
{
    private static final HashMap<Class<?>, List<EventListener>> EVENT_HANDLERS;
    
    public static void registerListener(final Object listener) {
        try {
            for (final Method method : listener.getClass().getMethods()) {
                if (method.isAnnotationPresent(EventHandler.class)) {
                    final EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                    final Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                        final Class<?> parameter = parameterTypes[0];
                        List<EventListener> eventListeners = TeamSpeakEventDispatcher.EVENT_HANDLERS.get(parameter);
                        if (eventListeners == null) {
                            eventListeners = new ArrayList<>();
                            TeamSpeakEventDispatcher.EVENT_HANDLERS.put(parameter, eventListeners);
                        }
                        eventListeners.add(new EventListener(listener, method, eventHandler.priority()));
                        Collections.sort(eventListeners);
                    }
                }
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            System.err.println("Could not register listener " + listener.getClass().getSimpleName());
        }
    }
    
    public static void unregisterListener(final Object listener) {
        try {
            for (final List<EventListener> eventListeners : TeamSpeakEventDispatcher.EVENT_HANDLERS.values()) {
                final Iterator<EventListener> iterator = eventListeners.iterator();
                while (iterator.hasNext()) {
                    final EventListener next = iterator.next();
                    if (next.getListener() == listener) {
                        iterator.remove();
                    }
                }
            }
        }
        catch (Throwable throwable) {
            throwable.printStackTrace();
            System.err.println("Could not unregister listener " + listener.getClass().getSimpleName());
        }
    }
    
    public static void dispatch(final Event event) {
        final List<EventListener> eventListeners = TeamSpeakEventDispatcher.EVENT_HANDLERS.get(event.getClass());
        if (eventListeners == null) {
            return;
        }
        for (final EventListener eventListener : eventListeners) {
            try {
                eventListener.getListenerMethod().invoke(eventListener.getListener(), event);
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println("Could not dispatch event " + event.getClass().getSimpleName());
            }
        }
    }
    
    static {
        EVENT_HANDLERS = Maps.newHashMap();
    }
}
