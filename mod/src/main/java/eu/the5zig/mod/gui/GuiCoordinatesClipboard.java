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

package eu.the5zig.mod.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.manager.CoordinateClipboard;
import eu.the5zig.mod.manager.DeathLocation;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.mod.util.Vector2i;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiCoordinatesClipboard extends GuiOptions {

	private List<?> description;

	private static final Pattern COORDINATE_PATTERN = Pattern.compile("(-?[0-9]+) -?[0-9]+ (-?[0-9]+)|(-?[0-9]+) (-?[0-9]+)");
	private boolean pressedPaste = false;

	public GuiCoordinatesClipboard(Gui lastScreen) {
		super(lastScreen);
	}

	/**
	 * Init gui
	 */
	public void initGui() {
		// button done
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.done")));
		// button cancel
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 82, 90, 80, 20, I18n.translate("coordinate_clipboard.previous")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 2, 90, 80, 20, I18n.translate("coordinate_clipboard.next")));

		addTextField(The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 70, 60, 50, 20));
		addTextField(The5zigMod.getVars().createTextfield(2, getWidth() / 2 + 20, 60, 50, 20));

		Vector2i location = The5zigMod.getDataManager().getCoordinatesClipboard().getLocation();
		if (location != null) {
			getTextfieldById(1).callSetText(String.valueOf(location.getX()));
			getTextfieldById(2).callSetText(String.valueOf(location.getY()));
		}

		description = The5zigMod.getVars().splitStringToWidth(I18n.translate("coordinate_clipboard.description"), Math.max(getWidth() - 100, 100));
	}

	/**
	 * Action performed
	 */
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			trySetLocation();
			The5zigMod.getDataManager().setCoordinatesClipboard(The5zigMod.getDataManager().getCoordinatesClipboard().getPrevious());
			if (getTextfieldById(1).callGetText().isEmpty() && getTextfieldById(2).callGetText().isEmpty()) {
				The5zigMod.getDataManager().getCoordinatesClipboard().setNext(null);
			}
			Vector2i location = The5zigMod.getDataManager().getCoordinatesClipboard().getLocation();
			if (location != null) {
				getTextfieldById(1).callSetText(String.valueOf(location.getX()));
				getTextfieldById(2).callSetText(String.valueOf(location.getY()));
			}
		}
		if (button.getId() == 2) {
			trySetLocation();
			CoordinateClipboard next = The5zigMod.getDataManager().getCoordinatesClipboard().getNext();
			if (next == null) {
				next = new CoordinateClipboard(The5zigMod.getDataManager().getCoordinatesClipboard());
				getTextfieldById(1).callSetText("");
				getTextfieldById(2).callSetText("");
				The5zigMod.getDataManager().getCoordinatesClipboard().setNext(next);
			} else if (next.getLocation() != null) {
				getTextfieldById(1).callSetText(String.valueOf(next.getLocation().getX()));
				getTextfieldById(2).callSetText(String.valueOf(next.getLocation().getY()));
			}
			The5zigMod.getDataManager().setCoordinatesClipboard(next);
		}
		if (button.getId() == 100) {
			trySetLocation();
			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}

	private void trySetLocation() {
		if (getTextfieldById(1).callGetText().length() > 0 && getTextfieldById(2).callGetText().length() > 0) {
			try {
				Vector2i location = new Vector2i(Integer.parseInt(getTextfieldById(1).callGetText()), Integer.parseInt(getTextfieldById(2).callGetText()));
				The5zigMod.getDataManager().getCoordinatesClipboard().setLocation(location);
			} catch (NumberFormatException ignored) {
				The5zigMod.getDataManager().getCoordinatesClipboard().setLocation(null);
			}
		} else {
			The5zigMod.getDataManager().getCoordinatesClipboard().setLocation(null);
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		if (key == Keyboard.KEY_SPACE && !The5zigMod.getVars().isPlayerNull()) {
			getTextfieldById(1).callSetText(String.valueOf((int) Math.floor(The5zigMod.getVars().getPlayerPosX())));
			getTextfieldById(2).callSetText(String.valueOf((int) Math.floor(The5zigMod.getVars().getPlayerPosZ())));
		}
		if (key == Keyboard.KEY_DELETE) {
			getTextfieldById(1).callSetText("");
			getTextfieldById(2).callSetText("");
		}
		DeathLocation deathLocation = The5zigMod.getDataManager().getDeathLocation();
		if (key == Keyboard.KEY_D && The5zigMod.isCtrlKeyDown() && deathLocation != null) {
			getTextfieldById(1).callSetText(String.valueOf((int) Math.floor(deathLocation.getCoordinates().getX())));
			getTextfieldById(2).callSetText(String.valueOf((int) Math.floor(deathLocation.getCoordinates().getZ())));
		}
	}

	@Override
	protected void tick() {
		getButtonById(1).setEnabled(The5zigMod.getDataManager().getCoordinatesClipboard().getPrevious() != null);

		boolean enabled = textfields.get(0).callGetText().length() > 0 && textfields.get(1).callGetText().length() > 0 && isInt(textfields.get(0).callGetText()) &&
				isInt(textfields.get(1).callGetText());
		getButtonById(2).setEnabled(enabled);
		getButtonById(100).setEnabled(enabled || (textfields.get(0).callGetText().length() == 0 && textfields.get(1).callGetText().length() == 0));

		// Paste clipboard contents.
		if (The5zigMod.isCtrlKeyDown() && Keyboard.isKeyDown(Keyboard.KEY_V) && !pressedPaste) {
			pressedPaste = true;
			try {
				String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				if (clipboard != null && !clipboard.isEmpty()) {
					Matcher matcher = COORDINATE_PATTERN.matcher(clipboard);
					if (matcher.find()) {
						for (int i = 1; i <= matcher.groupCount(); i += 2) {
							String x = matcher.group(i);
							String z = matcher.group(i + 1);
							if (x != null && z != null) {
								// set empty text first so that the cursor gets reset
								getTextfieldById(1).callSetText("");
								getTextfieldById(1).callSetText(x);
								getTextfieldById(2).callSetText("");
								getTextfieldById(2).callSetText(z);
							}
						}
					}
				}
			} catch (Exception e) {
				The5zigMod.logger.warn("Could not paste clipboard contents!", e);
			}
		} else if (!The5zigMod.isCtrlKeyDown() || Keyboard.isKeyDown(Keyboard.KEY_V)) {
			pressedPaste = false;
		}
	}

	@Override
	public String getTitleKey() {
		return "coordinate_clipboard.title";
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int numberOfPrevious = 0;
		int numberOfNext = 0;
		CoordinateClipboard clipboard = The5zigMod.getDataManager().getCoordinatesClipboard();
		do {
			numberOfPrevious++;
		} while ((clipboard = clipboard.getPrevious()) != null);
		clipboard = The5zigMod.getDataManager().getCoordinatesClipboard();
		do {
			numberOfNext++;
		} while ((clipboard = clipboard.getNext()) != null);

		drawCenteredString(I18n.translate("coordinate_clipboard.number", numberOfPrevious, numberOfPrevious + numberOfNext - 1), getWidth() / 2, 46);

		drawCenteredString("X:", getWidth() / 2 - 80, 67);
		drawCenteredString("Z:", getWidth() / 2 + 10, 67);

		int y = 120;
		for (Iterator<?> it = description.iterator(); it.hasNext(); y += The5zigMod.getVars().getFontHeight()) {
			The5zigMod.getVars().drawString((String) it.next(), 50, y);
		}
		y += 4;
		The5zigMod.getVars().drawString(I18n.translate("coordinate_clipboard.shortcut.cur_pos"), 50, y);
		y += 10;
		The5zigMod.getVars().drawString(I18n.translate("coordinate_clipboard.shortcut.delete"), 50, y);
		y += 10;
		The5zigMod.getVars().drawString(I18n.translate("coordinate_clipboard.shortcut.death"), 50, y);
	}

}
