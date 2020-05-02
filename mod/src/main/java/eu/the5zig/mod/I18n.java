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

package eu.the5zig.mod;

import com.google.common.collect.Lists;
import eu.the5zig.mod.config.items.StringItem;
import eu.the5zig.mod.plugin.LoadedPlugin;

import java.util.*;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class I18n {

	private static final String PRE = "lang/" + Version.LANGVERSION + "/", MID = "language", END = ".properties";
	private static final Locale[] defaultLocales = new Locale[]{
			Locale.US, Locale.GERMANY,
			Locale.ITALY, new Locale("es", "ES"),
			new Locale("nl", "NL"),
			Locale.CHINA, new Locale("pt", "BR"),
			Locale.JAPAN, new Locale("da", "DK"),
			Locale.FRANCE, new Locale("no", "NO"),
			new Locale("pl", "PL"),
			new Locale("ru", "RU"),
			new Locale("sv", "SE"),
			new Locale("tr", "TR"),
			new Locale("cs", "CZ")
	};

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

		languages.sort(Comparator.comparing(Locale::toString));

		loadLocales();

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
		resourceBundle = ResourceBundle.getBundle(PRE + MID, currentLanguage);
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

	public static boolean loadLocales() {
		languages.addAll(Arrays.asList(defaultLocales));
		return true;
	}
}
