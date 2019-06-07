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

import eu.the5zig.util.Callable;
import eu.the5zig.mod.MinecraftFactory;

import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class BasicRow implements Row {

	protected int HEIGHT = 14;
	protected int LINE_HEIGHT = 11;

	protected String string;
	protected int maxWidth;

	private Callable<String> callback;

	public BasicRow(String string) {
		this.string = string;
		this.maxWidth = 95;
	}

	public BasicRow(String string, int maxWidth) {
		this.string = string;
		this.maxWidth = maxWidth;
	}

	public BasicRow(String string, int maxWidth, int height) {
		this.string = string;
		this.maxWidth = maxWidth;
		HEIGHT = height;
	}

	public BasicRow(Callable<String> callback, int maxWidth) {
		this.callback = callback;
		this.maxWidth = maxWidth;
	}

	public String getString() {
		return string;
	}

	@Override
	public int getLineHeight() {
		String toDraw = callback != null ? callback.call() : string;
		return (MinecraftFactory.getVars().splitStringToWidth(toDraw, maxWidth).size() - 1) * LINE_HEIGHT + HEIGHT;
	}

	@Override
	public void draw(int x, int y) {
		x = x + 2;
		y = y + 2;
		String toDraw = callback != null ? callback.call() : string;
		List<String> split = MinecraftFactory.getVars().splitStringToWidth(toDraw, maxWidth);
		for (int i = 0, splitStringToWidthSize = split.size(); i < splitStringToWidthSize; i++) {
			String line = split.get(i);
			MinecraftFactory.getVars().drawString(line, x, y);
			if (i < splitStringToWidthSize - 1)
				y += LINE_HEIGHT;
			else
				y += HEIGHT;
		}
	}
}
