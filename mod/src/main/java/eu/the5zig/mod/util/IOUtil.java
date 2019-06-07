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

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class IOUtil {

	private static final Gson gson = new Gson();

	public static <T> T downloadToJson(String url, boolean post, Class<T> jsonClass) throws IOException {
		return gson.fromJson(download(url, post), jsonClass);
	}

	public static String download(String url, boolean post) throws IOException {
		InputStream inputStream = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod(post ? "POST" : "GET");
			connection.setConnectTimeout(20000);
			connection.setReadTimeout(60000);
			inputStream = connection.getInputStream();
			return IOUtils.toString(inputStream);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

}
