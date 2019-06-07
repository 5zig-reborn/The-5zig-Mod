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

public class GuiDonatorSettings extends Gui {

	private String message;

	public GuiDonatorSettings(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addDoneButton();

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 155, getHeight() / 6 - 6, 150, 20, I18n.translate("config.main.cape_settings")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 + 5, getHeight() / 6 - 6, 150, 20, I18n.translate("config.main.item_model_settings")));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			The5zigMod.getVars().displayScreen(new GuiCapeSettings(this));
		}
	}

	@Override
	protected void tick() {
		super.tick();
	}

	@Override
	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
