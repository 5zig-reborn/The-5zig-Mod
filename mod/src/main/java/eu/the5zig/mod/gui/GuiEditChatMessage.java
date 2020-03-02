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
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.ChatFilter;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Arrays;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiEditChatMessage extends Gui {

	private final ChatFilter.ChatFilterMessage chatMessage;
	private final ChatFilter.ChatFilterMessage chatMessageCopy;

	private boolean displayHelp;
	private IButton closeHelpButton;

	public GuiEditChatMessage(Gui lastScreen, ChatFilter.ChatFilterMessage chatMessage) {
		super(lastScreen);
		this.chatMessage = chatMessage;
		this.chatMessageCopy = chatMessage.clone();
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.done")));
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addTextField(The5zigMod.getVars().createTextfield(9, getWidth() / 2 - 150, getHeight() / 6, 145, 20, 150));
		if (chatMessage.getName() != null) {
			getTextfieldById(9).callSetText(chatMessage.getName());
		}
		addTextField(The5zigMod.getVars().createTextfield(2, getWidth() / 2 + 5, getHeight() / 6, 145, 20, 1024));

		addTextField(The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 150, getHeight() / 6 + 40, 300, 20, 1024));
		addTextField(The5zigMod.getVars().createTextfield(3, getWidth() / 2 - 150, getHeight() / 6 + 84, 145, 20, 1024));
		addButton(The5zigMod.getVars()
				.createButton(2, getWidth() / 2 + 5, getHeight() / 6 + 64, 145, 20, I18n.translate("chat_filter.edit.use_regex") + " " + The5zigMod.toBoolean(chatMessage.useRegex())));
		addButton(The5zigMod.getVars()
				.createButton(1, getWidth() / 2 + 5, getHeight() / 6 + 86, 145, 20, I18n.translate("chat_filter.edit.action") + " " + chatMessage.getAction().getName()));

		getTextfieldById(1).callSetText(chatMessage.getMessage());
		getTextfieldById(3).callSetText(chatMessage.getExcept() == null ? "" : chatMessage.getExcept());
		String s = Arrays.toString(chatMessage.getServers());
		if (!s.isEmpty()) {
			s = s.substring(1, s.length() - 1);
		}
		getTextfieldById(2).callSetText(s);
		if (chatMessage.getAction() == ChatFilter.Action.AUTO_TEXT) {
			addTextField(The5zigMod.getVars().createTextfield(4, getWidth() / 2 - 150, getHeight() / 6 + 130, 145, 20, 1024));
			addTextField(The5zigMod.getVars().createTextfield(6, getWidth() / 2 + 5, getHeight() / 6 + 130, 145, 20, 1024));
			addTextField(The5zigMod.getVars().createTextfield(5, getWidth() / 2 + 160, getHeight() / 6 + 130, 40, 20, 4));
			if (chatMessage.getAutoText() != null) {
				getTextfieldById(4).callSetText(chatMessage.getAutoText());
			}
			getTextfieldById(5).callSetText(String.valueOf(chatMessage.getAutoTextDelay()));
			if (chatMessage.getAutoTextCancel() != null) {
				getTextfieldById(6).callSetText(chatMessage.getAutoTextCancel());
			}
		}

		String helpText = I18n.translate("chat_filter.edit.help.title");
		int stringWidth = The5zigMod.getVars().getStringWidth(ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString() + helpText);
		addButton(The5zigMod.getVars().createStringButton(99, getWidth() / 2 + 150 - stringWidth, getHeight() / 6 - 12,
				stringWidth, 10,
				ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString() + helpText));
		closeHelpButton = The5zigMod.getVars().createButton(50, getWidth() / 2 - 75, (getHeight() - 200) / 2 + 135, 150, 20, The5zigMod.getVars().translate("gui.done"));
	}

	@Override
	protected void tick() {
		getButtonById(200).setEnabled(!getTextfieldById(1).callGetText().isEmpty());

		if (displayHelp) {
			closeHelpButton.tick();
		}
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			if (!getTextfieldById(9).callGetText().isEmpty()) {
				chatMessage.setName(getTextfieldById(9).callGetText());
			} else {
				chatMessage.setName(null);
			}
			chatMessage.setUseRegex(chatMessageCopy.useRegex());
			chatMessage.setMessage(getTextfieldById(1).callGetText());
			chatMessage.setExcept(getTextfieldById(3).callGetText());
			chatMessage.setAction(chatMessageCopy.getAction());
			chatMessage.clearServers();
			for (String server : getTextfieldById(2).callGetText().replace(" ", "").split(",")) {
				if (server.isEmpty())
					continue;
				chatMessage.addServer(server);
			}
			if (getTextfieldById(4) != null && !getTextfieldById(4).callGetText().isEmpty()) {
				chatMessage.setAutoText(getTextfieldById(4).callGetText());
			}
			if (getTextfieldById(5) != null && !getTextfieldById(5).callGetText().isEmpty()) {
				try {
					int delay = Integer.parseInt(getTextfieldById(5).callGetText());
					if (delay > 0) {
						chatMessage.setAutoTextDelay(delay);
					}
				} catch (NumberFormatException ignored) {
				}
			}
			if (getTextfieldById(6) != null && !getTextfieldById(6).callGetText().isEmpty()) {
				chatMessage.setAutoTextCancel(getTextfieldById(6).callGetText());
			} else {
				chatMessage.setAutoTextCancel(null);
			}
			The5zigMod.getChatFilterConfig().saveConfig();
		}
		if (button.getId() == 100) {
			if (chatMessage.getMessage().isEmpty()) {
				The5zigMod.getChatFilterConfig().getConfigInstance().getChatMessages().remove(chatMessage);
				The5zigMod.getChatFilterConfig().saveConfig();
			}
			The5zigMod.getVars().displayScreen(lastScreen);
		}
		if (button.getId() == 1) {
			chatMessageCopy.setAction(chatMessageCopy.getAction().getNext());
			button.setLabel(I18n.translate("chat_filter.edit.action") + " " + chatMessageCopy.getAction().getName());
			if (chatMessageCopy.getAction() == ChatFilter.Action.AUTO_TEXT) {
				addTextField(The5zigMod.getVars().createTextfield(4, getWidth() / 2 - 150, getHeight() / 6 + 130, 145, 20, 1024));
				addTextField(The5zigMod.getVars().createTextfield(6, getWidth() / 2 + 5, getHeight() / 6 + 130, 145, 20, 1024));
				addTextField(The5zigMod.getVars().createTextfield(5, getWidth() / 2 + 160, getHeight() / 6 + 130, 40, 20, 4));
			} else if (getTextfieldById(4) != null) {
				removeTextField(getTextfieldById(4));
				removeTextField(getTextfieldById(5));
				removeTextField(getTextfieldById(6));
			}
		}
		if (button.getId() == 2) {
			chatMessageCopy.setUseRegex(!chatMessageCopy.useRegex());
			button.setLabel(I18n.translate("chat_filter.edit.use_regex") + " " + The5zigMod.toBoolean(chatMessageCopy.useRegex()));
		}
		if (button.getId() == 99) {
			displayHelp = true;
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.name"), getWidth() / 2 - 150, getHeight() / 6 - 12);
		The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.servers"), getWidth() / 2 + 5, getHeight() / 6 - 12);
		The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.chat_message"), getWidth() / 2 - 150, getHeight() / 6 + 28);
		The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.except"), getWidth() / 2 - 150, getHeight() / 6 + 70);
		if (chatMessageCopy.getAction() == ChatFilter.Action.AUTO_TEXT) {
			The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.auto_text"), getWidth() / 2 - 150, getHeight() / 6 + 118);
			The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.auto_text_cancel"), getWidth() / 2 + 5, getHeight() / 6 + 118);
			The5zigMod.getVars().drawString(I18n.translate("chat_filter.edit.auto_text_delay"), getWidth() / 2 + 160, getHeight() / 6 + 118);
		}
	}

	@Override
	public void mouseClicked0(int x, int y, int button) {
		if (displayHelp) {
			if (closeHelpButton.mouseClicked(x, y)) {
				closeHelpButton.playClickSound();
				displayHelp = false;
			}
			return;
		}
		super.mouseClicked0(x, y, button);
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		if (displayHelp) {
			closeHelpButton.callMouseReleased(x, y);
		}
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);
		if (displayHelp) {
			GLUtil.color(1, 1, 1, 1);
			The5zigMod.getVars().bindTexture(The5zigMod.DEMO_BACKGROUND);
			drawTexturedModalRect((getWidth() - 247) / 2, (getHeight() - 200) / 2, 0, 0, 256, 256);
			The5zigMod.getVars().drawCenteredString(ChatColor.BOLD + I18n.translate("chat_filter.edit.title"), getWidth() / 2, (getHeight() - 200) / 2 + 10);
			int y = 0;
			for (String line : The5zigMod.getVars().splitStringToWidth(I18n.translate("chat_filter.edit.help"), 236)) {
				drawCenteredString(ChatColor.WHITE + line, getWidth() / 2, (getHeight() - 200) / 2 + 30 + y);
				y += 10;
			}
			closeHelpButton.draw(mouseX, mouseY);
		}
	}

	@Override
	public String getTitleKey() {
		return "chat_filter.edit.title";
	}
}
