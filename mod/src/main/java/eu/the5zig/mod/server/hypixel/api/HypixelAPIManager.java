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

package eu.the5zig.mod.server.hypixel.api;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketUserSearch;
import eu.the5zig.mod.util.APIManager;
import eu.the5zig.util.Utils;
import eu.the5zig.util.io.FileUtils;
import eu.the5zig.util.io.http.HttpResponseCallback;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class HypixelAPIManager extends APIManager {

	private final int MAX_REQUESTS_PER_MINUTE = 60;

	private UUID key;

	private final List<Long> requests = Lists.newArrayList();

	public boolean keyRequested = false;
	private boolean friendsChecked = false;

	public HypixelAPIManager() {
		super("https://api.hypixel.net/");
		try {
			this.key = loadKey();
			The5zigMod.logger.debug("Loaded Hypixel API key: " + key);
		} catch (HypixelAPIMissingKeyException e) {
			keyRequested = The5zigMod.getConfig().getBool("autoHypixelApiKey");
			The5zigMod.logger.debug("Could not load Hypixel API-key!");
		}
	}

	/**
	 * Loads the API key from file.
	 *
	 * @return the API key.
	 * @throws HypixelAPIMissingKeyException if the API key file does not exist or is corrupted.
	 */
	private UUID loadKey() throws HypixelAPIMissingKeyException {
		File keyFile = new File(The5zigMod.getModDirectory(), "servers/hypixel/api_" + The5zigMod.getDataManager().getUniqueId() + ".key");
		if (!keyFile.exists())
			throw new HypixelAPIMissingKeyException();

		try {
			return UUID.fromString(IOUtils.toString(keyFile.toURI()));
		} catch (Exception e) {
			throw new HypixelAPIMissingKeyException(e);
		}
	}

	/**
	 * Sets a new API key and saves it to a file.
	 *
	 * @param key The new API key.
	 */
	public void setKey(UUID key) {
		this.key = key;
		try {
			File keyFile = new File(The5zigMod.getModDirectory(), "servers/hypixel/api_" + The5zigMod.getDataManager().getUniqueId() + ".key");
			if (keyFile.exists() && !keyFile.delete())
				throw new IOException("Could not delete existing Key File!");
			if (!keyFile.createNewFile())
				throw new IOException("Could not create new Key File!");

			FileUtils.writeFile(keyFile, key.toString());
		} catch (IOException e) {
			The5zigMod.logger.info("Could not save Key!", e);
		}
	}

	/**
	 * Makes an async HTTP Request to {@link #BASE_URL}.
	 *
	 * @param endpoint The endpoint of the API.
	 * @param callback The Callback.
	 * @throws HypixelAPIMissingKeyException      if no API key has been found.
	 * @throws HypixelAPITooManyRequestsException if more than {@link #MAX_REQUESTS_PER_MINUTE} requests have been executed.
	 * @throws HypixelAPIException                if there is a problem with setting up the connection.
	 */
	public void get(String endpoint, final HypixelAPICallback callback) throws HypixelAPIException {
		checkRequests();
		UUID key = getKey();
		try {
			get(endpoint + "&key=" + key, new HttpResponseCallback() {
				@Override
				public void call(String response, int responseCode, Throwable throwable) {
					HypixelAPIResponse apiResponse = new HypixelAPIResponse(response);
					if (throwable != null || responseCode != 200 || !apiResponse.isSuccess()) {
						if (response != null) {
							if ("Invalid API key!".equals(apiResponse.getCause())) {
								The5zigMod.logger.warn("Invalid Hypixel API key!");
								keyRequested = The5zigMod.getConfig().getBool("autoHypixelApiKey");
							}
							callback.call(new HypixelAPIResponseException(responseCode, apiResponse.getCause()));
						} else {
							callback.call(new HypixelAPIResponseException(responseCode, null));
						}
					} else {
						try {
							callback.call(apiResponse);
						} catch (Exception e) {
							callback.call(new HypixelAPIResponseException(e));
							The5zigMod.logger.warn("Failed to fetch hypixel api key", e);
						}
					}
				}
			});
		} catch (Exception e) {
			throw new HypixelAPIException(e);
		}
	}

	private void checkRequests() throws HypixelAPITooManyRequestsException {
		synchronized (requests) {
			for (Iterator<Long> iterator = requests.iterator(); iterator.hasNext(); ) {
				if (System.currentTimeMillis() - iterator.next() > 1000 * 60)
					iterator.remove();
			}
			if (requests.size() >= MAX_REQUESTS_PER_MINUTE)
				throw new HypixelAPITooManyRequestsException();
			requests.add(System.currentTimeMillis());
		}
	}

	/**
	 * @return the API key.
	 * @throws HypixelAPIMissingKeyException if no API key has been found.
	 */
	public UUID getKey() throws HypixelAPIMissingKeyException {
		if (!hasKey())
			throw new HypixelAPIMissingKeyException();
		return key;
	}

	public boolean hasKey() {
		return key != null;
	}

	public void checkFriendSuggestions() {
		if (friendsChecked)
			return;
		friendsChecked = true;
		try {
			The5zigMod.getHypixelAPIManager().get("friends?uuid=" + The5zigMod.getDataManager().getUniqueIdWithoutDashes(), new HypixelAPICallback() {
				@Override
				public void call(HypixelAPIResponse response) {
					List<User> friendList = Lists.newArrayList();

					JsonArray friends = response.data().getAsJsonArray("records");
					for (JsonElement element : friends) {
						JsonObject friend = element.getAsJsonObject();
						if (!friend.has("sender") || !friend.has("receiver") || !friend.has("uuidSender") || !friend.has("uuidReceiver")) {
							continue;
						}
						String sender = friend.get("sender").getAsString();
						UUID senderUUID = Utils.getUUID(friend.get("uuidSender").getAsString());
						String receiver = friend.get("receiver").getAsString();
						UUID receiverUUID = Utils.getUUID(friend.get("uuidReceiver").getAsString());
						if (The5zigMod.getDataManager().getUniqueId().equals(senderUUID)) {
							if (!The5zigMod.getFriendManager().isFriend(receiverUUID) && !The5zigMod.getFriendManager().isSuggested(receiverUUID)) {
								friendList.add(new User(receiver, receiverUUID));
							}
						} else if (The5zigMod.getDataManager().getUniqueId().equals(receiverUUID)) {
							if (!The5zigMod.getFriendManager().isFriend(senderUUID) && !The5zigMod.getFriendManager().isSuggested(senderUUID)) {
								friendList.add(new User(sender, senderUUID));
							}
						}
					}

					if (!friendList.isEmpty()) {
						The5zigMod.getNetworkManager().sendPacket(new PacketUserSearch(PacketUserSearch.Type.FRIEND_LIST, friendList.toArray(new User[friendList.size()])));
					}
				}

				@Override
				public void call(HypixelAPIResponseException e) {
					The5zigMod.logger.error("Could not fetch Hypixel friends", e);
				}
			});
		} catch (Exception e) {
			The5zigMod.logger.error("Could not fetch Hypixel friends", e);
		}
	}
}
