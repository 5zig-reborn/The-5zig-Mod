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

package eu.the5zig.mod.gui.elements;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.modules.ActiveModuleItem;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.RenderSettingsImpl;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.GLUtil;

public class StaticModulePreviewRow implements Row {

	private Module module;
	private final int x;
	private final int y;
	private final int width;
	private final int height;

	public StaticModulePreviewRow(Module module, int x, int y, int width, int height) {
		this.module = module;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	@Override
	public void draw(int x, int y) {
		renderModulePreviewBox(this.x, this.y, width, height);
		if (module != null) {
			if (module.getScale() != 1.0f) {
				GLUtil.pushMatrix();
				GLUtil.scale(module.getScale(), module.getScale(), module.getScale());
			}
			renderModulePreview(module, (int) (x / module.getScale()), (int) (y / module.getScale()));
			if (module.getScale() != 1.0f) {
				GLUtil.popMatrix();
			}
		}
	}

	@Override
	public int getLineHeight() {
		return module == null ? 0 : module.getTotalHeight(true);
	}

	private void renderModulePreviewBox(int x, int y, int width, int height) {
		Gui.drawRect(x, y, x + width, y + height, 0x22888888);
		Gui.drawRectOutline(x, y, x + width, y + height, 0xff000000);
	}

	private void renderModulePreview(Module module, int x, int y) {
		x += 2;
		int yPos = y + 2;
		if (module.isShowLabel()) {
			The5zigMod.getVars().drawString(module.getDisplayName(), x, yPos);
			yPos += 14;
		}
		for (ActiveModuleItem item : module.getItems()) {
			int itemHeight = item.getHandle().getHeight(true);
			RenderSettingsImpl.assign(new RenderSettingsImpl(module.getScale()), item.getHandle());
			item.getHandle().render(x, yPos, RenderLocation.LEFT, true);
			yPos += itemHeight;
		}
	}
}