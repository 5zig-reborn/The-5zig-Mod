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
import eu.the5zig.mod.config.items.ListItem;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class GuiSettingsList extends Gui {

	private final ListItem<?> configItem;
	private IGuiList<SettingsRow> guiList;
	private GuiArrayList<SettingsRow> rows = new GuiArrayList<>();

	public GuiSettingsList(Gui lastScreen, ListItem<?> configItem) {
		super(lastScreen);
		this.configItem = configItem;
		for (Object entry : configItem.get()) {
			rows.add(new SettingsRow(entry));
		}
	}

	@Override
	public void initGui() {
		addBottomDoneButton();
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 100, getHeight() - 58, 98, 20, I18n.translate("config.list.add_entry")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 2, getHeight() - 58, 98, 20, I18n.translate("config.list.remove_entry")));

		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 64, getHeight() - 64, 0, getWidth(), rows);
		guiList.setRowWidth(220);
		addGuiList(guiList);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			List<Object> entries = new ArrayList<Object>(rows.size());
			for (SettingsRow row : rows) {
				entries.add(row.value);
			}
			configItem.setSafely(entries);
			configItem.action();
			if (configItem.hasChanged())
				The5zigMod.getConfig().save();
		}
		if (button.getId() == 2 && !rows.isEmpty()) {
			rows.remove(guiList.getSelectedId());
		}
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
				@Override
				public void onDone(String text) {
					rows.add(new SettingsRow(text));
				}

				@Override
				public String title() {
					return configItem.translate();
				}
			}));
		}
	}

	@Override
	protected void onEscapeType() {
		actionPerformed(getButtonById(200));
		super.onEscapeType();
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int y = 0;
		for (String line : The5zigMod.getVars().splitStringToWidth(configItem.translateDescription(), getWidth() / 4 * 3)) {
			drawCenteredString(ChatColor.GRAY + line, getWidth() / 2, 34 + y);
			y += 10;
		}
	}

	@Override
	protected void tick() {
		getButtonById(2).setEnabled(!rows.isEmpty());
	}

	@Override
	public String getTitleName() {
		return "The 5zig Mod - " + configItem.translate();
	}

	private class SettingsRow extends BasicRow {

		private final Object value;

		public SettingsRow(Object o) {
			super(String.valueOf(o), 200, 18);
			this.value = o;
		}
	}

}
