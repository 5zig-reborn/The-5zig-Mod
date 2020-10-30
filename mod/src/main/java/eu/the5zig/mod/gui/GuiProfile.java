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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiProfile extends GuiOptions implements CenteredTextfieldCallback {

	private static final Base64Renderer base64Renderer = new Base64Renderer();

	private GuiArrayList<Row> rows = new GuiArrayList<>();

	public GuiProfile(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if ((base64Renderer.getBase64String() == null || !base64Renderer.getBase64String().equals(
				The5zigMod.getSkinManager().getBase64EncodedSkin(The5zigMod.getDataManager().getUniqueId()))) && The5zigMod.getSkinManager().getBase64EncodedSkin(
				The5zigMod.getDataManager().getUniqueId()) != null)
			base64Renderer.setBase64String(The5zigMod.getSkinManager().getBase64EncodedSkin(The5zigMod.getDataManager().getUniqueId()),
					"player_skin/" + The5zigMod.getDataManager().getUniqueId());
		base64Renderer.renderImage(getWidth() / 2 - 155, 40, 88, 88);
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 170, 200, 20, MinecraftFactory.getVars().translate("gui.done")));

		rows.clear();
		int maxWidth = getWidth() / 2 + 155 - (getWidth() / 2 - 155 + 16 + 88) - 10;
		rows.add(new BasicRow(ChatColor.UNDERLINE + I18n.translate("profile.title"), maxWidth) {
			@Override
			public int getLineHeight() {
				return 14;
			}
		});
		rows.add(new BasicRow(String.format("%s%s: %s#%s", ChatColor.YELLOW, I18n.translate("profile.id"), ChatColor.RESET, The5zigMod.getDataManager().getProfile().getId()), maxWidth));
		rows.add(new BasicRow(String.format("%s%s: %s", ChatColor.YELLOW, I18n.translate("profile.name"), The5zigMod.getDataManager().getColoredName()), maxWidth));
		rows.add(new BasicRow(String.format("%s%s: %s", ChatColor.YELLOW, I18n.translate("profile.first_login_time"), ChatColor.RESET + Utils.convertToDate(
				The5zigMod.getDataManager().getProfile().getFirstTime()).replace("Today", I18n.translate("profile.today")).replace("Yesterday", I18n.translate("profile.yesterday"))),
				maxWidth));
		rows.add(new BasicRow(String.format("%s%s: %s", ChatColor.YELLOW, I18n.translate("profile.rank"),
				Rank.buildList(The5zigMod.getDataManager().getProfile().getRank())), maxWidth));
		int x = getWidth() / 2 - 155 + 16 + 88 + The5zigMod.getVars().getStringWidth(ChatColor.YELLOW + I18n.translate("profile.message") + ":") + 10;
		rows.add(new ButtonRow(
				The5zigMod.getVars().createStringButton(9, x, 88 + 40, The5zigMod.getVars().getStringWidth(I18n.translate("profile.edit")) + 2, 9, I18n.translate("profile.edit")), null) {
			@Override
			public void draw(int x, int y) {
				The5zigMod.getVars().drawString(ChatColor.YELLOW + I18n.translate("profile.message") + ":", x + 2, y + 2);
			}

			@Override
			public int getLineHeight() {
				return 12;
			}
		});

		rows.add(new BasicRow(The5zigMod.getDataManager().getProfile().getProfileMessage(), maxWidth));

		IGuiList statusMessage = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 40, 40 + 110, getWidth() / 2 - 155 + 16 + 88, getWidth() / 2 + 155, rows);
		statusMessage.setBottomPadding(4);
		statusMessage.setRowWidth(400);
		statusMessage.setLeftbound(true);
		statusMessage.setDrawSelection(false);
		statusMessage.setScrollX(getWidth() / 2 + 155 - 5);
		addGuiList(statusMessage);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 155, getHeight() / 6 + 120, 150, 20, I18n.translate("profile.settings.view")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 5, getHeight() / 6 + 120, 150, 20, I18n.translate("chat.settings")));
		addButton(The5zigMod.getVars().createButton(3, getWidth() / 2 - 155, getHeight() / 6 + 144, 150, 20, I18n.translate("profile.blocked_contacts")));
		addButton(The5zigMod.getVars().createButton(4, getWidth() / 2 + 5, getHeight() / 6 + 144, 150, 20, I18n.translate("profile.show_statistics")));

	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiSettings(this, "profile_settings"));
		}
		if (button.getId() == 2) {
			The5zigMod.getVars().displayScreen(new GuiSettings(this, "chat_settings"));
		}
		if (button.getId() == 3) {
			The5zigMod.getVars().displayScreen(new GuiBlockedUsers(this));
		}
		if (button.getId() == 4) {
			The5zigMod.getVars().displayScreen(new GuiNetworkStatistics(this));
		}
		if (button.getId() == 9) {
			The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, this, 255));
		}
	}

	@Override
	public void onDone(String text) {
		The5zigMod.getDataManager().getProfile().setProfileMessage(text);
	}

	@Override
	public String title() {
		return I18n.translate("profile.enter_new_profile_message");
	}

	@Override
	public String getTitleKey() {
		return "profile.title";
	}
}
