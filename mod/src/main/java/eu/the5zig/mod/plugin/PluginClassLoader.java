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

package eu.the5zig.mod.plugin;

import com.google.common.collect.Maps;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

public class PluginClassLoader extends URLClassLoader {

	private final Map<String, Class<?>> classes = Maps.newHashMap();
	private final PluginManagerImpl pluginManager;

	public PluginClassLoader(PluginManagerImpl pluginManager, ClassLoader parent, File file) throws MalformedURLException {
		super(new URL[]{file.toURI().toURL()}, parent);
		this.pluginManager = pluginManager;
	}

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}

	Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		Class<?> result = this.classes.get(name);
		if (result == null) {
			if (checkGlobal) {
				result = pluginManager.getClassByName(name, this);
			}

			if (result == null) {
				result = super.findClass(name);
			}

			this.classes.put(name, result);
		}
		return result;
	}

}
