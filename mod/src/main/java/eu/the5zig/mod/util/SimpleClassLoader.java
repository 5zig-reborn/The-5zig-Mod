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

package eu.the5zig.mod.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple custom class loader implementation
 */
public class SimpleClassLoader extends ClassLoader {

	/**
	 * The HashMap where the classes will be cached
	 */
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	@Override
	public String toString() {
		return SimpleClassLoader.class.getName();
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {

		if (classes.containsKey(name)) {
			return classes.get(name);
		}

		byte[] classData;

		try {
			classData = loadClassData(name);
		} catch (IOException e) {
			throw new ClassNotFoundException("Class [" + name
					+ "] could not be found", e);
		}

		Class<?> c = defineClass(name, classData, 0, classData.length);
		resolveClass(c);
		classes.put(name, c);

		return c;
	}

	/**
	 * Load the class file into byte array
	 *
	 * @param name The name of the class e.g. com.codeslices.test.TestClass}
	 * @return The class file as byte array
	 * @throws IOException
	 */
	private byte[] loadClassData(String name) throws IOException {
		BufferedInputStream in = new BufferedInputStream(
				ClassLoader.getSystemResourceAsStream(name.replace(".", "/")
						+ ".class"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int i;

		while ((i = in.read()) != -1) {
			out.write(i);
		}

		in.close();
		byte[] classData = out.toByteArray();
		out.close();

		return classData;
	}

}