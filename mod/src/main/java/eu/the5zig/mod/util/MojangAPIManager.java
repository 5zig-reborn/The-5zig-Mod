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

package eu.the5zig.mod.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.util.ExceptionCallback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.io.http.HttpResponseCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MojangAPIManager extends APIManager {

	private final Map<String, List<NameHistory>> uuidToNameHistory = Maps.newConcurrentMap();
	private final Map<String, String> nameToUuid = Maps.newConcurrentMap();

	public MojangAPIManager() {
		super("");
		Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("Mojang Status").build()).scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (!The5zigMod.getConfig().getBool("mojangStatus")) {
					return;
				}
				try {
					get("https://status.mojang.com/check", new HttpResponseCallback() {
						@Override
						public void call(String response, int responseCode, Throwable throwable) {
							if (response == null) {
								return;
							}
							if (responseCode != 200) {
								return;
							}
							JsonElement element = new JsonParser().parse(response);
							if (!element.isJsonArray()) {
								return;
							}
							JsonArray array = element.getAsJsonArray();
							for (MojangService mojangService : MojangService.values()) {
								if (mojangService.getEndpoints().length == 0) {
									continue;
								}
								boolean found = false;
								for (JsonElement jsonElement : array) {
									if (jsonElement.isJsonObject()) {
										JsonObject statusObject = jsonElement.getAsJsonObject();
										for (String endpoint : mojangService.getEndpoints()) {
											if (statusObject.has(endpoint)) {
												found = true;
												MojangStatus status = MojangStatus.byName(statusObject.get(endpoint).getAsString());
												mojangService.setStatus(status == null ? MojangStatus.UNAVAILABLE : status);
											}
										}
									}
								}
								if (!found) {
									mojangService.setStatus(MojangStatus.UNAVAILABLE);
								}
							}
						}
					});
				} catch (Exception ignored) {
				}
			}
		}, 5, 30, TimeUnit.SECONDS);
	}

	public void resolveNameHistory(String uuid, final ExceptionCallback<List<NameHistory>> callback) {
		uuid = uuid.toLowerCase(Locale.ROOT);
		if (uuidToNameHistory.containsKey(uuid)) {
			callback.call(uuidToNameHistory.get(uuid), null);
			return;
		}

		try {
			final String finalUuid = uuid;
			get("https://api.mojang.com/user/profiles/" + uuid + "/names", new HttpResponseCallback() {
				@Override
				public void call(String response, int responseCode, Throwable throwable) {
					if (throwable != null) {
						callback.call(null, throwable);
						return;
					}
					JsonParser parser = new JsonParser();
					JsonElement parse = parser.parse(response);
					if (!parse.isJsonArray()) {
						callback.call(null, new RuntimeException("Illegal Response Received!"));
						return;
					}
					List<NameHistory> nameHistories = Lists.newArrayList();
					for (JsonElement jsonElement : parse.getAsJsonArray()) {
						nameHistories.add(The5zigMod.gson.fromJson(jsonElement, NameHistory.class));
					}
					Collections.reverse(nameHistories);
					uuidToNameHistory.put(finalUuid, nameHistories);
					callback.call(nameHistories, null);
				}
			});
		} catch (Exception e) {
			callback.call(null, e);
		}
	}

	public static class NameHistory implements Row {

		public String name;
		public long changedToAt;

		@Override
		public void draw(int x, int y) {
			if (changedToAt != 0) {
				The5zigMod.getVars().drawString(name + " " + ChatColor.GRAY + ChatColor.ITALIC + Utils.convertToDateAndTime(changedToAt) + ChatColor.RESET, x + 2, y + 2);
			} else {
				The5zigMod.getVars().drawString(name, x + 2, y + 2);
			}
		}

		@Override
		public int getLineHeight() {
			return 18;
		}
	}

	public void resolveUUID(String username, final ExceptionCallback<String> callback) {
		username = username.toLowerCase(Locale.ROOT);
		if (nameToUuid.containsKey(username)) {
			callback.call(nameToUuid.get(username), null);
			return;
		}

		try {
			final String finalUsername = username;
			get("https://api.mojang.com/users/profiles/minecraft/" + username, new HttpResponseCallback() {
				@Override
				public void call(String response, int responseCode, Throwable throwable) {
					if (throwable != null) {
						callback.call(null, throwable);
						return;
					}
					if (Strings.isNullOrEmpty(response)) {
						callback.call(null, null);
					} else {
						JsonParser parser = new JsonParser();
						JsonElement parse = parser.parse(response);
						User user = The5zigMod.gson.fromJson(parse, User.class);
						nameToUuid.put(finalUsername, user.id);
						callback.call(user.id, null);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			callback.call(null, e);
		}
	}

	private class User {
		private String id, name;
	}

	private enum MojangService {

		SESSION("session.minecraft.net", "sessionserver.mojang.com"), ACCOUNT("account.mojang.com"), AUTHENTICATION("authserver.mojang.com"), SKINS(
				"textures.minecraft.net");

		private final String[] endpoints;
		private MojangStatus status = MojangStatus.NO_ISSUES;
		private int count;

		MojangService(String... endpoints) {
			this.endpoints = endpoints;
		}

		public String[] getEndpoints() {
			return endpoints;
		}

		public MojangStatus getStatus() {
			return status;
		}

		public void setStatus(MojangStatus status) {
			if (status == this.status) {
				return;
			}
			if (++count < 3) {
				return;
			}
			count = 0;
			this.status = status;
			The5zigMod.getOverlayMessage().displayMessageAndSplit(
					ChatColor.YELLOW + I18n.translate("mojang.service." + name().toLowerCase(Locale.ROOT)) + " " + I18n.translate("mojang.status." + status.name().toLowerCase(Locale.ROOT)));
		}
	}

	private enum MojangStatus {

		NO_ISSUES("green"), SOME_ISSUES("yellow"), UNAVAILABLE("red");


		private final String status;

		MojangStatus(String status) {
			this.status = status;
		}

		public static MojangStatus byName(String name) {
			for (MojangStatus mojangStatus : values()) {
				if (mojangStatus.status.equals(name)) {
					return mojangStatus;
				}
			}
			return null;
		}
	}
}
