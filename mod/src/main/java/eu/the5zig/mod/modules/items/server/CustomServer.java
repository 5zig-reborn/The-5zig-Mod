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

package eu.the5zig.mod.modules.items.server;

import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.api.ServerAPIBackend;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomServer extends AbstractModuleItem {

	private static final Base64Renderer base64Renderer = new Base64Renderer();

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		ServerAPIBackend backend = The5zigMod.getServerAPIBackend();

		if (backend.getLargeText() != null) {
			List<?> texts = The5zigMod.getVars().splitStringToWidth(backend.getLargeText(), (int) (The5zigMod.getVars().getScaledWidth() / 1.5 / 3 * 2));
			for (int i = 0, textsSize = texts.size(); i < textsSize && i <= 9; i++) {
				Object o = texts.get(i);
				String text = String.valueOf(o);
				DisplayRenderer.largeTextRenderer.render(text, 1.5f, The5zigMod.getVars().getScaledHeight() / 4 + i * 15);
				DisplayRenderer.largeTextRenderer.flush();
			}
		} else if (backend.getCountdownTime() != -1) {
			DisplayRenderer.largeTextRenderer.render(
					The5zigMod.getRenderer().getPrefix() + backend.getCountdownName() + ": " + shorten((double) (backend.getCountdownTime() - System.currentTimeMillis()) / 1000.0));
		}

		if (The5zigMod.getServerAPIBackend().getBase64() != null) {
			if (base64Renderer.getBase64String() == null || !base64Renderer.getBase64String().equals(The5zigMod.getServerAPIBackend().getBase64()))
				base64Renderer.setBase64String(The5zigMod.getServerAPIBackend().getBase64(), The5zigMod.getServerAPIBackend().getBase64().substring(0, 16));

			int xx = The5zigMod.getVars().getScaledWidth() - 64;
			int yy = (The5zigMod.getVars().getScaledHeight() - 64) / 2;
			base64Renderer.renderImage(xx, yy, 64, 64);
		}

		Map<String, String> renderItems = getRenderItems(dummy);
		if (!renderItems.isEmpty()) {
			The5zigMod.getVars().drawString(The5zigMod.getRenderer().getPrefix() + ChatColor.UNDERLINE + backend.getDisplayName(), x, y, getPrefixColor());
			y += 12;
			for (Map.Entry<String, String> renderItem : renderItems.entrySet()) {
				renderPrefix(renderItem.getKey(), x, y);
				The5zigMod.getVars().drawString(renderItem.getValue(), x + The5zigMod.getVars().getStringWidth(renderItem.getKey()), y, getMainColor());
				y += 10;
			}
		}
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		return !getRenderItems(dummy).isEmpty();
	}

	@Override
	public int getWidth(boolean dummy) {
		int maxWidth = 0;
		Map<String, String> renderItems = getRenderItems(dummy);
		for (Map.Entry<String, String> renderItem : renderItems.entrySet()) {
			int width = The5zigMod.getVars().getStringWidth(renderItem.getKey() + renderItem.getValue());
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight(boolean dummy) {
		Map<String, String> renderItems = getRenderItems(dummy);
		return renderItems.isEmpty() ? 0 : 12 + renderItems.size() * 10;
	}

	private Map<String, String> getRenderItems(boolean dummy) {
		Map<String, String> stats = dummy ? Maps.<String, String>newHashMap() : The5zigMod.getServerAPIBackend().getStats();
		if (dummy) {
			stats.put("Kills", "8");
			stats.put("Deaths", "3");
		}
		String lobby = The5zigMod.getServerAPIBackend().getLobby();
		if (stats.isEmpty() && lobby == null) {
			return Collections.emptyMap();
		}
		if (lobby != null) {
			stats.put(getPrefix(I18n.translate("ingame.lobby")), lobby);
		}
		return stats.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>(getPrefix(e.getKey()), e.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
