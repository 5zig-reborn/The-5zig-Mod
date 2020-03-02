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

package eu.the5zig.mod.util;

import eu.the5zig.mod.chat.network.NetworkManager;
import eu.the5zig.util.io.http.HttpClient;
import eu.the5zig.util.io.http.HttpResponseCallback;

public abstract class APIManager {

	private final String BASE_URL;

	public APIManager(String baseURL) {
		this.BASE_URL = baseURL;
	}

	/**
	 * Makes an async HTTP Request to {@link #BASE_URL}.
	 *
	 * @param endpoint The endpoint of the API.
	 * @param callback The Callback.
	 */
	protected void get(String endpoint, final HttpResponseCallback callback) throws Exception {
		if (NetworkManager.CLIENT_NIO_EVENTLOOP == null) {
			callback.call(null, 300, new RuntimeException("No NIO EventLoop"));
		} else {
			HttpClient.get(BASE_URL + endpoint, NetworkManager.CLIENT_NIO_EVENTLOOP, callback);
		}
	}

}
