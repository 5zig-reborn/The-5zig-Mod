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

package eu.the5zig.mod.gui.ts;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.ts.rows.GroupRow;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Client;
import eu.the5zig.teamspeak.api.Group;
import eu.the5zig.teamspeak.api.ServerTab;

import java.util.List;

public class GuiTeamSpeakClientServerGroups extends Gui implements Clickable<GroupRow> {

	private final Client client;

	private IGuiList<GroupRow> guiList;

	public GuiTeamSpeakClientServerGroups(Gui currentScreen, Client client) {
		super(currentScreen);
		this.client = client;
	}

	@Override
	public void initGui() {
		List<GroupRow> rows = Lists.newArrayList();
		ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
		if (selectedTab == null) {
			The5zigMod.getVars().displayScreen(lastScreen);
			return;
		}
		for (Group group : selectedTab.getServerGroups()) {
			if (group.getType() == 1) {
				rows.add(new GroupRow(group, selectedTab.getServerInfo().getUniqueId()));
			}
		}

		guiList = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 48, getHeight() - 48, 0, getWidth(), rows);
		guiList.setRowWidth(200);
		addGuiList(guiList);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 150, getHeight() - 32, 98, 20, I18n.translate("teamspeak.edit_server_groups.add_to_group")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 50, getHeight() - 32, 98, 20, I18n.translate("teamspeak.edit_server_groups.remove_from_group")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 52, getHeight() - 32, 98, 20, The5zigMod.getVars().translate("gui.done")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		GroupRow selectedRow = guiList.getSelectedRow();
		if (selectedRow == null) {
			return;
		}
		if (button.getId() == 1) {
			client.addToServerGroup(selectedRow.group);
		} else if (button.getId() == 2) {
			client.removeFromServerGroup(selectedRow.group);
		}
	}

	@Override
	protected void tick() {
		ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
		if (selectedTab == null) {
			The5zigMod.getVars().displayScreen(lastScreen);
			return;
		}
		List<? extends Group> serverGroups = client.getServerGroups();
		for (GroupRow groupRow : guiList.getRows()) {
			groupRow.member = serverGroups.contains(groupRow.group);
		}
		GroupRow selectedRow = guiList.getSelectedRow();
		getButtonById(1).setEnabled(selectedRow != null && !selectedRow.member && !selectedRow.group.equals(selectedTab.getDefaultServerGroup()));
		getButtonById(2).setEnabled(selectedRow != null && selectedRow.member && !selectedRow.group.equals(selectedTab.getDefaultServerGroup()));
	}

	@Override
	public void onSelect(int id, GroupRow row, boolean doubleClick) {
		if (doubleClick) {
			if (row.member) {
				actionPerformed(getButtonById(2));
			} else {
				actionPerformed(getButtonById(1));
			}
		}
	}

	@Override
	public String getTitleKey() {
		return "teamspeak.edit_server_groups.title";
	}
}
