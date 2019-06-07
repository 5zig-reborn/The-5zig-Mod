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

package eu.the5zig.mod.server.cytooxien;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketUserSearch;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.IMultiPatternResult;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.Callback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CytooxienListener extends AbstractGameListener<GameMode> {

	private boolean partyrequestsend;
	private boolean partyrequest;
	private long partyrequesttime;

	@Override
	public Class<GameMode> getGameMode() {
		return null;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return false;
	}

	@Override
	public void onPlayerListHeaderFooter(GameMode gameMode, String header, String footer) {
		// Du bist auf Lobby 2
		header = ChatColor.stripColor(header);
		if (!header.startsWith("Du bist auf ")) {
			return;
		}
		getGameListener().switchLobby(header.substring("Du bist auf ".length()));
	}

	@Override
	public boolean onServerChat(GameMode gameMode, String message) {
		if (partyrequestsend && message.contains("Diese Aktion wurde wegen Spam geblockt!")) {
			partyrequestsend = false;
			partyrequest = true;
			return true;
		}
		message = ChatColor.stripColor(message);
		if (partyrequestsend && message.contains("Leader: ")) {
			if (getGameListener().getPartyMembers().size() != 0) {
				partyrequestsend = false;
			}
			getGameListener().getPartyMembers().add("!" + message.replace("Leader: ", ""));
			return true;
		}
		if (partyrequestsend && message.contains("Mitglieder: ")) {
			if (getGameListener().getPartyMembers().size() != 0) {
				partyrequestsend = false;
			}
			String[] members = message.replace("Mitglieder: ", "").split(", ");
			for (int i = 0; i < members.length; i++) {
				if (!getGameListener().getPartyMembers().contains(members[i])) {
					getGameListener().getPartyMembers().add(members[i]);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onMatch(GameMode mode, String key, IPatternResult match) {
		if (key.equals("nick.nicked")) {
			getGameListener().setCurrentNick(match.get(0));
		} else if (key.equals("nick.unnicked")) {
			getGameListener().setCurrentNick(null);
		} else if (key.equals("friend.join")) {
			getGameListener().getOnlineFriends().add(match.get(0));
		} else if (key.equals("friend.leave")) {
			getGameListener().getOnlineFriends().remove(match.get(0));
		} else if (key.equals("party.invite_accepted")) {
			partyrequesttime = System.currentTimeMillis();
			partyrequest = true;
		} else if (key.equals("party.invited") && getGameListener().getPartyMembers().isEmpty()) {
			getGameListener().getPartyMembers().add("!" + The5zigMod.getDataManager().getUsername());
		} else if (key.equals("party.invite_was_accepted") || key.equals("party.someone_joined")) {
			if (!getGameListener().getPartyMembers().contains(match.get(0))) {
				getGameListener().getPartyMembers().add(match.get(0));
			}
		} else if (key.equals("party.someone_left") || key.equals("party.someone_was_removed")) {
			getGameListener().getPartyMembers().remove(match.get(0));
		} else if (key.equals("party.left") || key.equals("party.destroyed") || key.equals("party.kicked")) {
			getGameListener().getPartyMembers().clear();
		}
	}

	@Override
	public void onTick(GameMode gamemode) {
		if (partyrequest && (System.currentTimeMillis() - partyrequesttime > 3000)) {
			partyrequesttime = System.currentTimeMillis();
			partyrequest = false;
			partyrequestsend = true;
			The5zigMod.getVars().sendMessage("/party list");
		}
	}

	@Override
	public void onServerJoin() {
		getFriendsList(new ArrayList<User>());
	}

	private void getFriendsList(final List<User> users) {
		getGameListener().sendAndIgnoreMultiple("/friend list", ">                              < Freunde >                              <", ">                              < Freunde >                              <", new Callback<IMultiPatternResult>() {
			@Override
			public void call(IMultiPatternResult callback) {
				if (callback.getRemainingMessageCount() == 0) {
					return;
				}
				while (callback.getRemainingMessageCount() > 0) {
					String message = callback.getMessage(0);
					String name = ChatColor.stripColor(message).split(" ")[0];
					if (message.startsWith("§a")) {
						getGameListener().getOnlineFriends().add(name);
					}
					if ((!The5zigMod.getFriendManager().isFriend(name)) && (!The5zigMod.getFriendManager().isSuggested(name))) {
						users.add(new User(name, null));
					}
					if (!users.isEmpty()) {
						The5zigMod.getNetworkManager().sendPacket(new PacketUserSearch(PacketUserSearch.Type.FRIEND_LIST, users.toArray(new User[users.size()])));
					}
				}
			}
		});
	}

}
