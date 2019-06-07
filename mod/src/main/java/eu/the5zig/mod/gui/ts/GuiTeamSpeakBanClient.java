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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.teamspeak.api.Client;
import eu.the5zig.util.Utils;

public class GuiTeamSpeakBanClient extends Gui {

	private final Client client;

	public GuiTeamSpeakBanClient(Gui lastScreen, Client client) {
		super(lastScreen);
		this.client = client;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.done")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.cancel")));

		addTextField(The5zigMod.getVars().createTextfield(100, getWidth() / 2 - 100, getHeight() / 6 + 40, 250, 18, 1000));
		addTextField(The5zigMod.getVars().createTextfield(101, getWidth() / 2 - 100, getHeight() / 6 + 75, 100, 18, 1000));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			String reason = getTextfieldById(100).callGetText();
			int time = Utils.parseInt(getTextfieldById(101).callGetText());
			client.banFromServer(reason, time);

			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.ban_client.name"), getWidth() / 2 - 150, getHeight() / 6 + 18);
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(client.getName(), 250), getWidth() / 2 - 100, getHeight() / 6 + 18);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.ban_client.reason"), getWidth() / 2 - 150, getHeight() / 6 + 45);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.ban_client.duration"), getWidth() / 2 - 150, getHeight() / 6 + 80);
		The5zigMod.getVars().drawString(I18n.translate("teamspeak.ban_client.seconds"), getWidth() / 2 + 5, getHeight() / 6 + 80);
	}

	@Override
	public String getTitleKey() {
		return "teamspeak.ban_client.title";
	}
}
