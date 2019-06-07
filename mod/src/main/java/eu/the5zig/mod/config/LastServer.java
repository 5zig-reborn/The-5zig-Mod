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

package eu.the5zig.mod.config;

import eu.the5zig.mod.server.Server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class LastServer {

	private List<Server> lastServers;
	private final int MAX_SERVER_COUNT = 5;

	public Server getLastServer() {
		return lastServers == null || lastServers.isEmpty() ? null : lastServers.get(0);
	}

	public void setLastServer(Server lastServer) {
		if (this.lastServers == null)
			this.lastServers = new ArrayList<Server>();
		this.lastServers.remove(lastServer);
		this.lastServers.add(0, lastServer);
		while (this.lastServers.size() > MAX_SERVER_COUNT)
			this.lastServers.remove(MAX_SERVER_COUNT);
	}

}
