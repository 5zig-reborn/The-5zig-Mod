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

package eu.the5zig.mod.listener;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.util.IKeybinding;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.Mouse;

public class ZoomListener {

	private boolean zoomed = false;
	private float previousFOV;
	private boolean previousSmoothCamera;

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getVars().getMinecraftScreen() == null && isKeyDown(The5zigMod.getKeybindingManager().zoom)) {
			if (!zoomed) {
				zoomed = true;
				previousFOV = The5zigMod.getVars().getFOV();
				previousSmoothCamera = The5zigMod.getVars().isSmoothCamera();

				The5zigMod.getVars().setFOV(previousFOV / The5zigMod.getConfig().getFloat("zoomFactor"));
				The5zigMod.getVars().setSmoothCamera(true);
			}
		} else {
			if (zoomed) {
				zoomed = false;

				The5zigMod.getVars().setFOV(previousFOV);
				The5zigMod.getVars().setSmoothCamera(previousSmoothCamera);
			}
		}
	}

	private boolean isKeyDown(IKeybinding keybinding) {
		return keybinding.callGetKeyCode() != 0 && (keybinding.callGetKeyCode() < 0 ? Mouse.isButtonDown(keybinding.callGetKeyCode() + 100) : keybinding.callGetKeyCode() < 256 && Keyboard.isKeyDown(
				keybinding.callGetKeyCode()));
	}

}
