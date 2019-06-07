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

package eu.the5zig.mod.manager.itunes;

import com.google.common.base.Charsets;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.util.Callback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ITunesMacDelegate extends ITunesDelegate {

	private String script;

	private boolean connecting = false;
	private int reconnectTicks = 0;

	private ITunesStatus status;
	private boolean connected = false;

	public ITunesMacDelegate() {
		try {
			script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("core/itunes/iTunes.applescript"));

			The5zigMod.getListener().registerListener(this);
		} catch (Throwable throwable) {
			The5zigMod.logger.error("Could not load iTunes AppleScript!", throwable);
		}
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("ITUNES")) {
			if (!connected && !connecting && --reconnectTicks <= 0) {
				connecting = true;
				fetchStatusInitial();
			}
		}
	}

	private void reconnect() {
		reconnectTicks = 200;
		connecting = false;
		connected = false;
	}

	private void fetchStatusInitial() {
		status(-1);
	}

	private void pollStatus() {
		status(30);
	}

	private void status(int maxTime) {
		executeScript("set max_time to " + maxTime + "\n" + script, new Callback<ITunesStatus>() {
			@Override
			public void call(ITunesStatus callback) {
				if (callback == null || !callback.isRunning()) {
					status = null;
					reconnect();
				} else {
					connected = true;

					if (callback.getTrack() != null && callback.getTrack().getArtwork() != null) {
						if (status != null && callback.getTrack().equals(status.getTrack())) {
							callback.getTrack().setImage(status.getTrack().getImage());
						} else {
							try {
								String artwork = callback.getTrack().getArtwork();
								byte[] imageData = hexStringToByteArray(artwork);
								BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
								BufferedImage image1 = new BufferedImage(128, 128, image.getType());
								Graphics graphics = image1.getGraphics();
								try {
									graphics.drawImage(image, 0, 0, image1.getWidth(), image1.getHeight(), null);
								} finally {
									graphics.dispose();
								}
								// Converting Image byte array into Base64 String
								ByteBuf localByteBuf1 = Unpooled.buffer();
								ImageIO.write(image1, "PNG", new ByteBufOutputStream(localByteBuf1));
								ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
								String imageDataString = localByteBuf2.toString(Charsets.UTF_8);

								callback.getTrack().setImage(imageDataString);
							} catch (Exception e) {
								The5zigMod.logger.warn("Could not load iTunes album artwork!", e);
							}
						}
					}
					status = callback;
					status.setServerTime(System.currentTimeMillis());

					pollStatus();
				}
			}
		});
	}

	private void executeScript(final String script, final Callback<ITunesStatus> callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				InputStream inputStream = null;
				BufferedReader reader = null;
				try {
					String[] args = {"osascript", "-e", script};
					Process process = Runtime.getRuntime().exec(args);
					inputStream = process.getInputStream();
					reader = new BufferedReader(new InputStreamReader(inputStream));
					String json = reader.readLine();
					callback.call(The5zigMod.gson.fromJson(json, ITunesStatus.class));
				} catch (Throwable throwable) {
					The5zigMod.logger.warn("Could not evaluate apple script", throwable);
					callback.call(null);
				} finally {
					IOUtils.closeQuietly(reader);
					IOUtils.closeQuietly(inputStream);
				}
			}
		}).start();
	}

	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public ITunesStatus getStatus() {
		return status;
	}

}
