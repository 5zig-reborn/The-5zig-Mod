/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

import eu.the5zig.teamspeak.util.*;

public class ValueParameter extends Parameter
{
    private String key;
    private String value;
    
    ValueParameter(final String key, final Object value) {
        this.key = key;
        this.value = convertValue(value);
    }
    
    private static String convertValue(final Object value) {
        if (value instanceof Boolean) {
            return (boolean)value ? "1" : "0";
        }
        return String.valueOf(value);
    }
    
    public String getKey() {
        return this.key;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String serialize() {
        return this.getKey() + "=" + EscapeUtil.escape(String.valueOf(this.getValue()));
    }
}
