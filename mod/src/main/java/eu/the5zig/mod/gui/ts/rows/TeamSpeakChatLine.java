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

package eu.the5zig.mod.gui.ts.rows;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.gui.ChatLine;
import eu.the5zig.teamspeak.api.Message;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

public class TeamSpeakChatLine extends ChatLine {

	private String messageContent;
	private final int width;

	public TeamSpeakChatLine(Message message, int width) {
		super(new eu.the5zig.mod.chat.entity.Message(null, 0, null, message.getMessage(), message.getTime(), null));
		LINE_HEIGHT = 10;
		MESSAGE_HEIGHT = 12;
		this.messageContent = message.getMessage();
		int errorIndex = this.messageContent.indexOf("An error occurred: ");
		if (errorIndex == 11) {
			this.messageContent = this.messageContent.substring(0, errorIndex) + ChatColor.RED + this.messageContent.substring(errorIndex, this.messageContent.length());
		}
		int urlStartIndex = this.messageContent.indexOf("[URL]");
		int urlEndIndex = this.messageContent.lastIndexOf("[/URL]");
		while (urlStartIndex != -1 && urlEndIndex != -1 && urlEndIndex > urlStartIndex) {
			String start = this.messageContent.substring(0, urlStartIndex);
			String middle = this.messageContent.substring(urlStartIndex + "[URL]".length(), urlEndIndex);
			String end = this.messageContent.substring(urlEndIndex + "[/URL]".length(), this.messageContent.length());
			this.messageContent = start + middle + end;
			urlStartIndex = this.messageContent.indexOf("[URL]");
			urlEndIndex = this.messageContent.lastIndexOf("[/URL]");
		}
		this.message.setMessage(this.messageContent);

		this.width = width;
	}

	@Override
	public void draw(int x, int y) {
		List<String> lines = The5zigMod.getVars().splitStringToWidth(messageContent, width);
		for (String line : lines) {
			The5zigMod.getVars().drawString(line, x, y);
			y += LINE_HEIGHT;
		}
	}

	@Override
	public int getLineHeight() {
		List<?> objects = The5zigMod.getVars().splitStringToWidth(messageContent, width);
		return (objects.size() - 1) * LINE_HEIGHT + MESSAGE_HEIGHT;
	}

	public String getMessageContent() {
		return messageContent;
	}

}
