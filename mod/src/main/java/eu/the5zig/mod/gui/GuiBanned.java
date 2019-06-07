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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Iterator;
import java.util.List;

public class GuiBanned extends Gui {

	private String reason;
	private long time;
	private List<?> reasonList;

	private int exitCount = 20 * 15;

	public GuiBanned(String reason, long time) {
		super(null);
		this.reason = reason.replace("\r\n", "\n");
		this.time = time;
	}

	@Override
	public void initGui() {
		this.reasonList = The5zigMod.getVars().splitStringToWidth(reason, getWidth() - 50);
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawMenuBackground();
		int y = 100;
		for (Iterator<?> it = reasonList.iterator(); it.hasNext(); y += The5zigMod.getVars().getFontHeight()) {
			drawCenteredString((String) it.next(), getWidth() / 2, y);
		}
		y += 20;
		drawCenteredString("Ban-Time: " + Utils.convertToDate(time).replace("Today", I18n.translate("profile.today").replace("Yesterday", I18n.translate("profile.yesterday"))),
				getWidth() / 2, y);
		drawCenteredString(I18n.translate("banned.exiting", exitCount / 20), getWidth() / 2, getHeight() - 38);
		drawCenteredString(ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + I18n.translate("banned.help"), getWidth() / 2, getHeight() - 25);
	}

	@Override
	protected void tick() {
		exitCount--;
		if (exitCount < 0)
			The5zigMod.getVars().shutdown();
	}

	@Override
	protected void actionPerformed(IButton button) {
	}

	/**
	 * On Key Type. Override that method so that the user cannot press ESC anymore.
	 */
	@Override
	public void keyTyped0(char paramChar, int paramInt) {
	}

	@Override
	public String getTitleKey() {
		return "banned.title";
	}
}