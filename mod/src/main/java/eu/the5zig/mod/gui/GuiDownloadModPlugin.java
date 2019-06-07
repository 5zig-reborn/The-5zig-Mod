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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.api.PayloadUtils;
import eu.the5zig.mod.api.ServerAPIListener;
import eu.the5zig.mod.chat.network.filetransfer.FileTransferManager;
import eu.the5zig.mod.gui.elements.IButton;
import io.netty.buffer.Unpooled;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GuiDownloadModPlugin extends Gui {

	private final String pluginName;
	private final String sha1;
	private final String downloadPath;

	private boolean downloading = false;
	private String downloadStatus;
	private float downloadProgress;

	private File file;
	private final String customMessage;

	public GuiDownloadModPlugin(Gui lastScreen, String pluginName, String sha1, String url, String customMessage) {
		super(lastScreen);
		this.pluginName = pluginName;
		this.sha1 = sha1;
		this.downloadPath = url;
		this.file = new File(The5zigMod.getAPI().getPluginManager().getModuleDirectory(), pluginName + ".jar");
		this.customMessage = customMessage;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addButton(The5zigMod.getVars()
				.createButton(100, getWidth() / 2 + 5, getHeight() - 32, 150, 20, downloading ? The5zigMod.getVars().translate("gui.done") : I18n.translate("plugin_request.download")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			if (!downloading) {
				The5zigMod.getVars().displayScreen(new GuiYesNo(this, new YesNoCallback() {
					@Override
					public void onDone(boolean yes) {
						if (yes) {
							new Thread(new Runnable() {
								@Override
								public void run() {
									startDownload();
								}
							}).start();
						}
					}

					@Override
					public String title() {
						return I18n.translate("plugin_request.warning");
					}
				}));
			} else {
				The5zigMod.getVars().displayScreen(lastScreen);
			}
		}
		if (button.getId() == 200) {
			The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
					Unpooled.buffer().writeInt(ServerAPIListener.PayloadType.MOD_PLUGIN.ordinal()).writeInt(ServerAPIListener.ModPluginResponse.DENIED.ordinal()));
		}
	}

	private void startDownload() {
		downloading = true;
		downloadStatus = I18n.translate("plugin_request.downloading", "0%");
		IButton doneButton = getButtonById(100);
		doneButton.setLabel(The5zigMod.getVars().translate("gui.done"));
		doneButton.setEnabled(false);
		byte[] buffer = new byte[4096];
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			URL url = new URL(downloadPath);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			int responseCode = connection.getResponseCode();
			if (responseCode / 100 != 2) {
				throw new IOException("Illegal response code " + responseCode + " received!");
			}
			int contentLength = connection.getContentLength();
			inputStream = connection.getInputStream();
			outputStream = new FileOutputStream(file);

			int length;
			int totalLength = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
				totalLength += length;
				downloadProgress = (float) totalLength / (float) contentLength;
				downloadStatus = I18n.translate("plugin_request.downloading", (int) (downloadProgress * 100) + "%");
			}
		} catch (Exception e) {
			downloadStatus = I18n.translate("plugin_request.error") + ": " + e.toString();
			The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
					Unpooled.buffer().writeInt(ServerAPIListener.PayloadType.MOD_PLUGIN.ordinal()).writeInt(ServerAPIListener.ModPluginResponse.DOWNLOAD_FAILED.ordinal()));
			return;
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(outputStream);
		}
		downloadStatus = I18n.translate("plugin_request.verifying");
		String downloadedSha1 = FileTransferManager.sha1(file);
		if (!sha1.equals(downloadedSha1)) {
			if (!file.delete()) {
				file.deleteOnExit();
			}
			downloadStatus = I18n.translate("plugin_request.error");
			The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
					Unpooled.buffer().writeInt(ServerAPIListener.PayloadType.MOD_PLUGIN.ordinal()).writeInt(ServerAPIListener.ModPluginResponse.DOWNLOAD_FAILED.ordinal()));
		} else {
			The5zigMod.getScheduler().postToMainThread(new Runnable() {
				@Override
				public void run() {
					try {
						The5zigMod.getAPI().getPluginManager().unloadPlugin(pluginName);
						The5zigMod.getAPI().getPluginManager().loadPlugin(file);
						downloadStatus = I18n.translate("plugin_request.done");
						The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
								Unpooled.buffer().writeInt(ServerAPIListener.PayloadType.MOD_PLUGIN.ordinal()).writeInt(ServerAPIListener.ModPluginResponse.DOWNLOADED.ordinal()));
					} catch (Throwable e) {
						The5zigMod.logger.error("Could not enable downloaded plugin " + pluginName + "!", e);
						if (!file.delete()) {
							file.deleteOnExit();
						}
						downloadStatus = I18n.translate("plugin_request.error") + ": " + e.toString();
						The5zigMod.getVars().sendCustomPayload(PayloadUtils.API_CHANNEL,
								Unpooled.buffer().writeInt(ServerAPIListener.PayloadType.MOD_PLUGIN.ordinal()).writeInt(ServerAPIListener.ModPluginResponse.DOWNLOAD_FAILED.ordinal()));
					}
				}
			});
		}
		doneButton.setEnabled(true);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int y = getHeight() / 6;
		for (String line : The5zigMod.getVars().splitStringToWidth(I18n.translate("plugin_request.help", file.getAbsolutePath()), Math.min(300, getWidth() - 40))) {
			drawCenteredString(line, getWidth() / 2, y);
			y += 10;
		}
		y += 4;
		for (String line : The5zigMod.getVars().splitStringToWidth(customMessage, Math.min(300, getWidth() - 40))) {
			drawCenteredString(line, getWidth() / 2, y);
			y+= 10;
		}
		if (downloading) {
			if (downloadStatus != null) {
				drawCenteredString(downloadStatus, getWidth() / 2, y + 12);
			}
			y += 10;
			Gui.drawRect(getWidth() / 2 - 101, y + 13, getWidth() / 2 + 101, y + 26, 0xff333333);
			Gui.drawRect(getWidth() / 2 - 100, y + 14, getWidth() / 2 + 100, y + 25, 0xffffffff);
			Gui.drawRect(getWidth() / 2 - 100, y + 14, (getWidth() / 2 - 100) + downloadProgress * 200f, y + 25, 0xff888888);
			drawCenteredString((int) (downloadProgress * 100) + "%", getWidth() / 2, y + 16);
		}
	}

	@Override
	public String getTitleKey() {
		return "plugin_request.title";
	}
}
