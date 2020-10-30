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

package eu.the5zig.mod.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketFriendRequest;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.server.hypixel.api.*;
import eu.the5zig.util.Utils;

import java.util.Iterator;
import java.util.List;

public class GuiHypixelFriends extends Gui {

	private IGuiList guiList;
	private GuiArrayList<User> friends = new GuiArrayList<>();

	private String status;
	private List<String> statusSplit;

	public GuiHypixelFriends(Gui lastScreen) {
		super(lastScreen);

		load();
	}

	@Override
	public void initGui() {
		if (guiList == null)
			updateStatus(I18n.translate("server.hypixel.loading"));

		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 206, getHeight() - 38, 100, 20, I18n.translate("server.hypixel.friends.display_stats")));
		addButton(The5zigMod.getVars().createButton(101, getWidth() / 2 - 102, getHeight() - 38, 100, 20, I18n.translate("server.hypixel.friends.add_mod")));
		addButton(The5zigMod.getVars().createButton(102, getWidth() / 2 + 2, getHeight() - 38, 100, 20, I18n.translate("server.hypixel.friends.add_mod_all")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 106, getHeight() - 38, 100, 20, The5zigMod.getVars().translate("gui.done")));
		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 50, getHeight() - 50, 0, getWidth(), friends);
		guiList.setRowWidth(200);
		guiList.setScrollX(getWidth() / 2 + 124);
		addGuiList(guiList);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			User selectedRow = (User) guiList.getSelectedRow();
			if (selectedRow == null)
				return;
			The5zigMod.getVars().displayScreen(new GuiHypixelStats(this, selectedRow.getUsername()));
		}
		if (button.getId() == 101) {
			User selectedRow = (User) guiList.getSelectedRow();
			if (selectedRow == null)
				return;
			if (!The5zigMod.getNetworkManager().isConnected())
				return;
			if (The5zigMod.getFriendManager().isFriend(selectedRow.getUniqueId()))
				return;

			The5zigMod.getNetworkManager().sendPacket(new PacketFriendRequest(selectedRow.getUsername()));
		}
		if (button.getId() == 102) {
			if (!The5zigMod.getNetworkManager().isConnected())
				return;
			List<User> users = Lists.newArrayList(friends);
			for (Iterator<User> iterator = users.iterator(); iterator.hasNext(); ) {
				if (The5zigMod.getFriendManager().isFriend(iterator.next().getUniqueId()))
					iterator.remove();
			}

			for (User user : users) {
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendRequest(user.getUsername()));
			}
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (status != null) {
			int y = getHeight() / 2 - 30;
			for (String s : statusSplit) {
				drawCenteredString(s, getWidth() / 2, y);
				y += 12;
			}
		}
	}

	@Override
	protected void tick() {
		super.getButtonById(100).setEnabled(guiList.getSelectedRow() != null);
		super.getButtonById(101).setEnabled(guiList.getSelectedRow() != null && !The5zigMod.getFriendManager().isFriend(((User) guiList.getSelectedRow()).getUniqueId()) &&
				The5zigMod.getNetworkManager().isConnected());
		super.getButtonById(102).setEnabled(The5zigMod.getNetworkManager().isConnected());
	}

	private void load() {
		try {
			The5zigMod.getHypixelAPIManager().get("friends?uuid=" + The5zigMod.getDataManager().getUniqueIdWithoutDashes(), new HypixelAPICallback() {
				@Override
				public void call(HypixelAPIResponse response) {
					updateStatus(null);
					JsonArray friends = response.data().getAsJsonArray("records");
					for (JsonElement element : friends) {
						JsonObject friend = element.getAsJsonObject();
						if (!friend.has("sender") || !friend.has("uuidSender") || !friend.has("receiver") || !friend.has("uuidReceiver")) {
							continue;
						}
						String sender = friend.get("sender").getAsString();
						String senderUUID = friend.get("uuidSender").getAsString();
						String receiver = friend.get("receiver").getAsString();
						String receiverUUID = friend.get("uuidReceiver").getAsString();
						if (The5zigMod.getDataManager().getUniqueIdWithoutDashes().equals(senderUUID)) {
							GuiHypixelFriends.this.friends.add(new User(receiver, Utils.getUUID(receiverUUID)));
						} else if (The5zigMod.getDataManager().getUniqueIdWithoutDashes().equals(receiverUUID)) {
							GuiHypixelFriends.this.friends.add(new User(sender, Utils.getUUID(senderUUID)));
						}
					}
				}

				@Override
				public void call(HypixelAPIResponseException e) {
					updateStatus(e.getErrorMessage());
				}
			});
		} catch (HypixelAPITooManyRequestsException e) {
			updateStatus(I18n.translate("server.hypixel.too_many_requests"));
		} catch (HypixelAPIMissingKeyException e) {
			updateStatus(I18n.translate("server.hypixel.no_key"));
		} catch (HypixelAPIException e) {
			updateStatus(e.getMessage());
		}
	}

	private void updateStatus(String status) {
		this.status = status;
		if (status == null) {
			statusSplit = null;
		} else {
			this.statusSplit = The5zigMod.getVars().splitStringToWidth(status, Math.max(100, getWidth() - 50));
		}
	}

	@Override
	public String getTitleKey() {
		return "server.hypixel.friends.title";
	}
}
