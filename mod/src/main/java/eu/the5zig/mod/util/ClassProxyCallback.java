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

package eu.the5zig.mod.util;

import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.manager.SearchEntry;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public interface ClassProxyCallback {

	String getVersion();

	String getMinecraftVersion();

	Logger getLogger();

	File getModDirectory();

	String getLastServer();

	String translate(String key, Object... format);

	void displayGuiSettings(Gui lastScreen);

	boolean isShowLastServer();

	int getMaxChatLines();

	boolean is2ndChatTextLeftbound();

	boolean is2ndChatVisible();

	float get2ndChatOpacity();

	float get2ndChatWidth();

	float get2ndChatHeightFocused();

	float get2ndChatHeightUnfocused();

	float get2ndChatScale();

	boolean isShowTimeBeforeChatMessage();

	boolean isChatBackgroundTransparent();

	Object getChatComponentWithTime(Object originalChatComponent);

	void resetServer();

	boolean isRenderCustomModels();

	void checkAutoreconnectCountdown(int width, int height);

	void setAutoreconnectServerData(Object serverData);

	void launchCrashHopper(Throwable cause, File crashFile);

	void addSearch(SearchEntry searchEntry, SearchEntry... searchEntries);

	void renderSnow(int width, int height);

	void disableTray();

	boolean isTrayEnabled();

	List<String> getModList();

	String getChatSearchText();

	List<String> getHighlightWords();

	int getHighlightWordsColor();

	boolean shouldCancelChatMessage(String message, Object chatComponent);

	int getOverlayTexture();

	void fireKeyPressEvent(int keyCode);

}
