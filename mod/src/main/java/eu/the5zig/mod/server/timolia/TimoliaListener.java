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

package eu.the5zig.mod.server.timolia;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketUserSearch;
import eu.the5zig.mod.server.*;
import eu.the5zig.util.Callback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

public class TimoliaListener extends AbstractGameListener<GameMode> {

	@Override
	public Class<GameMode> getGameMode() {
		return null;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return false;
	}

	@Override
	public void onServerJoin() {
		getFriendsList(1, Lists.<User>newArrayList());
	}

	private void getFriendsList(int page, final List<User> users) {
		getGameListener().sendAndIgnoreMultiple("/friends " + page, "friends.list.title", 2, "friends.list.page_not_found", new Callback<IMultiPatternResult>() {
			@Override
			public void call(IMultiPatternResult callback) {
				IPatternResult pages = callback.parseKey("friends.list.title");
				final int currentPage = pages.get(1) == null ? 1 : Integer.parseInt(pages.get(1));
				int totalPages = pages.get(2) == null ? 1 : Integer.parseInt(pages.get(2));
				if (callback.getRemainingMessageCount() == 0)
					return;
				String message = callback.getMessage(0);
				List<String> list = Splitter.on(", ").splitToList(message.substring(6));
				for (String name : list) {
					String strippedName = ChatColor.stripColor(name);
					if (name.startsWith(ChatColor.GREEN.toString())) {
						getGameListener().getOnlineFriends().add(strippedName);
					}
					if (!The5zigMod.getFriendManager().isFriend(strippedName) && !The5zigMod.getFriendManager().isSuggested(strippedName)) {
						users.add(new User(strippedName, null));
					}
				}

				if (currentPage == totalPages) {
					if (!users.isEmpty()) {
						The5zigMod.getNetworkManager().sendPacket(new PacketUserSearch(PacketUserSearch.Type.FRIEND_LIST, users.toArray(new User[users.size()])));
					}
				} else {
					The5zigMod.getScheduler().postToMainThread(new Runnable() {
						@Override
						public void run() {
							getFriendsList(currentPage + 1, users);
						}
					}, true);
				}
			}
		});
	}

	@Override
	public void onMatch(GameMode gameMode, String key, IPatternResult match) {
		if (key.equals("friend.joined")) {
			getGameListener().getOnlineFriends().add(match.get(0));
		}
		if (key.equals("friend.left")) {
			getGameListener().getOnlineFriends().remove(match.get(0));
		}
		if (key.equals("nick.nicked")) {
			getGameListener().setCurrentNick(match.get(0));
		}
		if (key.equals("nick.unnicked")) {
			getGameListener().setCurrentNick(null);
		}

		if (gameMode == null)
			return;
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("leave")) {
				gameMode.setTime(-1);
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("win")) {
				gameMode.setWinner(match.get(0));
				gameMode.setState(GameState.FINISHED);
			}
			if (key.equals("kill") || key.equals("kill.potion")) {
				if (match.get(0).equals(The5zigMod.getDataManager().getUsername()) && !(gameMode instanceof ServerTimolia.TSpiele) && !(gameMode instanceof ServerTimolia.InTime)) {
					gameMode.setDeaths(gameMode.getDeaths() + 1);
					gameMode.setKillStreak(0);
				}
				if (match.get(1).equals(The5zigMod.getDataManager().getUsername())) {
					gameMode.setKills(gameMode.getKills() + 1);
					gameMode.setKillStreak(gameMode.getKillStreak() + 1);
				}
			}
			if (key.equals("suicide") && match.get(0).equals(The5zigMod.getDataManager().getUsername())) {
				gameMode.setDeaths(gameMode.getDeaths() + 1);
				gameMode.setKillStreak(0);
			}
		}
	}

	@Override
	public void onPlayerListHeaderFooter(GameMode gameMode, String header, String footer) {
		// §6 « §6§lTimolia Netzwerk§6 »
		String[] split = ChatColor.stripColor(header).split("\n");
		if (split.length < 2)
			return;
		String second = split[1];
		if (!second.startsWith(" Du spielst auf "))
			return;

		String lobby = second.split("Du spielst auf |\\.")[1];
		getGameListener().switchLobby(lobby);
	}

}
