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

package eu.the5zig.mod.config.items;

import com.google.gson.JsonObject;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.config.IConfigItem;
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

public abstract class ConfigItem<T> implements IConfigItem<T> {

	private final String key;
	private final String category;

	private T value;
	private final T defaultValue;

	private boolean restricted = false;

	protected boolean changed = false;

	private String translationPrefix = "config";

	/**
	 * Creates a Config Item.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 */
	public ConfigItem(String key, String category, T defaultValue) {
		this.key = key;
		this.category = category;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
	}

	public abstract void deserialize(JsonObject object);

	public abstract void serialize(JsonObject object);

	public abstract void next();

	/**
	 * Used for Displaying Screens etc.
	 */
	public void action() {
	}

	@Override
	public T get() {
		return value;
	}

	public void set(T value) {
		this.changed |= !((this.value == null && value == null) || (this.value != null && this.value.equals(value)));
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public void reset() {
		set(defaultValue);
	}

	public String getCategory() {
		return category;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean hasChanged() {
		return changed;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public boolean isDefault() {
		return defaultValue.equals(get());
	}

	public String getTranslationPrefix() {
		return translationPrefix;
	}

	public void setTranslationPrefix(String translationPrefix) {
		this.translationPrefix = translationPrefix;
	}

	public String translateValue() {
		return String.valueOf(get());
	}

	public String translate() {
		return I18n.translate(translationPrefix + "." + category + "." + Utils.upperToDash(key)) + ": " + translateValue();
	}

	public String translateDescription() {
		return I18n.translate(translationPrefix + "." + category + "." + Utils.upperToDash(key) + ".desc");
	}

	public final String translateDefaultValue() {
		T current = get();
		if (defaultValue == null) {
			return null;
		}
		boolean changed = hasChanged();
		set(defaultValue);
		String translated;
		if (this instanceof SliderItem && ((SliderItem) this).getCustomValue((Float) get()) != null) {
			translated = ((SliderItem) this).getCustomValue((Float) get() - ((SliderItem) this).getMinValue());
		} else {
			translated = translateValue();
			if (this instanceof SliderItem) {
				String suffix = ((SliderItem) this).getSuffix();
				if (suffix != null) {
					translated += suffix;
				}
			}
		}
		set(current);
		this.changed = changed;
		return translated;
	}

	public String getHoverText() {
		String text = !I18n.has(translationPrefix + "." + category + "." + Utils.upperToDash(key) + ".desc") ? "" : translateDescription();
		if (!(this instanceof NonConfigItem)) {
			String defaultValue = translateDefaultValue();
			if (defaultValue != null) {
				text += "\n\n" + ChatColor.DARK_GRAY + I18n.translate("config.default") + ": " + defaultValue;
			}
		}
		return text;
	}

	@Override
	public String toString() {
		return "ConfigItem{" +
				"key='" + key + '\'' +
				", value=" + value +
				", defaultValue=" + defaultValue +
				", category='" + category + '\'' +
				", changed=" + changed +
				'}';
	}
}