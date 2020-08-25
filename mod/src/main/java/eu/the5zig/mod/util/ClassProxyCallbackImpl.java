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

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.config.items.BoolItem;
import eu.the5zig.mod.config.items.HexColorItem;
import eu.the5zig.mod.config.items.StringListItem;
import eu.the5zig.mod.crashreport.CrashHopper;
import eu.the5zig.mod.event.KeyPressEvent;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiSettings;
import eu.the5zig.mod.listener.ChatSearchManager;
import eu.the5zig.mod.manager.SearchEntry;
import eu.the5zig.mod.plugin.LoadedPlugin;
import eu.the5zig.mod.server.Server;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

public class ClassProxyCallbackImpl implements ClassProxyCallback {

	@Override
	public String getVersion() {
		return Version.VERSION;
	}

	@Override
	public String getMinecraftVersion() {
		return Version.MCVERSION;
	}

	@Override
	public Logger getLogger() {
		return The5zigMod.logger;
	}

	@Override
	public File getModDirectory() {
		return The5zigMod.getModDirectory();
	}

	@Override
	public String getLastServer() {
		Server server = The5zigMod.getLastServerConfig().getConfigInstance().getLastServer();
		if (server == null)
			return null;
		return server.getHost() + ":" + server.getPort();
	}

	@Override
	public String translate(String key, Object... format) {
		return I18n.translate(key, format);
	}

	@Override
	public void displayGuiSettings(Gui lastScreen) {
		The5zigMod.getVars().displayScreen(new GuiSettings(lastScreen, "main"));
	}

	@Override
	public boolean isShowLastServer() {
		return The5zigMod.getConfig().getBool("showLastServer");
	}

	@Override
	public int getMaxChatLines() {
		return The5zigMod.getConfig().getInt("maxChatLines");
	}

	@Override
	public boolean is2ndChatTextLeftbound() {
		return The5zigMod.getConfig().getBool("2ndChatTextLeftbound");
	}

	@Override
	public boolean is2ndChatVisible() {
		return The5zigMod.getConfig().getBool("2ndChatVisible");
	}

	@Override
	public float get2ndChatOpacity() {
		return The5zigMod.getConfig().getFloat("2ndChatOpacity");
	}

	@Override
	public float get2ndChatWidth() {
		return The5zigMod.getConfig().getFloat("2ndChatWidth");
	}

	@Override
	public float get2ndChatHeightFocused() {
		return The5zigMod.getConfig().getFloat("2ndChatHeightFocused");
	}

	@Override
	public float get2ndChatHeightUnfocused() {
		return The5zigMod.getConfig().getFloat("2ndChatHeightUnfocused");
	}

	@Override
	public float get2ndChatScale() {
		return The5zigMod.getConfig().getFloat("2ndChatScale");
	}

	@Override
	public boolean isShowTimeBeforeChatMessage() {
		return The5zigMod.getConfig().getBool("chatTimePrefixEnabled");
	}

	@Override
	public boolean isChatBackgroundTransparent() {
		return The5zigMod.getConfig().getBool("transparentChatBackground");
	}

	@Override
	public Object getChatComponentWithTime(Object originalChatComponent) {
		return The5zigMod.getDataManager().getChatComponentWithTime(originalChatComponent);
	}

	@Override
	public void resetServer() {
		The5zigMod.getDataManager().resetServer();
	}

	@Override
	public boolean isRenderCustomModels() {
		return The5zigMod.getConfig().getBool("showCustomModels");
	}

	@Override
	public void checkAutoreconnectCountdown(int width, int height) {
		The5zigMod.getDataManager().getAutoReconnectManager().checkCountdown(width, height);
	}

	@Override
	public void setAutoreconnectServerData(Object serverData) {
		The5zigMod.getDataManager().getAutoReconnectManager().setServerData(serverData);
	}

	@Override
	public void launchCrashHopper(Throwable cause, File crashFile) {
		CrashHopper.launch(cause, crashFile);
	}

	@Override
	public void addSearch(SearchEntry searchEntry, SearchEntry... searchEntries) {
		The5zigMod.getDataManager().getSearchManager().addSearch(searchEntry, searchEntries);
	}

	@Override
	public void renderSnow(int width, int height) {
		The5zigMod.getDataManager().getSnowRenderer().render(width, height);
	}

	@Override
	public void disableTray() {
		The5zigMod.getConfig().get("showTrayNotifications", BoolItem.class).set(false);
		The5zigMod.getConfig().save();
	}

	@Override
	public boolean isTrayEnabled() {
		return The5zigMod.getConfig().getBool("showTrayNotifications");
	}

	@Override
	public List<String> getModList() {
		List<String> result = Lists.newArrayList();
		for (LoadedPlugin loadedPlugin : The5zigMod.getAPI().getPluginManager().getPlugins()) {
			result.add(loadedPlugin.getName());
		}
		return result;
	}

	@Override
	public String getChatSearchText() {
		ChatSearchManager chatSearchManager = The5zigMod.getDataManager().getChatSearchManager();
		return chatSearchManager.isSearching() ? chatSearchManager.getSearchText() : null;
	}

	@Override
	public List<String> getHighlightWords() {
		return The5zigMod.getConfig().get("highlight_words", StringListItem.class).get();
	}

	@Override
	public int getHighlightWordsColor() {
		return The5zigMod.getConfig().get("highlightWordsColor", HexColorItem.class).getColor();
	}

	@Override
	public boolean shouldCancelChatMessage(String message, Object chatComponent) {
		return The5zigMod.getListener().onServerChat(message, chatComponent, chatComponent);
	}

	@Override
	public int getOverlayTexture() {
		return The5zigMod.getConfig().getInt("overlayTexture");
	}

	@Override
	public void fireKeyPressEvent(int keyCode) {
		The5zigMod.getListener().fireEvent(new KeyPressEvent(keyCode));
	}
}
