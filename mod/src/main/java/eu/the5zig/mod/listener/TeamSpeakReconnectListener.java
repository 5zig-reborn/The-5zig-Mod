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

package eu.the5zig.mod.listener;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.teamspeak.TeamSpeak;

public class TeamSpeakReconnectListener {

	private boolean reconnecting;
	private int reconnectTicks = 1;
	private boolean enabled;

	@EventHandler
	public void onTick(TickEvent event) {
		if (!The5zigMod.getConfig().getBool("tsEnabled")) {
			enabled = false;
			if (TeamSpeak.getClient().isConnected()) {
				TeamSpeak.getClient().disconnect();
			}
			return;
		}
		if (!enabled) {
			reconnectTicks = 1;
		}
		enabled = true;
		if (!TeamSpeak.getClient().isConnected() && !reconnecting && reconnectTicks == 0) {
			reconnectTicks = 20 * 10;
		} else if (!The5zigMod.getDataManager().isTsRequiresAuth() && !TeamSpeak.getClient().isConnected() && reconnectTicks != 0
				&& --reconnectTicks == 0) {
			reconnecting = true;
			The5zigMod.getAsyncExecutor().execute(new Runnable() {
				@Override
				public void run() {
					try {
						TeamSpeak.getClient().connect(The5zigMod.getConfig().getString("tsAuthKey"));
					} catch (Throwable throwable) {
						The5zigMod.logger.error("Could not connect to TeamSpeak Client!", throwable);
					}
					reconnecting = false;
				}
			});
		}
	}

	public void reconnectNow() {
		reconnectTicks = 1;
	}

}
