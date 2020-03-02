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

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.network.packets.PacketAuthToken;
import eu.the5zig.mod.config.items.EnumItem;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.IPlaceholderTextfield;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.IServerData;
import eu.the5zig.mod.util.IServerPinger;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GuiFriends extends Gui {

	private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).build());
	private static final Cache<String, ServerIcon> iconCache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES).build();
	private static final Base64Renderer base64Renderer = new Base64Renderer();

	private final List<Friend> friendList = Lists.newArrayList();
	private String searchText;

	private final IServerPinger serverPinger = The5zigMod.getVars().getServerPinger();

	private boolean hoverProfile;

	public GuiFriends(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, 8, 6, 50, 20, I18n.translate("gui.back")));
		IGuiList<Friend> guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 32, getHeight() - 32, 0, getWidth(), friendList);
		guiList.setRowWidth(250);
		guiList.setDrawSelection(false);
		addGuiList(guiList);

		IPlaceholderTextfield textfield = The5zigMod.getVars().createTextfield(I18n.translate("gui.search"), 1, getWidth() / 2 - 120, getHeight() - 24, 140, 18);
		if (!Strings.isNullOrEmpty(searchText)) {
			textfield.callSetText(searchText);
		}
		addTextField(textfield);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 + 25, getHeight() - 25, 95, 20, The5zigMod.getConfig().get("friendSortation", EnumItem.class).translate()));

		addButton(The5zigMod.getVars().createIconButton(The5zigMod.ITEMS, 112, 16, 5, getWidth() - 188, 6));
		addButton(The5zigMod.getVars().createIconButton(The5zigMod.ITEMS, 112, 0, 4, getWidth() - 168, 6));
		addButton(The5zigMod.getVars().createIconButton(The5zigMod.ITEMS, 48, 16, 2, getWidth() - 148, 6));
		addButton(The5zigMod.getVars().createIconButton(The5zigMod.ITEMS, 32, 16, 3, getWidth() - 128, 6));

		addButton(The5zigMod.getVars().createButton(726, 8, getHeight() - 27, 80, 20, "Site Login..."));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			EnumItem item = The5zigMod.getConfig().get("friendSortation", EnumItem.class);
			item.next();
			item.action();
			button.setLabel(item.translate());
			if (item.hasChanged()) {
				The5zigMod.getConfig().save();
			}
		} else if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new GuiAddFriend(this));
		} else if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiFriendRequests(this));
		} else if (button.getId() == 4) {
			The5zigMod.getVars().displayScreen(new GuiConversations(this));
		} else if (button.getId() == 5) {
			The5zigMod.getVars().displayScreen(new GuiParty(this));
		}
		else if(button.getId() == 726) {
			The5zigMod.getNetworkManager().sendPacket(new PacketAuthToken(false));
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		if (hoverProfile) {
			The5zigMod.getVars().displayScreen(new GuiProfile(this));
			The5zigMod.getVars().playSound("ui.button.click", 1);
		}
	}

	@Override
	protected void tick() {
		serverPinger.callPingPendingNetworks();
		searchText = getTextfieldById(1).callGetText();

		friendList.clear();
		for (Friend friend : The5zigMod.getFriendManager().getFriends()) {
			if (searchText == null || friend.getUsername().toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))) {
				friendList.add(friend);
			}
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String string = I18n.translate("friend.title", The5zigMod.getNetworkManager().isConnected() ? ChatColor.GREEN + ChatColor.BOLD.toString() + I18n.translate("friend.connected") :
				ChatColor.RED + ChatColor.BOLD.toString() + I18n.translate("friend.disconnected"));
		The5zigMod.getVars().drawString(ChatColor.BOLD + string, (getWidth() - 248) / 2, 13);

		drawOwnProfile(mouseX, mouseY);

		if (The5zigMod.getFriendManager().getFriends().isEmpty()) {
			The5zigMod.getVars().drawCenteredString(I18n.translate("friend.no_friends"), getWidth() / 2, getHeight());
		}
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		GLUtil.enableDepth();
		super.drawScreen0(mouseX, mouseY, partialTicks);
		GLUtil.color(1, 1, 1, 1);

		The5zigMod.getVars().bindTexture(The5zigMod.ITEMS);
		int suggestions = The5zigMod.getFriendManager().getShownSuggestions().size();
		if (suggestions > 0) {
			Gui.drawModalRectWithCustomSizedTexture(getWidth() - 136, 6, 128 + (Math.min(suggestions, 6) - 1) * 8, 32, 8, 8, 256, 256);
		}
		int requests = The5zigMod.getFriendManager().getFriendRequests().size();
		if (requests > 0) {
			Gui.drawModalRectWithCustomSizedTexture(getWidth() - 116, 6, 128 + (Math.min(requests, 6) - 1) * 8, 32, 8, 8, 256, 256);
		}

		int partyInvitations = The5zigMod.getPartyManager().getPartyInvitations().size();
		if (partyInvitations > 0) {
			Gui.drawModalRectWithCustomSizedTexture(getWidth() - 176, 6, 128 + (Math.min(partyInvitations, 6) - 1) * 8, 32, 8, 8, 256, 256);
		}

		drawHover(mouseX, mouseY, getButtonById(2), ImmutableList.of(suggestions > 0 ? I18n.translate("friend.add.suggestions", suggestions) : I18n.translate("friend.add")));
		drawHover(mouseX, mouseY, getButtonById(3), ImmutableList.of(I18n.translate("friend.requests", requests)));
		drawHover(mouseX, mouseY, getButtonById(4), ImmutableList.of(I18n.translate("friend.chats")));
		drawHover(mouseX, mouseY, getButtonById(5), ImmutableList.of(partyInvitations > 0 ? I18n.translate("friend.party.invitations", partyInvitations) : I18n.translate("friend.party")));
		GLUtil.disableDepth();
	}

	private void drawHover(int mouseX, int mouseY, IButton button, List<String> text) {
		if (mouseX >= button.getX() && mouseX <= button.getX() + button.callGetWidth() && mouseY >= button.getY() && mouseY <= button.getY() + button.callGetHeight()) {
			drawHoveringText(text, mouseX, Math.max(15, mouseY));
		}
	}

	private void drawOwnProfile(int mouseX, int mouseY) {
		String base64EncodedSkin = The5zigMod.getSkinManager().getBase64EncodedSkin(The5zigMod.getDataManager().getUniqueId());
		if (base64Renderer.getBase64String() != null && base64EncodedSkin == null) {
			base64Renderer.reset();
		} else if (base64EncodedSkin != null && !base64EncodedSkin.equals(base64Renderer.getBase64String())) {
			base64Renderer.setBase64String(base64EncodedSkin, "player_skin/" + The5zigMod.getDataManager().getUniqueId());
		}
		int width = 16, height = 16;
		String coloredName = The5zigMod.getDataManager().getColoredName();
		String profile = I18n.translate("friend.profile");
		int x1 = (int) (getWidth() - width - 14 - 96 * 0.7f);
		int x2 = x1 + width;
		int y1 = 8;
		int y2 = y1 + height;

		int boxX2 = getWidth() - 8;
		hoverProfile = mouseX >= x1 && mouseX <= boxX2 && mouseY >= y1 && mouseY < y2;

		int c = hoverProfile ? 0xff333333 : 0xff000000;
		Gui.drawRect(x1 - 1, y1 - 1, boxX2 + 1, y2 + 1, 0xffaaaaaa);
		Gui.drawRect(x2, y1, boxX2, y2, c);

		if (hoverProfile)
			base64Renderer.renderImage(x1, y1, width, height, .5f, .5f, .5f, 1);
		else
			base64Renderer.renderImage(x1, y1, width, height);
		GLUtil.pushMatrix();
		GLUtil.scale(.7f, .7f, .7f);
		GLUtil.translate((x2 + 4) / .7f, (y1 + 2) / .7f, 0);
		The5zigMod.getVars().drawString(coloredName, 0, 0);
		The5zigMod.getVars().drawString(profile, 0, 10);
		GLUtil.popMatrix();
	}

	@Override
	protected void guiClosed() {
		serverPinger.callClearPendingNetworks();
	}

	@Override
	public String getTitleName() {
		return "";
	}

	public Base64Renderer getServerIconRenderer(String serverIP) {
		ServerIcon optionalRenderer = iconCache.getIfPresent(serverIP);
		if (optionalRenderer == null) {
			final ServerIcon serverIcon = new ServerIcon(new Base64Renderer(The5zigMod.MINECRAFT_UNKNOWN_SERVER), serverPinger.createServerData(serverIP));
			iconCache.put(serverIP, serverIcon);

			EXECUTOR.submit(new Runnable() {
				@Override
				public void run() {
					serverPinger.ping(serverIcon.serverData);
				}
			});

			return serverIcon.renderer;
		} else {
			Base64Renderer base64Renderer = optionalRenderer.renderer;
			if (base64Renderer.getBase64String() != null && optionalRenderer.serverData.getServerIcon() == null) {
				base64Renderer.reset();
			} else if (optionalRenderer.serverData.getServerIcon() != null && !optionalRenderer.serverData.getServerIcon().equals(base64Renderer.getBase64String())) {
				base64Renderer.setBase64String(optionalRenderer.serverData.getServerIcon(),
						The5zigMod.getVars().createResourceLocation("servers/" + optionalRenderer.serverData.getServerIP() + "/icon"));
			}
			return optionalRenderer.renderer;
		}
	}

	private class ServerIcon {

		private Base64Renderer renderer;
		private IServerData serverData;

		public ServerIcon(Base64Renderer renderer, IServerData serverData) {
			this.renderer = renderer;
			this.serverData = serverData;
		}
	}

}
