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

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.NetworkManager;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.util.Callback;
import eu.the5zig.util.io.http.HttpResponseCallback;

import java.util.Collections;
import java.util.Map;

public class SpotifyManager {

	public static final int SPOTIFY_COLOR_ACCENT = 0xFF1ED760;
	public static final int SPOTIFY_COLOR_SECOND = 0xFF404040;
	public static final int SPOTIFY_COLOR_BACKGROUND = 0xFF282828;

	private static final int PORT_START = 6463;
	private static final int PORT_END = 6463;
	private static final int DEFAULT_TIMEOUT_MILLIS = 10000;
	private static final int POLL_TIME = 60;

	private static final String characters = "abcdefghijklmnopqrstuvwxyz";
	private static final Map<String, String> originHeader = Collections.singletonMap("Origin", "https://open.spotify.com");

	private volatile boolean connected = false;
	private boolean connecting = false;
	private int reconnectTicks = 0;
	private SpotifyError disconnectError = null;
	private int port = PORT_START;
	private String oAuthToken;
	private String csrfToken;

	private SpotifyStatus status;
	private long lastStatusReceived;

	private final SpotifyWebAPI webAPI;

	public SpotifyManager() {
		webAPI = new SpotifyWebAPI();
		The5zigMod.getListener().registerListener(this);
	}

	public SpotifyWebAPI getWebAPI() {
		return webAPI;
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("SPOTIFY")) {
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
							loadInitialStatus();
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
		makeRequest(getURL("/service/version.json?service=remote", port), Collections.singletonMap("service", "remote"), originHeader, new HttpResponseCallback() {
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
		loadOAuthToken(new Runnable() {
			@Override
			public void run() {
				loadCsrfToken(onDone);
			}
		});
	}

	private void loadOAuthToken(final Runnable runnable) {
		if (oAuthToken != null) {
			runnable.run();
		} else {
			makeRequest("https://open.spotify.com/token", Collections.<String, String>emptyMap(), Collections.<String, String>emptyMap(), new HttpResponseCallback() {
				@Override
				public void call(String response, int responseCode, Throwable throwable) {
					if (throwable != null) {
						The5zigMod.logger.error("Could not load Spotify OAuth-Token!", throwable);
						reconnect();
					} else if (response != null) {
						JsonElement element = new JsonParser().parse(response);
						if (!parseError(element)) {
							if (element.isJsonObject() && element.getAsJsonObject().has("t") && element.getAsJsonObject().get("t").isJsonPrimitive()) {
								oAuthToken = element.getAsJsonObject().get("t").getAsString();
								oAuthToken = "BQAsFd3_YqXqoTUWZ7H_phBKIBZhITn5viQ-_jqsxeCzJLWhfh7tOMKWqj0L4FoLHxbwYtWUqO5kea8__4c";
								runnable.run();
							} else {
								The5zigMod.logger.error("Could not load Spotify OAuth-Token!");
								reconnect();
							}
						}
					}
				}
			});
		}
	}

	private void loadCsrfToken(final Runnable runnable) {
		makeRequest(getURL("/simplecsrf/token.json"), Collections.<String, String>emptyMap(), originHeader, new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
				if (throwable != null) {
					The5zigMod.logger.error("Could not load Spotify Csrf-Token!", throwable);
					reconnect();
				} else if (response != null) {
					JsonElement element = new JsonParser().parse(response);
					if (!parseError(element)) {
						if (element.isJsonObject() && element.getAsJsonObject().has("token") && element.getAsJsonObject().get("token").isJsonPrimitive()) {
							csrfToken = element.getAsJsonObject().get("token").getAsString();
							runnable.run();
						} else {
							The5zigMod.logger.error("Could not load Spotify Csrf-Token: " + response);
							reconnect();
						}
					}
				}
			}
		});
	}

	private void loadInitialStatus() {
		Map<String, String> authParams = createAuthParams();
		makeRequest(getURL("/remote/status.json"), authParams, originHeader, new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
				if (throwable != null) {
					The5zigMod.logger.error("Could not load Spotify Status!", throwable);
					reconnect();
				} else if (response != null) {
					JsonElement element = new JsonParser().parse(response);
					if (!parseError(element)) {
						setStatus(The5zigMod.gson.fromJson(element, SpotifyStatus.class));
						pollStatus();
					}
				}
			}
		});
	}

	private void pollStatus() {
		Map<String, String> authParams = createAuthParams();
		authParams.put("returnafter", String.valueOf(POLL_TIME));
		authParams.put("returnon", "login,logout,play,pause,error,ap");
		makeRequest(getURL("/remote/status.json"), authParams, originHeader, new HttpResponseCallback() {
			@Override
			public void call(String response, int responseCode, Throwable throwable) {
				if (throwable != null) {
					The5zigMod.logger.warn("Error while polling Spotify status!", throwable);
					setStatus(null);
				} else if (response != null) {
					JsonElement element = new JsonParser().parse(response);
					if (!parseError(element)) {
						try {
							setStatus(The5zigMod.gson.fromJson(element, SpotifyStatus.class));
						} catch (Exception e) {
							e.printStackTrace();
						}
						pollStatus();
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

	public SpotifyStatus getStatus() {
		return status;
	}

	public void setStatus(SpotifyStatus status) {
		long currentTimeMillis = System.currentTimeMillis();
		lastStatusReceived = currentTimeMillis;
		if (status != null) {
			status.setServerTime(currentTimeMillis);
			if (status.getTrack() != null && status.getTrack().hasTrackInformation()) {
				if ((this.status == null || (!status.getTrack().equals(this.status.getTrack())) || !this.status.isPlaying()) && status.isPlaying() ) {
					The5zigMod.logger.info(
							"[Spotify | Now Playing] \"" + status.getTrack().getTrackInformation().getName() + "\" by " + status.getTrack().getArtistInformation().getName());
				}
				resolveTrackImage(status.getTrack());
			}
		}
		this.status = status;
	}

	public void resolveTrackImage(final SpotifyTrack track) {
		webAPI.resolveTrackImage(track);
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
