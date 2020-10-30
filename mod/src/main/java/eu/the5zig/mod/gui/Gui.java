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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Keyboard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static eu.the5zig.mod.util.ReflectionUtil.invoke;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 * <p/>
 * Gui class is a Simple Wrapper for default Minecraft Gui Screens.
 * The Gui Wrapper makes it very simple to handle button clicks, textfields, last screens, ticks, drawing of strings, handling mouse input and lots more.
 * <p/>
 * To use this class, simply extend it from any class and implement all methods. Use then {@code The5zigMod.getVars().displayScreen(new YourScreen());}
 */
public abstract class Gui {

	private static final Class<?> handleClass;
	private static final Constructor<?> handleConstructor;
	private static final Method drawModalRectWithCustomSizedTexture;
	private static final Method drawRect;
	private static final Method drawGradientRect;

	static {
		try {
			handleClass = Class.forName("GuiHandle");
			handleConstructor = handleClass.getConstructor(Gui.class);

			drawModalRectWithCustomSizedTexture = handleClass.getMethod("callDrawModalRectWithCustomSizedTexture", int.class, int.class, float.class, float.class, int.class, int.class, float
					.class, float.class);
			drawRect = handleClass.getMethod("drawRect", double.class, double.class, double.class, double.class, int.class);
			drawGradientRect = handleClass.getMethod("drawGradientRect", double.class, double.class, double.class, double.class, int.class, int.class, boolean.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Gui lastScreen;
	private IGuiHandle handle;

	protected HashMap<Integer, IButton> buttonIdLookup = Maps.newHashMap();
	protected HashMap<Integer, ITextfield> textFieldIdLookup = Maps.newHashMap();
	protected List<IButton> buttons = Lists.newArrayList();
	protected List<ITextfield> textfields = Lists.newArrayList();
	protected List<IGuiList> guiLists = Lists.newArrayList();
	protected List<CheckBox> checkBoxes = Lists.newArrayList();
	protected List<RadioCheckBox> radioCheckBoxes = Lists.newArrayList();
	protected boolean actionOnTextFieldReturn = true;
	protected boolean selectTextFieldAfterInit = true;

	public Gui(Gui lastScreen) {
		try {
			handle = (IGuiHandle) handleConstructor.newInstance(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.lastScreen = lastScreen;
	}

	/**
	 * Init gui
	 */
	public void initGui0() {
		MinecraftFactory.getVars().updateScaledResolution();
		buttons.clear();
		textfields.clear();
		guiLists.clear();
		buttonIdLookup.clear();
		textFieldIdLookup.clear();
		checkBoxes.clear();
		radioCheckBoxes.clear();
		initGui();
		if (selectTextFieldAfterInit && textfields.size() > 0)
			textfields.get(0).setSelected(true);
		tick();
	}

	public abstract void initGui();

	public void addDoneButton() {
		addButton(MinecraftFactory.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, MinecraftFactory.getVars().translate("gui.done")));
	}

	public void addBottomDoneButton() {
		addButton(MinecraftFactory.getVars().createButton(200, getWidth() / 2 - 100, getHeight() - 32, MinecraftFactory.getVars().translate("gui.done")));
	}

	public void addCancelButton() {
		addButton(MinecraftFactory.getVars().createButton(200, getWidth() / 2 - 100, getHeight() / 6 + 168, MinecraftFactory.getVars().translate("gui.cancel")));
	}

	public void addTextField(ITextfield textfield) {
		Keyboard.enableRepeatEvents(true);
		textfields.add(textfield);
		textFieldIdLookup.put(textfield.callGetId(), textfield);
	}

	public void removeTextField(ITextfield textfield) {
		textfields.remove(textfield);
		textFieldIdLookup.remove(textfield.callGetId());
	}

	public void addButton(IButton button) {
		buttons.add(button);
		buttonIdLookup.put(button.getId(), button);
	}

	public void removeButton(IButton button) {
		buttons.remove(button);
		buttonIdLookup.remove(button.getId());
	}

	public void addGuiList(IGuiList guiList) {
		guiLists.add(guiList);
	}

	public void addCheckBox(CheckBox checkBox) {
		checkBoxes.add(checkBox);
	}

	public void addRadioCheckBox(RadioCheckBox radioCheckBox) {
		radioCheckBoxes.add(radioCheckBox);
	}

	/**
	 * Action performed
	 *
	 * @param button GuiButton
	 */
	public void actionPerformed0(IButton button) {
		if (!button.isEnabled()) {
			return;
		}
		actionPerformed(button);
		if (button.getId() == 200) {
			MinecraftFactory.getVars().displayScreen(lastScreen);
		}
	}

	protected abstract void actionPerformed(IButton button);

	/**
	 * Draws the screen
	 */
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		// draw centered String, title
		for (IGuiList guiList : guiLists) {
			guiList.callDrawScreen(mouseX, mouseY, partialTicks);
		}
		drawScreen(mouseX, mouseY, partialTicks);
		if (getTitleName() != null)
			drawCenteredString(getTitleName(), getWidth() / 2, 15);
		else
			drawCenteredString("The 5zig Mod - " + MinecraftFactory.getClassProxyCallback().translate(getTitleKey()), getWidth() / 2, 15);

		// draw textfields and buttons
		for (IButton button : buttons) {
			button.draw(mouseX, mouseY);
		}
		for (ITextfield textfield : textfields) {
			textfield.callDraw();
		}
		for (CheckBox checkBox : checkBoxes) {
			checkBox.draw();
		}
		for (RadioCheckBox radioCheckBox : radioCheckBoxes) {
			radioCheckBox.draw();
		}
	}

	protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
	}

	/**
	 * Update screen
	 */
	public void tick0() {
		// update textfields and buttons
		for (IButton button : buttons) {
			button.tick();
		}
		for (ITextfield textfield : textfields) {
			textfield.callTick();
		}
		tick();
	}

	protected void tick() {
	}

	public void handleMouseInput0() {
		for (IGuiList guiList : guiLists) {
			guiList.callHandleMouseInput();
		}
		handleMouseInput();
	}

	protected void handleMouseInput() {
	}

	/**
	 * On gui closed
	 */
	public void guiClosed0() {
		Keyboard.enableRepeatEvents(false);
		for (IButton button : buttons) {
			button.guiClosed();
		}
		guiClosed();
	}

	protected void guiClosed() {
	}

	/**
	 * Called from Handle
	 */
	public void keyTyped0(char character, int key) {
		if (key == Keyboard.KEY_ESCAPE) {
			onEscapeType();
		}
		for (ITextfield t : textfields) {
			if (t.callIsFocused()) {
				t.callKeyTyped(character, key);
			}
		}

		if ((key == Keyboard.KEY_RETURN) || (key == Keyboard.KEY_NUMPADENTER)) {
			// action performed
			if (getSelectedTextField() != null && actionOnTextFieldReturn)
				actionPerformed0(getButtonList().get(0));
		}
		if (key == Keyboard.KEY_TAB) {
			ITextfield curField = getSelectedTextField();
			if (curField == null && textfields.size() > 0) {
				textfields.get(0).setSelected(true);
			} else if (curField != null) {
				int id = textfields.indexOf(curField);
				int next = (id + 1) % textfields.size();
				curField.setSelected(false);
				textfields.get(next).setSelected(true);
			}
		}

		onKeyType(character, key);
	}

	protected void onKeyType(char character, int key) {
	}

	public void keyPressed0(int key, int scanCode, int modifiers) {
		for (ITextfield textfield : textfields) {
			textfield.onKeyPressed(key, scanCode, modifiers);
		}
		keyPressed(key, scanCode, modifiers);
	}

	protected void keyPressed(int key, int scanCode, int modifiers) {
	}

	protected void onEscapeType() {
		MinecraftFactory.getVars().displayScreen(null);
		if (MinecraftFactory.getVars().getCurrentScreen() == null) {
			MinecraftFactory.getVars().setIngameFocus();
		}
	}

	/**
	 * Called from Handle
	 */
	public void mouseClicked0(int x, int y, int button) {
		if (button == 0) {
			for (IButton b : buttons) {
				if (b.mouseClicked(x, y)) {
					b.playClickSound();
					actionPerformed0(b);
				}
			}
		}
		for (ITextfield textfield : textfields) {
			textfield.callMouseClicked(x, y, button);
		}
		for (IGuiList guiList : guiLists) {
			guiList.mouseClicked(x, y);
		}
		for (CheckBox checkBox : checkBoxes) {
			checkBox.mouseClicked(x, y);
		}
		for (RadioCheckBox radioCheckBox : radioCheckBoxes) {
			radioCheckBox.mouseClicked(x, y);
		}
		mouseClicked(x, y, button);
	}

	protected void mouseClicked(int x, int y, int button) {
	}

	public void mouseReleased0(int x, int y, int state) {
		for (IButton button : buttons) {
			button.callMouseReleased(x, y, state);
		}
		for (IGuiList guiList : guiLists) {
			guiList.mouseReleased(x, y, state);
		}
		mouseReleased(x, y, state);
	}

	protected void mouseReleased(int x, int y, int state) {
	}

	public void mouseDragged0(double v, double v1, int i, double v2, double v3) {
		for (IGuiList guiList : guiLists) {
			guiList.callMouseDragged(v, v1, i, v2, v3);
		}
	}

	public void mouseScrolled0(double v) {
		for (IGuiList guiList : guiLists) {
			guiList.callMouseScrolled(v);
		}
	}

	public ITextfield getSelectedTextField() {
		if (textfields.size() == 0)
			return null;
		for (ITextfield textfield : textfields) {
			if (textfield.callIsFocused())
				return textfield;
		}
		return null;
	}

	protected void drawMenuBackground() {
		handle.drawMenuBackground();
	}

	public String getTitleName() {
		return null;
	}

	public String getTitleKey() {
		return "config.main.title";
	}

	public static void drawCenteredString(String string, int x, int y) {
		drawCenteredString(string, x, y, 0xffffff);
	}

	public static void drawCenteredString(String string, int x, int y, int color) {
		MinecraftFactory.getVars().drawCenteredString(string, x, y, color);
	}

	/**
	 * Gets a Button by its id
	 *
	 * @param id The id of the Button
	 * @return The button by its id.
	 */
	public IButton getButtonById(int id) {
		return buttonIdLookup.get(id);
	}

	/**
	 * Gets a Textfield by its id
	 *
	 * @param id The id of the Textfield.
	 * @return The Textfield by its id.
	 */
	public ITextfield getTextfieldById(int id) {
		return textFieldIdLookup.get(id);
	}

	/**
	 * Gets the buttonList
	 *
	 * @return The Button List
	 */
	public List<IButton> getButtonList() {
		return this.buttons;
	}

	/**
	 * Gets the screen width.
	 *
	 * @return The width of the current screen.
	 */
	public int getWidth() {
		return handle.getWidth();
	}

	/**
	 * Gets the screen height.
	 *
	 * @return The height of the current screen.
	 */
	public int getHeight() {
		return handle.getHeight();
	}

	public void setResolution(int width, int height) {
		handle.setResolution(width, height);
	}

	public IGuiHandle getHandle() {
		return handle;
	}

	public void drawTexturedModalRect(int x, int y, int texX, int texY, int width, int height) {
		handle.callDrawTexturedModalRect(x, y, texX, texY, width, height);
	}

	public void drawHoveringText(List<String> lines, int x, int y) {
		handle.callDrawHoveringText(lines, x, y);
	}

	public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
		invoke(drawModalRectWithCustomSizedTexture, x, y, u, v, width, height, textureWidth, textureHeight);
	}

	public static void drawRect(double left, double top, double right, double bottom, int color) {
		invoke(drawRect, left, top, right, bottom, color);
	}

	public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor, boolean vertical) {
		invoke(drawGradientRect, left, top, right, bottom, startColor, endColor, vertical);
	}

	public static void drawRectOutline(int left, int top, int right, int bottom, int color) {
		drawRect(left - 1, top - 1, right + 1, top, color);
		drawRect(right, top, right + 1, bottom, color);
		drawRect(left - 1, bottom, right + 1, bottom + 1, color);
		drawRect(left - 1, top, left, bottom, color);
	}

	public static void drawRectInline(int left, int top, int right, int bottom, int color) {
		drawRect(left, top, right, top + 1, color);
		drawRect(right - 1, top, right, bottom, color);
		drawRect(left, bottom - 1, right, bottom, color);
		drawRect(left, top, left + 1, bottom, color);
	}

	public static void drawScaledCenteredString(String string, int x, int y, float scale) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		MinecraftFactory.getVars().drawCenteredString(string, 0, 0);
		GLUtil.popMatrix();
	}

	public static void drawScaledCenteredString(String string, int x, int y, float scale, int color) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		MinecraftFactory.getVars().drawCenteredString(string, 0, 0, color);
		GLUtil.popMatrix();
	}

	public static void drawScaledString(String string, float x, float y, float scale) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		MinecraftFactory.getVars().drawString(string, 0, 0);
		GLUtil.popMatrix();
	}

}
