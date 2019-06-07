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

package eu.the5zig.teamspeak.util;

import java.util.*;

public class PropertyMap
{
    private Map<String, String> properties;
    
    public PropertyMap(final Map<String, String> properties) {
        this.properties = properties;
    }
    
    public Map<String, String> getProperties() {
        return this.properties;
    }
    
    public boolean contains(final String key) {
        return this.properties.containsKey(key);
    }
    
    public String get(final String key) {
        return this.properties.get(key);
    }
    
    public String get(final String key, final String fallback) {
        return this.contains(key) ? this.properties.get(key) : fallback;
    }
    
    public boolean getBool(final String key) {
        return "1".equals(this.properties.get(key));
    }
    
    public boolean getBool(final String key, final boolean fallback) {
        return this.contains(key) ? "1".equals(this.properties.get(key)) : fallback;
    }
    
    public int getInt(final String key) {
        return this.getInt(key, 0);
    }
    
    public int getInt(final String key, final int fallback) {
        final String value = this.properties.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch (NumberFormatException ex) {}
        }
        return fallback;
    }
    
    public long getLong(final String key) {
        return this.getLong(key, 0L);
    }
    
    public long getLong(final String key, final long fallback) {
        final String value = this.properties.get(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            }
            catch (NumberFormatException ex) {}
        }
        return fallback;
    }
    
    public float getFloat(final String key) {
        return this.getFloat(key, 0.0f);
    }
    
    public float getFloat(final String key, final float fallback) {
        final String value = this.properties.get(key);
        if (value != null) {
            try {
                return Float.parseFloat(value);
            }
            catch (NumberFormatException ex) {}
        }
        return fallback;
    }
}
