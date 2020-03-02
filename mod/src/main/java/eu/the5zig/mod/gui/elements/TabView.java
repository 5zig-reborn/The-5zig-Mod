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

package eu.the5zig.mod.gui.elements;

import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Mouse;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class TabView<E> {

	private final int x;
	private final int y;
	private final int width;
	private final int elementWidth;
	private final int elementHeight;
	private final int elementBackground;
	private final int selectColor;

	private int offset;

	public final List<E> elements = Lists.newArrayList();
	public E selectedElement;

	private long pressed;

	public TabView(int x, int y, int width, int elementWidth, int elementHeight, int elementBackground, int selectColor) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.elementWidth = elementWidth;
		this.elementHeight = elementHeight;
		this.elementBackground = elementBackground;
		this.selectColor = selectColor;
	}

	public void draw(int mouseX, int mouseY) {
		if (Mouse.isButtonDown(Mouse.BUTTON_LEFT)) {
			if (pressed == 0 || System.currentTimeMillis() - pressed > 400) {
				if (pressed == 0) {
					pressed = System.currentTimeMillis();
				}
				if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + elementHeight) {
					if (elements.size() * (elementWidth + 1) > width && mouseX > x + width - 16 && mouseX < x + width - 2) {
						if (mouseX > x + width - 9) {
							offset = Math.max(width - elements.size() * (elementWidth + 1) - 20, offset - 5);
						} else {
							offset = Math.min(0, offset + 5);
						}
					}
				}
			}
		} else {
			pressed = 0;
		}


		boolean requiresScroll = elements.size() * (elementWidth + 1) > width;

		if (requiresScroll) {
			glEnable(GL_SCISSOR_TEST);
			float scaleFactor = The5zigMod.getVars().getScaleFactor();
			glScissor((int) Math.ceil(x * scaleFactor), (int) Math.ceil((The5zigMod.getVars().getScaledHeight() - y - elementHeight) * scaleFactor),
					(int) Math.floor((width - 20) * scaleFactor), (int) Math.floor(elementHeight * scaleFactor));
			drawTabs();
			glDisable(GL_SCISSOR_TEST);

			The5zigMod.getVars().drawString("<", x + width - 14, y + (elementHeight - The5zigMod.getVars().getFontHeight()) / 2 + 1, mouseX >= x + width - 14 && mouseX <= x + width - 9 &&
					mouseY >= y && mouseY <= y + elementHeight ? 0x00aaff : 0xffffff);
			The5zigMod.getVars().drawString(">", x + width - 7, y + (elementHeight - The5zigMod.getVars().getFontHeight()) / 2 + 1, mouseX >= x + width - 8 && mouseX <= x + width - 2 &&
					mouseY >= y && mouseY <= y + elementHeight ? 0x00aaff : 0xffffff);
		} else {
			drawTabs();
		}
	}

	private void drawTabs() {
		for (int i = 0; i * (elementWidth + 1) < width && i < elements.size(); i++) {
			E element = elements.get(i);
			int elementX1 = x + offset + i * (elementWidth + 1);
			int elementX2 = elementX1 + elementWidth;

			Gui.drawRect(elementX1, y, elementX2, y + elementHeight, selectedElement == element ? selectColor : elementBackground);
			if (selectedElement == element) {
				Gui.drawRectOutline(elementX1, y, elementX2, y + elementHeight, selectColor);
			}
			GLUtil.color(1, 1, 1, 1);
			int drawWidth = elementX2 - elementX1;
			if (canBeClosed(element)) {
				drawWidth -= 14;
			}
			drawElement(element, elementX1, y, drawWidth);
		}

		The5zigMod.getVars().bindTexture(The5zigMod.TEAMSPEAK_ICONS);
		for (int i = 0; i * (elementWidth + 1) < width && i < elements.size(); i++) {
			E element = elements.get(i);
			int elementX1 = x + i * (elementWidth + 1);
			int elementX2 = Math.min(elementX1 + elementWidth, x + width);

			if (canBeClosed(element)) {
				int closeY = y + (elementHeight - 12) / 2;
				Gui.drawModalRectWithCustomSizedTexture(elementX2 - 10, closeY, 13 * 128 / 12, 2 * 128 / 12, 128 / 12, 128 / 12, 2048 / 12, 2048 / 12);
			}
		}
	}

	protected abstract void drawElement(E element, int x, int y, int maxWidth);

	public void mouseClicked(int mouseX, int mouseY) {
		if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + elementHeight) {
			if (elements.size() * (elementWidth + 1) <= width || mouseX <= x + width - 16 || mouseX >= x + width - 2) {
				int clickedIndex = (mouseX - x) / (elementWidth + 1);
				if (clickedIndex < elements.size()) {
					E clicked = elements.get(clickedIndex);
					if (mouseX >= x + (elementWidth + 1) * (clickedIndex + 1) - 10 && canBeClosed(clicked)) {
						onClose(clicked);
					} else {
						if (clicked != selectedElement) {
							selectedElement = clicked;
							onSelectElement(clicked);
						}
					}
				}
			}
		}
	}

	protected abstract void onSelectElement(E element);

	protected abstract boolean canBeClosed(E element);

	protected void onClose(E element) {
	}

}
