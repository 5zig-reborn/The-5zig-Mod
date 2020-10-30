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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.util.Callable;

import java.util.Locale;

public class GuiModuleRender extends Gui {

	private final Module module;

	private IGuiList<RenderRow> guiList;
	private GuiArrayList<RenderRow> renderTypes = new GuiArrayList<>();

	public GuiModuleRender(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 5, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.done")));

		guiList = The5zigMod.getVars().createGuiList(new Clickable<RenderRow>() {
			@Override
			public void onSelect(int id, RenderRow row, boolean doubleClick) {
				if (doubleClick) {
					actionPerformed0(getButtonById(200));
				}
			}
		}, getWidth(), getHeight(), 32, getHeight() - 48, 0, getWidth(), renderTypes);
		guiList.setRowWidth(200);
		addGuiList(guiList);

		renderTypes.clear();
		renderTypes.add(new RenderRow(null));
		for (Module.RenderType renderType : Module.RenderType.values()) {
			renderTypes.add(new RenderRow(renderType));
		}
		if (module.getRenderType() != null) {
			for (int i = 0; i < renderTypes.size(); i++) {
				if (module.getRenderType() == renderTypes.get(i).renderType) {
					guiList.setSelectedId(i);
					break;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			RenderRow selected = guiList.getSelectedRow();
			module.setRenderType(selected == null ? null : selected.renderType);
			The5zigMod.getModuleMaster().save();
		}
	}

	@Override
	public String getTitleKey() {
		return "modules.settings.title";
	}

	private class RenderRow extends BasicRow {

		private Module.RenderType renderType;

		public RenderRow(final Module.RenderType renderType) {
			super(new Callable<String>() {
				@Override
				public String call() {
					return renderType == null ? "(" + I18n.translate("modules.settings.none") + ")" : I18n.translate(
							"modules.settings.render." + renderType.toString().toLowerCase(Locale.ROOT));
				}
			}, 195);
			this.renderType = renderType;
		}

		@Override
		public int getLineHeight() {
			return 18;
		}
	}

}
