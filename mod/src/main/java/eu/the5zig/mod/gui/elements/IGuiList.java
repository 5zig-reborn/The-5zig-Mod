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

import java.util.List;

public interface IGuiList<E extends Row> {

	void callDrawScreen(int mouseX, int mouseY, float partialTicks);

	void callHandleMouseInput();

	void onSelect(int id, E row, boolean doubleClick);

	void mouseClicked(int mouseX, int mouseY);

	void mouseReleased(int mouseX, int mouseY, int state);

	/**
	 * Currently only called from 1.13+
	 *
	 * @param v
	 * @param v1
	 * @param i
	 * @param v2
	 * @param v3
	 */
	boolean callMouseDragged(double v, double v1, int i, double v2, double v3);

	/**
	 * Currently only called from 1.13+
	 *
	 * @param v
	 * @return
	 */
	boolean callMouseScrolled(double v);

	void scrollToBottom();

	float getCurrentScroll();

	void scrollTo(float to);

	boolean callIsSelected(int id);

	int callGetContentHeight();

	int callGetRowWidth();

	void setRowWidth(int rowWidth);

	int getSelectedId();

	int setSelectedId(int id);

	E getSelectedRow();

	int getWidth();

	void setWidth(int width);

	int getHeight();

	void setHeight(int height);

	int getHeight(int id);

	int getTop();

	void setTop(int top);

	int getBottom();

	void setBottom(int bottom);

	int getLeft();

	void setLeft(int left);

	int getRight();

	void setRight(int right);

	int getScrollX();

	void setScrollX(int scrollX);

	boolean isLeftbound();

	void setLeftbound(boolean leftbound);

	boolean isDrawSelection();

	void setDrawSelection(boolean drawSelection);

	int getHeaderPadding();

	void callSetHeaderPadding(int headerPadding);

	String getHeader();

	void setHeader(String header);

	int getBottomPadding();

	void setBottomPadding(int bottomPadding);

	boolean isDrawDefaultBackground();

	void setDrawDefaultBackground(boolean drawDefaultBackground);

	Object getBackgroundTexture();

	void setBackgroundTexture(Object resourceLocation, int imageWidth, int imageHeight);

	List<E> getRows();

	void calculateHeightMap();

	E getHoverItem(int mouseX, int mouseY);

}
