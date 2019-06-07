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

package eu.the5zig.mod.chat.entity;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Longs;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.gui.GuiFriendProfile;
import eu.the5zig.mod.gui.GuiFriends;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.RowExtended;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.IResourceLocation;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Friend extends User implements RowExtended, Comparable<Friend> {

	private static final IResourceLocation RESOURCE_SELECTION = MinecraftFactory.getVars().createResourceLocation("textures/gui/resource_packs.png");

	private long firstOnline;
	private String statusMessage;
	private OnlineStatus status;
	private String server;
	private String lobby;
	private long lastOnline;
	private Rank rank;
	private boolean favorite;
	private String modVersion;
	private Locale locale;

	private final Base64Renderer base64Renderer = new Base64Renderer();
	private int x, y;

	public Friend(String username, UUID uuid) {
		super(username, uuid);
	}

	public String getDisplayName() {
		return getRank().getColorCode() + getUsername();
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String status) {
		this.statusMessage = status;
	}

	public OnlineStatus getStatus() {
		return status;
	}

	public void setStatus(OnlineStatus status) {
		this.status = status;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server == null || server.isEmpty() ? null : server;
		this.lobby = null;
		if (The5zigMod.getDataManager().getServer() instanceof GameServer) {
			((GameServer) The5zigMod.getDataManager().getServer()).checkFriendServer(this);
		}
	}

	public String getLobby() {
		return lobby;
	}

	public void setLobby(String lobby) {
		this.lobby = lobby == null || lobby.isEmpty() ? null : lobby;
	}

	public String getLastOnline() {
		return lastOnline == -1 ? I18n.translate("profile.hidden") : Utils.convertToDate(lastOnline).replace("Today", I18n.translate("profile.today")).replace("Yesterday",
				I18n.translate("profile.yesterday"));
	}

	public void setLastOnline(long lastOnline) {
		this.lastOnline = lastOnline;
	}

	public String getFirstOnline() {
		return firstOnline == -1 ? I18n.translate("profile.hidden") : Utils.convertToDate(firstOnline).replace("Today", I18n.translate("profile.today")).replace("Yesterday",
				I18n.translate("profile.yesterday"));
	}

	public void setFirstOnline(long firstOnline) {
		this.firstOnline = firstOnline;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String getModVersion() {
		return modVersion;
	}

	public void setModVersion(String modVersion) {
		this.modVersion = modVersion;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public int getLineHeight() {
		return 36;
	}

	@Override
	public void draw(int x, int y) {
	}

	@Override
	public void draw(int x, int y, int slotHeight, int mouseX, int mouseY) {
		this.x = x;
		this.y = y;
		if (isFavorite()) {
			Gui.drawRect(x, y, x + 240, y + slotHeight, 0x2200ffff);
		}

		String base64EncodedSkin = The5zigMod.getSkinManager().getBase64EncodedSkin(getUniqueId());
		if (base64Renderer.getBase64String() != null && base64EncodedSkin == null) {
			base64Renderer.reset();
		} else if (base64EncodedSkin != null && !base64EncodedSkin.equals(base64Renderer.getBase64String())) {
			base64Renderer.setBase64String(base64EncodedSkin, "player_skin/" + getUniqueId());
		}
		base64Renderer.renderImage(x, y, 32, 32);

		String displayName = getRank().getColorCode() + ChatColor.BOLD + getUsername();
		int stringWidth = The5zigMod.getVars().getStringWidth(displayName);

		The5zigMod.getVars().drawString(displayName, x + 38, y + 2);
		The5zigMod.getVars().drawString(getStatus().getDisplayName(), x + 54 + stringWidth, y + 2);
		GLUtil.color(1, 1, 1, 1);

		if (getStatus() == OnlineStatus.OFFLINE) {
			Gui.drawRectOutline(x + 38, y + 14, x + 38 + 16, y + 14 + 16, 0x99ffffff);

			if (lastOnline > 0) {
				long displayTime;
				String displayUnit;
				long difference = System.currentTimeMillis() - lastOnline;
				long seconds = difference / 1000;
				if (seconds < 60) {
					displayTime = seconds;
					displayUnit = seconds == 1 ? "second" : "seconds";
				} else {
					long minutes = Math.round(seconds / 60.0);
					if (minutes < 60) {
						displayTime = minutes;
						displayUnit = minutes == 1 ? "minute" : "minutes";
					} else {
						long hours = Math.round(minutes / 60.0);
						if (hours < 24) {
							displayTime = hours;
							displayUnit = hours == 1 ? "hour" : "hours";
						} else {
							long days = Math.round(hours / 24.0);
							if (days <= 31) {
								displayTime = days;
								displayUnit = days == 1 ? "day" : "days";
							} else {
								Calendar startCalendar = new GregorianCalendar();
								startCalendar.setTimeInMillis(lastOnline);
								Calendar endCalendar = new GregorianCalendar();
								endCalendar.setTimeInMillis(System.currentTimeMillis());
								int years = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
								int months = years * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
								if (months < 12) {
									displayTime = months;
									displayUnit = months == 1 ? "month" : "months";
								} else {
									displayTime = years;
									displayUnit = years == 1 ? "year" : "years";
								}
							}
						}
					}
				}
				Gui.drawRect(x + 38, y + 25, x + 38 + 16, y + 29, 0x44ffffff);
				Gui.drawScaledCenteredString(String.valueOf(displayTime), x + 38 + 8, y + 16, Math.min(1, 16f / The5zigMod.getVars().getStringWidth(String.valueOf(displayTime))));
				displayUnit = I18n.translate("friend.profile.last_seen." + displayUnit);
				Gui.drawScaledCenteredString(displayUnit, x + 38 + 8, y + 25, Math.min(0.5f, 16f / The5zigMod.getVars().getStringWidth(displayUnit)));

				The5zigMod.getVars().drawString(ChatColor.GRAY + I18n.translate("friend.profile.last_seen"), x + 58, y + 13);
				The5zigMod.getVars().drawString(Utils.convertToDate(lastOnline), x + 58, y + 23);
			} else {
				Gui.drawScaledCenteredString("?", x + 38 + 8, y + 18, 1.1f);

				The5zigMod.getVars().drawString(ChatColor.GRAY + I18n.translate("friend.profile.last_seen"), x + 58, y + 13);
				The5zigMod.getVars().drawString(I18n.translate("friend.info.hidden"), x + 58, y + 23);
			}
		} else {
			The5zigMod.getVars().drawString(ChatColor.GRAY + I18n.translate("friend.profile.playing_on_server"), x + 58, y + 13);
			String server = this.server;
			if (!Strings.isNullOrEmpty(server) && !"Hidden".equals(server) && The5zigMod.getVars().getCurrentScreen() instanceof GuiFriends) {
				Base64Renderer renderer = ((GuiFriends) The5zigMod.getVars().getCurrentScreen()).getServerIconRenderer(server);

				if (mouseX >= x && mouseX <= x + 250 && mouseY >= y && mouseY <= y + slotHeight) {
					int v = 0;
					if (isHoverJoinServer(mouseX, mouseY)) {
						v = 18;
					}
					renderer.renderImage(x + 37, y + 13, 18, 18);
					Gui.drawRect(x + 37, y + 13, x + 37 + 18, y + 13 + 18, 0x88ffffff);
					The5zigMod.getVars().bindTexture(RESOURCE_SELECTION);
					Gui.drawModalRectWithCustomSizedTexture(x + 37, y + 13, 0, v, 18, 18, 256 / 32 * 18, 256 / 32 * 18);
				} else {
					renderer.renderImage(x + 37, y + 13, 18, 18);
				}

				if (server.endsWith(":25565")) {
					server = server.substring(0, server.length() - ":25565".length());
				}
				if (!Strings.isNullOrEmpty(lobby)) {
					server += " (" + lobby + ")";
				}
				The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(server, 150), x + 58, y + 23);
			} else {
				The5zigMod.getVars().bindTexture(The5zigMod.MINECRAFT_UNKNOWN_SERVER);
				GLUtil.color(0.4f, 0.4f, 0.4f, 1);
				Gui.drawModalRectWithCustomSizedTexture(x + 37, y + 13, 0, 0, 18, 18, 18, 18);

				if ("Hidden".equals(server)) {
					The5zigMod.getVars().drawString(I18n.translate("friend.info.hidden"), x + 58, y + 23);
				} else {
					The5zigMod.getVars().drawString(I18n.translate("friend.info.server.none"), x + 58, y + 23);
				}
			}
		}

		The5zigMod.getVars().bindTexture(The5zigMod.ITEMS);
		GLUtil.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(x + 44 + stringWidth, y + 2, (getStatus() == OnlineStatus.OFFLINE ? 2 : 3) * 8, 24, 8, 8, 128, 128);
		boolean hoverChat = isHoverChat(mouseX, mouseY);
		Gui.drawModalRectWithCustomSizedTexture(x + 210, y + 15, hoverChat ? 32 : 48, 32, 16, 16, 256, 256);
		boolean unread = The5zigMod.getConversationManager().conversationExists(this) && !The5zigMod.getConversationManager().getConversation(this).isRead();
		if (unread) {
			Gui.drawModalRectWithCustomSizedTexture(x + 218, y + 15, 128 + 5 * 8, 32, 8, 8, 256, 256);
		}
		Gui.drawModalRectWithCustomSizedTexture(x + 226, y + 15, 96, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x + 226, y - 2, 0, isFavorite() ? 16 : 0, 16, 16, 128, 128);

		GLUtil.pushMatrix();
		GLUtil.translate(0, 0, 300);
		if (hoverChat) {
			The5zigMod.getVars().getCurrentScreen().drawHoveringText(ImmutableList.of(I18n.translate("friend.chat")), mouseX, mouseY);
		}
		if (isHoverProfile(mouseX, mouseY)) {
			The5zigMod.getVars().getCurrentScreen().drawHoveringText(ImmutableList.of(I18n.translate("friend.profile")), mouseX, mouseY);
		}
		if (isHoverFavorite(mouseX, mouseY)) {
			The5zigMod.getVars().getCurrentScreen().drawHoveringText(ImmutableList.of(isFavorite() ? I18n.translate("friend.profile.unfavorite") : I18n.translate("friend.profile.favorite")),
					mouseX, mouseY);
		}
		GLUtil.popMatrix();
		GLUtil.disableBlend();
	}

	@Override
	public IButton mousePressed(int mouseX, int mouseY) {
		if (isHoverJoinServer(mouseX, mouseY)) {
			if (getStatus() != OnlineStatus.OFFLINE) {
				String server = this.server;
				if (!Strings.isNullOrEmpty(server) && !"Hidden".equals(server)) {
					The5zigMod.getVars().joinServer(server.split(":")[0], Integer.parseInt(server.split(":")[1]));
					return The5zigMod.getVars().createButton(0, 0, 0, null);
				}
			}
		}
		if (isHoverChat(mouseX, mouseY)) {
			Conversation conversation = The5zigMod.getConversationManager().getConversation(this);
			GuiConversations tab = new GuiConversations(The5zigMod.getVars().getCurrentScreen());
			The5zigMod.getVars().displayScreen(tab);
			tab.setCurrentConversation(conversation);
			tab.onSelect(tab.chatList.getRows().indexOf(conversation), conversation, false);
			return The5zigMod.getVars().createButton(0, 0, 0, null);
		}
		if (isHoverProfile(mouseX, mouseY)) {
			The5zigMod.getVars().displayScreen(new GuiFriendProfile(The5zigMod.getVars().getCurrentScreen(), this));
			return The5zigMod.getVars().createButton(0, 0, 0, null);
		}
		if (isHoverFavorite(mouseX, mouseY)) {
			The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(getUniqueId(), !isFavorite()));
			return The5zigMod.getVars().createButton(0, 0, 0, null);
		}

		return null;
	}

	private boolean isHoverJoinServer(int mouseX, int mouseY) {
		return mouseX >= x + 37 && mouseX <= x + 37 + 18 && mouseY >= y + 13 && mouseY <= y + 13 + 18;
	}

	private boolean isHoverChat(int mouseX, int mouseY) {
		return mouseX >= x + 210 && mouseX < x + 226 && mouseY >= y + 15 && mouseY <= y + 29;
	}

	private boolean isHoverProfile(int mouseX, int mouseY) {
		return mouseX >= x + 226 && mouseX < x + 242 && mouseY >= y + 15 && mouseY <= y + 29;
	}

	private boolean isHoverFavorite(int mouseX, int mouseY) {
		return mouseX >= x + 230 && mouseX < x + 240 && mouseY >= y && mouseY <= y + 10;
	}

	@Override
	public String toString() {
		return "Friend{" + "name='" + username + '\'' + ", uuid=" + uuid + '}';
	}

	@Override
	public int compareTo(Friend friend) {
		if (friend.isFavorite() && !isFavorite())
			return 1;
		if (isFavorite() && !friend.isFavorite())
			return -1;
		if (The5zigMod.getConfig().getEnum("friendSortation", Sortation.class) == Sortation.ONLINE) {
			if (getStatus() != friend.getStatus() && (getStatus() != OnlineStatus.OFFLINE || friend.getStatus() != OnlineStatus.OFFLINE)) {
				return getStatus().compareTo(friend.getStatus());
			} else if (getStatus() == OnlineStatus.OFFLINE && friend.getStatus() == OnlineStatus.OFFLINE) {
				return Longs.compare(friend.lastOnline, lastOnline);
			}
		}
		return getUsername().toLowerCase(Locale.ROOT).compareTo(friend.getUsername().toLowerCase(Locale.ROOT));
	}

	public enum OnlineStatus {

		ONLINE("connection.online", ChatColor.GREEN), AWAY("connection.away", ChatColor.YELLOW), OFFLINE("connection.offline", ChatColor.RED);

		private String name;
		private ChatColor color;

		OnlineStatus(String name, ChatColor color) {
			this.name = name;
			this.color = color;
		}

		public OnlineStatus getNext() {
			return values()[(ordinal() + 1) % values().length];
		}

		public String getDisplayName() {
			return color + getName();
		}

		public String getName() {
			return I18n.translate(name);
		}
	}

	public enum Sortation {

		NAME, ONLINE

	}
}
