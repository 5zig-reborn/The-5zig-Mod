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

import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.list.GuiArrayList;
import eu.the5zig.mod.render.Base64Renderer;
import eu.the5zig.mod.util.MojangAPIManager;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.UUID;

public class GuiNameHistoryResult extends Gui {

	private final Base64Renderer base64Renderer = new Base64Renderer();

	private final String username;
	private final String uuidString;
	private final UUID uuid;
	private final GuiArrayList<MojangAPIManager.NameHistory> rows;

	public GuiNameHistoryResult(Gui lastScreen, String username, String uuidString, UUID uuid, GuiArrayList<MojangAPIManager.NameHistory> rows) {
		super(lastScreen);
		this.username = username;
		this.uuidString = uuidString;
		this.uuid = uuid;
		this.rows = rows;
	}

	@Override
	public void initGui() {
		addBottomDoneButton();
		IGuiList guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 70, getHeight() - 48, 0, getWidth(), rows);
		guiList.setRowWidth(220);
		guiList.setDrawSelection(false);
		guiList.setHeader(I18n.translate("name_history.profile.history"));
		guiList.callSetHeaderPadding(10);
		addGuiList(guiList);
	}

	@Override
	protected void actionPerformed(IButton button) {
	}

	@Override
	protected void mouseClicked(int x, int y, int state) {
		if (x >= getWidth() / 2 - 66 && x <= getWidth() / 2 - 66 + The5zigMod.getVars().getStringWidth(uuid.toString()) && y >= 44 && y < 54) {
			Utils.copyToClipboard(uuid.toString());
			The5zigMod.getVars().playSound("ui.button.click", 1);
		}
		if (x >= getWidth() / 2 - 66 && x <= getWidth() / 2 - 66 + The5zigMod.getVars().getStringWidth(uuidString) && y >= 54 && y < 64) {
			Utils.copyToClipboard(uuidString);
			The5zigMod.getVars().playSound("ui.button.click", 1);
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if ((base64Renderer.getBase64String() == null || !base64Renderer.getBase64String().equals(The5zigMod.getSkinManager().getBase64EncodedSkin(uuid))) &&
				The5zigMod.getSkinManager().getBase64EncodedSkin(uuid) != null) {
			base64Renderer.setBase64String(The5zigMod.getSkinManager().getBase64EncodedSkin(uuid), "player_skin/" + uuid);
		}
		base64Renderer.renderImage(getWidth() / 2 - 104, 30, 32, 32);
		The5zigMod.getVars().drawString(ChatColor.BOLD + username, getWidth() / 2 - 66, 32);
		boolean hover = mouseX >= getWidth() / 2 - 66 && mouseX <= getWidth() / 2 - 66 + The5zigMod.getVars().getStringWidth(uuid.toString()) && mouseY >= 44 && mouseY < 54;
		The5zigMod.getVars().drawString((hover ? ChatColor.GRAY : ChatColor.DARK_GRAY) + uuid.toString(), getWidth() / 2 - 66, 44);
		boolean hover2 = mouseX >= getWidth() / 2 - 66 && mouseX <= getWidth() / 2 - 66 + The5zigMod.getVars().getStringWidth(uuidString) && mouseY >= 54 && mouseY < 64;
		The5zigMod.getVars().drawString((hover2 ? ChatColor.GRAY : ChatColor.DARK_GRAY) + uuidString, getWidth() / 2 - 66, 54);

		if (hover || hover2) {
			drawHoveringText(ImmutableList.of(I18n.translate("name_history.profile.copy")), mouseX, mouseY);
		}
	}

	@Override
	public String getTitleKey() {
		return "name_history.profile.title";
	}

}
