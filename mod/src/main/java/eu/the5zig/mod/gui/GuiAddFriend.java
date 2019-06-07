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
import eu.the5zig.mod.chat.entity.FriendSuggestion;
import eu.the5zig.mod.chat.entity.User;
import eu.the5zig.mod.chat.network.packets.PacketFriendRequest;
import eu.the5zig.mod.chat.network.packets.PacketUserSearch;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.render.Base64Renderer;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class GuiAddFriend extends Gui implements Clickable<GuiAddFriend.ProfileRow> {

	private static final Pattern PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

	private final ImmutableList<User> suggestions = ImmutableList.copyOf(The5zigMod.getFriendManager().getShownSuggestions());

	private IGuiList<ProfileRow> guiList;
	public List<ProfileRow> rows = Lists.newArrayList();
	private String keyword;
	private int lastSelected;
	private long lastTyped;

	public GuiAddFriend(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addButton(MinecraftFactory.getVars().createButton(100, getWidth() / 2 + 5, getHeight() - 32, 150, 20, MinecraftFactory.getVars().translate("gui.done")));
		addButton(MinecraftFactory.getVars().createButton(200, getWidth() / 2 - 155, getHeight() - 32, 150, 20, MinecraftFactory.getVars().translate("gui.cancel")));
		ITextfield textfield = The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 70, 32, 140, 14, 16);
		if (keyword != null)
			textfield.callSetText(keyword);
		addTextField(textfield);
		guiList = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 50, getHeight() - 48, 0, getWidth(), rows);
		guiList.setRowWidth(140);
		guiList.setSelectedId(lastSelected);
		guiList.onSelect(guiList.getSelectedId(), guiList.getSelectedRow(), false);
		addGuiList(guiList);

		if (textfield.callGetText().length() < 3) {
			rows.clear();
			if (!suggestions.isEmpty()) {
				for (User shownSuggestion : suggestions) {
					rows.add(new ProfileRow(shownSuggestion));
				}
				guiList.callSetHeaderPadding(10);
				guiList.setHeader(I18n.translate("friend.invite.suggestions"));
			}
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		String text = PATTERN.matcher(getTextfieldById(1).callGetText()).replaceAll("");
		getTextfieldById(1).callSetText(text);
		if (text.length() >= 3) {
			guiList.setHeader(null);
			guiList.callSetHeaderPadding(0);

			for (Iterator<ProfileRow> iterator = rows.iterator(); iterator.hasNext();) {
				ProfileRow row = iterator.next();
				if (suggestions.contains(row.user)) {
					iterator.remove();
				}
			}
			lastTyped = System.currentTimeMillis();
		} else {
			rows.clear();
			if (!suggestions.isEmpty()) {
				for (User shownSuggestion : suggestions) {
					rows.add(new ProfileRow(shownSuggestion));
				}
				guiList.callSetHeaderPadding(10);
				guiList.setHeader(I18n.translate("friend.invite.suggestions"));
			}
		}
		keyword = text;
		guiList.setSelectedId(0);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			if (guiList.getSelectedRow() != null) {
				guiList.onSelect(guiList.getSelectedId(), guiList.getSelectedRow(), true);
			} else {
				The5zigMod.getVars().displayScreen(lastScreen);
			}
		}
	}

	@Override
	protected void tick() {
		if (lastTyped != 0 && System.currentTimeMillis() - lastTyped > 500) {
			The5zigMod.getNetworkManager().sendPacket(new PacketUserSearch(PacketUserSearch.Type.KEYWORD, new User[]{new User(getTextfieldById(1).callGetText(), null)}));
			lastTyped = 0;
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		for (Iterator<ProfileRow> iterator = rows.iterator(); iterator.hasNext();) {
			if (iterator.next().toRemove) {
				iterator.remove();
			}
		}
		if (suggestions.isEmpty() && guiList.getHeader() != null) {
			guiList.setHeader(null);
			guiList.callSetHeaderPadding(0);
		}
	}

	@Override
	public void onSelect(int id, ProfileRow row, boolean doubleClick) {
		lastSelected = id;

		if (doubleClick) {
			if (The5zigMod.getNetworkManager().isConnected()) {
				The5zigMod.getFriendManager().removeSuggestion(row.user.getUniqueId());
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendRequest(row.user.getUsername()));
			}
			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}

	@Override
	public String getTitleKey() {
		return "friend.invite.title";
	}

	public static class ProfileRow implements RowExtended {

		private final Base64Renderer base64Renderer = new Base64Renderer();

		private User user;
		private int x, y;
		private boolean toRemove = false;

		public ProfileRow(User user) {
			this.user = user;
		}

		@Override
		public void draw(int x, int y) {
		}

		@Override
		public void draw(int x, int y, int slotHeight, int mouseX, int mouseY) {
			this.x = x;
			this.y = y;

			if ((base64Renderer.getBase64String() == null || !base64Renderer.getBase64String().equals(The5zigMod.getSkinManager().getBase64EncodedSkin(user.getUniqueId()))) &&
					The5zigMod.getSkinManager().getBase64EncodedSkin(user.getUniqueId()) != null) {
				base64Renderer.setBase64String(The5zigMod.getSkinManager().getBase64EncodedSkin(user.getUniqueId()), "player_skin/" + user.getUniqueId());
			}
			base64Renderer.renderImage(x + 2, y + 2, 16, 16);
			The5zigMod.getVars().drawString(user.getUsername(), x + 24, y + 6);

			if (user instanceof FriendSuggestion) {
				int color = mouseX >= x + 120 && mouseX <= x + 126 && mouseY >= y + 5 && mouseY <= y + 13 ? 0xffffa0 : 0xffffff;
				The5zigMod.getVars().drawString("X", x + 120, y + 6, color);
			}
		}

		@Override
		public IButton mousePressed(int mouseX, int mouseY) {
			if (user instanceof FriendSuggestion && mouseX >= x + 120 && mouseX <= x + 126 && mouseY >= y + 5 && mouseY <= y + 13) {
				The5zigMod.getFriendManager().hideSuggestion(user.getUniqueId());
				toRemove = true;
			}
			return null;
		}

		@Override
		public int getLineHeight() {
			return 24;
		}

	}
}
