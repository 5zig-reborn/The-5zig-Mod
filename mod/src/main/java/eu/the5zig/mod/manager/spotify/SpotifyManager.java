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

package eu.the5zig.mod.manager.spotify;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.chat.network.NetworkManager;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.util.Callback;
import eu.the5zig.util.io.http.HttpResponseCallback;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SpotifyManager {

	public static final int SPOTIFY_COLOR_ACCENT = 0xFF1ED760;
	public static final int SPOTIFY_COLOR_SECOND = 0xFF404040;
	public static final int SPOTIFY_COLOR_BACKGROUND = 0xFF282828;

	private static final int PORT_START = 6463;
	private static final int PORT_END = 6463;
	private static final int DEFAULT_TIMEOUT_MILLIS = 10000;
	private static final int POLL_TIME = 60;

	private static final String currentlyPlayingUrl = "https://api.spotify.com/v1/me/player/currently-playing";
	private static final String getTokenUrl = "https://secure.5zigreborn.eu/spotify?refresh=";

	private static final String characters = "abcdefghijklmnopqrstuvwxyz";
	private static final Map<String, String> originHeader = Collections.singletonMap("Origin", "https://open.spotify.com");

	private String authToken, refreshToken;

	private volatile boolean connected = false;
	private boolean connecting = false;
	private int reconnectTicks = 0;
	private SpotifyError disconnectError = null;
	private int port = PORT_START;
	private String oAuthToken;
	private String csrfToken;

	private SpotifyNewStatus status;
	private long lastStatusReceived;

	private final SpotifyWebAPI webAPI;

	public SpotifyManager() {
		webAPI = new SpotifyWebAPI();
		The5zigMod.getListener().registerListener(this);
		initTokens();
	}

	public SpotifyWebAPI getWebAPI() {
		return webAPI;
	}

	private void initTokens() {
		String refresh = The5zigMod.getConfig().getString("refresh_token");
		String auth = The5zigMod.getConfig().getString("spotify_auth_token");
		if(auth != null && !auth.isEmpty()) {
			authToken = auth;
			refreshToken = refresh;
			return;
		}
		if(refresh != null && !refresh.isEmpty())
			refreshToken(refresh);
	}

	public void setTokens(String tokens) {
		String[] data = tokens.split("/");
		authToken = data[0];
		refreshToken = data[1];
		The5zigMod.getConfig().get("spotify_auth_token").set(authToken);
		The5zigMod.getConfig().get("refresh_token").set(refreshToken);
		The5zigMod.getConfig().save();
	}

	private void refreshToken(String refreshToken) {
		The5zigMod.getAsyncExecutor().execute(() -> {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(getTokenUrl + refreshToken);
			request.addHeader("User-Agent", "5zig/" + Version.VERSION);
			try {
				HttpResponse response = client.execute(request);
				if(response.getStatusLine().getStatusCode() == 200) {
					authToken = IOUtils.toString(response.getEntity().getContent());
					The5zigMod.getConfig().get("spotify_auth_token").set(authToken);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("SPOTIFY") && authToken != null && !authToken.isEmpty()) {
			if (!connected && !connecting && --reconnectTicks <= 0 && NetworkManager.CLIENT_NIO_EVENTLOOP != null) {
				connecting = true;
				The5zigMod.getAsyncExecutor().execute(new Runnable() {
					@Override
					public void run() {
						connect();
					}
				});
			} else if (connected && System.currentTimeMillis() - lastStatusReceived > (POLL_TIME + 30) * 1000) {
				reconnect(0);
			}
		}
	}

	private void connect() {
		detectPort(new Callback<Integer>() {
			@Override
			public void call(Integer callback) {
				if (callback == null) {
					The5zigMod.logger.debug("Could not find any running Spotify instance!");
					reconnect(60);
				} else {
					port = callback;
					The5zigMod.logger.debug("Found running Spotify instance on port " + port + "!");

					doAuth(new Runnable() {
						@Override
						public void run() {
							lastStatusReceived = System.currentTimeMillis();
							connecting = false;
							disconnectError = null;
							connected = true;
							loadStatus();
						}
					});
				}
			}
		});
	}

	private void reconnect() {
		reconnect(10);
	}

	private void reconnect(int time) {
		if (disconnectError == null)
			disconnectError = SpotifyError.UNKNOWN;
		reconnectTicks = 20 * time;
		connecting = false;
		connected = false;
		csrfToken = null;
		oAuthToken = null;
		The5zigMod.logger.debug("Reconnecting to Spotify...");
	}

	public boolean isConnected() {
		return connected;
	}

	public SpotifyError getDisconnectError() {
		return disconnectError;
	}

	private void detectPort(Callback<Integer> callback) {
		checkPort(PORT_START, callback);
	}

	private void checkPort(final int port, final Callback<Integer> callback) {
		if(authToken == null) {
			callback.call(null);
			return;
		}
		makeRequest(currentlyPlayingUrl, new HashMap<>(), Collections.singletonMap("Authorization", "Bearer " + authToken), new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
				if (responseCode == 200) {
					callback.call(port);
				} else if (port < PORT_END) {
					checkPort(port + 1, callback);
				} else {
					callback.call(null);
				}
			}
		});
	}

	private void doAuth(final Runnable onDone) {
		onDone.run();
	}

	private void loadStatus() {
		Map<String, String> authParams = Collections.singletonMap("Authorization", "Bearer " + authToken);
		makeRequest(currentlyPlayingUrl, new HashMap<>(), authParams, new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
				if(responseCode == 401) {
					refreshToken(refreshToken);
					loadStatus();
				} else if (responseCode != 200 || throwable != null) {
					The5zigMod.logger.warn("Error while polling Spotify status!", throwable);
					setStatus(null);
				} else if (response != null) {
					JsonElement element = new JsonParser().parse(response);
					if (!parseError(element)) {
						try {
							setStatus(The5zigMod.gson.fromJson(element, SpotifyNewStatus.class));
						} catch (Exception e) {
							e.printStackTrace();
						}
						loadStatus();
					}
				}
			}
		}, DEFAULT_TIMEOUT_MILLIS + POLL_TIME * 1000);
	}

	private void play(String uri) {
		Map<String, String> authParams = createAuthParams();
		authParams.put("uri", uri);
		authParams.put("context", uri);
		makeRequest(getURL("/remote/play.json"), authParams, originHeader, new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
			}
		});
	}

	public SpotifyNewStatus getStatus() {
		return status;
	}

	public void setStatus(SpotifyNewStatus status) {
		long currentTimeMillis = System.currentTimeMillis();
		lastStatusReceived = currentTimeMillis;
		if (status != null) {
			status.setTimestamp(currentTimeMillis);
			if (status.getTrack() != null) {
				if ((this.status == null || (!status.getTrack().equals(this.status.getTrack())) || !this.status.isPlaying()) && status.isPlaying() ) {
					The5zigMod.logger.info(
							"[Spotify | Now Playing] \"" + status.getTrack().getName() + "\" by " + status.getTrack().getArtistsString());
				}
				resolveTrackImage(status.getTrack());
			}
		}
		this.status = status;
	}

	public void resolveTrackImage(final SpotifyNewTrack track) {
		try {
			webAPI.resolveTrackImage(track);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getURL(String path, int port) {
		return "http://" + generateLocalHostname() + ":" + port + path;
	}

	private String getURL(String path) {
		return getURL(path, port);
	}

	private String generateLocalHostname() {
		StringBuilder buffer = new StringBuilder(10);
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.append(characters.charAt(The5zigMod.random.nextInt(characters.length())));
		}
		//return buffer.toString() + ".spotilocal.com";
		return "127.0.0.1";
	}

	private Map<String, String> createAuthParams() {
		Map<String, String> params = Maps.newHashMap();
		params.put("oauth", oAuthToken);
		params.put("csrf", csrfToken);
		return params;
	}

	private void makeRequest(String url, Map<String, String> params, Map<String, String> headers, final HttpResponseCallback callback) {
		makeRequest(url, params, headers, callback, DEFAULT_TIMEOUT_MILLIS);
	}

	private void makeRequest(String url, Map<String, String> params, Map<String, String> headers, final HttpResponseCallback callback, int timeout) {
		if (!params.isEmpty()) {
			StringBuilder path = new StringBuilder();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				path.append("&").append(entry.getKey()).append("=").append(entry.getValue());
			}
			path.replace(0, 1, "?");
			url += path.toString();
		}
		SpotifyHttpClient.get(url, headers, timeout, true, NetworkManager.CLIENT_NIO_EVENTLOOP, callback);
	}

	private boolean parseError(JsonElement element) {
		if (element.isJsonObject() && element.getAsJsonObject().has("error") && element.getAsJsonObject().get("error").isJsonObject()) {
			JsonObject error = element.getAsJsonObject().get("error").getAsJsonObject();
			if (error.has("type") && error.get("type").isJsonPrimitive()) {
				int type = error.get("type").getAsInt();
				disconnectError = SpotifyError.byId(type);
				if (disconnectError != null) {
					The5zigMod.logger.debug("Could not connect to Spotify: " + disconnectError);
				}
			}
			reconnect();
			return true;
		}
		return false;
	}

}
