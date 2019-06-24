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

package eu.the5zig.mod.plugin;

import com.google.common.collect.Lists;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.server.ServerInstance;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class LoadedPlugin {

	private final String name;
	private final String version;
	private final Object instance;
	private final PluginClassLoader classLoader;
	private final Map<Locale, ResourceBundle> locales;
	private final List<Method> loadMethods;
	private final List<Method> unloadMethods;
	private final File file;
	private String imageUrl;
	private String author;
	private String license;
	private String shortDesc;

	private final List<Object> registeredListeners = Lists.newArrayList();
	private final List<Class<? extends AbstractModuleItem>> registeredModuleItems = Lists.newArrayList();
	private final List<Class<? extends ServerInstance>> registeredServerInstances = Lists.newArrayList();

	public LoadedPlugin(String name, String version, Object instance, PluginClassLoader classLoader, Map<Locale, ResourceBundle> locales, List<Method> loadMethods, List<Method> unloadMethods, File file) {
		this.name = name;
		this.version = version;
		this.instance = instance;
		this.classLoader = classLoader;
		this.locales = locales;
		this.loadMethods = loadMethods;
		this.unloadMethods = unloadMethods;
		this.file = file;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getShortDescription() {
		return shortDesc;
	}

	public void setShortDescription(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public Object getInstance() {
		return instance;
	}

	public PluginClassLoader getClassLoader() {
		return classLoader;
	}

	public Map<Locale, ResourceBundle> getLocales() {
		return locales;
	}

	public List<Method> getLoadMethods() {
		return loadMethods;
	}

	public List<Method> getUnloadMethods() {
		return unloadMethods;
	}

	public File getFile() {
		return file;
	}

	public List<Object> getRegisteredListeners() {
		return registeredListeners;
	}

	public List<Class<? extends AbstractModuleItem>> getRegisteredModuleItems() {
		return registeredModuleItems;
	}

	public List<Class<? extends ServerInstance>> getRegisteredServerInstances() {
		return registeredServerInstances;
	}
}
