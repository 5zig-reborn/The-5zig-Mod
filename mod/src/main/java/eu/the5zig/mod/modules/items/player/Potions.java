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

package eu.the5zig.mod.modules.items.player;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.PotionEffect;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

public class Potions extends AbstractModuleItem {

	@Override
	public void registerSettings() {
		getProperties().addSetting("coloredPotionDurability", false);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		List<? extends PotionEffect> potionEffects = dummy ? The5zigMod.getVars().getDummyPotionEffects() : The5zigMod.getVars().getActivePotionEffects();
		GLUtil.enableBlend(); 
		for (PotionEffect potionEffect : potionEffects) {
			if (potionEffect.getIconIndex() != -1) {
				String display = toString(potionEffect);
				GLUtil.color(1.0F, 1.0F, 1.0F, 1.0F);
				The5zigMod.getVars().bindTexture(The5zigMod.INVENTORY_BACKGROUND);
				float scale = .7f;
				GLUtil.pushMatrix();
				GLUtil.scale(scale, scale, scale);
				GLUtil.translate(x / scale, (y - 3) / scale, 0);
				The5zigMod.getVars().renderPotionIcon(potionEffect.getIconIndex());
				GLUtil.popMatrix();

				The5zigMod.getVars().drawString(display, x + 16, y);
			} else {
				The5zigMod.getVars().drawString(toString(potionEffect), x, y);
			}
			y += 12;
		}
	}

	@Override
	public int getWidth(boolean dummy) {
		int maxWidth = 0;
		for (PotionEffect potionEffect : dummy ? The5zigMod.getVars().getDummyPotionEffects() : The5zigMod.getVars().getActivePotionEffects()) {
			int width = The5zigMod.getVars().getStringWidth(toString(potionEffect)) + 10;
			if (potionEffect.getIconIndex() != -1) {
				width += 10;
			}
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight(boolean dummy) {
		return dummy ? 24 : The5zigMod.getVars().getActivePotionEffects().size() * 12;
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return dummy || !The5zigMod.getVars().getActivePotionEffects().isEmpty() &&
				(The5zigMod.getDataManager().getServer() == null || The5zigMod.getDataManager().getServer().isRenderPotionEffects());
	}

	protected String toString(PotionEffect potionEffect) {
		return getColorByDurability(potionEffect.getTime()) + The5zigMod.getVars().translate(potionEffect.getName()) + " " + potionEffect.getAmplifier() + " - " +
				potionEffect.getTimeString();
	}

	private String getColorByDurability(int time) {
		if (!(Boolean) getProperties().getSetting("coloredPotionDurability").get()) {
			return The5zigMod.getRenderer().getMain();
		}
		if (time >= 20 * 60) {
			return ChatColor.DARK_GREEN.toString();
		}
		if (time >= 20 * 30) {
			return ChatColor.GREEN.toString();
		}
		if (time >= 20 * 10) {
			return ChatColor.YELLOW.toString();
		}
		if (time >= 20 * 5) {
			return ChatColor.RED.toString();
		}
		return ChatColor.DARK_RED.toString();
	}
}
