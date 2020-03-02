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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.ITextfield;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class GuiCenteredTextfield extends Gui {

	private final CenteredTextfieldCallback callback;
	private final String defaultText;
	private ITextfield textfield;
	private int minLength;
	private int maxStringLength;
	private boolean password;

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback) {
		this(lastScreen, callback, 0, 100);
	}

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback, String text) {
		this(lastScreen, callback, text, 0, 100);
	}

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback, String text, int maxStringLength) {
		this(lastScreen, callback, text, 0, maxStringLength);
	}

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback, int maxStringLength) {
		this(lastScreen, callback, 0, maxStringLength);
	}

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback, int minLength, int maxStringLength) {
		this(lastScreen, callback, "", minLength, maxStringLength);
	}

	public GuiCenteredTextfield(Gui lastScreen, CenteredTextfieldCallback callback, String text, int minLength, int maxStringLength) {
		super(lastScreen);
		this.callback = callback;
		this.defaultText = text;
		this.minLength = minLength;
		this.maxStringLength = maxStringLength;
	}

	@Override
	public void initGui() {
		addTextField(textfield = The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 150, getHeight() / 6 + 80, 300, 20, maxStringLength));
		textfield.callSetText(defaultText);
		textfield.setIsPassword(password);

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 152, getHeight() / 6 + 140, 150, 20, The5zigMod.getVars().translate("gui.done"),
				textfield.callGetText().length() > minLength && (defaultText.isEmpty() || !defaultText.equals(textfield.callGetText()))));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 2, getHeight() / 6 + 140, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawCenteredString(callback.title(), getWidth() / 2, getHeight() / 6);
	}

	@Override
	protected void onKeyType(char character, int key) {
		getButtonById(1).setEnabled(textfield.callGetText().length() > minLength && (defaultText.isEmpty() || !defaultText.equals(textfield.callGetText())));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			callback.onDone(textfield.callGetText());
			The5zigMod.getVars().displayScreen(lastScreen);
		}
		if (button.getId() == 2) {
			callback.onCancel(lastScreen);
		}
	}

	public void setIsPassword(boolean b) {
		password = b;
		if(textfield != null)
			textfield.setIsPassword(b);
	}

	@Override
	public String getTitleKey() {
		return "input.title";
	}
}
