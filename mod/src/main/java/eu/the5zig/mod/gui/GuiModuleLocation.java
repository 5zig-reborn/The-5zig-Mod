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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.modules.AnchorPoint;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.ModuleLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Mouse;
import eu.the5zig.mod.util.Rectangle;

import java.util.Locale;

public class GuiModuleLocation extends Gui {

	@SuppressWarnings("unused")
	private static final float CENTER_THRESHOLD = 0.03f;

	private final Module module;

	private boolean pressed = false;
	private int xOff, yOff;

	public GuiModuleLocation(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;
	}

	@Override
	public void initGui() {
		addButton(
				The5zigMod.getVars().createButton(100, getWidth() / 2 - 125, getHeight() - 20, 60, 20, I18n.translate("modules.location." + module.getLocation().toString().toLowerCase(Locale.ROOT))));
		addButton(The5zigMod.getVars().createButton(101, getWidth() / 2 - 60, getHeight() - 20, 120, 20, I18n.translate("modules.anchor") + ": " +
				I18n.translate("modules.anchor." + (module.getAnchorPoint() == null ? AnchorPoint.TOP_LEFT : module.getAnchorPoint()).toString().toLowerCase(Locale.ROOT))));
		if (module.getLocation() != ModuleLocation.CUSTOM) {
			getButtonById(101).setEnabled(false);
		}
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 65, getHeight() - 20, 60, 20, The5zigMod.getVars().translate("gui.done")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			The5zigMod.getModuleMaster().save();
		}
		if (button.getId() == 100) {
			module.setLocation(module.getLocation().getNext());
			module.setLocationX(0);
			module.setLocationY(0);
			button.setLabel(I18n.translate("modules.location." + module.getLocation().toString().toLowerCase(Locale.ROOT)));
			The5zigMod.getModuleMaster().save();
			if (module.getLocation() == ModuleLocation.CUSTOM) {
				getButtonById(101).setEnabled(true);
				module.setAnchorPoint(AnchorPoint.TOP_LEFT);
				getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
			} else {
				getButtonById(101).setEnabled(false);
			}
		}
		if (button.getId() == 101) {
			module.setAnchorPoint((module.getAnchorPoint() == null ? AnchorPoint.TOP_LEFT : module.getAnchorPoint()).getNext());
			float scale = The5zigMod.getConfig().getFloat("scale");
			int windowWidth = (int) (getWidth() / scale);
			int windowHeight = (int) (getHeight() / scale);
			int curX = (int) (windowWidth * module.getLocationX());
			int curY = (int) (windowHeight * module.getLocationY());
			int maxWidth = module.getMaxWidth(true);
			int totalHeight = module.getTotalHeight(true) - 6;
			if (module.getAnchorPoint() == AnchorPoint.TOP_LEFT) {
				module.setLocationX((float) (curX - maxWidth) / (float) windowWidth);
				module.setLocationY((float) (curY - totalHeight) / (float) windowHeight);
			} else if (module.getAnchorPoint() == AnchorPoint.TOP_CENTER) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			} else if (module.getAnchorPoint() == AnchorPoint.TOP_RIGHT) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			} else if (module.getAnchorPoint() == AnchorPoint.CENTER_LEFT) {
				module.setLocationX((float) (curX - maxWidth) / (float) windowWidth);
				module.setLocationY((float) (curY + totalHeight / 2) / (float) windowHeight);
			} else if (module.getAnchorPoint() == AnchorPoint.CENTER_CENTER) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			} else if (module.getAnchorPoint() == AnchorPoint.CENTER_RIGHT) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_LEFT) {
				module.setLocationX((float) (curX - maxWidth) / (float) windowWidth);
				module.setLocationY((float) (curY + totalHeight / 2) / (float) windowHeight);
			} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_CENTER) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_RIGHT) {
				module.setLocationX((float) (curX + maxWidth / 2) / (float) windowWidth);
			}
			button.setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
			The5zigMod.getModuleMaster().save();
		}
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().updateScaledResolution();


		float scale = The5zigMod.getConfig().getFloat("scale");
		GLUtil.pushMatrix();
		GLUtil.scale(scale, scale, scale);
		mouseX = (int) (mouseX / scale);
		mouseY = (int) (mouseY / scale);
		int x;
		int y;
		int width = module.getMaxWidth(true);
		int height = module.getTotalHeight(true) - (int) (6 * module.getScale());
		int windowWidth = (int) (getWidth() / scale);
		int windowHeight = (int) (getHeight() / scale);
		switch (module.getLocation()) {
			case TOP_LEFT:
				x = 2;
				y = 2;
				break;
			case TOP_RIGHT:
				x = windowWidth - width;
				y = 2;
				break;
			case CENTER_LEFT:
				x = 2;
				y = (windowHeight - height) / 2;
				break;
			case CENTER_RIGHT:
				x = windowWidth - width - 2;
				y = (windowHeight - height) / 2;
				break;
			case BOTTOM_LEFT:
				x = 2;
				y = windowHeight - height;
				break;
			case BOTTOM_RIGHT:
				x = windowWidth - width - 2;
				y = windowHeight - height;
				break;
			case CUSTOM:
				x = (int) (windowWidth * module.getLocationX());
				y = (int) (windowHeight * module.getLocationY());
				break;
			default:
				throw new AssertionError();
		}
		module.render(The5zigMod.getRenderer(), x, y, true);

		int color = 0xffff0000;

		int boxX = x;
		int boxY = y;
		if (module.getLocation() == ModuleLocation.CUSTOM) {
			boxX = getBoxOffX(x, width);
			boxY = getBoxOffY(y, height);
		}

		if (pressed || (mouseX >= boxX && mouseX <= boxX + width && mouseY >= boxY && mouseY <= boxY + height)) {
			color = 0xff990000;
			if (Mouse.isButtonDown(0) && !pressed) {
				pressed = true;
				xOff = getBoxOffX(mouseX - boxX, width);
				yOff = getBoxOffY(mouseY - boxY, height);
				module.setLocation(ModuleLocation.CUSTOM);
				if (module.getAnchorPoint() == null) {
					module.setAnchorPoint(AnchorPoint.TOP_LEFT);
				}
				getButtonById(100).setLabel(I18n.translate("modules.location." + ModuleLocation.CUSTOM.toString().toLowerCase(Locale.ROOT)));
				getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
				getButtonById(101).setEnabled(true);
			} else if (!Mouse.isButtonDown(0)) {
				pressed = false;
				The5zigMod.getModuleMaster().save();
			}
			if (pressed) {
				float locationX = (float) (mouseX - xOff) / (float) windowWidth;
				float locationY = (float) (mouseY - yOff) / (float) windowHeight;

				Rectangle vertical = new Rectangle(windowWidth / 2 - 5, 0, 10, windowHeight);
				Rectangle horizontal = new Rectangle(0, windowHeight / 2 - 5, windowWidth, 10);
//				drawRectOutline(vertical.getX(), vertical.getY(), vertical.getX() + vertical.callGetWidth(), vertical.getY() + vertical.callGetHeight(), 0x66ffffff);
//				drawRectOutline(horizontal.getX(), horizontal.getY(), horizontal.getX() + horizontal.callGetWidth(), horizontal.getY() + horizontal.callGetHeight(), 0x66ffffff);
				drawRect(windowWidth / 2 - 1, 0, windowWidth / 2 + 1, windowHeight, 0x44ffffff);
				drawRect(0, windowHeight / 2 - 1, windowWidth, windowHeight / 2 + 1, 0x44ffffff);

				int centerX = getBoxOffX(mouseX - xOff, width) + width / 2;
				int centerY = getBoxOffY(mouseY - yOff, height) + height / 2;
//				drawRect(centerX, 0, centerX + 1, windowHeight, 0x66ffffff);
//				drawRect(0, centerY, windowWidth, centerY + 1, 0x66ffffff);
				if (vertical.contains(centerX, centerY) && centerY < horizontal.getY()) {
					module.setLocationX(0.5f);
					module.setLocationY(locationY);
					if (module.getAnchorPoint() != AnchorPoint.TOP_CENTER) {
						module.setAnchorPoint(AnchorPoint.TOP_CENTER);
						getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
						xOff -= boxX - getBoxOffX(x, width);
						yOff -= boxY - getBoxOffY(y, height);
						module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
					}
				} else if (vertical.contains(centerX, centerY) && centerY > horizontal.getY() + horizontal.getHeight()) {
					module.setLocationX(0.5f);
					module.setLocationY(locationY);
					if (module.getAnchorPoint() != AnchorPoint.BOTTOM_CENTER) {
						module.setAnchorPoint(AnchorPoint.BOTTOM_CENTER);
						getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
						xOff -= boxX - getBoxOffX(x, width);
						yOff -= boxY - getBoxOffY(y, height);
						module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
					}
				} else if (centerX < vertical.getX() && centerY < horizontal.getY() && module.getAnchorPoint() != AnchorPoint.TOP_LEFT) {
					module.setAnchorPoint(AnchorPoint.TOP_LEFT);
					getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
					xOff -= boxX - getBoxOffX(x, width);
					yOff -= boxY - getBoxOffY(y, height);
					module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
				} else if (centerX > vertical.getX() + vertical.getWidth() && centerY < horizontal.getY() && module.getAnchorPoint() != AnchorPoint.TOP_RIGHT) {
					module.setAnchorPoint(AnchorPoint.TOP_RIGHT);
					getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
					xOff -= boxX - getBoxOffX(x, width);
					yOff -= boxY - getBoxOffY(y, height);
					module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
				} else if (centerX < vertical.getX() && centerY > horizontal.getY() + horizontal.getHeight() && module.getAnchorPoint() != AnchorPoint.BOTTOM_LEFT) {
					module.setAnchorPoint(AnchorPoint.BOTTOM_LEFT);
					getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
					xOff -= boxX - getBoxOffX(x, width);
					yOff -= boxY - getBoxOffY(y, height);
					module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
				} else if (centerX > vertical.getX() + vertical.getWidth() && centerY > horizontal.getY() + horizontal.getHeight() && module.getAnchorPoint() != AnchorPoint.BOTTOM_RIGHT) {
					module.setAnchorPoint(AnchorPoint.BOTTOM_RIGHT);
					getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
					xOff -= boxX - getBoxOffX(x, width);
					yOff -= boxY - getBoxOffY(y, height);
					module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					module.setLocationY((float) (mouseY - yOff) / (float) windowHeight);
				} else if (centerX < vertical.getX() && horizontal.contains(centerX, centerY)) {
					module.setLocationX(locationX);
					module.setLocationY(0.5f);
					if (module.getAnchorPoint() != AnchorPoint.CENTER_LEFT) {
						module.setAnchorPoint(AnchorPoint.CENTER_LEFT);
						getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
						xOff -= boxX - getBoxOffX(x, width);
						yOff -= boxY - getBoxOffY(y, height);
						module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					}
				} else if (centerX > vertical.getX() + vertical.getWidth() && horizontal.contains(centerX, centerY)) {
					module.setLocationX(locationX);
					module.setLocationY(0.5f);
					if (module.getAnchorPoint() != AnchorPoint.CENTER_RIGHT) {
						module.setAnchorPoint(AnchorPoint.CENTER_RIGHT);
						getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
						xOff -= boxX - getBoxOffX(x, width);
						yOff -= boxY - getBoxOffY(y, height);
						module.setLocationX((float) (mouseX - xOff) / (float) windowWidth);
					}
				} else if (vertical.contains(centerX, centerY) && horizontal.contains(centerX, centerY)) {
					module.setLocationX(0.5f);
					module.setLocationY(0.5f);
					if (module.getAnchorPoint() != AnchorPoint.CENTER_CENTER) {
						module.setAnchorPoint(AnchorPoint.CENTER_CENTER);
						getButtonById(101).setLabel(I18n.translate("modules.anchor") + ": " + I18n.translate("modules.anchor." + module.getAnchorPoint().toString().toLowerCase(Locale.ROOT)));
						xOff -= boxX - getBoxOffX(x, width);
						yOff -= boxY - getBoxOffY(y, height);
					}
				} else {
					module.setLocationX(locationX);
					module.setLocationY(locationY);
				}
			}
		}
		drawRectOutline(boxX, boxY, boxX + width, boxY + height, color);
		if (module.getLocation() == ModuleLocation.CUSTOM) {
			drawRect(x - 1, y - 1, x + 1, y + 1, 0xff000000);
			drawRectOutline(x - 1, y - 1, x + 1, y + 1, color);
		}

		GLUtil.popMatrix();
	}

	private int getBoxOffX(int x, int width) {
		if (module.getAnchorPoint() == AnchorPoint.TOP_CENTER) {
			return x - width / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.TOP_RIGHT) {
			return x - width;
		} else if (module.getAnchorPoint() == AnchorPoint.CENTER_CENTER) {
			return x - width / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.CENTER_RIGHT) {
			return x - width;
		} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_CENTER) {
			return x - width / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_RIGHT) {
			return x - width;
		} else {
			return x;
		}
	}

	private int getBoxOffY(int y, int height) {
		if (module.getAnchorPoint() == AnchorPoint.CENTER_LEFT) {
			return y - height / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.CENTER_CENTER) {
			return y - height / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.CENTER_RIGHT) {
			return y - height / 2;
		} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_LEFT) {
			return y - height;
		} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_CENTER) {
			return y - height;
		} else if (module.getAnchorPoint() == AnchorPoint.BOTTOM_RIGHT) {
			return y - height;
		} else {
			return y;
		}
	}

	@Override
	protected void onEscapeType() {
		actionPerformed0(getButtonById(200));
	}

	@Override
	public String getTitleName() {
		return "";
	}
}
