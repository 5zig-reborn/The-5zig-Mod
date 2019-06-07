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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.util.Utils;

public class SliderItem extends FloatItem {

	private String suffix;
	private final float minValue;
	private final float maxValue;
	private final int steps;

	/**
	 * Creates a Config Item that works as a Slider.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param suffix       The Suffix of the Item that should be displayed.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 * @param minValue     The lowest Value that is possible.
	 * @param maxValue     The highest Value that is possible.
	 * @param steps        The amount of steps the Slider can have (or {@code -1} if the Slider shouldn't be able to get locked).
	 */
	public SliderItem(String key, String suffix, String category, float defaultValue, float minValue, float maxValue, int steps) {
		super(key, category, defaultValue);
		this.suffix = suffix;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.steps = steps;
	}

	public String translate() {
		return I18n.translate(getTranslationPrefix() + "." + getCategory() + "." + Utils.upperToDash(getKey()));
	}

	public float getMinValue() {
		return minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

	public int getSteps() {
		return steps;
	}

	public String getCustomValue(float value) {
		return null;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void next() {
	}

}
