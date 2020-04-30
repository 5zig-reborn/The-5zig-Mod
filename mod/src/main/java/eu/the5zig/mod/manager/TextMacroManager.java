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

package eu.the5zig.mod.manager;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.TextMacro;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.WorldTickEvent;
import eu.the5zig.mod.util.Keyboard;

import java.util.List;

public class TextMacroManager {

	@EventHandler
	public void onTick(WorldTickEvent event) {
		if (The5zigMod.getVars().isPlayerNull() || The5zigMod.getVars().getMinecraftScreen() != null) {
			return;
		}
		List<TextMacro> macros = The5zigMod.getTextMacroConfiguration().getConfigInstance().getMacros();
		for (TextMacro macro : macros) {
			int pressed = 0;
			for (Integer key : macro.getKeys()) {
				if (Keyboard.isKeyDown(key)) {
					pressed++;
				}
			}
			if (pressed == macro.getKeys().size()) {
				if (!macro.pressed) {
					String autoText = macro.getMessage();
					autoText = ChatFilterManager.replacePlaceholders(autoText);
					if (macro.isAutoSend()) {
						The5zigMod.getListener().doSendChatMessage(autoText);
					} else {
						The5zigMod.getVars().typeInChatGUI(autoText);
					}
					macro.pressed = true;
				}
			} else {
				macro.pressed = false;
			}
		}
	}

}
