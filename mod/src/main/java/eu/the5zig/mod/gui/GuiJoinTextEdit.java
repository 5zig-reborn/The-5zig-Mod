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
import eu.the5zig.mod.config.JoinText;
import eu.the5zig.mod.gui.elements.IButton;

public class GuiJoinTextEdit extends Gui {

	private final JoinText joinText;

	public GuiJoinTextEdit(Gui lastScreen, JoinText joinText) {
		super(lastScreen);
		this.joinText = joinText;
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 5, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.done")));
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 155, getHeight() / 6 + 168, 150, 20, The5zigMod.getVars().translate("gui.cancel")));

		addTextField(The5zigMod.getVars().createTextfield(1, getWidth() / 2 - 150, getHeight() / 6 + 20, 300, 20, 256));
		addTextField(The5zigMod.getVars().createTextfield(2, getWidth() / 2 - 150, getHeight() / 6 + 65, 240, 20, 256));
		addTextField(The5zigMod.getVars().createTextfield(3, getWidth() / 2 + 100, getHeight() / 6 + 65, 50, 20, 4));

		if (joinText.getMessage() != null) {
			getTextfieldById(1).callSetText(joinText.getMessage());
		}
		if (joinText.getServer() != null) {
			getTextfieldById(2).callSetText(joinText.getServer());
		}
		getTextfieldById(3).callSetText(String.valueOf(joinText.getDelay()));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 200) {
			joinText.setMessage(getTextfieldById(1).callGetText());
			if (getTextfieldById(2).callGetText().isEmpty()) {
				joinText.setServer(null);
			} else {
				joinText.setServer(getTextfieldById(2).callGetText());
			}
			try {
				joinText.setDelay(Integer.parseInt(getTextfieldById(3).callGetText()));
			} catch (NumberFormatException e) {
				The5zigMod.logger.error(e);
			}
			The5zigMod.getJoinTextConfiguration().saveConfig();
		}
		if (button.getId() == 100) {
			if (joinText.getMessage() == null || joinText.getMessage().isEmpty()) {
				The5zigMod.getJoinTextConfiguration().getConfigInstance().getTexts().remove(joinText);
				The5zigMod.getJoinTextConfiguration().saveConfig();
			}
			The5zigMod.getVars().displayScreen(lastScreen);
		}
	}

	@Override
	protected void tick() {
		getButtonById(200).setEnabled(!getTextfieldById(1).callGetText().isEmpty());
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		The5zigMod.getVars().drawString(I18n.translate("join_texts.edit.message"), getWidth() / 2 - 150, getHeight() / 6 + 8);
		The5zigMod.getVars().drawString(I18n.translate("join_texts.edit.server"), getWidth() / 2 - 150, getHeight() / 6 + 54);
		The5zigMod.getVars().drawString(I18n.translate("join_texts.edit.delay"), getWidth() / 2 + 100, getHeight() / 6 + 54);
	}

	@Override
	public String getTitleKey() {
		return "join_texts.edit.title";
	}
}
