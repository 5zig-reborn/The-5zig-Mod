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

package eu.the5zig.mod.listener;

import io.netty.buffer.ByteBuf;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Listener implements IListener {

	@Override
	public void onTick() {
	}

	@Override
	public void onKeyPress(int code) {
	}

	@Override
	public void onServerJoin(String host, int port) {
	}

	@Override
	public void onServerConnect() {
	}

	@Override
	public void onServerDisconnect() {
	}

	@Override
	public void onPayloadReceive(String channel, ByteBuf packetData) {
	}

	/**
	 * Handles all received chat messages.
	 *
	 * @param message The message that has been received.
	 * @return true, if the message should be ignored.
	 */
	@Override
	public boolean onServerChat(String message) {
		return false;
	}

	@Override
	public boolean onServerChat(String message, Object chatComponent) {
		return false;
	}

	@Override
	public boolean onActionBar(String message) {
		return false;
	}

	@Override
	public void onPlayerListHeaderFooter(String header, String footer) {
	}

	@Override
	public void onTitle(String title, String subTitle) {
	}
}
