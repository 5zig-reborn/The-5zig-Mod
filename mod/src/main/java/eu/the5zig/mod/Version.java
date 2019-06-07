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

package eu.the5zig.mod;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {

	public static final String VERSION;
	public static final String MCVERSION;
	public static final int LANGVERSION = 44;
	public static final int PROTOCOL = 5;
	public static final int APIVERSION = 4;

	static {
		String version = "DEV", mcVersion = "unknown";

		Properties properties = new Properties();
		InputStream in = null;
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream("the5zigmod.properties");
			properties.load(in);
			version = properties.getProperty("version");
			mcVersion = properties.getProperty("mcversion");
		} catch (Exception e) {
			System.err.println("Could not load version details of the 5zig mod! Corrupted file or development environment?!");
		} finally {
			VERSION = version;
			MCVERSION = mcVersion;
			if (in != null) {
				try {
					in.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

}