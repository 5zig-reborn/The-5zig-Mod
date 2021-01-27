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
public interface IListener {

	void onTick();

	void onKeyPress(int code);

	void onServerJoin(String host, int port);

	/**
	 * Called, when a MC|Brand Payload has been received. Very effective way to detect server switches!
	 */
	void onServerConnect();

	void onServerDisconnect();

	void onPayloadReceive(String channel, ByteBuf packetData);

	/**
	 * Handles all received chat messages.
	 *
	 * @param message The message that has been received.
	 */
	boolean onServerChat(String message);

	/**
	 * Handles all received chat messages.
	 *
	 * @param message       The Chat Message as String
	 * @param chatComponent The Chat Message as internal Chat Component
	 * @return true, if the message should not be displayed
	 */
	boolean onServerChat(String message, Object chatComponent);

	boolean onActionBar(String message);

	void onPlayerListHeaderFooter(String header, String footer);

	void onTitle(String title, String subTitle);

	void onWorldSwitch();

}