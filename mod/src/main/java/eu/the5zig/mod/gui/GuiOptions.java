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

import com.google.common.collect.Maps;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.Callback;
import eu.the5zig.util.Utils;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;

public abstract class GuiOptions extends Gui {

	private boolean guiInitialized = false;
	private int optionButtonCount;

	private HashMap<Integer, Callback<IButton>> callbacks = Maps.newHashMap();

	public GuiOptions(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		guiInitialized = true;
		optionButtonCount = 0;
		callbacks.clear();
		addDoneButton();
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (callbacks.containsKey(button.getId()))
			callbacks.get(button.getId()).call(button);
	}

	protected boolean isInt(String str) {
		return Utils.isInt(str);
	}

	protected int addOptionButton(int id, int row, boolean left, String label, boolean enabled, int offset, Callback<IButton> actionPerformed) {
		Validate.isTrue(guiInitialized, "Gui hasn't been initialized yet!");

		addButton(The5zigMod.getVars().createButton(id, getWidth() / 2 + (left ? -155 : 5), getHeight() / 6 + row * 24 + offset - 6, 150, 20, label, enabled));
		if (actionPerformed != null)
			callbacks.put(id, actionPerformed);
		return optionButtonCount++;
	}

	protected int addOptionButton(String label, boolean enabled, int idOffset, int offset, Callback<IButton> actionPerformed) {
		return addOptionButton(optionButtonCount + idOffset, optionButtonCount / 2, optionButtonCount % 2 == 0, label, enabled, offset, actionPerformed);
	}

	protected int addOptionButton(String label, int idOffset, int offset, Callback<IButton> actionPerformed) {
		return addOptionButton(label, true, idOffset, offset, actionPerformed);
	}

	protected int addOptionButton(String label, int offset, Callback<IButton> actionPerformed) {
		return addOptionButton(label, 0, offset, actionPerformed);
	}

	protected int addOptionButton(String label, boolean enabled, Callback<IButton> actionPerformed) {
		return addOptionButton(label, enabled, 0, 0, actionPerformed);
	}

	protected int addOptionButton(String label, Callback<IButton> actionPerformed) {
		return addOptionButton(label, 0, 0, actionPerformed);
	}

}
