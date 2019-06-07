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

package eu.the5zig.mod.gui;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.StringListItem;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.plugin.LoadedPlugin;
import eu.the5zig.mod.plugin.PluginManagerImpl;
import eu.the5zig.util.minecraft.ChatColor;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GuiPlugins extends Gui {

	private PluginManagerImpl pluginManager;
	private String infoText = I18n.translate("plugin_manager.help");

	private IGuiList<PluginRow> guiList;
	private List<PluginRow> pluginRows = Lists.newArrayList();

	public GuiPlugins(Gui lastScreen) {
		super(lastScreen);
		this.pluginManager = The5zigMod.getAPI().getPluginManager();
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 190, getHeight() - 38, 90, 20, I18n.translate("plugin_manager.disable")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 95, getHeight() - 38, 90, 20, I18n.translate("plugin_manager.reload")));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2, getHeight() - 38, 90, 20, I18n.translate("plugin_manager.plugin_folder")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 95, getHeight() - 38, 95, 20, The5zigMod.getVars().translate("gui.back")));

		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 64, getHeight() - 50, 0, getWidth(),
				pluginRows);
		guiList.setRowWidth(250);
		guiList.setScrollX(getWidth() / 2 + 150);
		addGuiList(guiList);
		pluginRows.clear();
		for (LoadedPlugin loadedPlugin : pluginManager.getPlugins()) {
			pluginRows.add(new PluginRow(loadedPlugin.getFile(), loadedPlugin));
		}
		Collections.sort(pluginRows);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			PluginRow selectedRow = guiList.getSelectedRow();
			if (selectedRow == null) {
				return;
			}
			if (selectedRow.loadedPlugin == null || !pluginManager.getPlugins().contains(selectedRow.loadedPlugin)) {
				StringListItem list = The5zigMod.getConfig().get("disabled_plugins", StringListItem.class);
				list.get().remove(selectedRow.file.getName());
				list.setChanged(true);
				The5zigMod.getConfig().save();
				try {
					selectedRow.loadedPlugin = pluginManager.loadPlugin(selectedRow.file);
					button.setLabel(I18n.translate("plugin_manager.disable"));
					infoText = I18n.translate("plugin_manager.help");
				} catch (Throwable e) {
					infoText = e.getLocalizedMessage();
					The5zigMod.logger.error("Failed to load plugin!", e);
				}
			} else {
				StringListItem list = The5zigMod.getConfig().get("disabled_plugins", StringListItem.class);
				list.get().add(selectedRow.file.getName());
				list.setChanged(true);
				The5zigMod.getConfig().save();
				pluginManager.unloadPlugin(selectedRow.loadedPlugin.getName());
				selectedRow.loadedPlugin = null;
				button.setLabel(I18n.translate("plugin_manager.enable"));
				System.gc();
			}
			Collections.sort(pluginRows);
		} else if (button.getId() == 2) {
			PluginRow selectedRow = guiList.getSelectedRow();
			if (selectedRow == null || selectedRow.loadedPlugin == null) {
				return;
			}
			pluginManager.unloadPlugin(selectedRow.loadedPlugin.getName());
			System.gc();
			try {
				selectedRow.loadedPlugin = pluginManager.loadPlugin(selectedRow.file);
				getButtonById(1).setLabel(I18n.translate("plugin_manager.disable"));
				infoText = I18n.translate("plugin_manager.help");
			} catch (Throwable e) {
				infoText = e.getLocalizedMessage();
				The5zigMod.logger.error("Failed to load plugin!", e);
				selectedRow.loadedPlugin = null;
				getButtonById(1).setLabel(I18n.translate("plugin_manager.enable"));
			}
			Collections.sort(pluginRows);
		} else if (button.getId() == 3) {
			try {
				Desktop.getDesktop().open(pluginManager.getModuleDirectory());
			} catch (IOException e) {
				The5zigMod.logger.error("Failed to open plugin directory!", e);
			}
		}
	}

	@Override
	protected void tick() {
		File[] pluginCandidates = pluginManager.getPluginCandidates();
		if (pluginCandidates != null) {
			for (File pluginCandidate : pluginCandidates) {
				boolean alreadyLoaded = false;
				for (PluginRow pluginRow : pluginRows) {
					if (pluginRow.file.equals(pluginCandidate)) {
						alreadyLoaded = true;
						break;
					}
				}
				if (!alreadyLoaded) {
					pluginRows.add(new PluginRow(pluginCandidate, null));
					Collections.sort(pluginRows);
				}
			}
		}

		PluginRow selectedRow = guiList.getSelectedRow();
		if (selectedRow == null) {
			getButtonById(1).setLabel(I18n.translate("plugin_manager.enable"));
			getButtonById(1).setEnabled(false);
			getButtonById(2).setEnabled(false);
		} else if (selectedRow.loadedPlugin == null || The5zigMod.getConfig().getStringList("disabled_plugins").contains(selectedRow.loadedPlugin.getFile().getName())) {
			getButtonById(1).setLabel(I18n.translate("plugin_manager.enable"));
			getButtonById(1).setEnabled(true);
			getButtonById(2).setEnabled(true);
		} else {
			getButtonById(1).setLabel(I18n.translate("plugin_manager.disable"));
			getButtonById(1).setEnabled(true);
			getButtonById(2).setEnabled(true);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int y = 0;
		for (String line : The5zigMod.getVars().splitStringToWidth(infoText, getWidth() / 4 * 3)) {
			drawCenteredString(ChatColor.GRAY + line, getWidth() / 2, 34 + y);
			y += 10;
		}
	}

	@Override
	public String getTitleKey() {
		return "plugin_manager.title";
	}

	private class PluginRow implements Row, Comparable<PluginRow> {

		public File file;
		public LoadedPlugin loadedPlugin;

		private PluginRow(File file, LoadedPlugin loadedPlugin) {
			this.file = file;
			this.loadedPlugin = loadedPlugin;
		}

		@Override
		public void draw(int x, int y) {
			String name = loadedPlugin != null ? loadedPlugin.getName() + " (v" + loadedPlugin.getVersion() + ") " : "";
			The5zigMod.getVars().drawString(name + ChatColor.GRAY + file.getName(), x + 2, y + 2);
		}

		@Override
		public int getLineHeight() {
			return 18;
		}

		@Override
		public int compareTo(PluginRow o) {
			return ComparisonChain.start().compareTrueFirst(loadedPlugin != null, o.loadedPlugin != null).compare(file.getName(), o.file.getName()).result();
		}
	}
}
