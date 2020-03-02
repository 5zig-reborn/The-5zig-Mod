/*
 * Copyright (c) 2019-2020 5zig Reborn
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

import com.mojang.authlib.GameProfile;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.manager.DeathLocation;
import eu.the5zig.mod.manager.WorldType;
import eu.the5zig.mod.util.BytecodeAccess;
import eu.the5zig.mod.util.TabList;
import eu.the5zig.mod.util.Vector3f;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;

import java.io.File;
import java.util.List;

/**
 * This class is used as an interface between Minecraft bytecode hooks and mod classes. All methods in here are called from Minecraft internal classes.
 */
public class BytecodeHook {

	@BytecodeAccess
	public static void onDispatchKeyPresses() {
		The5zigMod.getVars().dispatchKeypresses();
	}

	@BytecodeAccess
	public static void onShutdown() {
		The5zigMod.shutdown();
	}

	@BytecodeAccess
	public static void appendCrashCategory(Object crashReport) {
		try {
			ClassProxy.appendCategoryToCrashReport(crashReport);
		} catch (Throwable ignored) {
		}
	}

	@BytecodeAccess
	public static void onDisplayCrashReport(Throwable cause, File crashFile) {
		ClassProxy.publishCrashReport(cause, crashFile);
	}

	@BytecodeAccess
	public static void onAbstractClientPlayerInit(GameProfile gameProfile) {
		ClassProxy.setupPlayerTextures(gameProfile);
	}

	@BytecodeAccess
	public static Object getCapeLocation(Object player) {
		return The5zigMod.getVars().getResourceManager().getCapeLocation(player);
	}

	@BytecodeAccess
	public static boolean isStaticFOV() {
		return The5zigMod.getConfig().getBool("staticFov");
	}

	@BytecodeAccess
	public static void onLeftClickMouse() {
		The5zigMod.getDataManager().getCpsManager().getLeftClickCounter().incrementCount();
	}

	@BytecodeAccess
	public static void onRightClickMouse() {
		The5zigMod.getDataManager().getCpsManager().getRightClickCounter().incrementCount();
	}

	@BytecodeAccess
	public static void onDisplayScreen() {
		if (!The5zigMod.hasBeenInitialized()) {
			The5zigMod.init();
		}
		The5zigMod.getKeybindingManager().flushTextfields();
	}

	@BytecodeAccess
	public static boolean isCtrlKeyDown(boolean isMacOS) {
		return The5zigMod.isCtrlKeyDown(isMacOS);
	}

	@BytecodeAccess
	public static void setWorldAndResolution() {
		if (The5zigMod.getKeybindingManager() == null) {
			return;
		}
		The5zigMod.getKeybindingManager().flushTextfields();
	}

	@BytecodeAccess
	public static void onTextfieldInit(Object textfield) {
		if (The5zigMod.getKeybindingManager() == null) {
			return;
		}
		The5zigMod.getKeybindingManager().onTextFieldInit(textfield);
	}

	@BytecodeAccess
	public static int getMaxChatMessages() {
		return MinecraftFactory.getClassProxyCallback().getMaxChatLines();
	}

	@BytecodeAccess
	public static void onChatMouseInput(int scroll) {
		The5zigMod.getVars().get2ndChat().scroll(scroll);
	}

	@BytecodeAccess
	public static boolean onChatMouseClicked(int mouseX, int mouseY, int button) {
		return The5zigMod.getRenderer().getChatSymbolsRenderer().mouseClicked(mouseX, mouseY) || The5zigMod.getVars().get2ndChat().mouseClicked(mouseX, mouseY, button);
	}

	@BytecodeAccess
	public static boolean onChatMouseClicked(double mouseX, double mouseY, int button) {
		return The5zigMod.getRenderer().getChatSymbolsRenderer().mouseClicked((int)mouseX, (int)mouseY) || The5zigMod.getVars().get2ndChat().mouseClicked((int)mouseX, (int)mouseY, button);
	}

	@BytecodeAccess
	public static void onChatClosed() {
		The5zigMod.getVars().get2ndChat().resetScroll();
	}

	@BytecodeAccess
	public static void onChatKeyTyped(int key) {
		The5zigMod.getVars().get2ndChat().keyTyped(key);
	}

	@BytecodeAccess
	public static void onChatDrawScreen(int mouseX, int mouseY) {
		The5zigMod.getVars().get2ndChat().drawComponentHover(mouseX, mouseY);
	}

	@BytecodeAccess
	public static void onClearChat() {
		The5zigMod.getVars().get2ndChat().clear();
	}

	@BytecodeAccess
	public static void onDrawChat(int updateCounter) {
		The5zigMod.getVars().get2ndChat().draw(updateCounter);
	}

	@BytecodeAccess
	public static Object getChatComponentWithTime(Object originalChatComponent) {
		return The5zigMod.getDataManager().getChatComponentWithTime(originalChatComponent);
	}

	@BytecodeAccess
	public static boolean isShowTimeBeforeChatMessage() {
		return The5zigMod.getConfig().getBool("chatTimePrefixEnabled");
	}

	@BytecodeAccess
	public static void onRenderGameOverlay() {
		The5zigMod.getGuiIngame().renderGameOverlay();
	}

	@BytecodeAccess
	public static boolean onRenderPotionEffectIndicator() {
		return The5zigMod.getConfig().getBool("showVanillaPotionIndicator");
	}

	@BytecodeAccess
	public static void onRenderHotbar() {
		The5zigMod.getGuiIngame().onRenderHotbar();
	}

	@BytecodeAccess
	public static void onIngameTick() {
		The5zigMod.getGuiIngame().tick();
	}

	@BytecodeAccess
	public static void onRenderFood() {
		The5zigMod.getGuiIngame().onRenderFood();
	}

	@BytecodeAccess
	public static void onRenderVignette() {
		The5zigMod.getRenderer().getPotionIndicatorRenderer().render();
	}

	@BytecodeAccess
	public static void onMainStatic() {
		// The5zigMod.init();
	}

	@BytecodeAccess
	public static void onMainDraw() {
		The5zigMod.getVars().drawString(ChatColor.GOLD + "The 5zig Mod v" + Version.VERSION, 2, 2);
	}

	@BytecodeAccess
	public static IButton get5zigOptionButton(Object instance) {
		return ClassProxy.getThe5zigModButton(instance);
	}

	@BytecodeAccess
	public static void onCustomPayload(String channel, ByteBuf byteBuf) {
		The5zigMod.getListener().handlePluginMessage(channel, byteBuf);
	}

	@BytecodeAccess
	public static ByteBuf packetBufferToByteBuf(Object packetBuffer) {
		return ClassProxy.packetBufferToByteBuf(packetBuffer);
	}

	@BytecodeAccess
	public static void onPlayerListHeaderFooter(TabList tabList) {
		The5zigMod.getListener().onPlayerListHeaderFooter(tabList);
	}

	@BytecodeAccess
	public static boolean onChat(String message, Object chatComponent) {
		return The5zigMod.getListener().onServerChat(message, chatComponent);
	}

	@BytecodeAccess
	public static boolean onActionBar(String message) {
		return The5zigMod.getListener().onActionBar(message);
	}

	@BytecodeAccess
	public static void onSetSlot(int slot, ItemStack itemStack) {
		The5zigMod.getListener().onInventorySetSlot(slot, itemStack);
	}

	@BytecodeAccess
	public static void onGuiDisconnectedInit(Object parentScreen) {
		The5zigMod.getDataManager().getAutoReconnectManager().startCountdown(parentScreen);
	}

	@BytecodeAccess
	public static void onGuiConnecting(Object serverData) {
		ClassProxy.setServerData(serverData);
	}

	@BytecodeAccess
	public static void onTitle(String title, String subTitle) {
		The5zigMod.getListener().onTitle(title, subTitle);
	}

	@BytecodeAccess
	public static void onTeleport(double x, double y, double z, float yaw, float pitch) {
		The5zigMod.getListener().onTeleport(x, y, z, yaw, pitch);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksInit(Object instance, List list, List list2) {
		ClassProxy.handleGuiResourcePackInit(instance, list, list2);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksInit(Object instance, Object list, Object list2) {
		ClassProxy.handleGuiResourcePackInit(instance, list, list2);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksClosed() {
		The5zigMod.getDataManager().getSearchManager().onGuiClose();
	}

	@BytecodeAccess
	public static void onGuiResourcePacksDraw() {
		The5zigMod.getDataManager().getSearchManager().draw();
	}

	@BytecodeAccess
	public static void onGuiResourcePacksKey(char character, int code) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(character, code);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksKey(int key, int scanCode, int modifiers) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(key, scanCode, modifiers);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksMouseClicked(int mouseX, int mouseY, int button) {
		The5zigMod.getDataManager().getSearchManager().mouseClicked(mouseX, mouseY, button);
	}

	@BytecodeAccess
	public static void onGuiResourcePacksMouseClicked(double mouseX, double mouseY, int button) {
		onGuiResourcePacksMouseClicked((int)mouseX, (int)mouseY, button);
	}

	@BytecodeAccess
	public static void onGuiMultiplayerClosed() {
		The5zigMod.getDataManager().getSearchManager().onGuiClose();
	}

	@BytecodeAccess
	public static void onGuiMultiplayerDraw() {
		The5zigMod.getDataManager().getSearchManager().draw();
	}

	@BytecodeAccess
	public static void onGuiMultiplayerKey(char character, int code) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(character, code);
	}

	@BytecodeAccess
	public static void onGuiMultiplayerKey(int key, int scanCode, int modifiers) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(key, scanCode, modifiers);
	}

	@BytecodeAccess
	public static void onGuiMultiplayerMouseClicked(int mouseX, int mouseY, int button) {
		The5zigMod.getDataManager().getSearchManager().mouseClicked(mouseX, mouseY, button);
	}

	@BytecodeAccess
	public static void onGuiMultiplayerMouseClicked(double mouseX, double mouseY, int button) {
		onGuiMultiplayerMouseClicked((int)mouseX, (int)mouseY, button);
	}

	@BytecodeAccess
	public static void onGuiSelectWorldInit(Object instance, List list) {
		//ClassProxy.handleGuiSelectWorldInit(instance, list);
	}

	@BytecodeAccess
	public static void onGuiSelectWorldClosed() {
		The5zigMod.getDataManager().getSearchManager().onGuiClose();
	}

	@BytecodeAccess
	public static void onGuiSelectWorldDraw() {
		The5zigMod.getDataManager().getSearchManager().draw();
	}

	@BytecodeAccess
	public static void onGuiSelectWorldKey(char character, int code) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(character, code);
	}

	@BytecodeAccess
	public static void onGuiSelectWorldKey(int key, int scanCode, int modifiers) {
		The5zigMod.getDataManager().getSearchManager().keyTyped(key, scanCode, modifiers);
	}

	@BytecodeAccess
	public static void onGuiSelectWorldMouseClicked(int mouseX, int mouseY, int button) {
		The5zigMod.getDataManager().getSearchManager().mouseClicked(mouseX, mouseY, button);
	}

	@BytecodeAccess
	public static void onGuiSelectWorldMouseClicked(double mouseX, double mouseY, int button) {
		onGuiSelectWorldMouseClicked((int)mouseX, (int)mouseY, button);
	}

	@BytecodeAccess
	public static void onRealTick() {
		The5zigMod.getListener().onTick();
	}

	@BytecodeAccess
	public static boolean onRenderItemPerson(Object instance, Object itemStack, Object entityPlayer, Object cameraTransformType, boolean leftHand) {
		return ClassProxy.onRenderItemPerson(instance, itemStack, entityPlayer, cameraTransformType, leftHand);
	}

	@BytecodeAccess
	public static boolean onRenderItemPerson(Object instance, Object itemStack, Object entityPlayer, Object cameraTransformType) {
		return ClassProxy.onRenderItemPerson(instance, itemStack, entityPlayer, cameraTransformType, false);
	}

	@BytecodeAccess
	public static boolean onRenderItemInventory(Object instance, Object itemStack, int x, int y) {
		return ClassProxy.onRenderItemInventory(instance, itemStack, x, y);
	}

	@BytecodeAccess
	public static void onSendChatMessage(String message) {
		The5zigMod.getListener().onSendChatMessage(message);
	}


	@BytecodeAccess
	public static void onGuiGameOverInit() {
		if (The5zigMod.getVars().isPlayerNull()) {
			return;
		}
		Vector3f coordinates = new Vector3f((float) The5zigMod.getVars().getPlayerPosX(), (float) The5zigMod.getVars().getPlayerPosY(),
				(float) The5zigMod.getVars().getPlayerPosZ());
		WorldType worldType = WorldType.OVERWORLD;
		String biome = The5zigMod.getVars().getBiome();
		if (biome != null) {
			worldType = WorldType.byName(biome);
		}
		The5zigMod.getDataManager().setDeathLocation(new DeathLocation(coordinates, worldType));
	}

	@BytecodeAccess
	public static int getChatAlphaMultiplicator() {
		return The5zigMod.getConfig().getBool("transparentChatBackground") ? 0 : 1;
	}

	@BytecodeAccess
	public static boolean shouldRenderOwnName() {
		return The5zigMod.getConfig().getBool("showOwnNameTag");
	}
}
