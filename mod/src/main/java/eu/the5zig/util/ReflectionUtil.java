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

package eu.the5zig.util;

public class ReflectionUtil {

	public static Class<?> forName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class '" + name + "' not found!");
		}
	}

	public static <T> T cast(Class<T> c, Object o) {
		return c.cast(o);
	}

	public static Object fieldValue(Class c, Object o, String f) {
		try {
			return c.getField(f).get(o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object newInstance(Class<?> c, Class[] parameterTypes, Object[] args) {
		try {
			return c.getConstructor(parameterTypes).newInstance(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Object invoke(Class<?> c, String m, Class[] parameterTypes, Object o, Object[] args) {
		try {
			return c.getMethod(m, parameterTypes).invoke(o, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
