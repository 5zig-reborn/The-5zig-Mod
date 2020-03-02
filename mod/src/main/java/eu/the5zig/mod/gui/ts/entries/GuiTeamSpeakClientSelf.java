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

package eu.the5zig.mod.gui.ts.entries;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.CenteredTextfieldCallback;
import eu.the5zig.mod.gui.GuiCenteredTextfield;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.Client;
import eu.the5zig.teamspeak.api.ServerTab;

public class GuiTeamSpeakClientSelf extends GuiTeamSpeakClient {

	public GuiTeamSpeakClientSelf(Client client, String serverUniqueId) {
		super(client, serverUniqueId);
	}

	@Override
	public void onClick(boolean doubleClick) {
		if (doubleClick) {
			final ServerTab selectedTab = TeamSpeak.getClient().getSelectedTab();
			if (selectedTab != null && selectedTab.getSelf() != null) {
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(The5zigMod.getVars().getCurrentScreen(), new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						if (text != null) {
							selectedTab.getSelf().setNickName(text);
						}
					}

					@Override
					public String title() {
						return I18n.translate("teamspeak.enter_nickname");
					}
				}, selectedTab.getSelf().getName() == null ? "" : selectedTab.getSelf().getName()));
			}
		}
	}

}
