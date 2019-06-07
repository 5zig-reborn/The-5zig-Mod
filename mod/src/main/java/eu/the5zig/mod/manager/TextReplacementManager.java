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

package eu.the5zig.mod.manager;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.TextReplacement;
import eu.the5zig.mod.event.ChatSendEvent;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.SignEditEvent;

import java.util.List;

public class TextReplacementManager {

	@EventHandler
	public void onSendChatMessage(ChatSendEvent event) {
		event.setMessage(replaceText(event.getMessage()));
	}

	@EventHandler
	public void onSignEdit(SignEditEvent event) {
		for (int i = 0; i < event.getLines().length; i++) {
			event.setLine(i, replaceText(event.getLine(i)));
		}
	}

	public static String replaceText(String text) {
		List<TextReplacement> replacements = The5zigMod.getTextReplacementConfig().getConfigInstance().getReplacements();
		for (TextReplacement replacement : replacements) {
			if (text.startsWith("/") && replacement.isIgnoringCommands()) {
				continue;
			}
			int index;
			int nextIndex = 0;
			while ((index = text.indexOf(replacement.getMessage(), nextIndex)) != -1) {
				nextIndex = index + replacement.getMessage().length();
				if (!replacement.isReplaceInsideWords()) {
					if (index > 0) {
						char previousChar = Character.toLowerCase(text.charAt(index - 1));
						if (previousChar != ' ') {
							continue;
						}
					}
					if (index + replacement.getMessage().length() < text.length()) {
						char nextChar = text.charAt(index + replacement.getMessage().length());
						if (nextChar != ' ') {
							continue;
						}
					}

				}
				nextIndex = index + replacement.getReplacement().length();
				text = text.substring(0, index) + replacement.getReplacement() + text.substring(index + replacement.getMessage().length(),
						text.length());
			}
		}
		return text;
	}

}
