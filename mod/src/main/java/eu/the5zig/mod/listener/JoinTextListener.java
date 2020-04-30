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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.JoinText;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.ServerJoinEvent;

import java.util.List;

public class JoinTextListener {

	@EventHandler
	public void onServerJoin(ServerJoinEvent event) {
		List<JoinText> texts = The5zigMod.getJoinTextConfiguration().getConfigInstance().getTexts();
		for (final JoinText text : texts) {
			if (text.getServer() == null || (text.getServerPattern() != null && text.getServerPattern().matcher(event.getHost()).matches())) {
				The5zigMod.getScheduler().postToMainThread(new Runnable() {
					@Override
					public void run() {
						The5zigMod.getListener().doSendChatMessage(text.getMessage());
					}
				}, true, text.getDelay());
			}
		}
	}

}
