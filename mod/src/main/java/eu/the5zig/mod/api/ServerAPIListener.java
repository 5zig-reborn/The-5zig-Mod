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

package eu.the5zig.mod.api;

import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.chat.network.filetransfer.FileTransferManager;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.PayloadEvent;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.GuiDownloadModPlugin;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.File;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ServerAPIListener {

	private final List<GuiDownloadModPlugin> guiQueue = Lists.newArrayList();
	private boolean registered = false;

	@EventHandler
	public void onPayloadReceive(PayloadEvent event) {
		String channel = event.getChannel();
		ByteBuf packetData = event.getPayload();
		if (channel.equals(PayloadUtils.API_CHANNEL_REGISTER)) {
			int version = packetData.readInt();
//			if (version < Version.APIVERSION) {
//				The5zigMod.getVars().messagePlayer(The5zigMod.getRenderer().getPrefix("The5zigMod") + I18n.translate("api.outdated_server"));
//			} else if (version > Version.APIVERSION) {
//				The5zigMod.getVars().messagePlayer(The5zigMod.getRenderer().getPrefix("The5zigMod") + I18n.translate("api.outdated_client"));
//			} else {
			// The5zigMod.getVars().messagePlayer(The5zigMod.getRenderer().getPrefix("The5zigMod") + I18n.translate("api.connected"));
			The5zigMod.getServerAPIBackend().reset();
			if (version == Version.APIVERSION) {
				registered = true;
				PayloadUtils.sendPayload(Unpooled.buffer().writeByte((byte) Version.APIVERSION));
			} else {
				registered = false;
			}
//			}
		} else if (registered && channel.equals(PayloadUtils.API_CHANNEL)) {
			int ordinal = packetData.readInt();
			if (ordinal < 0 || ordinal >= PayloadType.values().length) {
				The5zigMod.logger.warn("Could not handle Custom Payload on Channel {}. Could not handle received integer.", PayloadUtils.API_CHANNEL);
				return;
			}
			PayloadType payloadType = PayloadType.values()[ordinal];

			switch (payloadType) {
				case UPDATE:
					String statName = PayloadUtils.readString(packetData, 100);
					String statScore = PayloadUtils.readString(packetData, 100);
					The5zigMod.getServerAPIBackend().updateStat(statName, statScore);
					break;
				case RESET:
					statName = PayloadUtils.readString(packetData, 100);
					The5zigMod.getServerAPIBackend().resetStat(statName);
					break;
				case CLEAR:
					The5zigMod.getServerAPIBackend().getStats().clear();
					break;
				case DISPLAY_NAME:
					String displayName = PayloadUtils.readString(packetData, 150);
					The5zigMod.getServerAPIBackend().setDisplayName(displayName);
					break;
				case LARGE_TEXT:
					String largeText = PayloadUtils.readString(packetData, 250);
					The5zigMod.getServerAPIBackend().setLargeText(largeText);
					break;
				case RESET_LARGE_TEXT:
					The5zigMod.getServerAPIBackend().setLargeText(null);
					break;
				case IMAGE:
					String base64 = PayloadUtils.readString(packetData, Short.MAX_VALUE);
					int id = packetData.readInt();
					The5zigMod.getServerAPIBackend().setImage(base64, id);
					break;
				case IMAGE_ID:
					id = packetData.readInt();
					The5zigMod.getServerAPIBackend().setImage(id);
					break;
				case RESET_IMAGE:
					The5zigMod.getServerAPIBackend().resetImage();
					break;
				case OVERLAY:
					The5zigMod.getOverlayMessage().displayMessageAndSplit(PayloadUtils.readString(packetData, 100));
					break;
				case COUNTDOWN:
					String name = PayloadUtils.readString(packetData, 50);
					long time = packetData.readLong();
					if (time == 0) {
						The5zigMod.getServerAPIBackend().resetCountdown();
					} else {
						The5zigMod.getServerAPIBackend().startCountdown(name, time);
					}
					break;
				case MOD_PLUGIN:
					if (!The5zigMod.getConfig().getBool("allowModPluginRequests")) {
						The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
								Unpooled.buffer().writeInt(PayloadType.MOD_PLUGIN.ordinal()).writeInt(ModPluginResponse.DENIED.ordinal()));
						break;
					}
					String pluginName = PayloadUtils.readString(packetData, 128);
					String sha1 = PayloadUtils.readString(packetData, 40);
					String url = PayloadUtils.readString(packetData, 256);
					String customMessage = PayloadUtils.readString(packetData, 256);

					File file = new File(The5zigMod.getAPI().getPluginManager().getModuleDirectory(), pluginName + ".jar");
					if (!file.exists() || !sha1.equals(FileTransferManager.sha1(file))) {
						GuiDownloadModPlugin gui = new GuiDownloadModPlugin(The5zigMod.getVars().createWrappedGui(The5zigMod.getVars().getCurrentScreen()), pluginName, sha1, url,
								customMessage);
						if (The5zigMod.getVars().getCurrentScreen() instanceof GuiDownloadModPlugin) {
							guiQueue.add(gui);
						} else {
							The5zigMod.getVars().displayScreen(gui);
						}
					} else {
						The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
								Unpooled.buffer().writeInt(PayloadType.MOD_PLUGIN.ordinal()).writeInt(ModPluginResponse.ALREADY_LOADED.ordinal()));
					}
					break;
				case LOBBY:
					String lobby = PayloadUtils.readString(packetData, 50);
					The5zigMod.getServerAPIBackend().setLobby(lobby.isEmpty() ? null : lobby);
					The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.LOBBY, lobby));
					break;
				default:
					The5zigMod.logger.warn("Could not handle custom server payload " + payloadType.toString());
					break;
			}
		}
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (!guiQueue.isEmpty() && !(The5zigMod.getVars().getCurrentScreen() instanceof GuiDownloadModPlugin)) {
			The5zigMod.getVars().displayScreen(guiQueue.remove(0));
		}
	}

	public enum PayloadType {
		UPDATE, RESET, CLEAR, DISPLAY_NAME, IMAGE, IMAGE_ID, RESET_IMAGE, LARGE_TEXT, RESET_LARGE_TEXT, OVERLAY, COUNTDOWN, MOD_PLUGIN, LOBBY
	}

	public enum ModPluginResponse {
		DENIED, ALREADY_LOADED, DOWNLOADED, DOWNLOAD_FAILED
	}

}