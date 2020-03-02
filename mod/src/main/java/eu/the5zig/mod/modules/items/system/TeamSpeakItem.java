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

package eu.the5zig.mod.modules.items.system;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ts.DummyChannel;
import eu.the5zig.mod.gui.ts.DummyClient;
import eu.the5zig.mod.gui.ts.entries.GuiTeamSpeakChannelDefault;
import eu.the5zig.mod.gui.ts.entries.GuiTeamSpeakClient;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.api.*;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamSpeakItem extends AbstractModuleItem {

	private static final Channel DUMMY_CHANNEL = new DummyChannel("Lobby");
	private static final ImmutableList<DummyClient> DUMMY_CLIENTS = ImmutableList.of(new DummyClient("5zig"), new DummyClient("TeamSpeakUser1"), new DummyClient("TeamSpeakUser2"));

	@Override
	public void registerSettings() {
		getProperties().addSetting("showAllTabs", true);
	}

	@Override
	public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
		if (dummy) {
			drawChannel(DUMMY_CHANNEL, DUMMY_CLIENTS, null, x + 14, y, 10);
			The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
			drawChannelIcons(DUMMY_CHANNEL, DUMMY_CLIENTS, x, y - 1, 10);
		} else {
			List<? extends ServerTab> serverTabs = TeamSpeak.getClient().getServerTabs();
			Map<ServerTab, Channel> channelCache = Maps.newHashMap();
			Map<ServerTab, List<Client>> clientCache = Maps.newHashMap();
			int yOffset = 0;
			boolean showAllTabs = (Boolean) getProperties().getSetting("showAllTabs").get();
			for (ServerTab serverTab : serverTabs) {
				OwnClient self = serverTab.getSelf();
				if (self == null || (!showAllTabs && !serverTab.isSelected())) {
					continue;
				}
				Channel channel = self.getChannel();
				if (channel == null) {
					continue;
				}
				channelCache.put(serverTab, channel);
				List<Client> clients = getClients(channel);
				clientCache.put(serverTab, clients);

				int maxClients = serverTab.isSelected() ? 10 : 5;
				drawChannel(channel, clients, self, x + 14, y + yOffset, maxClients);
				yOffset += 10 + Math.min(clients.size(), maxClients) * 10 + 2;
				if (clients.size() > maxClients) {
					yOffset += 10;
				}
			}
			The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
			yOffset = -2;
			for (ServerTab serverTab : serverTabs) {
				if (!channelCache.containsKey(serverTab) || !clientCache.containsKey(serverTab)) {
					continue;
				}
				Channel channel = channelCache.get(serverTab);
				List<Client> clients = clientCache.get(serverTab);

				int maxClients = serverTab.isSelected() ? 10 : 5;
				drawChannelIcons(channel, clients, x, y + yOffset, maxClients);
				yOffset += 10 + Math.min(clients.size(), maxClients) * 10 + 2;
				if (clients.size() > maxClients) {
					yOffset += 10;
				}
			}
		}
	}

	private void drawChannel(Channel channel, List<? extends Client> clients, Client self, int x, int y, int maxClients) {
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth((maxClients == 10 ? ChatColor.BOLD : "") + channel.getFormattedName(), 106), x, y);
		for (int i = 0; i < Math.min(maxClients, clients.size()); i++) {
			Client client = clients.get(i);
			String name;
			if (client.equals(self)) {
				name = ChatColor.BOLD + client.getDisplayName();
			} else {
				name = client.getDisplayName();
			}
			The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(name, 106), x, y + 10 + 10 * i);
		}
		if (clients.size() > maxClients) {
			The5zigMod.getVars().drawString(I18n.translate("modules.item.teamspeak.more", clients.size() - maxClients), x, y + 10 + 10 * maxClients);
		}
	}

	private void drawChannelIcons(Channel channel, List<? extends Client> clients, int x, int y, int maxClients) {
		GuiTeamSpeakChannelDefault.drawChannelIcon(channel, x, y);
		for (int i = 0; i < Math.min(maxClients, clients.size()); i++) {
			Client client = clients.get(i);
			GuiTeamSpeakClient.drawSpeechBubble(client, x, y + 10 + 10 * i);
		}
	}

	@Override
	public boolean shouldRender(boolean dummy) {
		if (dummy) {
			return true;
		} else {
			boolean showAllTabs = (Boolean) getProperties().getSetting("showAllTabs").get();
			boolean render = false;
			List<? extends ServerTab> serverTabs = TeamSpeak.getClient().getServerTabs();
			for (ServerTab serverTab : serverTabs) {
				render |= serverTab.getSelf() != null && (showAllTabs || serverTab.isSelected());
			}
			return render;
		}
	}

	@Override
	public int getWidth(boolean dummy) {
		int maxWidth = 0;
		if (dummy) {
			maxWidth = The5zigMod.getVars().getStringWidth(DUMMY_CHANNEL.getName()) + 14;
			for (Client client : DUMMY_CLIENTS) {
				int width = The5zigMod.getVars().getStringWidth(client.getDisplayName()) + 14;
				if (width > maxWidth) {
					maxWidth = width;
				}
			}
		} else {
			boolean showAllTabs = (Boolean) getProperties().getSetting("showAllTabs").get();
			List<? extends ServerTab> serverTabs = TeamSpeak.getClient().getServerTabs();
			for (ServerTab serverTab : serverTabs) {
				OwnClient self = serverTab.getSelf();
				if (self != null && (showAllTabs || serverTab.isSelected())) {
					Channel channel = self.getChannel();
					if (channel == null) {
						continue;
					}
					int cWidth = The5zigMod.getVars().getStringWidth(channel.getName());
					if (cWidth > maxWidth) {
						maxWidth = cWidth;
					}
					List<Client> clients = getClients(channel);
					int maxClients = serverTab.isSelected() ? 10 : 5;
					for (int i = 0, clientsSize = clients.size(); i < clientsSize; i++) {
						Client client = clients.get(i);
						int width = The5zigMod.getVars().getStringWidth(client.getDisplayName()) + 14;
						if (width > maxWidth) {
							maxWidth = width;
						}
						if (i == maxClients) {
							int mWidth = The5zigMod.getVars().getStringWidth(I18n.translate("modules.item.teamspeak.more", clients.size() - maxClients));
							if (mWidth > maxWidth) {
								maxWidth = mWidth;
							}
							break;
						}
					}
				}
			}
		}
		return Math.min(120, maxWidth);
	}

	@Override
	public int getHeight(boolean dummy) {
		if (dummy) {
			return DUMMY_CLIENTS.size() * 10 + 10;
		} else {
			boolean showAllTabs = (Boolean) getProperties().getSetting("showAllTabs").get();
			List<? extends ServerTab> serverTabs = TeamSpeak.getClient().getServerTabs();
			int height = 0;
			for (ServerTab serverTab : serverTabs) {
				OwnClient self = serverTab.getSelf();
				if (self != null && (showAllTabs || serverTab.isSelected())) {
					Channel channel = self.getChannel();
					if (channel != null) {
						int clientSize = getClients(channel).size();
						int maxClients = serverTab.isSelected() ? 10 : 5;
						height += (clientSize > maxClients ? (maxClients + 1) * 10 + 10 : clientSize * 10 + 10) + 2;
					}
				}
			}
			return height == 0 ? 0 : height - 2;
		}
	}

	private List<Client> getClients(Channel channel) {
		List<? extends Client> clients = channel.getClients();
		List<Client> result = new ArrayList<Client>(clients.size());
		for (Client client : clients) {
			if (client.getType() == ClientType.NORMAL) {
				result.add(client);
			}
		}
		return result;
	}

}
