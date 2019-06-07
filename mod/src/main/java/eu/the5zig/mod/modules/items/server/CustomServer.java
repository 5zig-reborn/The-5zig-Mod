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

package eu.the5zig.mod.modules.items.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.api.ServerAPIBackend;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

		List<String> renderItems = getRenderItems(dummy);
		if (!renderItems.isEmpty()) {
			The5zigMod.getVars().drawString(The5zigMod.getRenderer().getPrefix() + ChatColor.UNDERLINE + backend.getDisplayName(), x, y);
			y += 12;
			for (String renderItem : renderItems) {
				The5zigMod.getVars().drawString(renderItem, x, y);
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
		List<String> renderItems = getRenderItems(dummy);
		for (String renderItem : renderItems) {
			int width = The5zigMod.getVars().getStringWidth(renderItem);
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	public int getHeight(boolean dummy) {
		List<String> renderItems = getRenderItems(dummy);
		return renderItems.isEmpty() ? 0 : 12 + renderItems.size() * 10;
	}

	private List<String> getRenderItems(boolean dummy) {
		Map<String, String> stats = dummy ? Maps.<String, String>newHashMap() : The5zigMod.getServerAPIBackend().getStats();
		if (dummy) {
			stats.put("Kills", "8");
			stats.put("Deaths", "3");
		}
		String lobby = The5zigMod.getServerAPIBackend().getLobby();
		if (stats.isEmpty() && lobby == null) {
			return Collections.emptyList();
		}
		List<String> result = Lists.newArrayListWithCapacity(stats.size() + (lobby == null ? 0 : 1));
		if (lobby != null) {
			result.add(getPrefix(I18n.translate("ingame.lobby")) + lobby);
		}
		for (Map.Entry<String, String> entry : stats.entrySet()) {
			result.add(getPrefix(entry.getKey()) + entry.getValue());
		}
		return result;
	}
}
