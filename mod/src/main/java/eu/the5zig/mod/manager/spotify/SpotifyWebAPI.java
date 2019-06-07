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

package eu.the5zig.mod.manager.spotify;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.NetworkManager;
import eu.the5zig.util.Callback;
import eu.the5zig.util.io.http.HttpResponseCallback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpotifyWebAPI {

	private static final String BASE_URL = "https://api.spotify.com/v1";
	private static final int TIMEOUT = 10000;
	private static final Pattern JS_PATTERN = Pattern.compile("\\s+Spotify\\.Entity = (.*);");

	private final Cache<String, String> TRACK_IMAGE_LOOKUP = CacheBuilder.newBuilder().maximumSize(500).build();
	private final Cache<String, SpotifyTrack> SPOTIFY_SEARCH_RESULTS = CacheBuilder.newBuilder().maximumSize(500).build();

	public void resolveTrackImage(final SpotifyTrack track) {
		final String id = track.getTrackInformation().getId();
		if (id != null) {
			String optional = TRACK_IMAGE_LOOKUP.getIfPresent(id);
			if (optional != null) {
				track.setImage(optional);
			} else {
				try {
					String response = IOUtils.toString(new URL(track.getTrackInformation().getLocation().getOg()));
					for (String s : response.split("\n")) {
						Matcher matcher = JS_PATTERN.matcher(s);
						if (matcher.matches()) {
							if (!resolveTrackImage(track, id, new JsonParser().parse(matcher.group(1)))) {
								The5zigMod.logger.warn("Could not resolve image for track " + id + "!");
							}
							break;
						}
					}
				} catch (Exception e) {
					The5zigMod.logger.warn("Could not resolve image for track " + id + "!", e);
				}
			}
		}
	}

	public void searchSpotify(final String text, final Callback<SpotifyTrack> callback) {
		SpotifyTrack optional = SPOTIFY_SEARCH_RESULTS.getIfPresent(text);
		if (optional != null) {
			callback.call(optional);
			return;
		}
		try {
			SpotifyHttpClient.get(BASE_URL + "/search?q=" + URLEncoder.encode(text, "UTF-8") + "&type=track&limit=1", Collections.<String, String>emptyMap(), 5000, false,
					NetworkManager.CLIENT_NIO_EVENTLOOP, new HttpResponseCallback() {
						@Override
						public void call(String response, int responseCode, Throwable throwable) {
							if (throwable != null) {
								The5zigMod.logger.warn("Could not execute search query for text \"" + text + "\"!", throwable);
							} else if (response != null) {
								JsonElement element = new JsonParser().parse(response);
								if (element.isJsonObject() && element.getAsJsonObject().has("tracks") && element.getAsJsonObject().get("tracks").isJsonObject()) {
									JsonObject tracksObject = element.getAsJsonObject().getAsJsonObject("tracks");
									if (tracksObject.has("items") && tracksObject.get("items").isJsonArray()) {
										JsonArray itemsArray = tracksObject.getAsJsonArray("items");
										if (itemsArray.size() == 0) {
											SpotifyTrack track = new SpotifyTrack(new SpotifyResource(text, null, null), null, null, 0, SpotifyTrack.Type.ad);
											SPOTIFY_SEARCH_RESULTS.put(text, track);
											callback.call(track);
											return;
										}
										JsonElement itemElement = itemsArray.get(0);
										if (itemElement.isJsonObject()) {
											JsonObject itemObject = itemElement.getAsJsonObject();
											if (itemObject.has("id") && itemObject.get("id").isJsonPrimitive() && itemObject.has("name") && itemObject.get("name").isJsonPrimitive() &&
													itemObject.has("duration_ms") && itemObject.get("duration_ms").isJsonPrimitive() && itemObject.get("duration_ms").getAsJsonPrimitive()
													.isNumber() && itemObject.has("artists") && itemObject.get("artists").isJsonArray() && itemObject.getAsJsonArray("artists").size() > 0 &&
													itemObject.getAsJsonArray("artists").get(0).isJsonObject() && itemObject.getAsJsonArray("artists").get(0).getAsJsonObject().has("name") &&
													itemObject.has("album") && itemObject.get("album").isJsonObject() && itemObject.getAsJsonObject("album").has("name") &&
													itemObject.getAsJsonObject("album").get("name").isJsonPrimitive()) {
												String trackId = itemObject.getAsJsonPrimitive("id").getAsString();
												String trackName = itemObject.getAsJsonPrimitive("name").getAsString();
												long durationMillis = itemObject.getAsJsonPrimitive("duration_ms").getAsLong();
												String artistName = itemObject.getAsJsonArray("artists").get(0).getAsJsonObject().getAsJsonPrimitive("name").getAsString();
												String albumName = itemObject.getAsJsonObject("album").getAsJsonPrimitive("name").getAsString();
												SpotifyTrack track = new SpotifyTrack(new SpotifyResource(trackName, "spotify:track:" + trackId, null),
														new SpotifyResource(artistName, null, null), new SpotifyResource(albumName, null, null), (int) (durationMillis / 1000),
														SpotifyTrack.Type.normal);
												SPOTIFY_SEARCH_RESULTS.put(text, track);
												resolveTrackImage(track, trackId, itemObject);
												callback.call(track);
											}
										}
									}
								}
							}
						}
					});
		} catch (UnsupportedEncodingException e) {
			The5zigMod.logger.warn("Could not encode \"" + text + "\" to URL!", e);
		}
	}

	private boolean resolveTrackImage(final SpotifyTrack track, String trackId, JsonElement element) {
		if (element.isJsonObject() && element.getAsJsonObject().has("album") && element.getAsJsonObject().get("album").isJsonObject()) {
			JsonObject album = element.getAsJsonObject().get("album").getAsJsonObject();
			if (album.has("images") && album.get("images").isJsonArray()) {
				JsonArray images = album.get("images").getAsJsonArray();
				int smallestSize = Integer.MAX_VALUE;
				String smallestURL = null;
				for (JsonElement imageElement : images) {
					if (!imageElement.isJsonObject()) {
						continue;
					}
					JsonObject image = imageElement.getAsJsonObject();
					if (!image.has("height") || !image.get("height").isJsonPrimitive() || !image.get("height").getAsJsonPrimitive().isNumber() || !image.has("width") || !image.get("width")
							.isJsonPrimitive() || !image.get("width").getAsJsonPrimitive().isNumber() || !image.has("url") || !image.get("url").isJsonPrimitive()) {
						continue;
					}
					int height = image.get("height").getAsInt();
					int width = image.get("width").getAsInt();
					String url = image.get("url").getAsString();
					if (width == 300 && height == 300) {
						smallestURL = url;
						break;
					}
					if (width < smallestSize || height < smallestSize) {
						smallestSize = Math.max(width, height);
						smallestURL = url;
					}
				}
				if (smallestURL != null) {
					try {
						URL url = new URL(smallestURL);
						BufferedImage image = ImageIO.read(url);
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

						track.setImage(imageDataString);
						TRACK_IMAGE_LOOKUP.put(trackId, imageDataString);
					} catch (Exception e) {
						The5zigMod.logger.warn("Could not resolve image for track " + trackId + "!");
					}
					return true;
				}
			}
		}
		return false;
	}

}
