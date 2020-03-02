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

package eu.the5zig.mod.render;

import com.google.common.base.Charsets;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.Mouse;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.compress.utils.IOUtils;

import java.io.*;

public class ChatSymbolsRenderer {

	private final String DEFAULT_SYMBOLS =
			"\u2661\u2133\u24b6\u2740\u212c\u2112\u211b\u2130\u2605\u2764\u2122\u2665\u273f\u2134\u2620\u10e6\u270c\u24ba\u2741\u210b\u2765\u265b\u212f\u211c\u3016\u3010\u2131\u2118\u2727\u275d\u2115\u2110\u24c7\u2623\u3017\u2655\u2113\u24c4\u00bb\u3011\u275e\u24c9\u2622\u24c2\u25cf\u24c8\u24c3\u2763\u24b9\u265a\u2192\u2729\u2654\u2020\u2708\u212b\u2102\u263e\u24be\u262e\u272a\u2743\u261b\u261e\u24c1\u262a\u27b3\u24c0\u2730\u2714\u300c\u271e\u300b\u210a\u00ab\u212d\u262f\u300a\u2119\u27a4\u21dd\u211d\u300d\u22c6\u03df\u2501\u2606\u300e\u24b8\u24b7\u27b5\u263c\u24d8\u2726\u21ac\u273e\u00a9\u272f\u24c5\u2111\u2718\u212a\u20b3\u262a\u2127\u203a\u263d\u25ba\u24bc\u261c\u261a\u21a0\u271d\u300f\u2601\u273d\u21b3\u2039\u2766\u24bd\u24ca\u2135\u2020\u2600\u3014\u2720\u2554\u2500\u2663\u24d0\u2660\u2193\u20ae\u3018\u210d\u201c\u2123\u25b8\u2124\u212e\u2602\u275b\u2713\u2716\u2502\u2756\u278a\u210c\u2709\u2190\u210e\u274b\u3019\u25e4\u21dc\u25b2\u24ce\u262d\u27b8\u2122\u275c\u21bb\u24e2\u262e\u274a\u262f\u3015\u262c\u272f\u265e\u301a\u30c4\u270e\u00ae\u27bd\u25e6\u2551\u250a\u25c6\u270c\u24e1\u270d\u2638\u221e\u201d\u25e2\u21ab\u25c4\u24db\u25ac\u264b\u24bb\u2507\u2717\u2191\u24d4\u2129\u2666\u24de\u2764\u2605\u2606\u2730\u272f\u2721\u272a\u2736\u2731\u2732\u2734\u273c\u273b\u2735\u2747\u2748\u274a\u2756\u2744\u2746\u274b\u2742\u2042\u262f\u2721\u2628\u271e\u271d\u262e\u2625\u2626\u2627\u2629\u262a\u262b\u262c\u262d\u270c\u265b\u2655\u265a\u2654\u265c\u2656\u265d\u2657\u265e\u2658\u265f\u2659\u10e6\u2582\u2583\u2585\u2586\u2587\u2587\u2586\u2585\u2583\u2582\u24ff\u2776\u2777\u2778\u2779\u277a\u277b\u277c\u277d\u277e\u2206\u25bdO\u25a0\u25a1\u25cf\u25cb\u25b2\u25ba\u25bc\u25c4";
	private String SYMBOLS = "";
	private final float SCALE = 1.6f;

	private boolean opened = false;
	private int scrollOffset;
	private boolean scrollPressed = false;
	private int scrollMouseYOffset;

	private int boxX1, boxX2, boxY1, boxY2;
	private int panelX1, panelX2, panelY1, panelY2;

	public ChatSymbolsRenderer() {
		File symbolsFile = new File(The5zigMod.getModDirectory(), "symbols.txt");
		if (!symbolsFile.exists()) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(symbolsFile), Charsets.UTF_8));
				out.write(DEFAULT_SYMBOLS);
			} catch (Exception e) {
				The5zigMod.logger.warn("Could not create default chat symbols file!", e);
			} finally {
				SYMBOLS = DEFAULT_SYMBOLS;
				IOUtils.closeQuietly(out);
			}
		} else {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(new FileInputStream(symbolsFile), Charsets.UTF_8));
				SYMBOLS = in.readLine();
				if (SYMBOLS == null)
					SYMBOLS = DEFAULT_SYMBOLS;
			} catch (Exception e) {
				The5zigMod.logger.warn("Could not load chat symbols from file!", e);
				SYMBOLS = DEFAULT_SYMBOLS;
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

	public void render() {
		GLUtil.translate(0, 0, 500);
		int mouseX = Mouse.getX() / The5zigMod.getVars().getScaleFactor();
		int height = The5zigMod.getVars().getScaledHeight();
		int mouseY = height - Mouse.getY() / The5zigMod.getVars().getScaleFactor();

		boxX1 = 2;
		boxX2 = boxX1 + 11;
		boxY1 = height - 26;
		boxY2 = boxY1 + 11;

		panelX1 = boxX1;
		panelX2 = panelX1 + 108;
		panelY1 = boxY1 - 1 - 120;
		panelY2 = panelY1 + 120;

		opened = (mouseX >= boxX1 && mouseX <= boxX2 && mouseY >= boxY1 && mouseY <= boxY2) ||
				(((mouseX >= panelX1 && mouseX <= panelX2 && mouseY >= panelY1 && mouseY <= panelY2) || (mouseX >= panelX1 && mouseX <= panelX2 && mouseY >= panelY2 && mouseY <= boxY2)) &&
						opened) || scrollPressed;

		int rectColor = opened ? 0xb0000000 : 0x80000000;
		Gui.drawRect(boxX1, boxY1, boxX2, boxY2, rectColor);
		The5zigMod.getVars().drawString("+", boxX1 + 2, boxY1 + 2);

		if (opened) {
			// Panel Background
			The5zigMod.getVars().renderTextureOverlay(panelX1, panelX2, panelY1, panelY2);

			// Scroll Logic
			int scrollX1 = panelX2 - 8;
			int scrollX2 = scrollX1 + 5;
			int totalRows = Math.max(0, (int) Math.ceil((double) SYMBOLS.length() / 5.0) - 6);
			double rowPercent = (double) scrollOffset / (double) totalRows;
			int h = totalRows > 0 ? (int) (6.0 / (totalRows + 6) * (double) (panelY2 - panelY1)) : (panelY2 - panelY1);
			int scrollHeight = Math.max(20, h);
			int scrollY1 = panelY1 + (int) (rowPercent * (panelY2 - panelY1 - scrollHeight));
			int scrollY2 = scrollY1 + scrollHeight;
			if (mouseX >= scrollX1 && mouseX <= scrollX2 && mouseY >= panelY1 && mouseY <= panelY2) {
				if (!scrollPressed && Mouse.isButtonDown(0) && mouseY >= scrollY1 && mouseY <= scrollY2) {
					scrollPressed = true;
					scrollMouseYOffset = (int) (mouseY - panelY1 - Math.ceil(((double) scrollOffset / (double) totalRows) * (panelY2 - panelY1 - scrollHeight)));
				} else if (!Mouse.isButtonDown(0)) {
					scrollPressed = false;
				}
				if (scrollPressed) {
					double a = (double) (mouseY - scrollMouseYOffset - panelY1) / (double) (panelY2 - panelY1 - scrollHeight) * totalRows;
					scrollOffset = (int) Math.min(Math.max(a, 0), totalRows);
				}
			} else if (!Mouse.isButtonDown(0)) {
				scrollPressed = false;
			}
			Gui.drawRect(scrollX1, panelY1, scrollX2, panelY2, 0xbb333333);
			Gui.drawRect(scrollX1, scrollY1, scrollX2, scrollY2, 0xff000000);
			Gui.drawRect(scrollX1, scrollY1, scrollX2 - 1, scrollY2 - 1, scrollPressed ? (0x00FFFFFF & ((ChatColor) The5zigMod.getConfig().get("colorPrefix").get()).getColor()) |
					0xFF << 24 : 0xff888888);

			GLUtil.pushMatrix();
			GLUtil.translate(panelX1, panelY1, 0);
			GLUtil.scale(SCALE, SCALE, SCALE);

			// Panel Icons
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 5; col++) {
					int index = col + (row + scrollOffset) * 5;
					if (index < 0 || index >= SYMBOLS.length())
						break;
					char symbol = SYMBOLS.charAt(index);
					int x = 2 + col * 12;
					int y = 2 + row * 12;
					boolean isHover = mouseX >= x * SCALE + panelX1 && mouseX <= (x + 10) * SCALE + panelX1 && mouseY >= y * SCALE + panelY1 && mouseY <= (y + 10) * SCALE + panelY1;
					int backgroundColor = isHover ? 0x77888888 : 0x99555555;
					Gui.drawRect(x, y, x + 10, y + 10, backgroundColor);
					String symbolString = String.valueOf(symbol);
					int stringWidth = The5zigMod.getVars().getStringWidth(symbolString);
					The5zigMod.getVars().drawString(symbolString, x + (10 - stringWidth) / 2 + 1, y + 1);
				}
			}

			GLUtil.popMatrix();
		}
		GLUtil.translate(0, 0, -500);
	}

	public boolean mouseClicked(int mouseX, int mouseY) {
		// Panel Icons
		if (opened) {
			int pressedSymbolIndex = -1;
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 5; col++) {
					int index = col + (row + scrollOffset) * 5;
					if (index >= SYMBOLS.length())
						break;
					int x = 2 + col * 12;
					int y = 2 + row * 12;
					boolean isHover = mouseX >= x * SCALE + panelX1 && mouseX <= (x + 10) * SCALE + panelX1 && mouseY >= y * SCALE + panelY1 && mouseY <= (y + 10) * SCALE + panelY1;

					if (isHover && !scrollPressed) {
						pressedSymbolIndex = index;
					}
				}
			}
			if (pressedSymbolIndex != -1) {
				char pressedSymbol = SYMBOLS.charAt(pressedSymbolIndex);
				The5zigMod.getVars().typeInChatGUI(String.valueOf(pressedSymbol));
			}
		}

		return opened;
	}
}
