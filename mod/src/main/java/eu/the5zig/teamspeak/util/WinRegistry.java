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

import java.util.prefs.*;
import java.lang.reflect.*;
import java.util.*;

public class WinRegistry
{
    public static final int HKEY_CURRENT_USER = -2147483647;
    public static final int HKEY_LOCAL_MACHINE = -2147483646;
    public static final int REG_SUCCESS = 0;
    public static final int REG_NOTFOUND = 2;
    public static final int REG_ACCESSDENIED = 5;
    private static final int KEY_ALL_ACCESS = 983103;
    private static final int KEY_READ = 131097;
    private static Preferences userRoot;
    private static Preferences systemRoot;
    private static Class<? extends Preferences> userClass;
    private static Method regOpenKey;
    private static Method regCloseKey;
    private static Method regQueryValueEx;
    private static Method regEnumValue;
    private static Method regQueryInfoKey;
    private static Method regEnumKeyEx;
    private static Method regCreateKeyEx;
    private static Method regSetValueEx;
    private static Method regDeleteKey;
    private static Method regDeleteValue;
    
    public static String readString(final int hkey, final String key, final String valueName) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == -2147483646) {
            return readString(WinRegistry.systemRoot, hkey, key, valueName);
        }
        if (hkey == -2147483647) {
            return readString(WinRegistry.userRoot, hkey, key, valueName);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }
    
    public static Map<String, String> readStringValues(final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == -2147483646) {
            return readStringValues(WinRegistry.systemRoot, hkey, key);
        }
        if (hkey == -2147483647) {
            return readStringValues(WinRegistry.userRoot, hkey, key);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }
    
    public static List<String> readStringSubKeys(final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == -2147483646) {
            return readStringSubKeys(WinRegistry.systemRoot, hkey, key);
        }
        if (hkey == -2147483647) {
            return readStringSubKeys(WinRegistry.userRoot, hkey, key);
        }
        throw new IllegalArgumentException("hkey=" + hkey);
    }
    
    public static void createKey(final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] ret;
        if (hkey == -2147483646) {
            ret = createKey(WinRegistry.systemRoot, hkey, key);
            WinRegistry.regCloseKey.invoke(WinRegistry.systemRoot, ret[0]);
        }
        else {
            if (hkey != -2147483647) {
                throw new IllegalArgumentException("hkey=" + hkey);
            }
            ret = createKey(WinRegistry.userRoot, hkey, key);
            WinRegistry.regCloseKey.invoke(WinRegistry.userRoot, ret[0]);
        }
        if (ret[1] != 0) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    }
    
    public static void writeStringValue(final int hkey, final String key, final String valueName, final String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (hkey == -2147483646) {
            writeStringValue(WinRegistry.systemRoot, hkey, key, valueName, value);
        }
        else {
            if (hkey != -2147483647) {
                throw new IllegalArgumentException("hkey=" + hkey);
            }
            writeStringValue(WinRegistry.userRoot, hkey, key, valueName, value);
        }
    }
    
    public static void deleteKey(final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (hkey == -2147483646) {
            rc = deleteKey(WinRegistry.systemRoot, hkey, key);
        }
        else if (hkey == -2147483647) {
            rc = deleteKey(WinRegistry.userRoot, hkey, key);
        }
        if (rc != 0) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key);
        }
    }
    
    public static void deleteValue(final int hkey, final String key, final String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int rc = -1;
        if (hkey == -2147483646) {
            rc = deleteValue(WinRegistry.systemRoot, hkey, key, value);
        }
        else if (hkey == -2147483647) {
            rc = deleteValue(WinRegistry.userRoot, hkey, key, value);
        }
        if (rc != 0) {
            throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
        }
    }
    
    private static int deleteValue(final Preferences root, final int hkey, final String key, final String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final int[] handles = (int[])WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), 983103);
        if (handles[1] != 0) {
            return handles[1];
        }
        final int rc = (int)WinRegistry.regDeleteValue.invoke(root, handles[0], toCstr(value));
        WinRegistry.regCloseKey.invoke(root, handles[0]);
        return rc;
    }
    
    private static int deleteKey(final Preferences root, final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final int rc = (int)WinRegistry.regDeleteKey.invoke(root, hkey, toCstr(key));
        return rc;
    }
    
    private static String readString(final Preferences root, final int hkey, final String key, final String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final int[] handles = (int[])WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), 131097);
        if (handles[1] != 0) {
            return null;
        }
        final byte[] valb = (byte[])WinRegistry.regQueryValueEx.invoke(root, handles[0], toCstr(value));
        WinRegistry.regCloseKey.invoke(root, handles[0]);
        return (valb != null) ? new String(valb).trim() : null;
    }
    
    private static Map<String, String> readStringValues(final Preferences root, final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final HashMap<String, String> results = new HashMap<String, String>();
        final int[] handles = (int[])WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), 131097);
        if (handles[1] != 0) {
            return null;
        }
        final int[] info = (int[])WinRegistry.regQueryInfoKey.invoke(root, handles[0]);
        final int count = info[0];
        final int maxlen = info[3];
        for (int index = 0; index < count; ++index) {
            final byte[] name = (byte[])WinRegistry.regEnumValue.invoke(root, handles[0], index, maxlen + 1);
            final String value = readString(hkey, key, new String(name));
            results.put(new String(name).trim(), value);
        }
        WinRegistry.regCloseKey.invoke(root, handles[0]);
        return results;
    }
    
    private static List<String> readStringSubKeys(final Preferences root, final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final List<String> results = new ArrayList<String>();
        final int[] handles = (int[])WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), 131097);
        if (handles[1] != 0) {
            return null;
        }
        final int[] info = (int[])WinRegistry.regQueryInfoKey.invoke(root, handles[0]);
        final int count = info[0];
        final int maxlen = info[3];
        for (int index = 0; index < count; ++index) {
            final byte[] name = (byte[])WinRegistry.regEnumKeyEx.invoke(root, handles[0], index, maxlen + 1);
            results.add(new String(name).trim());
        }
        WinRegistry.regCloseKey.invoke(root, handles[0]);
        return results;
    }
    
    private static int[] createKey(final Preferences root, final int hkey, final String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        return (int[])WinRegistry.regCreateKeyEx.invoke(root, hkey, toCstr(key));
    }
    
    private static void writeStringValue(final Preferences root, final int hkey, final String key, final String valueName, final String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final int[] handles = (int[])WinRegistry.regOpenKey.invoke(root, hkey, toCstr(key), 983103);
        WinRegistry.regSetValueEx.invoke(root, handles[0], toCstr(valueName), toCstr(value));
        WinRegistry.regCloseKey.invoke(root, handles[0]);
    }
    
    private static byte[] toCstr(final String str) {
        final byte[] result = new byte[str.length() + 1];
        for (int i = 0; i < str.length(); ++i) {
            result[i] = (byte)str.charAt(i);
        }
        result[str.length()] = 0;
        return result;
    }
    
    static {
        WinRegistry.userRoot = Preferences.userRoot();
        WinRegistry.systemRoot = Preferences.systemRoot();
        WinRegistry.userClass = WinRegistry.userRoot.getClass();
        WinRegistry.regOpenKey = null;
        WinRegistry.regCloseKey = null;
        WinRegistry.regQueryValueEx = null;
        WinRegistry.regEnumValue = null;
        WinRegistry.regQueryInfoKey = null;
        WinRegistry.regEnumKeyEx = null;
        WinRegistry.regCreateKeyEx = null;
        WinRegistry.regSetValueEx = null;
        WinRegistry.regDeleteKey = null;
        WinRegistry.regDeleteValue = null;
        try {
            (WinRegistry.regOpenKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegOpenKey", Integer.TYPE, byte[].class, Integer.TYPE)).setAccessible(true);
            (WinRegistry.regCloseKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegCloseKey", Integer.TYPE)).setAccessible(true);
            (WinRegistry.regQueryValueEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegQueryValueEx", Integer.TYPE, byte[].class)).setAccessible(true);
            (WinRegistry.regEnumValue = WinRegistry.userClass.getDeclaredMethod("WindowsRegEnumValue", Integer.TYPE, Integer.TYPE, Integer.TYPE)).setAccessible(true);
            (WinRegistry.regQueryInfoKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegQueryInfoKey1", Integer.TYPE)).setAccessible(true);
            (WinRegistry.regEnumKeyEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegEnumKeyEx", Integer.TYPE, Integer.TYPE, Integer.TYPE)).setAccessible(true);
            (WinRegistry.regCreateKeyEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegCreateKeyEx", Integer.TYPE, byte[].class)).setAccessible(true);
            (WinRegistry.regSetValueEx = WinRegistry.userClass.getDeclaredMethod("WindowsRegSetValueEx", Integer.TYPE, byte[].class, byte[].class)).setAccessible(true);
            (WinRegistry.regDeleteValue = WinRegistry.userClass.getDeclaredMethod("WindowsRegDeleteValue", Integer.TYPE, byte[].class)).setAccessible(true);
            (WinRegistry.regDeleteKey = WinRegistry.userClass.getDeclaredMethod("WindowsRegDeleteKey", Integer.TYPE, byte[].class)).setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
