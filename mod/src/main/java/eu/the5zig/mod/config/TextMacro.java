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

package eu.the5zig.mod.config;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.Row;

import java.util.List;

public class TextMacro implements Row {

	private List<Integer> keys;
	private String message;
	private boolean autoSend = true;

	public transient boolean pressed;

	public TextMacro() {
	}

	public TextMacro(List<Integer> keys, String message, boolean autoSend) {
		this.keys = keys;
		this.message = message;
		this.autoSend = autoSend;
	}

	public List<Integer> getKeys() {
		return keys;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isAutoSend() {
		return autoSend;
	}

	public void setAutoSend(boolean autoSend) {
		this.autoSend = autoSend;
	}

	public String getKeysAsString() {
		StringBuilder macroText = new StringBuilder();
		for (int key : keys) {
			if (macroText.length() != 0) {
				macroText.append(" + ");
			}
			macroText.append(The5zigMod.getVars().getKeyDisplayStringShort(key));
		}
		return macroText.toString();
	}

	@Override
	public void draw(int x, int y) {
		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(message, 100), x + 2, y + 2);
		The5zigMod.getVars().drawString(":", x + 102, y + 2);

		The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(getKeysAsString(), 100), x + 115, y + 2);
	}

	@Override
	public int getLineHeight() {
		return 18;
	}
}
