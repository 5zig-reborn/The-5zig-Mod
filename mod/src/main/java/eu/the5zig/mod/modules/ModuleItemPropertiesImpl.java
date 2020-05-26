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

package eu.the5zig.mod.modules;

import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.IConfigItem;
import eu.the5zig.mod.config.items.*;
import eu.the5zig.mod.config.items.StringItem;
import eu.the5zig.mod.render.BracketsFormatting;
import eu.the5zig.util.Callable;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class ModuleItemPropertiesImpl implements ModuleItemProperties {

	private final AbstractModuleItem item;
	private final String itemCategory;

	private Map<String, ConfigItem> settings = Maps.newHashMap();
	private ModuleItemFormatting formatting;
	private boolean showPrefix = true;
	private String customLabel;

	public ModuleItemPropertiesImpl(AbstractModuleItem item) {
		this.item = item;
		this.itemCategory = The5zigMod.getModuleItemRegistry().byItem(item.getClass()).getKey().toLowerCase(Locale.ROOT);
	}

	@Override
	public void addSetting(String key, int defaultValue, final int maxValue) {
		addSetting(new IntItem(key, itemCategory, defaultValue) {
			@Override
			public void next() {
				set((get() + 1) % maxValue);
			}
		});
	}

	@Override
	public void addSetting(String key, int defaultValue, final int maxValue, final Callable<String> customValue) {
		addSetting(new IntItem(key, itemCategory, defaultValue) {
			@Override
			public String translateValue() {
				return customValue.call();
			}

			@Override
			public void next() {
				set((get() + 1) % maxValue);
			}
		});
	}

	@Override
	public void addSetting(String key, float defaultValue, final float maxValue) {
		addSetting(new FloatItem(key, itemCategory, defaultValue) {
			@Override
			public void next() {
				float next = get() + 1;
				if (next >= maxValue)
					next = 0;
				set(next);
			}
		});
	}

	@Override
	public void addSetting(String key, String suffix, float defaultValue, float minValue, float maxValue, int steps) {
		addSetting(new SliderItem(key, suffix, itemCategory, defaultValue, minValue, maxValue, steps));
	}

	@Override
	public void addSetting(String key, boolean defaultValue) {
		addSetting(new BoolItem(key, itemCategory, defaultValue));
	}

	@Override
	public <E extends Enum> void addSetting(String key, E defaultValue, Class<E> enumClass) {
		addSetting(new EnumItem<E>(key, itemCategory, defaultValue, enumClass));
	}

	@Override
	public void addSetting(String key, String defaultValue) {
		addSetting(new StringItem(key, itemCategory, defaultValue));
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void addSetting(ConfigItem item) {
		item.setTranslationPrefix("modules.item");
		settings.put(item.getKey(), item);
	}

	@Override
	public IConfigItem getSetting(String key) {
		return settings.get(key);
	}

	public Collection<ConfigItem> getSettings() {
		return settings.values();
	}

	@Override
	public ModuleItemFormatting getFormatting() {
		return formatting;
	}

	@Override
	public void setFormatting(ModuleItemFormatting formatting) {
		this.formatting = formatting;
	}

	@Override
	public boolean isShowPrefix() {
		return showPrefix;
	}

	@Override
	public void setShowPrefix(boolean showPrefix) {
		this.showPrefix = showPrefix;
	}

	@Override
	public String getDisplayName() {
		if(customLabel != null) return customLabel;
		return item.getTranslation() != null && !item.getTranslation().isEmpty() ? I18n.translate(item.getTranslation()) : item.getName();
	}

	@Override
	public String buildPrefix() {
		return buildPrefix(getDisplayName());
	}

	@Override
	public String buildPrefix(String prefixText) {
		ChatColor mainFormatting = formatting != null && formatting.getMainFormatting() != null ? formatting.getMainFormatting() : The5zigMod.getConfig().get("formattingMain", ColorFormattingItem.class).get();
		if (!showPrefix) {
			return (mainFormatting == ChatColor.RESET ? "" : mainFormatting.toString());
		}
		ChatColor bracketsColor = The5zigMod.getConfig().get("colorBrackets", SelectColorItem.class).get();
		BracketsFormatting bracketsFormatting = (BracketsFormatting) The5zigMod.getConfig().get("formattingBrackets", EnumItem.class).get();
		ChatColor prefixFormatting = formatting != null && formatting.getPrefixFormatting() != null ? formatting.getPrefixFormatting() : The5zigMod.getConfig().get("formattingPrefix", ColorFormattingItem.class)
				.get();
		return bracketsColor.toString() + bracketsFormatting.getFirst() + ChatColor.RESET.toString() + (prefixFormatting == ChatColor.RESET ? "" : prefixFormatting.toString()) + prefixText +
				bracketsColor.toString() + bracketsFormatting.getLast() + " " + (mainFormatting == ChatColor.RESET ? "" : mainFormatting.toString());
	}

	@Override
	public String shorten(double d) {
		return Utils.getShortenedDouble(d, The5zigMod.getConfig().getInt("numberPrecision"));
	}

	@Override
	public String shorten(float f) {
		return Utils.getShortenedFloat(f, The5zigMod.getConfig().getInt("numberPrecision"));
	}

	@Override
	public void setCustomLabel(String customLabel) {
		this.customLabel = customLabel;
	}

	public String getCustomLabel() {
		return customLabel;
	}
}
