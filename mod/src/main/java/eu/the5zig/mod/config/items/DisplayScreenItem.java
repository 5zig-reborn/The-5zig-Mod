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

package eu.the5zig.mod.config.items;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.util.Utils;

public class DisplayScreenItem extends NonConfigItem {

	private final Class<? extends Gui> gui;
	private final Class[] constructorParams;
	private final Object[] constructorArgs;

	/**
	 * Creates an Item that is able to display a new GUI with given constructor params.
	 *
	 * @param key               Der Key of the Item. Used in config File and to translate the Item.
	 * @param category          The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param gui               The GUI that should be displayed.
	 * @param constructorParams The classes of all constructor parameters (<b>except {@link Gui#lastScreen}</b>) of {@param gui}.
	 * @param constructorArgs   All constructor arguments (<b>except {@link Gui#lastScreen}</b>).
	 */
	public DisplayScreenItem(String key, String category, Class<? extends Gui> gui, Class[] constructorParams, Object[] constructorArgs) {
		super(key, category);
		this.gui = gui;

		Class[] construct;
		if (!checkConstructor(construct = Utils.concat(new Class[]{Gui.class}, constructorParams))) {
			if (!checkConstructor(construct = constructorParams)) {
				throw new RuntimeException("Could not find any matching constructor!");
			}
		}
		this.constructorParams = construct;
		this.constructorArgs = constructorArgs;
	}

	private boolean checkConstructor(Class[] params) {
		try {
			gui.getConstructor(params);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates an Item that is able to display a new GUI.
	 *
	 * @param key      Der Key of the Item. Used in config File and to translate the Item.
	 * @param category The Category of the Item. Used by {@link GuiSettings} for finding the corresponding items.
	 * @param gui      The GUI that should be displayed.
	 */
	public DisplayScreenItem(String key, String category, Class<? extends Gui> gui) {
		this(key, category, gui, new Class[0], new Object[0]);
	}

	@Override
	public void action() {
		try {
			Gui screen = gui.getConstructor(constructorParams).newInstance(Utils.concat(Utils.asArray((Object) The5zigMod.getVars().getCurrentScreen()), constructorArgs));
			The5zigMod.getVars().displayScreen(screen);
		} catch (Exception e) {
			The5zigMod.logger.error("Could not display GUI " + gui.getSimpleName() + "!", e);
		}
	}
}
