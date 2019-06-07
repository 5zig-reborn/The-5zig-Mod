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

package eu.the5zig.mod;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.the5zig.mod.config.items.StringItem;
import eu.the5zig.mod.plugin.LoadedPlugin;
import eu.the5zig.mod.util.IOUtil;
import eu.the5zig.util.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class I18n {

	private static final String PRE = "lang/" + Version.LANGVERSION + "/", MID = "language", END = ".properties";
	private static final Locale[] defaultLocales = new Locale[]{Locale.US, Locale.GERMANY, new Locale("es", "ES"), new Locale("nl", "NL"), Locale.CHINA, new Locale("pt", "BR")};

	private static List<Locale> languages = Lists.newArrayList();
	private static Locale currentLanguage;
	private static ResourceBundle resourceBundle;
	private static ResourceBundle defaultBundle;

	static {
		The5zigMod.logger.info("Loading Language files...");

		try {
			defaultBundle = ResourceBundle.getBundle(PRE + MID, Locale.US);
		} catch (Exception e) {
			The5zigMod.logger.error("Could not load fallback resource bundle!", e);
		}

		try {
			extractLocales();
			loadLocales();
		} catch (Exception e) {
			The5zigMod.logger.error("Could not load extracted resource bundles!", e);
		}

		Collections.sort(languages, new Comparator<Locale>() {
			@Override
			public int compare(Locale l1, Locale l2) {
				return l1.toString().compareTo(l2.toString());
			}
		});

		currentLanguage = get(The5zigMod.getConfig().getString("language"));
		if (currentLanguage == null) {
			setLanguage(Locale.US);
		} else {
			try {
				loadPropertyBundle();
			} catch (Exception e) {
				The5zigMod.logger.error("Could not load property bundle " + currentLanguage + "!", e);
			}
		}
		The5zigMod.logger.info("Loaded {} languages! Using Language {}!", languages.size(), currentLanguage);

		checkUpdates();
	}

	private static Locale get(String code) {
		for (Locale language : languages) {
			if (language.toString().equals(code))
				return language;
		}
		return null;
	}

	private static void loadResourceBundle() {
		The5zigMod.logger.debug("Reloading Resource Bundle {}...", currentLanguage);
		resourceBundle = ResourceBundle.getBundle(PRE + MID, currentLanguage);
	}

	private static void loadPropertyBundle() {
		The5zigMod.logger.debug("Reloading Property Bundle {}...", currentLanguage);
		resourceBundle = getBundle(currentLanguage);
	}

	private static PropertyResourceBundle getBundle(Locale locale) {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(The5zigMod.getModDirectory(), serialize(locale)));
			return new PropertyResourceBundle(stream);
		} catch (IOException e) {
			The5zigMod.logger.error("Could not Load Property Resource Bundle!", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return null;
	}

	public static void setLanguage(Locale locale) {
		String code = locale.toString();
		setLanguage(code);
	}

	public static void setLanguage(String code) {
		if (get(code) == null) {
			currentLanguage = Locale.US;
			loadResourceBundle();
			return;
		}
		currentLanguage = get(code);
		loadPropertyBundle();
		The5zigMod.getConfig().get("language", StringItem.class).set(code);
		The5zigMod.getConfig().save();
	}

	public static Locale getCurrentLanguage() {
		return currentLanguage;
	}

	/**
	 * Returns an unmodifiable List containing all loaded Locales.
	 *
	 * @return All loaded Locales.
	 */
	public static List<Locale> getLanguages() {
		return Collections.unmodifiableList(languages);
	}

	/**
	 * Translates a key with current Resource Bundle.
	 *
	 * @param key The key of the Resource.
	 * @return The translated value of the key.
	 */
	public static String translate(String key) {
		return translate(key, false);
	}

	/**
	 * Translates a key with current Resource Bundle.
	 *
	 * @param key The key of the Resource.
	 * @param useDefaultBundle true, if the default resource bundle should be used for the translation.
	 * @return The translated value of the key.
	 */
	public static String translate(String key, boolean useDefaultBundle) {
		if (useDefaultBundle && defaultBundle != null && defaultBundle.containsKey(key)) {
			return defaultBundle.getString(key);
		}
		if (resourceBundle != null && resourceBundle.containsKey(key)) {
			return resourceBundle.getString(key);
		}
		if (defaultBundle != null && defaultBundle.containsKey(key)) {
			return defaultBundle.getString(key);
		}

		if (The5zigMod.getAPI() != null && The5zigMod.getAPI().getPluginManager() != null) {
			for (LoadedPlugin loadedPlugin : The5zigMod.getAPI().getPluginManager().getPlugins()) {
				if (loadedPlugin.getLocales().containsKey(currentLanguage) && loadedPlugin.getLocales().get(currentLanguage).containsKey(key)) {
					return loadedPlugin.getLocales().get(currentLanguage).getString(key);
				}
				if (loadedPlugin.getLocales().containsKey(Locale.US) && loadedPlugin.getLocales().get(Locale.US).containsKey(key)) {
					return loadedPlugin.getLocales().get(Locale.US).getString(key);
				}
			}
		}

		return key;
	}

	/**
	 * Translates a key with current Resource Bundle and formats the String.
	 *
	 * @param key    The key of the Resource.
	 * @param format The objects to format the String
	 * @return The formatted, translated value of the key.
	 */
	public static String translate(String key, Object... format) {
		return translate(key, false, format);
	}

	/**
	 * Translates a key with current Resource Bundle and formats the String.
	 *
	 * @param key              The key of the Resource.
	 * @param useDefaultBundle true, if the default resource bundle should be used for the translation.
	 * @param format           The objects to format the String
	 * @return The formatted, translated value of the key.
	 */
	public static String translate(String key, boolean useDefaultBundle, Object... format) {
		try {
			return String.format(translate(key, useDefaultBundle), format);
		} catch (IllegalFormatException ignored) {
			return key;
		}
	}

	public static boolean has(String key) {
		if (defaultBundle != null && defaultBundle.containsKey(key)) {
			return true;
		}
		if (resourceBundle != null && resourceBundle.containsKey(key)) {
			return true;
		}

		if (The5zigMod.getAPI() != null && The5zigMod.getAPI().getPluginManager() != null) {
			for (LoadedPlugin loadedPlugin : The5zigMod.getAPI().getPluginManager().getPlugins()) {
				if (loadedPlugin.getLocales().containsKey(currentLanguage) && loadedPlugin.getLocales().get(currentLanguage).containsKey(key)) {
					return true;
				}
				if (loadedPlugin.getLocales().containsKey(Locale.US) && loadedPlugin.getLocales().get(Locale.US).containsKey(key)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Locale deserialize(String file) {
		String[] args = file.split("_");
		if (args.length != 3)
			return null;
		if (!args[0].equals(MID))
			return null;
		return new Locale(args[1].toLowerCase(Locale.ROOT), args[2].toUpperCase());
	}

	public static String serialize(Locale locale) {
		return PRE + MID + "_" + locale.toString() + END;
	}

	private static void extract(Locale locale) {
		String file = serialize(locale);
		File destination = new File(The5zigMod.getModDirectory(), file);
		try {
			if (destination.exists())
				return;
			The5zigMod.logger.debug("Extracting {} to {}", file, destination);
			org.apache.commons.io.FileUtils.copyInputStreamToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream(file), destination);
		} catch (Exception e) {
			The5zigMod.logger.error("Could not extract File " + file + "!", e);
		}
	}

	private static void extractLocales() {
		try {
			for (Locale locale : defaultLocales) {
				extract(locale);
			}
		} catch (Throwable e) {
			The5zigMod.logger.warn("Could not extract Language Files! Using default Language " + Locale.US + "!", e);
			extract(Locale.US);
		}
	}

	public static boolean loadLocales() {
		File languageDir = new File(The5zigMod.getModDirectory(), PRE);
		File[] languages = languageDir.listFiles();
		if (languages == null)
			return !I18n.languages.isEmpty();

		boolean changed = false;
		List<Locale> folderLocales = Lists.newArrayList();
		for (File file : languages) {
			if (file.isDirectory())
				continue;
			String name = file.getName();
			if (name.length() <= END.length())
				continue;
			name = name.substring(0, name.length() - END.length());
			Locale locale = deserialize(name);
			if (locale == null)
				continue;
			folderLocales.add(locale);
			if (!I18n.languages.contains(locale)) {
				changed = true;
				I18n.languages.add(locale);
				if (locale.equals(currentLanguage))
					loadPropertyBundle();
			}
		}
		List<Locale> currentLanguages = Lists.newArrayList(I18n.languages);
		currentLanguages.removeAll(folderLocales);
		if (!currentLanguages.isEmpty()) {
			I18n.languages.removeAll(currentLanguages);
			changed = true;
		}

		if (currentLanguage != null && !I18n.languages.contains(currentLanguage)) {
			currentLanguage = I18n.languages.get(0);
			loadPropertyBundle();
		}
		return changed;
	}

	private static void checkUpdates() {
		Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Update language thread").build()).execute(new Runnable() {
			@Override
			public void run() {
				String json;
				try {
					json = IOUtil.download("http://5zig.net/api/lang?version=" + Version.LANGVERSION, false);
				} catch (IOException e) {
					throw new RuntimeException("Could not fetch Language File updates!", e);
				}
				Gson gson = new Gson();
				JsonParser jsonParser = new JsonParser();
				JsonArray jsonElement = (JsonArray) jsonParser.parse(json);
				List<LanguageJSON> allLanguages = Lists.newArrayList();
				for (JsonElement p : jsonElement) {
					LanguageJSON server = gson.fromJson(p, LanguageJSON.class);
					allLanguages.add(server);
				}

				File languageDir = new File(The5zigMod.getModDirectory(), "lang/" + Version.LANGVERSION);
				File[] languages = languageDir.listFiles();
				if (languages == null) {
					The5zigMod.logger.warn("Language Directory is empty! Did something went wrong with extracting all Resource Bundles?");
					return;
				}

				boolean updated = false;
				for (LanguageJSON languageJSON : allLanguages) {
					File lang = null;
					for (File language : languages) {
						if (!language.getName().equals(languageJSON.name))
							continue;
						lang = language;
						break;
					}
					if (lang != null) {
						String md5 = null;
						try {
							md5 = FileUtils.md5(lang);
						} catch (Exception e) {
							The5zigMod.logger.error("Could not calculate md5 of " + lang, e);
						}
						The5zigMod.logger.debug("MD5 of Language File {} is {}", lang, md5);
						if (!languageJSON.md5.equals(md5)) {
							downloadUpdate(languageJSON.name);
							updated = true;
						}
					} else {
						updated = true;
						downloadUpdate(languageJSON.name);
					}
				}
				if (!updated) {
					The5zigMod.logger.info("All Language Files are up to date!");
				}
				loadLocales();
			}
		});
	}

	private static void downloadUpdate(String name) {
		try {
			String path = "http://5zig.net/api/lang?version=" + Version.LANGVERSION + "&name=" + name;
			File dest = new File(The5zigMod.getModDirectory(), "lang/" + Version.LANGVERSION + "/" + name);
			The5zigMod.logger.info("Found an Update for Language File {}. Downloading from {} to {}", name, path, dest);
			FileUtils.downloadToFile(path, dest);
			if (currentLanguage.equals(deserialize(name.substring(0, name.length() - END.length()))))
				loadPropertyBundle();
		} catch (IOException e) {
			The5zigMod.logger.error("Could not download File " + name + "!", e);
		}
	}

	private static class LanguageJSON {

		public String name;
		public String md5;

		@Override
		public String toString() {
			return "LanguageJSON{" + "name='" + name + '\'' + ", md5='" + md5 + '\'' + '}';
		}
	}

}
