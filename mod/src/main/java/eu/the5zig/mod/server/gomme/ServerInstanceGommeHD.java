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

package eu.the5zig.mod.server.gomme;

import eu.the5zig.mod.server.ServerInstance;

import java.util.Locale;

public class ServerInstanceGommeHD extends ServerInstance {

	@Override
	public void registerListeners() {
		getGameListener().registerListener(new GommeHDListener());
		getGameListener().registerListener(new GommeHDPvPListener());
		getGameListener().registerListener(new GommeHDPvPFFAListener());
		getGameListener().registerListener(new GommeHDSurvivalGamesListener());
		getGameListener().registerListener(new GommeHDEnderGamesListener());
		getGameListener().registerListener(new GommeHDBedWarsListener());
		getGameListener().registerListener(new GommeHDSkyWarsListener());
		getGameListener().registerListener(new GommeHDRageModeListener());
	}

	@Override
	public String getName() {
		return "GommeHD.net";
	}

	@Override
	public String getConfigName() {
		return "gommehd";
	}

	@Override
	public boolean handleServer(String host, int port) {
		return host.toLowerCase(Locale.ROOT).endsWith("gommehd.net") || host.toLowerCase(Locale.ROOT).endsWith("gommehd.de") || host.toLowerCase(Locale.ROOT).endsWith("gommehd.tk");
	}

}
