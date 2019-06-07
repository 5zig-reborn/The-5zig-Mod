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

package eu.the5zig.mod.config;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.Row;

public class TextReplacement implements Row {

	private String message;
	private String replacement;
	private boolean ignoresCommands = true;
	private boolean replaceInsideWords = true;

	public TextReplacement(String message, String replacement, boolean ignoresCommands, boolean replaceInsideWords) {
		this.message = message;
		this.replacement = replacement;
		this.ignoresCommands = ignoresCommands;
		this.replaceInsideWords = replaceInsideWords;
	}

	public TextReplacement() {
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	public boolean isIgnoringCommands() {
		return ignoresCommands;
	}

	public void setIgnoringCommands(boolean ignoresCommands) {
		this.ignoresCommands = ignoresCommands;
	}

	public boolean isReplaceInsideWords() {
		return replaceInsideWords;
	}

	public void setReplaceInsideWords(boolean replaceInsideWords) {
		this.replaceInsideWords = replaceInsideWords;
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(message, 100), x + 2, y + 2);
		The5zigMod.getVars().drawString("=>", x + 102, y + 2);
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(replacement, 100), x + 115, y + 2);
	}

	@Override
	public int getLineHeight() {
		return 18;
	}
}
