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

package eu.the5zig.mod.util;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;

import static org.lwjgl.opengl.GL11.*;

public class ScrollingText {

	private final String text;
	private final float stringWidth;
	private final int width;
	private final int height;
	private final int backgroundColor;
	private final int transparentBackgroundColor;
	private final int textColor;
	private float scale = 1;

	private ScrollingText parent;
	private ScrollingText child;

	private long lastTime;
	private long startOfWait;
	private float offset;
	private State state = State.LEFT;

	public ScrollingText(String text, int width, int height, int backgroundColor, int textColor) {
		this.text = text;
		this.stringWidth = The5zigMod.getVars().getStringWidth(text) * (height / 10f);
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		this.transparentBackgroundColor = ((backgroundColor >> 16 & 255) << 16) | ((backgroundColor >> 8 & 255) << 8) | (backgroundColor & 255);
		this.textColor = textColor;
	}

	public String getText() {
		return text;
	}

	public void setParent(ScrollingText parent) {
		this.parent = parent;
	}

	public void setChild(ScrollingText child) {
		this.child = child;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void render(int x, int y) {
		if (stringWidth > width) {
			double delta = (The5zigMod.getVars().getSystemTime() - lastTime) / 50.0;
			lastTime = The5zigMod.getVars().getSystemTime();
			switch (state) {
				case LEFT:
					if (startOfWait == 0) {
						startOfWait = lastTime;
					}
					if ((child == null || (child.stringWidth <= child.width || (child.state == State.LEFT && child.startOfWait != 0 && child.lastTime - child.startOfWait > 4000))) &&
							(parent == null || parent.stringWidth <= parent.width || parent.state != State.LEFT) && (lastTime - startOfWait > 4000)) {
						startOfWait = 0;
						state = State.SCROLL_RIGHT;
					}
					break;
				case SCROLL_RIGHT:
					offset += delta;
					if (offset >= stringWidth - width) {
						offset = stringWidth - width;
						state = State.RIGHT;
					}
					break;
				case RIGHT:
					if (startOfWait == 0) {
						startOfWait = lastTime;
					}
					if ((child == null || (child.stringWidth <= child.width || (child.state == State.RIGHT && child.startOfWait != 0 && child.lastTime - child.startOfWait > 2500))) &&
							(parent == null || parent.stringWidth <= parent.width || parent.state != State.RIGHT) && (lastTime - startOfWait > 2500)) {
						startOfWait = 0;
						state = State.SCROLL_LEFT;
					}
					break;
				case SCROLL_LEFT:
					offset -= delta;
					if (offset <= 0) {
						offset = 0;
						state = State.LEFT;
					}
					break;
			}
		}

		float scaleFactor = The5zigMod.getVars().getScaleFactor() * scale * The5zigMod.getConfig().getFloat("scale");
		glEnable(GL_SCISSOR_TEST);
		glScissor((int) Math.ceil(x * scaleFactor), (int) Math.ceil(The5zigMod.getVars().getHeight() - (y + height) * scaleFactor), (int) Math.floor(width * scaleFactor),
				(int) Math.floor(height * scaleFactor));
		drawScaledString(text, x - offset, y, textColor, height / 10f);
		glDisable(GL_SCISSOR_TEST);

		if (offset > 0) {
			Gui.drawGradientRect(x, y, x + 4, y + height, backgroundColor, transparentBackgroundColor, true);
		}
		if (stringWidth > width && offset < stringWidth - width) {
			Gui.drawGradientRect(x + width - 4, y, x + width, y + height, transparentBackgroundColor, backgroundColor, true);
		}
	}

	private void drawScaledString(String string, float x, float y, int color, float scale) {
		GLUtil.pushMatrix();
		GLUtil.translate(x, y, 1);
		GLUtil.scale(scale, scale, scale);
		The5zigMod.getVars().drawString(string, 0, 0, color);
		GLUtil.popMatrix();
	}

	private enum State {
		LEFT, SCROLL_RIGHT, RIGHT, SCROLL_LEFT
	}
}
