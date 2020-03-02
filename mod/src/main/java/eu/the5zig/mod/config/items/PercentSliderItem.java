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

package eu.the5zig.mod.config.items;

import eu.the5zig.mod.gui.GuiSettings;

public class PercentSliderItem extends SliderItem {

	/**
	 * Creates a Config Item that works as a Slider.
	 *
	 * @param key          Der Key of the Item. Used in config File and to translate the Item.
	 * @param category     The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param defaultValue The Default Value of the Item.
	 * @param minValue     The lowest Value that is possible.
	 * @param maxValue     The highest Value that is possible.
	 * @param steps        The amount of steps the Slider can have (or {@code -1} if the Slider shouldn't be able to get locked).
	 */
	public PercentSliderItem(String key, String category, Float defaultValue, Float minValue, Float maxValue, int steps) {
		super(key, "%", category, defaultValue, minValue, maxValue, steps);
	}

	@Override
	public String getCustomValue(float value) {
		return Math.round((value * (getMaxValue() - getMinValue()) + getMinValue()) * 100) + getSuffix();
	}
}
