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
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.server.mcpvp.MCPVPServer;
import eu.the5zig.mod.server.mcpvp.slot.ServerListMCPvP;
import eu.the5zig.mod.util.Keyboard;

import java.util.List;

public abstract class GuiMCPVPServersScroll extends GuiMCPVPServers {

	protected int currentServerId;
	protected IButton joinSelectedServerButton;
	protected IButton refreshButton;
	protected IGuiList<MCPVPServer> serverSlot;
	private int currentDots;

	public GuiMCPVPServersScroll(Gui lastScreen) {
		super(lastScreen);
		this.currentDots = 0;
	}

	protected void refreshServers() {
		this.currentServerId = -1;
		joinSelectedServerButton.setEnabled(false);
		refreshButton.setEnabled(false);
		super.refreshServers();
	}

	@Override
	protected void onKeyType(char character, int key) {
		if (key == Keyboard.KEY_F5) {
			refreshServers();
		}
	}

	public void run() {
		super.run();
		refreshButton.setEnabled(true);
		joinSelectedServerButton.setEnabled(serverSlot.getSelectedRow() != null);
	}

	public void initGui() {
		initButtons();
		this.serverSlot = The5zigMod.getVars().createGuiList(new Clickable<MCPVPServer>() {
			@Override
			public void onSelect(int id, MCPVPServer row, boolean doubleClick) {
				if (row == null) {
					joinSelectedServerButton.setEnabled(false);
					return;
				}
				joinSelectedServerButton.setEnabled(true);
				if (doubleClick) {
					joinServer(id);
				}
			}
		}, getWidth(), getHeight(), 32, getHeight() - 64, 0, getWidth(), servers);
		serverSlot.setScrollX(getWidth() / 2 + 124);
		serverSlot.setRowWidth(220);
		addGuiList(serverSlot);
		refreshServers();
	}

	protected void initButtons() {
		addButton(this.joinSelectedServerButton = The5zigMod.getVars().createButton(1, getWidth() / 2 - 154, getHeight() - 40, 150, 20, I18n.translate("mcpvp_servers.join_server")));
		addButton(this.refreshButton = The5zigMod.getVars().createButton(7, getWidth() / 2 + 4, getHeight() - 40, 72, 20, I18n.translate("mcpvp_servers.refresh")));
		addButton(The5zigMod.getVars().createButton(0, getWidth() / 2 + 82, getHeight() - 40, 72, 20, The5zigMod.getVars().translate("gui.cancel")));
		joinSelectedServerButton.setEnabled(false);
	}

	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			joinServer(this.serverSlot.getSelectedId());
		} else if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new ServerListMCPvP(lastScreen));
		} else if (button.getId() == 0) {
			The5zigMod.getVars().displayScreen(lastScreen);
		} else if (button.getId() == 7) {
			refreshServers();
		}
	}

	@Override
	public void tick() {
		this.currentDots += 1;
		if (this.currentDots > 10) {
			this.currentDots = 0;
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (this.isLoadingServers) {
			String dots = "";
			for (int i = 0; i < this.currentDots / 3; i++) {
				dots = dots + ".";
			}
			drawCenteredString(I18n.translate("gui.loading") + " " + dots, getWidth() / 2, getHeight() / 2 - 20);
		}
	}

	protected abstract void sort(List<MCPVPServer> paramList);

}
