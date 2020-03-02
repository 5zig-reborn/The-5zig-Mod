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
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.util.MojangAPIManager;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.ExceptionCallback;
import eu.the5zig.util.Utils;

import java.util.*;

public class GuiNameHistory extends Gui implements Clickable<GuiNameHistory.NameRow> {

	private List<NameRow> rows = Lists.newArrayList();

	private String username;
	private boolean notFound;

	public GuiNameHistory(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addTextField(The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 100, 40, 120, 20, 16));
		if (username != null) {
			getTextfieldById(1).callSetText(username);
		}
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 + 25, 40, 75, 20, I18n.translate("name_history.search")));

		IGuiList<NameRow> guiList = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 70, getHeight() - 48, 0, getWidth(), rows);
		guiList.setRowWidth(220);
		guiList.setHeader(I18n.translate("name_history.suggestions"));
		guiList.callSetHeaderPadding(10);
		addGuiList(guiList);

		addBottomDoneButton();
	}

	@Override
	public void onSelect(int id, NameRow row, boolean doubleClick) {
		if (doubleClick) {
			lookupHistoryAndShowResult(row.username);
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			if (!getTextfieldById(1).callGetText().isEmpty()) {
				notFound = false;
				button.setEnabled(false);
				button.setLabel(I18n.translate("name_history.searching"));
				final String username = getTextfieldById(1).callGetText();
				lookupHistoryAndShowResult(username);
			}
		}
	}

	private void lookupHistoryAndShowResult(final String username) {
		The5zigMod.getMojangAPIManager().resolveUUID(username, new ExceptionCallback<String>() {
			@Override
			public void call(String callback, Throwable throwable) {
				if (callback != null) {
					final String uuidString = callback;
					final UUID uuid = Utils.getUUID(uuidString);
					The5zigMod.getMojangAPIManager().resolveNameHistory(callback, new ExceptionCallback<List<MojangAPIManager.NameHistory>>() {
						@Override
						public void call(List<MojangAPIManager.NameHistory> callback, Throwable throwable) {
							final List<MojangAPIManager.NameHistory> rows = Lists.newArrayList();
							if (callback != null) {
								rows.addAll(callback);
							} else {
								MojangAPIManager.NameHistory nameHistory = new MojangAPIManager.NameHistory();
								nameHistory.name = username;
								rows.add(nameHistory);
							}
							The5zigMod.getScheduler().postToMainThread(new Runnable() {
								@Override
								public void run() {
									The5zigMod.getVars().displayScreen(new GuiNameHistoryResult(GuiNameHistory.this, username, uuidString, uuid, rows));
								}
							});
						}
					});
				} else {
					notFound = true;
					getButtonById(1).setEnabled(true);
					getButtonById(1).setLabel(I18n.translate("name_history.search"));
				}
			}
		});
	}

	@Override
	protected void tick() {
		username = getTextfieldById(1).callGetText();

		rows.clear();
		if (The5zigMod.getDataManager().getUsername().startsWith(username)) {
			rows.add(new NameRow(The5zigMod.getDataManager().getUsername()));
		}
		if (!The5zigMod.getVars().isPlayerNull()) {
			List<NetworkPlayerInfo> serverPlayers = The5zigMod.getVars().getServerPlayers();
			for (NetworkPlayerInfo networkPlayerInfo : serverPlayers) {
				if (networkPlayerInfo.getGameProfile().getName().startsWith(username)) {
					NameRow row = new NameRow(networkPlayerInfo.getGameProfile().getName());
					if (!rows.contains(row)) {
						rows.add(row);
					}
				}
			}
		}
		for (Friend friend : The5zigMod.getFriendManager().getFriends()) {
			if (friend.getUsername().startsWith(username)) {
				NameRow row = new NameRow(friend.getUsername());
				if (!rows.contains(row)) {
					rows.add(row);
				}
			}
		}
		Collections.sort(rows, new Comparator<NameRow>() {
			@Override
			public int compare(NameRow o1, NameRow o2) {
				return o1.username.toLowerCase(Locale.ROOT).compareTo(o2.username.toLowerCase(Locale.ROOT));
			}
		});
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (notFound) {
			drawCenteredString(I18n.translate("name_history.profile.not_found"), getWidth() / 2, 28);
		}
	}

	@Override
	public String getTitleKey() {
		return "name_history.title";
	}

	public class NameRow implements Row {

		private String username;

		private NameRow(String username) {
			this.username = username;
		}

		@Override
		public void draw(int x, int y) {
			The5zigMod.getVars().drawString(username, x + 2, y + 2);
		}

		@Override
		public int getLineHeight() {
			return 18;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof NameRow && ((NameRow) obj).username.equals(username);
		}
	}
}
