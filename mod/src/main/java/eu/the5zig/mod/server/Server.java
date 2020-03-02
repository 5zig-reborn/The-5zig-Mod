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

package eu.the5zig.mod.server;

import eu.the5zig.mod.The5zigMod;

public class Server implements IServer {

	private String host;
	private int port;
	private long time;

	private transient boolean renderPotionEffects = true;
	private transient boolean renderArmor = true;
	private transient boolean renderPotionIndicator = true;
	private transient boolean renderSaturation = true;
	private transient boolean autoReconnect = true;

	// Default constructor for gson deserialization. Without this one, all server setting fields won't be initialized.
	public Server() {
	}

	public Server(String host, int port) {
		this.host = host;
		this.port = port;
		this.time = System.currentTimeMillis();
		The5zigMod.getLastServerConfig().getConfigInstance().setLastServer(this);
		save();
	}

	private void save() {
		The5zigMod.getLastServerConfig().saveConfig();
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public long getLastTimeJoined() {
		return time;
	}

	@Override
	public boolean isRenderPotionEffects() {
		return renderPotionEffects;
	}

	@Override
	public void setRenderPotionEffects(boolean renderPotionEffects) {
		this.renderPotionEffects = renderPotionEffects;
	}

	@Override
	public boolean isRenderArmor() {
		return renderArmor;
	}

	@Override
	public void setRenderArmor(boolean renderArmor) {
		this.renderArmor = renderArmor;
	}

	@Override
	public boolean isRenderPotionIndicator() {
		return renderPotionIndicator;
	}

	@Override
	public void setRenderPotionIndicator(boolean renderPotionIndicator) {
		this.renderPotionIndicator = renderPotionIndicator;
	}

	@Override
	public boolean isRenderSaturation() {
		return renderSaturation;
	}

	@Override
	public void setRenderSaturation(boolean renderSaturation) {
		this.renderSaturation = renderSaturation;
	}

	@Override
	public boolean isAutoReconnecting() {
		return autoReconnect;
	}

	@Override
	public void setAutoReconnecting(boolean autoReconnecting) {
		this.autoReconnect = autoReconnecting;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Server server = (Server) o;

		if (port != server.port)
			return false;
		return host.equals(server.host);

	}

	@Override
	public String toString() {
		return "Server{" +
				"host='" + host + '\'' +
				", port=" + port +
				'}';
	}
}