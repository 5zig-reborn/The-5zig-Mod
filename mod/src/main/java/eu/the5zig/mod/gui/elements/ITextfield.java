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

package eu.the5zig.mod.gui.elements;

/**
 * Created by 5zig.
 * All rights reserved � 2015
 * <p/>
 * Textfield class is a Simple Wrapper for default Minecraft Textfields.
 * Since I use obfuscated code, this textfield class simply refactors all required methods of the original Textfield.
 */
public interface ITextfield {

	/**
	 * Gets the id of the Textfield.
	 *
	 * @return The id of the Textfield.
	 */
	int callGetId();

	/**
	 * Focuses this Textfield.
	 *
	 * @param selected if this Textfield should be focused or not.
	 */
	void setSelected(boolean selected);

	/**
	 * Checks, if this Textfield is currently focused/selected.
	 *
	 * @return if this Textfield is currently focused.
	 */
	boolean callIsFocused();

	/**
	 * Sets focus to this Textfield.
	 *
	 * @param focused if the Textfield should be focused.
	 */
	void callSetFocused(boolean focused);

	/**
	 * Checks, if the Textfield is currently drawing its background texture.
	 *
	 * @return if the background of the Textfield is being drawed.
	 */
	boolean isBackgroundDrawing();

	/**
	 * Gets the x-location of the Textfield.
	 *
	 * @return the x-location of the Textfield.
	 */
	int getX();

	/**
	 * Sets the x-location of the Textfield.
	 *
	 * @param x The new x-location of the Textfield.
	 */
	void setX(int x);

	/**
	 * Gets the y-location of the Textfield.
	 *
	 * @return the y-location of the Textfield.
	 */
	int getY();

	/**
	 * Sets the y-location of the Textfield.
	 *
	 * @param y The new y-location of the Textfield.
	 */
	void setY(int y);

	/**
	 * Gets the width of the Textfield.
	 *
	 * @return the width of the Textfield.
	 */
	int getWidth();

	/**
	 * Gets the height of the Textfield.
	 *
	 * @return the height of the Textfield.
	 */
	int getHeight();

	/**
	 * Gets the max String Length the Text of the Textfield can have.
	 *
	 * @return the max String Length the Text of the Textfield can have.
	 */
	int callGetMaxStringLength();

	/**
	 * Sets the max String Length the Text of the Textfield can have.
	 *
	 * @param length The max String length of the Text.
	 */
	void callSetMaxStringLength(int length);

	/**
	 * Gets the current Text of the Textfield.
	 *
	 * @return The current Text of the Textfield.
	 */
	String callGetText();

	/**
	 * Sets the Text of the Textfield. Also checks if the text is larger, than callGetMaxStringLength().
	 *
	 * @param string The Text that should be put into the Textfield.
	 */
	void callSetText(String string);

	/**
	 * Simulates a Mouse click in the Textfield. Used in Gui when iterating through all textfields.
	 *
	 * @param x      The x-location of the Mouse.
	 * @param y      The y-location of the Mouse.
	 * @param button The button that has been pressed of the Mouse.
	 */
	void callMouseClicked(int x, int y, int button);

	/**
	 * Simulates a Key type in the Textfield. Used in Gui when iterating through all textfields.
	 *
	 * @param character The character that has been typed.
	 * @param key       The LWJGL-Integer of the typed key.
	 */
	boolean callKeyTyped(char character, int key);

	boolean keyPressed(int key, int scanCode, int modifiers);

	/**
	 * Used for blinking caret. Called from Gui when iterating through all textfields.
	 */
	void callTick();

	/**
	 * Draws the Textfield. Called from Gui when iterating through all textfields.
	 */
	void callDraw();

}
