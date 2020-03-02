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

package eu.the5zig.mod.manager;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.KeyPressEvent;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.util.Mouse;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class AFKManager {

	private double lastX, lastY;
	private long afkTime;
	private boolean afk = false;
	private int newMessages;
	/**
	 * Time, when the player didn't move for more than 15 seconds.
	 */
	private long lastTimeNotMoved = -1;
	private long lastAfkTime;
	public static final int AFK_COUNTER = 1000 * 30;

	public AFKManager() {
		The5zigMod.getListener().registerListener(this);
		afkTime = System.currentTimeMillis();
	}

	@EventHandler
	public void onTick(TickEvent event) {
		long goAfkAfter = The5zigMod.getConfig().getInt("afkTime") * 1000 * 60;
		// Mouse or keyboard event -> Afk = false
		if (Mouse.getX() != lastX || Mouse.getY() != lastY) {
			reset();
		}
		// Afk = true after x Minutes
		if (goAfkAfter > 0 && !afk && System.currentTimeMillis() - afkTime > goAfkAfter) { // Automatically go AFK after 5 Minutes.
			The5zigMod.logger.info("Now AFK!");
			The5zigMod.getOverlayMessage().displayMessage(I18n.translate("profile.now_afk"));
			if (The5zigMod.getDataManager().getProfile().getOnlineStatus() == Friend.OnlineStatus.ONLINE)
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.ONLINE_STATUS, Friend.OnlineStatus.AWAY));
			afk = true;
		}
		if (System.currentTimeMillis() - lastTimeNotMoved > 3000) {
			lastTimeNotMoved = 0;
			lastAfkTime = 0;
		}
		lastX = Mouse.getX();
		lastY = Mouse.getY();
	}

	@EventHandler
	public void onKeyType(KeyPressEvent event) {
		reset();
	}

	private void reset() {
		if (afk) {
			The5zigMod.logger.info("No longer AFK!");
			The5zigMod.getOverlayMessage().displayMessage(I18n.translate("profile.no_longer_afk"));
			if (newMessages > 0)
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("conn.unread_messages", newMessages));
			if (The5zigMod.getDataManager().getProfile().getOnlineStatus() == Friend.OnlineStatus.ONLINE)
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.ONLINE_STATUS, Friend.OnlineStatus.ONLINE));
		}
		if (System.currentTimeMillis() - afkTime > AFK_COUNTER) {
			lastTimeNotMoved = System.currentTimeMillis();
			lastAfkTime = System.currentTimeMillis() - afkTime;
		}
		afkTime = System.currentTimeMillis();
		afk = false;
		newMessages = 0;
	}

	public void addNewMessage() {
		newMessages++;
	}

	public boolean isAfk() {
		return afk;
	}

	public long getAFKTime() {
		return System.currentTimeMillis() - afkTime;
	}

	public long getLastAfkTime() {
		return lastAfkTime;
	}
}
