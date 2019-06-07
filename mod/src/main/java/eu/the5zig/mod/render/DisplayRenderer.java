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

package eu.the5zig.mod.render;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.ColorFormattingItem;
import eu.the5zig.mod.config.items.SelectColorItem;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.ModuleLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.ModCompatibility;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.HashMap;
import java.util.List;

public class DisplayRenderer {

	public static final LargeTextRenderer largeTextRenderer = new LargeTextRenderer();
	private final PotionIndicatorRenderer potionIndicatorRenderer = new PotionIndicatorRenderer();
	private final ChatSymbolsRenderer chatSymbolsRenderer = new ChatSymbolsRenderer();

	private final HashMap<ModuleLocation, Integer> totalHeights = Maps.newHashMap();
	private final HashMap<ModuleLocation, Integer> offsets = Maps.newHashMap();

	private float scale = 1.0f;

	public DisplayRenderer() {
	}

	public void drawScreen() {
		if (ModCompatibility.hasReplayMod && !The5zigMod.getVars().isLocalWorld() && The5zigMod.getVars().getServer() == null)
			return; // Let's be nice and don't render the mod in the replay viewer
		if (The5zigMod.getVars().isChatOpened() && The5zigMod.getConfig().getBool("showChatSymbols")) {
			chatSymbolsRenderer.render();
		}

		if (The5zigMod.getConfig().getBool("showMod")) {
			scale = The5zigMod.getConfig().getFloat("scale");
			GLUtil.pushMatrix();
			GLUtil.scale(scale, scale, scale);
			renderModules();
			GLUtil.popMatrix();
			largeTextRenderer.flush();
		}

		if (The5zigMod.getDataManager().getChatSearchManager().isSearching()) {
			GLUtil.translate(0, 0, 1000);
			Gui.drawRectOutline(2, getHeight() - 14, getWidth() - 2, getHeight() - 2, 0xffcccc00);
			GLUtil.translate(0, 0, -1000);
			String searchText = The5zigMod.getDataManager().getChatSearchManager().getSearchText();
			if (Strings.isNullOrEmpty(searchText)) {
				largeTextRenderer.render(ChatColor.YELLOW + I18n.translate("ingame.chat_search.enter_keyword"));
			} else {
				List<String> toRender = The5zigMod.getVars().splitStringToWidth(ChatColor.YELLOW + I18n.translate("ingame.chat_search.highlighting", searchText),
						(int) ((The5zigMod.getVars().getScaledWidth() - 20) / 1.5f));
				for (int i = 0; i < toRender.size(); i++) {
					largeTextRenderer.render(toRender.get(i), 1.5f, The5zigMod.getVars().getScaledHeight() / 4 + i * 15);
					largeTextRenderer.flush();
				}
			}
		}
	}

	private void renderModules() {
		List<Module> modules = The5zigMod.getModuleMaster().getModules();
		for (ModuleLocation location : ModuleLocation.values()) {
			totalHeights.put(location, 0);
		}
		for (Module module : modules) {
			if (!module.isShouldRender())
				continue;
			if (module.getLocation() != ModuleLocation.CUSTOM) {
				totalHeights.put(module.getLocation(), totalHeights.get(module.getLocation()) + module.getTotalHeight(false));
			}
		}

		offsets.put(ModuleLocation.TOP_LEFT, 2);
		offsets.put(ModuleLocation.BOTTOM_LEFT, getHeight() - totalHeights.get(ModuleLocation.BOTTOM_LEFT) + 6);
		int leftCenter = getHeight() / 2 - totalHeights.get(ModuleLocation.CENTER_LEFT) / 2;
		while (leftCenter > offsets.get(ModuleLocation.BOTTOM_LEFT)) {
			leftCenter--;
		}
		while (leftCenter < offsets.get(ModuleLocation.TOP_LEFT) + totalHeights.get(ModuleLocation.TOP_LEFT)) {
			leftCenter++;
		}
		offsets.put(ModuleLocation.CENTER_LEFT, leftCenter);

		offsets.put(ModuleLocation.TOP_RIGHT, 2 + (The5zigMod.getConfig().getBool("showVanillaPotionIndicator") ? The5zigMod.getVars().getPotionEffectIndicatorHeight() : 0));
		offsets.put(ModuleLocation.BOTTOM_RIGHT, getHeight() - totalHeights.get(ModuleLocation.BOTTOM_RIGHT) + 6);
		int rightCenter = getHeight() / 2 - totalHeights.get(ModuleLocation.CENTER_RIGHT) / 2;
		while (rightCenter > offsets.get(ModuleLocation.BOTTOM_RIGHT)) {
			rightCenter--;
		}
		while (rightCenter < offsets.get(ModuleLocation.TOP_RIGHT) + totalHeights.get(ModuleLocation.TOP_RIGHT)) {
			rightCenter++;
		}
		offsets.put(ModuleLocation.CENTER_RIGHT, rightCenter);

		for (Module module : modules) {
			if (!module.isShouldRender())
				continue;
			int x;
			int y = 0;
			switch (module.getLocation()) {
				case TOP_LEFT:
				case BOTTOM_LEFT:
				case CENTER_LEFT:
					x = 2;
					break;
				case TOP_RIGHT:
				case BOTTOM_RIGHT:
				case CENTER_RIGHT:
					x = getWidth() - module.getMaxWidth(false) - 2;
					break;
				case CUSTOM:
					x = (int) (module.getLocationX() * getWidth());
					y = (int) (module.getLocationY() * getHeight());
					break;
				default:
					throw new IllegalArgumentException();
			}
			if (module.getLocation() == ModuleLocation.CUSTOM) {
				module.render(this, x, y, false);
			} else {
				Integer yOffset = offsets.get(module.getLocation());
				module.render(this, x, yOffset, false);
				offsets.put(module.getLocation(), yOffset + module.getTotalHeight(false));
			}
		}
	}

	public String getPrefix(String name) {
		return getBrackets() + getBracketsLeft() + getPrefix() + name + getBrackets() + getBracketsRight() + " " + getMain();
	}

	public String getPrefix() {
		ChatColor formattingPrefix = The5zigMod.getConfig().get("formattingPrefix", ColorFormattingItem.class).get();
		ChatColor colorPrefix = The5zigMod.getConfig().get("colorPrefix", SelectColorItem.class).get();
		if (formattingPrefix != ChatColor.RESET) {
			return colorPrefix.toString() + formattingPrefix.toString();
		} else {
			return colorPrefix.toString();
		}
	}

	public String getMain() {
		ChatColor formattingMain = The5zigMod.getConfig().get("formattingMain", ColorFormattingItem.class).get();
		ChatColor colorMain = The5zigMod.getConfig().get("colorMain", SelectColorItem.class).get();
		if (formattingMain != ChatColor.RESET) {
			return colorMain.toString() + formattingMain.toString();
		} else {
			return colorMain.toString();
		}
	}

	public String getBrackets() {
		return The5zigMod.getConfig().get("colorBrackets", SelectColorItem.class).get().toString();
	}

	public String getBracketsLeft() {
		return The5zigMod.getConfig().getEnum("formattingBrackets", BracketsFormatting.class).getFirst();
	}

	public String getBracketsRight() {
		return The5zigMod.getConfig().getEnum("formattingBrackets", BracketsFormatting.class).getLast();
	}

	public int getWidth() {
		return (int) (The5zigMod.getVars().getScaledWidth() / scale);
	}

	public int getHeight() {
		return (int) (The5zigMod.getVars().getScaledHeight() / scale);
	}

	public PotionIndicatorRenderer getPotionIndicatorRenderer() {
		return potionIndicatorRenderer;
	}

	public ChatSymbolsRenderer getChatSymbolsRenderer() {
		return chatSymbolsRenderer;
	}
}