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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.KeyPressEvent;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.*;
import eu.the5zig.mod.gui.elements.IWrappedTextfield;
import eu.the5zig.mod.gui.ts.GuiTeamSpeak;
import eu.the5zig.mod.util.IKeybinding;
import eu.the5zig.mod.util.Keyboard;

import java.util.List;
import java.util.Map;

/**
 * This class is used to register and store all keybindings of the mod.
 */
public class KeybindingManager {

	public final IKeybinding toggleMod;
	public final IKeybinding toggleChat;
	public final IKeybinding saveCoords;
	public final IKeybinding raidTracker;
	public final IKeybinding hypixel;
	public final IKeybinding zoom;
	public final IKeybinding timerToggleStart;
	public final IKeybinding timerReset;
	public final IKeybinding teamspeak;
	public final IKeybinding nameHistory;
	public final IKeybinding toggleChatFilter;

	private final Map<RegisteredKeybinding, Class[]> globalKeybindings = Maps.newHashMap();
	private final List<IWrappedTextfield> initializedTextfields = Lists.newArrayList();
	private IWrappedGui lastScreen;

	public KeybindingManager() {
		The5zigMod.getListener().registerListener(this);

		toggleMod = registerKeybinding("the5zigmod.key.toggle_mod", Keyboard.KEY_M);
		toggleChat = registerKeybinding("the5zigmod.key.toggle_chat", Keyboard.KEY_F4, GuiFriends.class, GuiConversations.class, GuiParty.class);
		saveCoords = registerKeybinding("the5zigmod.key.coordinates_clipboard", Keyboard.KEY_F12);
		raidTracker = registerKeybinding("the5zigmod.key.raid_calculator", Keyboard.KEY_NONE);
		hypixel = registerKeybinding("the5zigmod.key.hypixel_stats", Keyboard.KEY_NONE);
		zoom = registerKeybinding("the5zigmod.key.zoom", Keyboard.KEY_Z);
		timerToggleStart = registerKeybinding("the5zigmod.key.timer.start", Keyboard.KEY_NUMPAD0);
		timerReset = registerKeybinding("the5zigmod.key.timer.reset", Keyboard.KEY_DECIMAL);
		teamspeak = registerKeybinding("the5zigmod.key.teamspeak", Keyboard.KEY_F9, GuiTeamSpeak.class);
		nameHistory = registerKeybinding("the5zigmod.key.username_history", Keyboard.KEY_NONE, GuiNameHistory.class);
		toggleChatFilter = registerKeybinding("the5zigmod.key.toggle_chat_filter", Keyboard.KEY_NONE);
	}

	private IKeybinding registerKeybinding(String description, int keyCode, Class... guiToOpen) {
		IKeybinding keybinding = The5zigMod.getVars().createKeybinding(description, keyCode, "The 5zig Mod");
		if (guiToOpen != null && guiToOpen.length != 0) {
			globalKeybindings.put(new RegisteredKeybinding(keybinding), guiToOpen);
		}
		The5zigMod.getVars().registerKeybindings(ImmutableList.of(keybinding));
		return keybinding;
	}

	public void flushTextfields() {
		initializedTextfields.clear();
	}

	public void onTextFieldInit(Object textfield) {
		initializedTextfields.add(The5zigMod.getVars().createWrappedTextfield(textfield));
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getVars().isSignGUIOpened()) {
			return;
		}
		for (IWrappedTextfield textfield : initializedTextfields) {
			if (textfield.isSelected()) {
				return;
			}
		}
		for (Map.Entry<RegisteredKeybinding, Class[]> entry : globalKeybindings.entrySet()) {
			RegisteredKeybinding registeredKeybinding = entry.getKey();
			if (registeredKeybinding.pressTime > 0) {
				registeredKeybinding.pressTime=0;
				Class<?>[] guiArray = entry.getValue();

				if (The5zigMod.getVars().getCurrentScreen() != null) {
					boolean cont = false;
					for (int i = 0; i < guiArray.length; i++) {
						Class<?> aClass = guiArray[i];
						if (aClass.isAssignableFrom(The5zigMod.getVars().getCurrentScreen().getClass())) {
							The5zigMod.getVars().displayScreen(lastScreen);
							registeredKeybinding.preferredGui = i;
							cont = true;
							break;
						}
					}
					if (cont) {
						continue;
					}
				}
				if (The5zigMod.getVars().getMinecraftScreen() instanceof Gui && !(The5zigMod.getVars().getCurrentScreen() instanceof GuiWelcome)) {
					lastScreen = null;
				} else {
					lastScreen = MinecraftFactory.getVars().createWrappedGui(The5zigMod.getVars().getMinecraftScreen());
				}

				Gui gui;
				try {
					gui = (Gui) guiArray[registeredKeybinding.preferredGui].getConstructor(Gui.class).newInstance(lastScreen);
				} catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
				The5zigMod.getVars().displayScreen(gui);
			}
		}
	}

	@EventHandler
	public void onKeyPress(KeyPressEvent event) {
		for (IWrappedTextfield textfield : initializedTextfields) {
			if (textfield.isSelected()) {
				return;
			}
		}
		for (RegisteredKeybinding registeredKeybinding : globalKeybindings.keySet()) {
			if (event.getKeyCode() != -1 && event.getKeyCode() == registeredKeybinding.keybinding.callGetKeyCode()) {
				registeredKeybinding.pressTime++;
			}
		}
	}

	private class RegisteredKeybinding {

		private IKeybinding keybinding;
		private int pressTime;
		private int preferredGui;

		private RegisteredKeybinding(IKeybinding keybinding) {
			this.keybinding = keybinding;
		}
	}

}
