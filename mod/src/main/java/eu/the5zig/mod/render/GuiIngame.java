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

package eu.the5zig.mod.render;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.util.GLUtil;
import org.lwjgl.opengl.GL11;

public class GuiIngame {

	private int hoverTextTime;
	private String hoverText;

	public GuiIngame() {
	}

	/**
	 * Render game overlay
	 */
	public void renderGameOverlay() {
		// render mod
		GLUtil.disableBlend();

		The5zigMod.getVars().updateScaledResolution();
		if (The5zigMod.getConfig().getBool("showMod") && The5zigMod.getModuleMaster().isItemActive("FPS")) {
			if (The5zigMod.getModuleMaster().isItemActive("FPS")) {
				The5zigMod.getDataManager().getFpsCalculator().render();
			}
		}
		if (!The5zigMod.getVars().showDebugScreen()) {
			The5zigMod.getRenderer().drawScreen();
		}
//		The5zigMod.getDataManager().getCrossHairDistanceListener().render();
		renderTextAboveHotbar();
		GLUtil.enableBlend();
	}

	public void onRenderHotbar() {
		if (The5zigMod.getConfig().getBool("showHotbarNumbers") && The5zigMod.getVars().isSpectatingSelf() && !The5zigMod.getVars().isPlayerSpectating()) {
			renderHotbarNumbers();
		}
	}

	public void onRenderFood() { 
		if (The5zigMod.getVars().isRidingEntity()) {
			return;
		}
		The5zigMod.getVars().bindTexture(The5zigMod.MINECRAFT_ICONS);

		boolean renderSaturation = The5zigMod.getConfig().getBool("showSaturation") && The5zigMod.getVars().isSpectatingSelf() && The5zigMod.getVars().shouldDrawHUD() &&
				(The5zigMod.getDataManager().getServer() == null || The5zigMod.getDataManager().getServer().isRenderSaturation());
		if (renderSaturation) {
			renderSaturation();
		}

		int foodLevel = The5zigMod.getVars().getFoodLevel();
		if (The5zigMod.getConfig().getBool("showFoodHealAmount") && foodLevel < 20) {
			GLUtil.enableBlend();
			GLUtil.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_FALSE, GL11.GL_TRUE);
			GLUtil.color(1, 1, 1, 0.2f);
			ItemStack stack;
			if ((stack = The5zigMod.getVars().getItemInMainHand()) != null && stack.getHealAmount() != 0) {
				renderFood(0, foodLevel, stack.getHealAmount());
				if (renderSaturation) {
					renderFood(-10, 0, (int) Math
							.min(The5zigMod.getVars().getSaturation() + (float) stack.getHealAmount() * stack.getSaturationModifier() * 2f, Math.min(foodLevel + stack.getHealAmount(), 20)));
				}
			} else if ((stack = The5zigMod.getVars().getItemInOffHand()) != null && stack.getHealAmount() != 0) {
				renderFood(0, foodLevel, stack.getHealAmount());
				if (renderSaturation) {
					renderFood(-10, 0, (int) Math
							.min(The5zigMod.getVars().getSaturation() + (float) stack.getHealAmount() * stack.getSaturationModifier() * 2f, Math.min(foodLevel + stack.getHealAmount(), 20)));
				}
			}
			GLUtil.disableBlend();
		}
	}

	/**
	 * Tick
	 */
	public void tick() {
		if (this.hoverTextTime > 0) {
			hoverTextTime--;
		}
	}

	private void renderFood(int yOffset, int foodLevel, int additionalLevel) {
		int y = The5zigMod.getVars().getScaledHeight() - 39 + yOffset;
		for (int i = 0; i < 10; i++) {
			int index = 16;
			int x = The5zigMod.getVars().getScaledWidth() / 2 + 91 - i * 8 - 9;

			if (i * 2 + 1 >= foodLevel && i * 2 + 1 < foodLevel + additionalLevel) {
				The5zigMod.getVars().drawIngameTexturedModalRect(x, y, index + 36, 27, 9, 9);
			}

			if (i * 2 + 1 > foodLevel && i * 2 + 1 == foodLevel + additionalLevel) {
				The5zigMod.getVars().drawIngameTexturedModalRect(x, y, index + 45, 27, 9, 9);
			}
		}
	}

	private void renderSaturation() {
		int y = The5zigMod.getVars().getScaledHeight() - 39 - 10;
		if (The5zigMod.getVars().isPlayerInsideWater()) {
			return;
		}
		for (int i = 0; i < 10; i++) {
			int index = 16;
			int offset = 0;
			int x = The5zigMod.getVars().getScaledWidth() / 2 + 91 - i * 8 - 9;

			if (The5zigMod.getVars().isHungerPotionActive()) {
				index += 36;
				offset = 13;
			}

			The5zigMod.getVars().drawIngameTexturedModalRect(x, y, 16 + offset * 9, 27, 9, 9);

			if (i * 2 + 1 < The5zigMod.getVars().getSaturation()) {
				The5zigMod.getVars().drawIngameTexturedModalRect(x, y, index + 36, 27, 9, 9);
			}

			if (i * 2 + 1 == The5zigMod.getVars().getSaturation()) {
				The5zigMod.getVars().drawIngameTexturedModalRect(x, y, index + 45, 27, 9, 9);
			}
		}
	}

	private void renderHotbarNumbers() {
		int x = The5zigMod.getVars().getScaledWidth() / 2 - 87;
		int y = The5zigMod.getVars().getScaledHeight() - 18;
		String[] hotbarKeys = The5zigMod.getVars().getHotbarKeys();
		for (int i = 0; i < 9; i++) {
			The5zigMod.getVars().drawString(hotbarKeys[i], x + i * 20, y, 0x999999);
		}
	}

	private void renderTextAboveHotbar() {
		int scaledWidth = The5zigMod.getVars().getScaledWidth();
		int scaledHeight = The5zigMod.getVars().getScaledHeight();
		if (this.hoverTextTime > 0) {
			int l3 = (int) (this.hoverTextTime * 256.0F / 10.0F);
			if (l3 > 255) {
				l3 = 255;
			}
			GLUtil.pushMatrix();
			GLUtil.translate((float) (scaledWidth / 2), (float) (scaledHeight - 78), 0.0F);
			GLUtil.enableBlend();
			GLUtil.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_FALSE, GL11.GL_TRUE);

			The5zigMod.getVars().drawString(this.hoverText, -The5zigMod.getVars().getStringWidth(this.hoverText) / 2, -4, 0xD33D3A + (l3 << 24));
			GLUtil.disableBlend();
			GLUtil.popMatrix();
		}
	}

	public void showTextAboveHotbar(String str) {
		hoverTextTime = 20 * 30;
		hoverText = str;
	}

}