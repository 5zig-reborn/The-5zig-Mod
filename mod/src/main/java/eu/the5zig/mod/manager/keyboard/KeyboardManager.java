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

package eu.the5zig.mod.manager.keyboard;

import com.google.common.collect.Maps;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.StringItem;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.gui.ingame.PotionEffect;
import eu.the5zig.mod.util.Display;
import eu.the5zig.mod.util.NativeLibrary;
import eu.the5zig.util.Utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class KeyboardManager {

	private final Map<KeyboardController.Device, KeyboardController> devices = Maps.newHashMap();

	private KeyboardController activeController;

	private boolean displayFocused = false;
	private float lastHealth, lastArmor;
	private PotionEffect activePotion = null;

	public KeyboardManager() {
		The5zigMod.getListener().registerListener(this);
		if (Utils.getPlatform() == Utils.Platform.WINDOWS) {
			try {
				NativeLibrary.load("vcruntime140-x${arch}", NativeLibrary.NativeOS.WINDOWS);
			} catch (Throwable e) {
				The5zigMod.logger.warn("Could not load native library vcruntime140.dll!", e);
			}
		}
		register(KeyboardController.Device.RAZER, new RazerController());
		register(KeyboardController.Device.ROCCAT, new RoccatController());
		register(KeyboardController.Device.LOGITECH, new LogitechController());

		updateDevice();
	}

	public void updateDevice() {
		displayFocused = false;
		if (activeController != null) {
			activeController.unInit();
		}
		KeyboardController.Device current = The5zigMod.getConfig().getEnum("controlKeyboard", KeyboardController.Device.class);
		activeController = devices.get(current);
		if (activeController == null) {
			return;
		}
		updateActiveLedKeys(false);
		updateShowHealth(false);
		updateShowArmor(false);
		activeController.updateHealthAndArmor(lastHealth, lastArmor);
	}

	private void register(KeyboardController.Device device, KeyboardController controller) {
		try {
			// only call init for same targets
			Utils.Platform platform = Utils.getPlatform();
			if (controller.getTarget() == NativeLibrary.NativeOS.ANY ||
					(controller.getTarget() == NativeLibrary.NativeOS.WINDOWS && platform == Utils.Platform.WINDOWS) ||
					(controller.getTarget() == NativeLibrary.NativeOS.UNIX && platform != Utils.Platform.WINDOWS)) {
				if (controller.getNativeNames() != null) {
					for (String nativeName : controller.getNativeNames()) {
						NativeLibrary.load(nativeName, controller.getTarget());
					}
				}
				devices.put(device, controller);
			}
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not initialize keyboard controller " + controller, e);
		}
	}

	private void init() {
		if (activeController != null) {
			boolean result = activeController.init();
			if (!result) {
				The5zigMod.logger.warn("Could not initialize " + activeController.getClass().getSimpleName());
				activeController = null;
			} else {
				activeController.update();
				updateShowPotionColor(false);
			}
		}
	}

	public void unInit() {
		if (activeController != null) {
			activeController.unInit();
		}
	}

	public void updateActiveLedKeys(boolean flush) {
		if (activeController != null) {
			KeyboardController.KeyGroup illuminatedKeys = The5zigMod.getConfig().getEnum("activeLedKeys", KeyboardController.KeyGroup.class);
			int color = 0xffffff;
			try {
				color = Integer.parseInt(The5zigMod.getConfig().getString("backgroundLedColor").substring(2), 16);
			} catch (NumberFormatException ignored) {
				The5zigMod.getConfig().get("backgroundLedColor", StringItem.class).set("0xffffff");
				The5zigMod.getConfig().save();
			}
			activeController.setIlluminatedKeys(illuminatedKeys, color);
			if (flush) {
				activeController.update();
			}
		}
	}

	public boolean isKeyboardInitialized() {
		return The5zigMod.getConfig().getEnum("controlKeyboard", KeyboardController.Device.class) != KeyboardController.Device.NONE && activeController != null;
	}

	public void updateShowHealth(boolean flush) {
		if (activeController != null) {
			activeController.setShowHealth(The5zigMod.getConfig().getBool("showLedHealth"));
			if (flush) {
				activeController.update();
			}
		}
	}

	public void updateShowArmor(boolean flush) {
		if (activeController != null) {
			activeController.setShowArmor(The5zigMod.getConfig().getBool("showLedArmor"));
			if (flush) {
				activeController.update();
			}
		}
	}

	public void updateShowPotionColor(boolean flush) {
		if (activeController != null && activePotion != null) {
			if (The5zigMod.getConfig().getBool("showLedPotions")) {
				activeController.displayPotionColor(activePotion.getLiquidColor());
			} else if (flush) {
				activeController.update();
			}
		}
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (activeController == null) {
			return;
		}

		if (Display.isActive() != displayFocused) {
			if (The5zigMod.getConfig().getBool("resetKeysOnUnfocus")) {
				if (displayFocused) {
					unInit();
				} else {
					init();
				}
			}
			displayFocused = !displayFocused;
		}

		List<? extends PotionEffect> potionEffects = The5zigMod.getVars().isPlayerNull() ? Collections.<PotionEffect>emptyList() : The5zigMod.getVars().getActivePotionEffects();
		PotionEffect effect = potionEffects.isEmpty() ? null : potionEffects.get(0);
		if ((effect != null && !effect.equals(activePotion)) || (effect == null && activePotion != null)) {
			activePotion = effect;
			if (activePotion != null) {
				if (The5zigMod.getConfig().getBool("showLedPotions")) {
					activeController.displayPotionColor(activePotion.getLiquidColor());
				}
			} else {
				activeController.update();
			}
		}
		if (activePotion != null) {
			return;
		}

		float health = The5zigMod.getVars().isPlayerNull() ? 0.0f : (The5zigMod.getVars().getPlayerHealth() / The5zigMod.getVars().getPlayerMaxHealth());
		float armor = The5zigMod.getVars().isPlayerNull() ? 0.0f : The5zigMod.getVars().getPlayerArmor() / 20f;

		if (health != lastHealth || armor != lastArmor) {
			activeController.updateHealthAndArmor(health, armor);
			if (health < lastHealth && lastHealth != 0 && The5zigMod.getConfig().getBool("showLedDamageFlash")) {
				activeController.onDamage();
			} else {
				activeController.update();
			}
			lastHealth = health;
			lastArmor = armor;
		}
	}

}
