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

package eu.the5zig.mod;

import com.mojang.authlib.GameProfile;
import eu.the5zig.mod.asm.Transformer;
import eu.the5zig.mod.gui.IOverlay;
import eu.the5zig.mod.gui.ingame.ArmorSlot;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.gui.ingame.PotionEffect;
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.mod.modules.AbstractModuleItem;
import eu.the5zig.mod.modules.Category;
import eu.the5zig.mod.plugin.LoadedPlugin;
import eu.the5zig.mod.plugin.PluginManagerImpl;
import eu.the5zig.mod.render.FormattingImpl;
import eu.the5zig.mod.render.RenderHelperImpl;
import eu.the5zig.mod.server.RegisteredServerInstance;
import eu.the5zig.mod.server.ServerInstance;
import eu.the5zig.mod.util.CoordinateClipboard;
import eu.the5zig.mod.util.IKeybinding;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.mod.util.PlayerGameMode;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.List;

public class ModAPIImpl implements ModAPI {

	private PluginManagerImpl pluginManager;
	private RenderHelperImpl renderHelper;
	private FormattingImpl formatting;

	public ModAPIImpl() {
		The5zigAPI.apiInstance = this;
		The5zigAPI.loggerInstance = The5zigMod.logger;
		this.pluginManager = new PluginManagerImpl();
		this.renderHelper = new RenderHelperImpl();
		this.formatting = new FormattingImpl();
	}

	@Override
	public String getModVersion() {
		return Version.VERSION;
	}

	@Override
	public String getMinecraftVersion() {
		return Version.MCVERSION;
	}

	@Override
	public boolean isForgeEnvironment() {
		return Transformer.FORGE;
	}

	@Override
	public PluginManagerImpl getPluginManager() {
		return pluginManager;
	}

	@Override
	public void registerModuleItem(Object plugin, String key, Class<? extends AbstractModuleItem> moduleItem, Category category) {
		Validate.notNull(category, "Category may not be null!");

		registerModuleItem(plugin, key, moduleItem, category.getName());
	}

	@Override
	public void registerModuleItem(Object plugin, String key, Class<? extends AbstractModuleItem> moduleItem, String category) {
		Validate.notNull(key, "Key may not be null!");
		Validate.notEmpty(key, "Key may not be empty!");
		Validate.notNull(moduleItem, "Module item may not be null!");
		Validate.notNull(category, "Category may not be null!");

		LoadedPlugin loadedPlugin = The5zigMod.getAPI().getPluginManager().getPlugin(plugin);
		if (loadedPlugin == null) {
			throw new IllegalArgumentException("Plugin has not been registered!");
		}
		The5zigMod.getModuleItemRegistry().registerItem(key, moduleItem, category);
		loadedPlugin.getRegisteredModuleItems().add(moduleItem);
	}

	@Override
	public boolean isModuleItemActive(String key) {
		return The5zigMod.getModuleMaster().isItemActive(key);
	}

	@Override
	public void registerServerInstance(Object plugin, Class<? extends ServerInstance> serverInstance) {
		Validate.notNull(serverInstance, "Server Instance may not be null!");
		LoadedPlugin loadedPlugin = The5zigMod.getAPI().getPluginManager().getPlugin(plugin);
		if (loadedPlugin == null) {
			throw new IllegalArgumentException("Plugin has not been registered!");
		}
		The5zigMod.getDataManager().getServerInstanceRegistry().registerServerInstance(serverInstance, loadedPlugin.getClassLoader());
		loadedPlugin.getRegisteredServerInstances().add(serverInstance);
	}

	@Override
	public ServerInstance getActiveServer() {
		for (RegisteredServerInstance registeredServerInstance : The5zigMod.getDataManager().getServerInstanceRegistry().getRegisteredInstances()) {
			if (registeredServerInstance.isOnServer()) {
				return registeredServerInstance.getServerInstance();
			}
		}
		return null;
	}

	@Override
	public IKeybinding registerKeyBiding(String description, int keyCode, String category) {
		return registerKeyBinding(description, keyCode, category);
	}

	@Override
	public IKeybinding registerKeyBinding(String description, int keyCode, String category) {
		Validate.notNull(description, "Description may not be null!");
		Validate.notEmpty(description, "Description may not be empty!");
		Validate.notNull(category, "Category may not be null!");
		Validate.notEmpty(category, "Category may not be empty!");

		IKeybinding keybinding = The5zigMod.getVars().createKeybinding(description, keyCode, category);
		The5zigMod.getVars().registerKeybindings(Collections.singletonList(keybinding));
		return keybinding;
	}

	@Override
	public RenderHelperImpl getRenderHelper() {
		return renderHelper;
	}

	@Override
	public FormattingImpl getFormatting() {
		return formatting;
	}

	@Override
	public IOverlay createOverlay() {
		return The5zigMod.getVars().newOverlay();
	}

	@Override
	public String translate(String key, Object... format) {
		Validate.notNull(key, "Key may not be null!");
		Validate.notEmpty(key, "Key may not be empty!");

		return I18n.translate(key, format);
	}

	@Override
	public boolean isInWorld() {
		return !The5zigMod.getVars().isPlayerNull() && !The5zigMod.getVars().isTerrainLoading();
	}

	private void validateInWorld() {
		Validate.validState(isInWorld(), "Method can only be executed when in world!");
	}

	@Override
	public void sendPlayerMessage(String message) {
		validateInWorld();

		The5zigMod.getVars().sendMessage(message);
	}

	@Override
	public void messagePlayer(String message) {
		validateInWorld();

		The5zigMod.getVars().messagePlayer(message);
	}

	@Override
	public void messagePlayerInSecondChat(String message) {
		validateInWorld();

		The5zigMod.getVars().get2ndChat().printChatMessage(message);
	}

	@Override
	public void sendPayload(String channel, ByteBuf payload) {
		validateInWorld();

		The5zigMod.getVars().sendCustomPayload(channel, payload);
	}

	@Override
	public int getWindowWidth() {
		return The5zigMod.getVars().getWidth();
	}

	@Override
	public int getWindowHeight() {
		return The5zigMod.getVars().getHeight();
	}

	@Override
	public int getScaledWidth() {
		return The5zigMod.getVars().getScaledWidth();
	}

	@Override
	public int getScaledHeight() {
		return The5zigMod.getVars().getScaledHeight();
	}

	@Override
	public int getScaleFactor() {
		return The5zigMod.getVars().getScaleFactor();
	}

	@Override
	public GameProfile getGameProfile() {
		return The5zigMod.getVars().getGameProfile();
	}

	@Override
	public String getServer() {
		return The5zigMod.getVars().getServer();
	}

	@Override
	public List<NetworkPlayerInfo> getServerPlayers() {
		validateInWorld();

		return The5zigMod.getVars().getServerPlayers();
	}

	@Override
	public double getPlayerPosX() {
		validateInWorld();

		return The5zigMod.getVars().getPlayerPosX();
	}

	@Override
	public double getPlayerPosY() {
		validateInWorld();

		return The5zigMod.getVars().getPlayerPosY();
	}

	@Override
	public double getPlayerPosZ() {
		validateInWorld();

		return The5zigMod.getVars().getPlayerPosZ();
	}

	@Override
	public float getPlayerRotationYaw() {
		validateInWorld();

		return The5zigMod.getVars().getPlayerRotationYaw();
	}

	@Override
	public float getPlayerRotationPitch() {
		validateInWorld();

		return The5zigMod.getVars().getPlayerRotationPitch();
	}

	@Override
	public boolean hasTargetBlock() {
		validateInWorld();

		return The5zigMod.getVars().hasTargetBlock();
	}

	@Override
	public int getTargetBlockX() {
		Validate.isTrue(hasTargetBlock(), "Player does not have any target block!");

		return The5zigMod.getVars().getTargetBlockX();
	}

	@Override
	public int getTargetBlockY() {
		Validate.isTrue(hasTargetBlock(), "Player does not have any target block!");

		return The5zigMod.getVars().getTargetBlockY();
	}

	@Override
	public int getTargetBlockZ() {
		Validate.isTrue(hasTargetBlock(), "Player does not have any target block!");

		return The5zigMod.getVars().getTargetBlockZ();
	}

	@Override
	public PlayerGameMode getGameMode() {
		validateInWorld();

		return The5zigMod.getVars().getGameMode();
	}

	@Override
	public List<? extends PotionEffect> getActivePotionEffects() {
		validateInWorld();

		return The5zigMod.getVars().getActivePotionEffects();
	}

	@Override
	public ItemStack getItemInMainHand() {
		validateInWorld();

		return The5zigMod.getVars().getItemInMainHand();
	}

	@Override
	public ItemStack getItemInOffHand() {
		validateInWorld();

		return The5zigMod.getVars().getItemInOffHand();
	}

	@Override
	public ItemStack getItemInArmorSlot(ArmorSlot slot) {
		validateInWorld();

		return The5zigMod.getVars().getItemInArmorSlot(slot.ordinal());
	}

	@Override
	public ItemStack getItemByName(String resourceName) {
		return The5zigMod.getVars().getItemByName(resourceName);
	}

	@Override
	public ItemStack getItemByName(String resourceName, int amount) {
		return The5zigMod.getVars().getItemByName(resourceName, amount);
	}

	@Override
	public int getItemCount(String key) {
		validateInWorld();

		return The5zigMod.getVars().getItemCount(key);
	}

	@Override
	public String getOpenContainerTitle() {
		validateInWorld();

		return The5zigMod.getVars().getOpenContainerTitle();
	}

	@Override
	public int getSelectedHotbarSlot() {
		validateInWorld();

		return The5zigMod.getVars().getSelectedHotbarSlot();
	}

	@Override
	public void setSelectedHotbarSlot(int slot) {
		validateInWorld();
		Validate.isTrue(slot >= 0 && slot <= 8, "Slot can only be between 0 and 8");

		The5zigMod.getVars().setSelectedHotbarSlot(slot);
	}

	@Override
	public void rightClickItem() {
		validateInWorld();

		The5zigMod.getVars().onRightClickMouse();
	}

	@Override
	public Scoreboard getSideScoreboard() {
		validateInWorld();

		return The5zigMod.getVars().getScoreboard();
	}

	@Override
	public boolean isPlayerListShown() {
		validateInWorld();

		return The5zigMod.getVars().isPlayerListShown();
	}

	@Override
	public void joinServer(String host, int port) {
		The5zigMod.getVars().joinServer(host, port);
	}

	@Override
	public void playSound(String sound, float pitch) {
		The5zigMod.getVars().playSound(sound, pitch);
	}

	@Override
	public void playSound(String domain, String sound, float pitch) {
		The5zigMod.getVars().playSound(domain, sound, pitch);
	}

	@Override
	public void disableDiscordPresence() {
		The5zigMod.getDiscordRPCManager().disable();
	}

	@Override
	public CoordinateClipboard getCoordinateClipboard() {
		return The5zigMod.getDataManager().getCoordinatesClipboard();
	}
}
