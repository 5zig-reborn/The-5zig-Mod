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

package eu.the5zig.mod.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketAddBlockedUser;
import eu.the5zig.mod.chat.network.packets.PacketDeleteFriend;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.util.Callback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

public class GuiFriendProfile extends GuiOptions {

	private static final Base64Renderer base64Renderer = new Base64Renderer();
	private final Friend friend;

	public GuiFriendProfile(Gui parentScreen, Friend friend) {
		super(parentScreen);
		this.friend = friend;
	}

	@Override
	public void initGui() {
		super.initGui();

		IGuiList statusMessage = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 40, 40 + 100, getWidth() / 2 - 155 + 16 + 88, getWidth() / 2 + 155,
				ImmutableList.of(new StaticProfileRow()));
		statusMessage.setBottomPadding(4);
		statusMessage.setRowWidth(400);
		statusMessage.setLeftbound(true);
		statusMessage.setDrawSelection(false);
		statusMessage.setScrollX(getWidth() / 2 + 155 - 5);
		addGuiList(statusMessage);

		addOptionButton(I18n.translate("friend.profile.delete"), 120, new Callback<IButton>() {
			@Override
			public void call(IButton button) {
				The5zigMod.getNetworkManager().sendPacket(new PacketDeleteFriend(friend.getUniqueId()));
				The5zigMod.getVars().displayScreen(lastScreen);
			}
		});
		addOptionButton(I18n.translate("friend.profile.block"), 120, new Callback<IButton>() {
			@Override
			public void call(IButton button) {
				The5zigMod.getNetworkManager().sendPacket(new PacketAddBlockedUser(new User(friend.getUsername(), friend.getUniqueId())));
				The5zigMod.getVars().displayScreen(lastScreen);
			}
		});
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if ((base64Renderer.getBase64String() == null || !base64Renderer.getBase64String().equals(The5zigMod.getSkinManager().getBase64EncodedSkin(friend.getUniqueId()))) &&
				The5zigMod.getSkinManager().getBase64EncodedSkin(friend.getUniqueId()) != null)
			base64Renderer.setBase64String(The5zigMod.getSkinManager().getBase64EncodedSkin(friend.getUniqueId()), "player_skin/" + friend.getUniqueId());
		base64Renderer.renderImage(getWidth() / 2 - 155, 40, 88, 88);
	}

	@Override
	public String getTitleKey() {
		return "friend.profile";
	}

	private class StaticProfileRow implements Row {

		private int totalHeight = 0;

		@Override
		public void draw(int x, int y) {
			totalHeight = 0;

			int width = Math.min(Math.max(getWidth() / 2 - 40, 190) + 200, getWidth() - 10) - Math.max(getWidth() / 2 - 40, 190) - 10;


			List<String> lines = Lists.newArrayList();
			lines.add(friend.getDisplayName());
			lines.add(ChatColor.GRAY.toString() + ChatColor.ITALIC + I18n.translate("friend.info.first_join_date", friend.getFirstOnline()));
			lines.add(I18n.translate("friend.info.status", friend.getStatus().getDisplayName() + ChatColor.RESET));
			lines.add(friend.getStatus() != Friend.OnlineStatus.OFFLINE ?
					I18n.translate("friend.info.server", friend.getServer() == null ? I18n.translate("friend.info.server.none") : (friend.getServer().split(":")[0] +
							(friend.getLobby() != null ? " (" + friend.getLobby() + ")" : "")).replace("Hidden", I18n.translate("friend.info.hidden"))) :
					I18n.translate("friend.info.last_seen", friend.getLastOnline()));
			lines.add(I18n.translate("friend.info.profile_message") + " " + friend.getStatusMessage());
			lines.add(I18n.translate("friend.info.mod_version", friend.getModVersion()));
			lines.add(I18n.translate("friend.info.country",
					!The5zigMod.getDataManager().getProfile().isShowCountry() || friend.getLocale() == null ? I18n.translate("friend" + ".info.hidden") :
							friend.getLocale().getDisplayCountry(I18n.getCurrentLanguage())));

			totalHeight = y;
			for (String text : lines) {
				List<String> split = MinecraftFactory.getVars().splitStringToWidth(text, width);
				for (int j = 0, splitStringToWidthSize = split.size(); j < splitStringToWidthSize; j++) {
					String line = split.get(j);
					MinecraftFactory.getVars().drawString(line, x, y);
					if (j < splitStringToWidthSize - 1)
						y += 11;
					else
						y += 14;
				}
			}
			totalHeight = y - totalHeight;
		}

		@Override
		public int getLineHeight() {
			return totalHeight;
		}

	}
}
