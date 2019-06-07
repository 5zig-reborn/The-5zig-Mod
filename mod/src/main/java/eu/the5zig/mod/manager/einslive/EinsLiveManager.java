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

package eu.the5zig.mod.manager.einslive;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class EinsLiveManager {

	public static final int EINSLIVE_COLOR_BACKGROUND = 0xFFFF0099;

	private static final String TRACK_PATH = "http://www.wdr.de/radio/radiotext/streamtitle_1live.txt";
	private static final String ITUNES_SEARCH_PATH = "https://itunes.apple.com/search";

	private static final Cache<String, ITunesSearchResultEntry> ITUNES_SEARCH_RESULT_CACHE = CacheBuilder.newBuilder().maximumSize(500).build();

	private int lastUpdated = 150;
	private String lastStatus;
	private ITunesSearchResultEntry currentTrack;

	public EinsLiveManager() {
		The5zigMod.getListener().registerListener(this);
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("EINSLIVE")) {
			if (++lastUpdated >= 100) {
				lastUpdated = 0;
				The5zigMod.getAsyncExecutor().execute(new Runnable() {
					@Override
					public void run() {
						fetchCurrentTrack();
					}
				});
			}
		}
	}

	private void fetchCurrentTrack() {
		InputStream inputStream = null;
		BufferedReader reader = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(TRACK_PATH).openConnection();
			connection.setConnectTimeout(2000);
			connection.setReadTimeout(5000);
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				The5zigMod.logger.warn("Received response code " + responseCode + " when fetching latest 1live track!");
				return;
			}
			inputStream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream, Charsets.ISO_8859_1));
			String text = reader.readLine();
			if (lastStatus == null || !lastStatus.equals(text)) {
				currentTrack = doITunesSearch(text);
				lastStatus = text;
				if (currentTrack != null && currentTrack.getTrackName() != null && currentTrack.getArtistName() != null) {
					The5zigMod.logger.info("[1Live | Now Playing] \"" + currentTrack.getTrackName() + "\" by " + currentTrack.getArtistName());
				}
			}
		} catch (Exception e) {
			The5zigMod.logger.warn("Could not fetch latest 1live track!", e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(inputStream);
		}
	}

	private ITunesSearchResultEntry doITunesSearch(String text) throws Exception {
		ITunesSearchResultEntry optional = ITUNES_SEARCH_RESULT_CACHE.getIfPresent(text);
		if (optional != null) {
			return optional;
		}
		InputStream inputStream = null;
		BufferedReader reader = null;
		HttpURLConnection connection = (HttpURLConnection) new URL(ITUNES_SEARCH_PATH + "?term=" + URLEncoder.encode(text, "UTF-8") + "&media=music&limit=1&version=2").openConnection();
		connection.setConnectTimeout(2000);
		connection.setReadTimeout(5000);
		int responseCode = connection.getResponseCode();
		if (responseCode != 200) {
			return null;
		}
		try {
			inputStream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			StringBuilder stringBuilder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			String json = stringBuilder.toString();
			ITunesSearchResult searchResult = The5zigMod.gson.fromJson(json, ITunesSearchResult.class);
			ITunesSearchResultEntry entry;
			if (searchResult.getResultCount() > 0) {
				entry = searchResult.getResults().get(0);
				resolveImage(entry);
			} else {
				entry = new ITunesSearchResultEntry(text);
			}
			ITUNES_SEARCH_RESULT_CACHE.put(text, entry);
			return entry;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void resolveImage(ITunesSearchResultEntry searchEntry) {
		try {
			URL url = new URL(searchEntry.getArtworkUrl100());
			BufferedImage image = ImageIO.read(url);
			BufferedImage image1 = new BufferedImage(60, 60, image.getType());
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

			searchEntry.setImage(imageDataString);
		} catch (Exception e) {
			The5zigMod.logger.warn("Could not resolve image for track " + searchEntry.getTrackName() + "!");
		}
	}

	public ITunesSearchResultEntry getCurrentTrack() {
		return currentTrack;
	}
}
