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

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.BasicRow;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.server.ServerInstance;
import eu.the5zig.util.Callable;

import java.util.Collections;
import java.util.List;

public class GuiModuleServer extends Gui {

	private final Module module;

	private IGuiList<ServerRow> guiList;
	private List<ServerRow> servers = Lists.newArrayList();

	public GuiModuleServer(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 155, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 5, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.done")));

		guiList = The5zigMod.getVars().createGuiList(new Clickable<ServerRow>() {
			@Override
			public void onSelect(int id, ServerRow row, boolean doubleClick) {
				if (doubleClick) {
					actionPerformed0(getButtonById(200));
				}
			}
		}, getWidth(), getHeight(), 32, getHeight() - 48, 0, getWidth(), servers);
		guiList.setRowWidth(200);
		addGuiList(guiList);

		servers.clear();
		servers.add(new ServerRow(null));
		List<String> serverNames = The5zigMod.getDataManager().getServerInstanceRegistry().getServerNames();
		Collections.sort(serverNames);
		for (String server : serverNames) {
			servers.add(new ServerRow(The5zigMod.getDataManager().getServerInstanceRegistry().byConfigName(server)));
		}
		if (module.getServer() != null) {
			for (int i = 1; i < servers.size(); i++) {
				if (module.getServer().equals(servers.get(i).serverInstance.getConfigName())) {
					guiList.setSelectedId(i);
					break;
				}
			}
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			ServerRow selected = guiList.getSelectedRow();
			module.setServer(selected == null || selected.serverInstance == null ? null : selected.serverInstance.getConfigName());
		}
		if (button.getId() == 100) {
			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}

	@Override
	public String getTitleKey() {
		return "modules.settings.title";
	}

	private class ServerRow extends BasicRow {

		private ServerInstance serverInstance;

		public ServerRow(final ServerInstance serverInstance) {
			super(new Callable<String>() {
				@Override
				public String call() {
					return serverInstance == null ? "(" + I18n.translate("modules.settings.none") + ")" : serverInstance.getName();
				}
			}, 195);
			this.serverInstance = serverInstance;
		}

		@Override
		public int getLineHeight() {
			return 18;
		}
	}

}
