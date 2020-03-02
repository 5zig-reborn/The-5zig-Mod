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

import eu.the5zig.mod.util.NativeLibrary;

import static com.logitech.gaming.LogiLED.*;

public class LogitechController extends KeyboardController {

	private KeyGroup keyGroup = KeyGroup.SPECIAL;
	private int color = 0xffffff;
	private boolean showHealth;
	private boolean showArmor;
	private float health;
	private float armor;

	@Override
	public NativeLibrary.NativeOS getTarget() {
		return NativeLibrary.NativeOS.WINDOWS;
	}

	@Override
	public String[] getNativeNames() {
		return new String[]{"logitechSDK${arch}"};
	}

	@Override
	public boolean init() {
		boolean init = LogiLedInit();
		if (init) {
			init = LogiLedSetTargetDevice(LOGI_DEVICETYPE_PERKEY_RGB);
		}
		return init;
	}

	@Override
	public void unInit() {
		LogiLedShutdown();
	}

	@Override
	public void setIlluminatedKeys(KeyGroup group, int color) {
		this.keyGroup = group;
		this.color = color;
	}

	@Override
	public void setShowHealth(boolean show) {
		this.showHealth = show;
	}

	@Override
	public void setShowArmor(boolean show) {
		this.showArmor = show;
	}

	@Override
	public void updateHealthAndArmor(float health, float armor) {
		this.health = health;
		this.armor = armor;
	}

	@Override
	public void onDamage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				LogiLedFlashLighting(100, 0, 0, 50 * 5, 50);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ignored) {
				}
				update();
			}
		}).start();
	}

	@Override
	public void displayPotionColor(int color) {
		LogiLedStopEffects();
		LogiLedSetLighting((int) ((double) ((color >> 16) & 255) / 255.0 * 100.0), (int) ((double) ((color >> 8) & 255) / 255.0 * 100.0), (int) ((double) (color & 255) / 255.0 * 100.0));
	}

	@Override
	public void update() {
		int r = (color >> 16) & 255;
		int g = (color >> 8) & 255;
		int b = color & 255;
		byte[] bitmap = new byte[LOGI_LED_BITMAP_SIZE];
		if (keyGroup == KeyGroup.ALL) {
			for (int y = 0; y < LOGI_LED_BITMAP_HEIGHT; y++) {
				for (int x = 0; x < LOGI_LED_BITMAP_WIDTH; x++) {
					setKeyRGBA(bitmap, x, y, r, g, b);
				}
			}
		} else {
			setKeyRGBA(bitmap, 2, 2, r, g, b);
			setKeyRGBA(bitmap, 1, 3, r, g, b);
			setKeyRGBA(bitmap, 2, 3, r, g, b);
			setKeyRGBA(bitmap, 3, 3, r, g, b);
			setKeyRGBA(bitmap, 3, 5, r, g, b);
		}

		if (showHealth) {
			int remainingHealth = (int) (health * 12f);
			int healthR = (int) (((12 - remainingHealth) / 12.0) * 255);
			int healthG = (int) (((remainingHealth) / 12.0) * 255);
			for (int i = 0; i < 12; i++) {
				setKeyRGBA(bitmap, 1 + i, 0, i < remainingHealth ? healthR : 0, i < remainingHealth ? healthG : 0, 0);
			}
		}
		if (showArmor) {
			int remainingArmor = (int) (armor * 10f);
			for (int i = 0; i < 10; i++) {
				setKeyRGBA(bitmap, 1 + i, 1, i < remainingArmor ? 255 : 0, i < remainingArmor ? 255 : 0, 0);
			}
		}
		LogiLedSetLightingFromBitmap(bitmap);
	}

	private void setKeyRGBA(byte[] bitmap, int x, int y, int r, int g, int b) {
		int position = (x + y * LOGI_LED_BITMAP_WIDTH) * LOGI_LED_BITMAP_BYTES_PER_KEY;
		bitmap[position++] = (byte) b;
		bitmap[position++] = (byte) g;
		bitmap[position++] = (byte) r;
		bitmap[position] = (byte) 255;
	}
}
