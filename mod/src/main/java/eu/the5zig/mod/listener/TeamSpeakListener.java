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

package eu.the5zig.mod.listener;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.teamspeak.TeamSpeakAuthException;
import eu.the5zig.teamspeak.api.MessageTargetMode;
import eu.the5zig.teamspeak.api.OwnClient;
import eu.the5zig.teamspeak.event.*;
import eu.the5zig.teamspeak.listener.DisconnectListener;
import eu.the5zig.util.minecraft.ChatColor;

public class TeamSpeakListener implements DisconnectListener {

	@EventHandler
	public void onPoke(ClientPokeEvent event) {
		if (The5zigMod.getConfig().getBool("tsDmOverlay")) {
			The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("teamspeak.overlay.poked", event.invoker.getDisplayName()));
		}
		if (!The5zigMod.getVars().isPlayerNull() && The5zigMod.getConfig().getBool("tsTextMessagesInChat")) {
			The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.poked", event.invoker.getDisplayName(), event.message));
		}
	}

	@EventHandler
	public void onChatMessage(TextMessageEvent event) {
		OwnClient self = event.tab.getSelf();
		if (self == null || event.invoker == self) {
			return;
		}
		if (event.targetMode == MessageTargetMode.CLIENT && The5zigMod.getConfig().getBool("tsDmOverlay")) {
			The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("teamspeak.overlay.direct_message", event.invoker.getDisplayName()));
		}
		if (!The5zigMod.getVars().isPlayerNull() && The5zigMod.getConfig().getBool("tsTextMessagesInChat")) {
			String msg = event.msg;
			switch (event.targetMode) {
				case CLIENT:
					The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.client", event.invoker.getDisplayName(), msg));
					break;
				case CHANNEL:
					The5zigMod.getVars().messagePlayer(
							getTSPrefix() + I18n.translate("teamspeak.message.channel", event.invoker.getDisplayName(), event.invoker.getChannel().getFormattedName(), msg));
					break;
				case SERVER:
					The5zigMod.getVars().messagePlayer(getTSPrefix() +
							I18n.translate("teamspeak.message.server", event.invoker.getDisplayName(), event.invoker.getChannel().getServerTab().getServerInfo().getName(), msg));
					break;
			}
		}
	}

	@EventHandler
	public void onClientMove(ClientMovedEvent event) {
		OwnClient self = event.tab.getSelf();
		if (self == null || self == event.client) {
			return;
		}
		if (!The5zigMod.getVars().isPlayerNull() && The5zigMod.getConfig().getBool("tsChannelEventsInChat")) {
			if (event.from == self.getChannel()) {
				The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.channel.left", event.client.getDisplayName(), event.from.getName()));
			} else if (event.to == self.getChannel()) {
				The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.channel.joined", event.client.getDisplayName(), event.to.getName()));
			}
		}
	}

	@EventHandler
	public void onClientEnteredView(ClientEnteredViewEvent event) {
		OwnClient self = event.tab.getSelf();
		if (self == null || self == event.client) {
			return;
		}
		if (event.to == self.getChannel()) {
			if (!The5zigMod.getVars().isPlayerNull() && The5zigMod.getConfig().getBool("tsChannelEventsInChat")) {
				The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.channel.joined", event.client.getDisplayName(), event.to.getName()));
			}
		}
	}

	@EventHandler
	public void onClientLeftView(ClientLeftViewEvent event) {
		OwnClient self = event.tab.getSelf();
		if (self == null || self == event.client) {
			return;
		}
		if (event.from == self.getChannel()) {
			if (!The5zigMod.getVars().isPlayerNull() && The5zigMod.getConfig().getBool("tsChannelEventsInChat")) {
				The5zigMod.getVars().messagePlayer(getTSPrefix() + I18n.translate("teamspeak.message.channel.left", event.client.getName(), event.from.getName()));
			}
		}
	}

	private String getTSPrefix() {
		DisplayRenderer renderer = The5zigMod.getRenderer();
		return renderer.getBrackets() + renderer.getBracketsLeft() + renderer.getPrefix() + "TS" + renderer.getBrackets() + renderer.getBracketsRight() + " " + renderer.getMain();
	}

	@Override
	public void onDisconnect(Throwable cause) {
		if (cause != null && cause.getCause() instanceof TeamSpeakAuthException) {
			The5zigMod.getDataManager().setTsRequiresAuth(true);
			The5zigMod.getOverlayMessage().displayMessageAndSplit(I18n.translate("teamspeak.auth_key_required"));
		}
	}
}
