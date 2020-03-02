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

package eu.the5zig.mod.chat.gui;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.FileMessage;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.gui.GuiParty;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ChatLine implements Row {

	public float STATUS_SCALE = 0.6f;
	public int LINE_HEIGHT = 12;
	public int MESSAGE_HEIGHT = 18;
	protected final Message message;

	public ChatLine(Message message) {
		this.message = message;
	}

	public static ChatLine fromMessage(Message message) {
		switch (message.getMessageType()) {
			case CENTERED:
				return new CenteredChatLine(message);
			case DATE:
				return new DateChatLine(message);
			case IMAGE:
				return new ImageChatLine(message);
			case AUDIO:
				return new AudioChatLine(message);
			default:
				return new ChatLine(message);
		}
	}

	@Override
	public void draw(int x, int y) {
		Gui gui = The5zigMod.getVars().getCurrentScreen();
		if (gui == null) {
			return;
		}

		String time = ChatColor.GRAY + Utils.convertToTimeWithMinutes(message.getTime());
		int timeWidth = (int) (The5zigMod.getVars().getStringWidth(time) * STATUS_SCALE);

		List<String> lines = The5zigMod.getVars().splitStringToWidth(message.toString(), getMaxMessageWidth());
		int yy = 2;
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			int lineWidth = The5zigMod.getVars().getStringWidth(line);
			int xOff = 0;
			if (i + 1 == lines.size()) {
				xOff += timeWidth;
			}
			if (message.getMessageType() == Message.MessageType.LEFT) {
				The5zigMod.getVars().drawString(line, x + 2, y + yy);
				if (xOff > 0)
					Gui.drawScaledString(time, x + 2 + lineWidth + 6, y + yy + 3, STATUS_SCALE);
			}
			if (message.getMessageType() == Message.MessageType.RIGHT) {
				if (gui instanceof GuiParty) {
					if (xOff > 0) {
						The5zigMod.getVars().drawString(line, gui.getWidth() - 122 - lineWidth - xOff - 6, y + yy);
						Gui.drawScaledString(time, gui.getWidth() - 122 - timeWidth, y + yy + 3, STATUS_SCALE);
					} else {
						The5zigMod.getVars().drawString(line, gui.getWidth() - 122 - lineWidth, y + yy);
					}
				} else {
					if (xOff > 0) {
						The5zigMod.getVars().drawString(line, gui.getWidth() - 22 - lineWidth - xOff - 6, y + yy);
						Gui.drawScaledString(time, gui.getWidth() - 22 - timeWidth, y + yy + 3, STATUS_SCALE);
					} else {
						The5zigMod.getVars().drawString(line, gui.getWidth() - 22 - lineWidth, y + yy);
					}
				}
			}
			yy += LINE_HEIGHT;
		}

		if (message.getMessageType() == Message.MessageType.RIGHT) {
			Message lastMessage = null;
			List<Message> messages = Lists.newArrayList(message.getConversation().getMessages());
			for (Message conversationMessage : messages) {
				if (conversationMessage.getMessageType() == Message.MessageType.RIGHT ||
						(conversationMessage instanceof FileMessage && ((FileMessage) conversationMessage).getFileData().isOwn()))
					lastMessage = conversationMessage;
			}
			if (lastMessage == null || !lastMessage.equals(message))
				return;
			String status;
			switch (message.getConversation().getStatus()) {
				case SENT:
					status = I18n.translate("chat.status.sent");
					break;
				case DELIVERED:
					status = I18n.translate("chat.status.delivered");
					break;
				case READ:
					status = I18n.translate("chat.status.read");
					break;
				default:
					status = I18n.translate("chat.status.pending");
					break;
			}
			String string = ChatColor.ITALIC.toString() + status;
			int stringWidth = (int) (STATUS_SCALE * The5zigMod.getVars().getStringWidth(string));
			if (gui instanceof GuiParty) {
				Gui.drawScaledString(string, gui.getWidth() - 122 - stringWidth, y + yy, STATUS_SCALE);
			} else {
				Gui.drawScaledString(string, gui.getWidth() - 22 - stringWidth, y + yy, STATUS_SCALE);
			}
		}
	}

	public int getMaxMessageWidth() {
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
			return ((GuiConversations) The5zigMod.getVars().getCurrentScreen()).getChatBoxWidth() / 3 * 2;
		}
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiParty) {
			return ((GuiParty) The5zigMod.getVars().getCurrentScreen()).getChatBoxWidth() / 3 * 2;
		}
		return 100;
	}

	@Override
	public int getLineHeight() {
		List<?> objects = The5zigMod.getVars().splitStringToWidth(message.toString(), getMaxMessageWidth());
		return (objects.size() - 1) * LINE_HEIGHT + MESSAGE_HEIGHT;
	}

	public Message getMessage() {
		return message;
	}

}
