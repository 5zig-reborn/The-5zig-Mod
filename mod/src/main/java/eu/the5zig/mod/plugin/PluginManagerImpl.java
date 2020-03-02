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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.*;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.event.Event;
import eu.the5zig.mod.event.LoadEvent;
import eu.the5zig.mod.event.UnloadEvent;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.server.ServerInstance;
import eu.the5zig.mod.util.PluginException;
import eu.the5zig.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class PluginManagerImpl implements PluginManager {

	private final List<LoadedPlugin> plugins = Lists.newArrayList();
	private final Map<String, Class<?>> cachedClasses = Maps.newHashMap();
	private final File moduleDirectory = new File(The5zigMod.getModDirectory(), "plugins");

	public PluginManagerImpl() {
		try {
			loadPlugins();
			if (!plugins.isEmpty()) {
				The5zigMod.logger.info("Loaded " + plugins.size() + " plugins!");
			}
		} catch (Throwable throwable) {
			The5zigMod.logger.error("Could not load plugins!", throwable);
		}
	}

	public File getModuleDirectory() {
		return moduleDirectory;
	}

	/**
	 * Loads all plugins from the {@code the5zigmod/plugins} directory.
	 *
	 * @throws Throwable
	 */
	private void loadPlugins() throws Throwable {
		if (!moduleDirectory.exists() && !moduleDirectory.mkdirs()) {
			throw new IOException("Could not find or create the plugin directory!");
		}

		File[] jarFiles = getPluginCandidates();
		if (jarFiles == null) {
			// No plugins have been found!
			return;
		}

		List<String> disabledPlugins = The5zigMod.getConfig().getStringList("disabled_plugins");
		for (File file : jarFiles) {
			if (disabledPlugins.contains(file.getName())) {
				continue;
			}
			try {
				loadPlugin0(file, false);
			} catch (Throwable throwable) {
				The5zigMod.logger.error("Could not load plugin " + file.getName(), throwable);
			}
		}
	}

	public File[] getPluginCandidates() {
		return moduleDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase(Locale.ROOT).endsWith(".jar");
			}
		});
	}

	public void loadAll() {
		LoadEvent event = new LoadEvent();
		for (LoadedPlugin plugin : plugins) {
			try {
				for (Method method : plugin.getLoadMethods()) {
					method.invoke(plugin.getInstance(), event);
				}
			} catch (Exception e) {
				The5zigMod.logger.warn("Could not enable plugin " + plugin.getName() + "!", e);
			}
		}
	}

	public void unloadAll() {
		UnloadEvent event = new UnloadEvent();
		for (LoadedPlugin plugin : plugins) {
			try {
				for (Method method : plugin.getUnloadMethods()) {
					method.invoke(plugin.getInstance(), event);
				}
			} catch (Exception e) {
				The5zigMod.logger.warn("Could not disable plugin " + plugin.getName() + "!", e);
			}
		}
	}

	public void unloadPlugin(String name) {
		for (Iterator<LoadedPlugin> iterator = plugins.iterator(); iterator.hasNext(); ) {
			LoadedPlugin plugin = iterator.next();
			if (plugin.getName().equals(name)) {
				UnloadEvent event = new UnloadEvent();
				try {
					for (Method method : plugin.getUnloadMethods()) {
						method.invoke(plugin.getInstance(), event);
					}
				} catch (Exception e) {
					The5zigMod.logger.warn("Could not unload plugin " + plugin.getName() + "!", e);
				}
				for (Object listener : plugin.getRegisteredListeners()) {
					The5zigMod.getListener().unregisterListener(listener);
				}
				for (Class<? extends ServerInstance> serverInstance : plugin.getRegisteredServerInstances()) {
					The5zigMod.getDataManager().getServerInstanceRegistry().unregisterServerInstance(serverInstance);
				}
				for (Class<? extends AbstractModuleItem> moduleItem : plugin.getRegisteredModuleItems()) {
					The5zigMod.getModuleItemRegistry().unregisterItem(moduleItem);
				}
				iterator.remove();
			}
		}
	}

	public LoadedPlugin loadPlugin(File file) throws Throwable {
		return loadPlugin(file, false);
	}

	private LoadedPlugin loadPlugin(File file, boolean skipUpdateCheck) throws Throwable {
		LoadedPlugin loadedPlugin = loadPlugin0(file, skipUpdateCheck);

		if(loadedPlugin == null) return null;

		LoadEvent event = new LoadEvent();
		for (Method method : loadedPlugin.getLoadMethods()) {
			method.invoke(loadedPlugin.getInstance(), event);
		}
		The5zigMod.getModuleMaster().loadModules();
		return loadedPlugin;
	}

	private LoadedPlugin loadPlugin0(File file, boolean skipUpdateCheck) throws Exception {
		JsonElement modModulesElement = new JsonParser().parse(FileUtils.readFileToString(The5zigMod.getModuleMaster().getFile()));
		JarFile jarFile = new JarFile(file);
		ZipEntry pluginEntry = jarFile.getEntry("plugin.json");
		if (pluginEntry == null) {
			throw new PluginException("plugin.json not found!");
		}
		JsonElement jsonElement = parseJson(jarFile, pluginEntry);
		if (!jsonElement.isJsonObject()) {
			throw new PluginException("Invalid plugin.json in file " + jarFile.getName());
		}
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		if(!skipUpdateCheck && The5zigMod.getConfig().getBool("plugin_update")
			&& jsonObject.has("updateUrl")) {
			The5zigMod.logger.debug("Checking for updates.");
			LoadedPlugin newerPlugin =
					null;
			try {
				newerPlugin = checkOutdated(file, jsonObject.get("updateUrl").getAsString(),
						jsonObject.get("version").getAsString(), jsonObject.get("name").getAsString());
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}

			if (newerPlugin != null)
				return null;
		}

		if (!jsonObject.has("main") || !jsonObject.get("main").isJsonPrimitive()) {
			throw new PluginException("main not found in plugin.json in file " + jarFile.getName());
		}
		String main = jsonObject.get("main").getAsString();
		PluginClassLoader classLoader = new PluginClassLoader(this, this.getClass().getClassLoader(), file);
		Class mainClass = classLoader.loadClass(main);
		if (!mainClass.isAnnotationPresent(Plugin.class)) {
			throw new PluginException("Could not find plugin annotation in " + main + " in plugin " + jarFile.getName());
		}
		Plugin annotation = (Plugin) mainClass.getAnnotation(Plugin.class);
		LoadedPlugin registeredPlugin = getPlugin(annotation.name());
		if (registeredPlugin != null) {
			throw new PluginException(annotation.name() + " already has been loaded!");
		}

		Map<Locale, ResourceBundle> locales = getLocales(jarFile);

		ZipEntry moduleEntry = jarFile.getEntry("modules.json");
		if (moduleEntry != null) {
			JsonElement moduleElement = parseJson(jarFile, moduleEntry);
			addModules(modModulesElement, moduleElement);
		}

		Object instance;
		try {
			instance = mainClass.newInstance();
		} catch (Throwable throwable) {
			throw new PluginException("Could not create instance of main class in " + file.getName());
		}
		List<Method> loadMethods = findMethods(mainClass, LoadEvent.class);
		List<Method> unloadMethods = findMethods(mainClass, UnloadEvent.class);
		LoadedPlugin loadedPlugin = new LoadedPlugin(annotation.name(), annotation.version(), instance, classLoader, locales, loadMethods, unloadMethods, file);

		if(jsonObject.has("icon")) {
			loadedPlugin.setImageUrl(jsonObject.get("icon").getAsString());
		}
		if(jsonObject.has("desc")) {
			loadedPlugin.setShortDescription(jsonObject.get("desc").getAsString());
		}
		if(jsonObject.has("license")) {
			loadedPlugin.setLicense(jsonObject.get("license").getAsString());
		}
		if(jsonObject.has("author")) {
			loadedPlugin.setAuthor(jsonObject.get("author").getAsString());
		}

		plugins.add(loadedPlugin);

		registerListener(instance, instance);

		String json = The5zigMod.prettyGson.toJson(modModulesElement);
		FileWriter writer = new FileWriter(The5zigMod.getModuleMaster().getFile());
		writer.write(json);
		writer.close();
		return loadedPlugin;
	}

	/**
	 * Tries to add all locales of a plugin to the language system of the mod.
	 *
	 * @param jarFile the jar file of the plugin.
	 * @throws IOException
	 */
	private Map<Locale, ResourceBundle> getLocales(JarFile jarFile) throws IOException {
		Map<Locale, ResourceBundle> result = Maps.newHashMap();
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry e = entries.nextElement();
			String folder = "lang/";
			if (e.getName().startsWith(folder) && !e.getName().equals(folder)) {
				String language = e.getName().substring(folder.length());
				language = language.substring(0, language.lastIndexOf("."));
				Locale locale = I18n.deserialize(language);

				InputStream in = jarFile.getInputStream(e);
				try {
					result.put(locale, new PropertyResourceBundle(in));
				} finally {
					IOUtils.closeQuietly(in);
				}
			}
		}
		return result;
	}

	/**
	 * Tries to add all modules specified in the modules.json file of a plugin to the modules.json file of the mod.
	 *
	 * @param modModulesElement    the modules.json object of the mod.
	 * @param pluginModulesElement the modules.json object of the plugin.
	 */
	private void addModules(JsonElement modModulesElement, JsonElement pluginModulesElement) {
		// Check if both elements are objects and that they contain the modules array.
		if (!modModulesElement.isJsonObject() || !modModulesElement.getAsJsonObject().has("modules") ||
				!modModulesElement.getAsJsonObject().get("modules").isJsonArray()) {
			return;
		}
		if (!pluginModulesElement.isJsonObject() || !pluginModulesElement.getAsJsonObject().has("modules") ||
				!pluginModulesElement.getAsJsonObject().get("modules").isJsonArray()) {
			return;
		}

		JsonArray modModulesArray = modModulesElement.getAsJsonObject().getAsJsonArray("modules");
		JsonArray modDefaultModulesArray = modModulesElement.getAsJsonObject().getAsJsonArray("defaultModules");
		JsonArray pluginModulesArray = pluginModulesElement.getAsJsonObject().getAsJsonArray("modules");
		// Iterate through the plugin modules array and check, if there is any mod module with the same id.
		for (JsonElement pluginModuleElement : pluginModulesArray) {
			if (!pluginModuleElement.isJsonObject() || !pluginModuleElement.getAsJsonObject().has("id") ||
					!pluginModuleElement.getAsJsonObject().get("id").isJsonPrimitive()) {
				continue;
			}
			JsonObject pluginModuleObject = pluginModuleElement.getAsJsonObject();
			String pluginModuleId = pluginModuleObject.get("id").getAsString();
			JsonArray pluginModuleItems = pluginModuleObject.has("items") && pluginModuleObject.get("items").isJsonArray() ? pluginModuleObject.getAsJsonArray("items") : new JsonArray();
			if (pluginModuleItems.size() == 0) {
				continue;
			}

			// Iterate through all mod modules and try to find a module with the same id.
			boolean containsModule = false; // indicates, whether a module with the id of the plugin module already exists.
			for (JsonElement jsonElement : modDefaultModulesArray) {
				if (jsonElement.isJsonPrimitive()) {
					String id = jsonElement.getAsString();
					if (pluginModuleId.equals(id)) {
						containsModule = true;
						break;
					}
				}
			}
			if (containsModule) {
				continue;
			}
			for (JsonElement modModuleElement : modModulesArray) {
				// Validate id
				if (!modModuleElement.isJsonObject() || !modModuleElement.getAsJsonObject().has("id") ||
						!modModuleElement.getAsJsonObject().get("id").isJsonPrimitive()) {
					continue;
				}
				JsonObject modModuleObject = modModuleElement.getAsJsonObject();
				String modModuleId = modModuleObject.get("id").getAsString();
				if (pluginModuleId.equals(modModuleId)) {
					// We found a module with an equal id! Don't override module but add additional items to it!
					containsModule = true;
					JsonArray modModuleItems = modModuleObject.has("items") && modModuleObject.get("items").isJsonArray() ? modModuleObject.getAsJsonArray("items") : new JsonArray();

					// Iterate through all plugin module items and check, if it already exists.
					for (JsonElement pluginModuleItemElement : pluginModuleItems) {
						if (!pluginModuleItemElement.isJsonObject() || !pluginModuleItemElement.getAsJsonObject().has("type") || !pluginModuleItemElement.getAsJsonObject().get("type")
								.isJsonPrimitive()) {
							continue;
						}
						String pluginItemType = pluginModuleItemElement.getAsJsonObject().get("type").getAsString();

						// Iterate through all mod module items.
						boolean containsItem = false; // indicates, whether the item already exists or not.
						for (JsonElement modModuleItemElement : modModuleItems) {
							if (!modModuleItemElement.isJsonObject() || !modModuleItemElement.getAsJsonObject().has("type") || !modModuleItemElement.getAsJsonObject().get("type")
									.isJsonPrimitive()) {
								continue;
							}
							String modItemType = modModuleItemElement.getAsJsonObject().get("type").getAsString();

							if (pluginItemType.equals(modItemType)) {
								// The item of the plugin module already exists in the mod module. Continue!
								containsItem = true;
								break;
							}
						}
						if (!containsItem) {
							// The item does not exist in the module.
							// Simply copy & paste it to the items array of the mod module.
							modModuleItems.add(pluginModuleItemElement);
						}
					}
					break;
				}
			}
			if (!containsModule) {
				// The specified module didn't exist before!
				// Simply copy & paste the whole module element to the mod modules array.
				modModulesArray.add(pluginModuleElement);
			}
			modDefaultModulesArray.add(new JsonPrimitive(pluginModuleId));
		}
	}

	private JsonElement parseJson(ZipFile file, ZipEntry entry) throws IOException {
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream(entry);
			return new JsonParser().parse(new InputStreamReader(inputStream));
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private List<Method> findMethods(Class<?> clazz, Class<?> methodParameter) {
		List<Method> result = Lists.newArrayList();
		for (Method method : clazz.getMethods()) {
			if (method.getParameterTypes().length > 0 && methodParameter.isAssignableFrom(method.getParameterTypes()[0])) {
				result.add(method);
			}
		}
		return result;
	}

	public LoadedPlugin getPlugin(String name) {
		for (LoadedPlugin plugin : plugins) {
			if (plugin.getName().equals(name)) {
				return plugin;
			}
		}
		return null;
	}

	public LoadedPlugin getPlugin(Object instance) {
		for (LoadedPlugin plugin : plugins) {
			if (plugin.getInstance() == instance) {
				return plugin;
			}
		}
		return null;
	}

	public List<LoadedPlugin> getPlugins() {
		return plugins;
	}

	Class<?> getClassByName(String name, URLClassLoader ignore) {
		Class<?> clazz = cachedClasses.get(name);
		if (clazz != null) {
			return clazz;
		}
		for (LoadedPlugin plugin : plugins) {
			PluginClassLoader classLoader = plugin.getClassLoader();
			if (classLoader == ignore) {
				continue;
			}
			try {
				clazz = classLoader.findClass(name, false);
			} catch (ClassNotFoundException ignored) {
			}
			if (clazz != null) {
				cachedClasses.put(name, clazz);
				return clazz;
			}
		}
		return null;
	}

	@Override
	public void registerListener(Object plugin, Object listener) {
		LoadedPlugin loadedPlugin = getPlugin(plugin);
		if (loadedPlugin == null) {
			throw new IllegalArgumentException("Plugin has not been registered!");
		}
		The5zigMod.getListener().registerListener(listener);
		loadedPlugin.getRegisteredListeners().add(listener);
	}

	@Override
	public void unregisterListener(Object plugin, Object listener) {
		LoadedPlugin loadedPlugin = getPlugin(plugin);
		if (loadedPlugin == null) {
			throw new IllegalArgumentException("Plugin has not been registered!");
		}
		loadedPlugin.getRegisteredListeners().remove(listener);
		The5zigMod.getListener().unregisterListener(listener);
	}

	@Override
	public void unregisterListener(Object plugin) {
		LoadedPlugin loadedPlugin = getPlugin(plugin);
		if (loadedPlugin == null) {
			throw new IllegalArgumentException("Plugin has not been registered!");
		}
		for (Object listener : loadedPlugin.getRegisteredListeners()) {
			unregisterListener(plugin, listener);
		}
	}

	@Override
	public <T extends Event> T fireEvent(T event) {
		return The5zigMod.getListener().fireEvent(event);
	}

	private LoadedPlugin checkOutdated(File plugin, String updateUrl, String currentVer, String name) throws Throwable {
		if(updateUrl == null) return null;
		String file = Utils.downloadFile(updateUrl, 2000);
		if(file == null) {
			The5zigMod.logger.error("Could not download update info for this plugin.");
			return null;
		}

		JsonObject json = new JsonParser().parse(file).getAsJsonObject();
		ComparableVersion latest = new ComparableVersion(json.get("latest").getAsString());
		ComparableVersion local = new ComparableVersion(currentVer);

		if(latest.compareTo(local) > 0) {
			The5zigMod.logger.info("Found update. Downloading.");
			long start = System.currentTimeMillis();

			URL url = new URL(json.get("download").getAsString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", "5zig/" + Version.VERSION);

			FileUtils.copyInputStreamToFile(connection.getInputStream(), plugin);
			connection.disconnect();

			The5zigMod.logger.info("Download complete, took {} ms.", System.currentTimeMillis() - start);

			The5zigMod.getOverlayMessage().displayMessage("Plugin " + name + " updated to version " + latest.getCanonical());

			return loadPlugin(plugin, true);
		}

		return null;
	}

}
