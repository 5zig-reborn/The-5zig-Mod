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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.minecraft.ChatColor;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiWelcome extends Gui {

	private int index = 0;
	private int time = 0;
	private int underlined = 0;
	private String string = "Welcome to The 5zig Mod v" + Version.VERSION;
	private boolean underlinePlus = true;
	private int pulse = 0;

	public GuiWelcome() {
		super(null);
		The5zigMod.getTrayManager().displayMessage("Welcome to The 5zig Mod!",
				"This Tray Notification will be displayed every time you get a new message from a Friend while you haven't focused your game. You can disable this feature by simply right clicking " +
						"this icon and clicking on \"Disable\"");
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, "GO!"));
	}

	@Override
	protected void actionPerformed(IButton button) {
	}

	@Override
	protected void tick() {
		time++;
		if (time > 1) {
			time = 0;
			index++;
			if (index > string.length() + 10) {
				index = 0;
			}
		}
		if (underlinePlus)
			underlined++;
		else
			underlined--;
		if (underlined > string.length() / 2 + 4) {
			underlinePlus = false;
		}
		if (underlined < 0) {
			underlinePlus = true;
		}
		pulse++;
		if (pulse > 40)
			pulse = 0;
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		String result = "";
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			int u = string.length() / 2 - underlined;
			int u2 = string.length() / 2 + underlined;
			if (i >= u && i <= u2)
				result += (i == index ? ChatColor.YELLOW : ChatColor.GOLD) + ChatColor.UNDERLINE.toString() + ChatColor.BOLD.toString();
			else
				result += ChatColor.RESET.toString() + (i == index ? ChatColor.YELLOW : ChatColor.GOLD) + ChatColor.BOLD;
			result += c;
		}
		drawCenteredString(result, getWidth() / 2, 20);
		drawCenteredString(ChatColor.ITALIC + "The new PvP Experience", getWidth() / 2, 30);

		int y = 48;
		The5zigMod.getVars().drawString("Get supported while PvPing with many useful stats.", getWidth() / 2 - 150, y);
		The5zigMod.getVars().drawString("Go to \"Options\" -> \"The 5zig Mod...\" to customize the mod.", getWidth() / 2 - 150, y += 16);
		The5zigMod.getVars().drawString("Discover many different options.", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("Enable, disable or scale the mod.", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("Enjoy full support for many different servers", getWidth() / 2 - 150, y += 16);
		The5zigMod.getVars().drawString("Supports Servers like timolia.de, gommehd.net, mc.hypixel.net", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("mc.playminity.com and many more and shows all kinds", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("of different stats.", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("Press F4 to open the Chat Gui.", getWidth() / 2 - 150, y += 16);
		The5zigMod.getVars().drawString("Send unlimited and ultra-fast messages to your friends.", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("Add Friends and see what server they are currently playing on.", getWidth() / 2 - 140, y += 12);
		The5zigMod.getVars().drawString("Create Group Chats and Chat with multiple Friends at once.", getWidth() / 2 - 140, y += 12);

		The5zigMod.getVars().drawString("Note: by using our online features you agree to our Privacy Policy.", getWidth() / 2 - 140, y += 24);
		The5zigMod.getVars().drawString("You can find it over at §6https://5zigreborn.eu/privacy§r.", getWidth() / 2 - 140, y += 12);

		getButtonById(200).setLabel("What are you waiting for? Start " + (pulse > 20 ? ChatColor.GOLD : ChatColor.RESET) + "NOW" + ChatColor.RESET + "!");
	}

	@Override
	public String getTitleName() {
		return "";
	}
}
