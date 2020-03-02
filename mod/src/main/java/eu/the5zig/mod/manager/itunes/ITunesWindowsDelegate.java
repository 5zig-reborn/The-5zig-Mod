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

package eu.the5zig.mod.manager.itunes;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.manager.itunes.com.*;
import eu.the5zig.mod.util.NativeLibrary;
import eu.the5zig.util.Container;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ITunesWindowsDelegate extends ITunesDelegate {

	private static final String tasksCmd = System.getenv("windir") + "/system32/tasklist.exe /nh /fi \"Imagename eq itunes.exe\"";

	private IiTunes iTunes;
	private ITunesStatus status;
	private IiTunesEventHandler eventHandler = new IiTunesEventHandler() {
		@Override
		public void onPlayerPlay(IITTrack track) {
			updateStatus(track);
		}

		@Override
		public void onPlayerPlayingTrackChanged(IITTrack track) {
			updateStatus(track);
		}

		@Override
		public void onPlayerStop(IITTrack track) {
			updateStatus(track);
		}

		@Override
		public void onQuit() {
			scheduleReconnect();
		}
	};

	private final Cache<Integer, Container<String>> artworkCache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).maximumSize(100).build();
	private final File artworkFile = new File(The5zigMod.getModDirectory(), "itunes_artwork");
	private final Object ARTWORK_LOCK = new Object();

	private boolean connected;
	private boolean connecting;
	private int reconnectTicks;
	private int updateTicks;

	public ITunesWindowsDelegate() {
		try {
			String path = NativeLibrary.load("jacob-x${arch}", NativeLibrary.NativeOS.WINDOWS);
			System.setProperty("jacob.dll.path", path);

			The5zigMod.getListener().registerListener(this);
		} catch (Throwable e) {
			The5zigMod.logger.error("Failed to load natives for Windows COM!", e);
		}
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("ITUNES")) {
			if (!connected && !connecting && --reconnectTicks <= 0) {
				connecting = true;
				The5zigMod.getAsyncExecutor().execute(new Runnable() {
					@Override
					public void run() {
						if (isTunesRunning()) {
							connecting = false;
							iTunes = new IiTunes();
							iTunes.addEventHandler(eventHandler);
							connected = true;
							The5zigMod.logger.debug("Connected to iTunes COM Interface!");
						} else {
							scheduleReconnect();
						}
					}
				});
			}
			if (connected && --updateTicks <= 0) {
				updateTicks = 100;
				The5zigMod.getAsyncExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							updateStatus(iTunes.getCurrentTrack());
						} catch (Throwable throwable) {
							The5zigMod.logger.error("Could not update iTunes status", throwable);
							scheduleReconnect();
						}
					}
				});
			}
		}
	}

	private void scheduleReconnect() {
		status = null;
		iTunes = null;
		connected = false;
		connecting = false;
		reconnectTicks = 100;
		updateTicks = 0;
	}

	private void updateStatus(final IITTrack track) {
		if (track == null) {
			status = null;
		} else {
			try {
				final int trackID = track.getTrackID();
				final ITunesTrack iTunesTrack = new ITunesTrack(String.valueOf(trackID), track.getName(), track.getArtist(), track.getDuration());

				Container<String> artworkContainer = artworkCache.getIfPresent(trackID);
				if (artworkContainer == null) {
					IITArtworkCollection artworkCollection = track.getArtwork();
					final IITArtwork artwork = artworkCollection.getItem(1);
					if (artwork != null) {
						synchronized (ARTWORK_LOCK) {
							loadArtwork(iTunesTrack, trackID, artwork);
						}
					}
				} else {
					iTunesTrack.setImage(artworkContainer.getValue());
				}

				ITunesStatus newStatus = new ITunesStatus(iTunes.getPlayerState() == IITPlayerState.ITPlayerStatePlaying, iTunesTrack, iTunes.getPlayerPosition());
				if ((this.status == null || !newStatus.getTrack().equals(this.status.getTrack()) || !this.status.isPlaying()) && newStatus.isPlaying()) {
					The5zigMod.logger.info("[iTunes | Now Playing] \"" + newStatus.getTrack().getName() + "\" by " + newStatus.getTrack().getArtist());
				}
				status = newStatus;
			} catch (Throwable throwable) {
				The5zigMod.logger.error("Could not update iTunes status", throwable);
				scheduleReconnect();
			}
		}
	}

	private void loadArtwork(ITunesTrack iTunesTrack, int trackID, IITArtwork artwork) {
			artwork.SaveArtworkToFile(artworkFile.getAbsolutePath());
			String imageFormat;
			switch (artwork.getFormat()) {
				case ITArtworkFormatPNG:
					imageFormat = "PNG";
					break;
				case ITArtworkFormatJPEG:
					imageFormat = "JPEG";
					break;
				case ITArtworkFormatBMP:
					imageFormat = "BMP";
					break;
				default:
					imageFormat = "PNG";
					break;
			}

			try {
				BufferedImage image = ImageIO.read(artworkFile);
				BufferedImage image1 = new BufferedImage(128, 128, image.getType());

				Graphics graphics = image1.getGraphics();
				try {
					graphics.drawImage(image, 0, 0, image1.getWidth(), image1.getHeight(), null);
				} finally {
					graphics.dispose();
				}

				// Converting Image byte array into Base64 String
				ByteBuf localByteBuf1 = Unpooled.buffer();
				ImageIO.write(image1, imageFormat, new ByteBufOutputStream(localByteBuf1));
				ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
				String imageDataString = localByteBuf2.toString(Charsets.UTF_8);

				artworkCache.put(trackID, new Container<String>(imageDataString));
				iTunesTrack.setImage(imageDataString);
			} catch (IOException e) {
				The5zigMod.logger.warn("Could not load iTunes artwork!", e);
			} finally {
				FileUtils.deleteQuietly(artworkFile);
			}
	}

	private boolean isTunesRunning() {
		BufferedReader input = null;
		try {
			Process p = Runtime.getRuntime().exec(tasksCmd);
			input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			List<String> processes = Lists.newArrayList();
			String line;
			while ((line = input.readLine()) != null) {
				processes.add(line);
			}

			for (String process : processes) {
				if (process.toLowerCase(Locale.ROOT).contains("itunes.exe")) {
					return true;
				}
			}
		} catch (IOException e) {
			The5zigMod.logger.error("Could not check whether itunes is currently running!", e);
		} finally {
			IOUtils.closeQuietly(input);
		}
		return false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public ITunesStatus getStatus() {
		return status;
	}

	public void release() {
		if (iTunes != null) {
			iTunes.release();
		}
	}

}
