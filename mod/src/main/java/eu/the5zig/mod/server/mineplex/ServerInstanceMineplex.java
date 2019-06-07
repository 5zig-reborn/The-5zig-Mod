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

package eu.the5zig.mod.server.mineplex;

import eu.the5zig.mod.server.ServerInstance;

import java.util.Locale;

public class ServerInstanceMineplex extends ServerInstance {

	@Override
	public void registerListeners() {
		getGameListener().registerListener(new MineplexListener());
	}

	@Override
	public String getName() {
		return "Mineplex.com";
	}

	@Override
	public String getConfigName() {
		return "mineplex";
	}

	@Override
	public boolean handleServer(String host, int port) {
		return host.toLowerCase(Locale.ROOT).endsWith("mineplex.com") || host.toLowerCase(Locale.ROOT).endsWith("mineplex.eu") || host.toLowerCase(Locale.ROOT).endsWith("mineplex.us");
	}
}
