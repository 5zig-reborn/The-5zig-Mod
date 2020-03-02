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

package eu.the5zig.mod.modules.items;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.IConfigItem;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;

public abstract class ItemStackItem extends AbstractModuleItem {

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		ItemStack itemStack = getStack(dummy);
		if (itemStack == null) {
			return;
		}
		String string = getString(itemStack);
		if (renderLocation == RenderLocation.LEFT || renderLocation == RenderLocation.CENTERED) {
			if (durabilityStyle() != DurabilityStyle.DISABLED) {
				The5zigMod.getVars().drawString(string, x + 18, y + 5);
			}
			itemStack.render(x, y, renderWithGenericAttributes());
		} else {
			if (durabilityStyle() != DurabilityStyle.DISABLED) {
				The5zigMod.getVars().drawString(string, x, y + 5);
				int x2 = x + The5zigMod.getVars().getStringWidth(string) + 2;
				itemStack.render(x2, y, renderWithGenericAttributes());
			} else {
				itemStack.render(x, y, renderWithGenericAttributes());
			}
		}
	}

	private String getString(ItemStack itemStack) {
		if (itemStack.getMaxDurability() == 0 || itemStack.getCurrentDurability() < 0) {
			return "";
		}
		float percentage = (float) (itemStack.getMaxDurability() - itemStack.getCurrentDurability()) / (float) itemStack.getMaxDurability();
		String color = getColorByDurability(percentage);
		switch (durabilityStyle()) {
			case RELATIVE:
				return color + shorten(percentage * 100f) + "%";
			case ABSOLUTE:
				return color + (itemStack.getMaxDurability() - itemStack.getCurrentDurability());
			case TOTAL:
				return color + (itemStack.getMaxDurability() - itemStack.getCurrentDurability()) + "/" + itemStack.getMaxDurability();
			default:
				return "";
		}
	}

	private String getColorByDurability(float value) {
		if (!The5zigMod.getConfig().getBool("coloredEquipmentDurability")) {
			return The5zigMod.getRenderer().getMain();
		}
		if (value >= 0.95f) {
			return ChatColor.DARK_GREEN.toString();
		}
		if (value >= 0.8f) {
			return ChatColor.GREEN.toString();
		}
		if (value >= 0.5f) {
			return ChatColor.YELLOW.toString();
		}
		if (value >= 0.1f) {
			return ChatColor.RED.toString();
		}
		return ChatColor.DARK_RED.toString();
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return getStack(dummy) != null && (The5zigMod.getDataManager().getServer() == null || The5zigMod.getDataManager().getServer().isRenderArmor());
	}

	@Override
	public int getWidth(boolean dummy) {
		ItemStack stack = getStack(dummy);
		if (stack == null) {
			return 0;
		}
		return durabilityStyle() != DurabilityStyle.DISABLED ? (The5zigMod.getVars().getStringWidth(getString(stack)) + 18) : 16;
	}

	@Override
	public int getHeight(boolean dummy) {
		return 14;
	}

	private boolean renderWithGenericAttributes() {
		IConfigItem item = getProperties().getSetting("attributes");
		return item != null && (Boolean) item.get();
	}

	private DurabilityStyle durabilityStyle() {
		IConfigItem item = getProperties().getSetting("durability");
		return item == null ? DurabilityStyle.DISABLED : (DurabilityStyle) item.get();
	}

	protected abstract ItemStack getStack(boolean dummy);

	protected enum DurabilityStyle {
		DISABLED, RELATIVE, ABSOLUTE, TOTAL
	}

}
