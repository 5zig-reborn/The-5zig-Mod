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

package eu.the5zig.mod.gui.ingame;

public class PotionEffectImpl implements PotionEffect {

	private final String name;
	private final int time;
	private final String timeString;
	private final int amplifier;
	private final int iconIndex;
	private final boolean good;
	private final boolean hasParticles;
	private final int liquidColor;

	public PotionEffectImpl(String name, int time, String timeString, int amplifier, int iconIndex, boolean good, boolean hasParticles, int liquidColor) {
		this.name = name;
		this.time = time;
		this.timeString = timeString;
		this.amplifier = amplifier;
		this.iconIndex = iconIndex;
		this.good = good;
		this.hasParticles = hasParticles;
		this.liquidColor = liquidColor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getTime() {
		return time;
	}

	@Override
	public String getTimeString() {
		return timeString;
	}

	@Override
	public int getAmplifier() {
		return amplifier;
	}

	@Override
	public int getIconIndex() {
		return iconIndex;
	}

	@Override
	public boolean isGood() {
		return good;
	}

	@Override
	public boolean hasParticles() {
		return hasParticles;
	}

	@Override
	public int getLiquidColor() {
		return liquidColor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PotionEffectImpl that = (PotionEffectImpl) o;

		return name != null ? name.equals(that.name) : that.name == null;

	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public int compareTo(PotionEffect o) {
		int compare = Boolean.valueOf(o.isGood()).compareTo(isGood());
		return compare == 0 ? Integer.valueOf(getTime()).compareTo(o.getTime()) : compare;
	}
}
