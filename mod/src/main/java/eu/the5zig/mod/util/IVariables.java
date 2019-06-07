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

import com.mojang.authlib.GameProfile;
import eu.the5zig.mod.gui.IOverlay;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.gui.ingame.PotionEffect;
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.util.Callback;
import io.netty.buffer.ByteBuf;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.IWrappedGui;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.gui.ingame.IGui2ndChat;
import eu.the5zig.mod.gui.ingame.resource.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.Proxy;
import java.util.List;

/**
 * Contains various utility methods for Minecraft. Due to the Minecraft code & class names being obfuscated, this class has its own implementation for every Minecraft version.
 */
public interface IVariables {

	/**
	 * Draws a String at given position with shadow.
	 *
	 * @param string The String
	 * @param x      The x Coordinate
	 * @param y      The y Coordinate
	 * @param format Arguments referenced by the format specifiers in the format string
	 */
	void drawString(String string, int x, int y, Object... format);

	/**
	 * Draws a String at given position with shadow.
	 *
	 * @param string The String
	 * @param x      The x Coordinate
	 * @param y      The y Coordinate
	 */
	void drawString(String string, int x, int y);

	/**
	 * Draws a centered String at given position with shadow.
	 *
	 * @param string The String
	 * @param x      The x Coordinate/the middle of the rendered text.
	 * @param y      The y Coordinate
	 */
	void drawCenteredString(String string, int x, int y);

	/**
	 * Draws a centered String at given position.
	 *
	 * @param string The String
	 * @param x      The x Coordinate/the middle of the rendered text.
	 * @param y      The y Coordinate
	 * @param color  The hex-color the rendered text should have.
	 */
	void drawCenteredString(String string, int x, int y, int color);

	/**
	 * Draws a String at given position with color and shadow.
	 *
	 * @param string The String
	 * @param x      The y Coordinate
	 * @param y      The x Coordinate
	 * @param color  The color of the String
	 * @param format Arguments referenced by the format specifiers in the format string
	 */
	void drawString(String string, int x, int y, int color, Object... format);

	/**
	 * Draws a String at given position with color and shadow.
	 *
	 * @param string The String
	 * @param x      The y Coordinate
	 * @param y      The x Coordinate
	 * @param color  The color of the String
	 */
	void drawString(String string, int x, int y, int color);

	/**
	 * Draws a String at given position with color.
	 *
	 * @param string     The String
	 * @param x          The y Coordinate
	 * @param y          The x Coordinate
	 * @param color      The color of the String
	 * @param withShadow True, if the String should have a shadow.
	 */
	void drawString(String string, int x, int y, int color, boolean withShadow);

	/**
	 * Splits the input string into parts that are not longer than the specified max width.
	 *
	 * @param string   The string that should be split.
	 * @param maxWidth The maximum width the split parts should be long.
	 * @return a list containing all split parts of the String.
	 */
	List<String> splitStringToWidth(String string, int maxWidth);

	/**
	 * @param string The String whose width should be calculated.
	 * @return the width of the String.
	 */
	int getStringWidth(String string);

	/**
	 * Shortens a String to a specified width.
	 *
	 * @param string The String that should be shortened.
	 * @param width  The maximum width the String should have.
	 * @return a String that is not longer than the specified width.
	 */
	String shortenToWidth(String string, int width);

	/**
	 * Creates an instance of a button.
	 *
	 * @param id    the id of the button.
	 * @param x     the x-coordinate of the button.
	 * @param y     the y-coordinate of the button.
	 * @param label the label of the button.
	 * @return an instance of a button.
	 */
	IButton createButton(int id, int x, int y, String label);

	/**
	 * Creates an instance of a button.
	 *
	 * @param id      the id of the button.
	 * @param x       the x-coordinate of the button.
	 * @param y       the y-coordinate of the button.
	 * @param label   the label of the button.
	 * @param enabled true, if the button should be enabled (/clickable).
	 * @return an instance of a button.
	 */
	IButton createButton(int id, int x, int y, String label, boolean enabled);

	/**
	 * Creates an instance of a button.
	 *
	 * @param id     the id of the button.
	 * @param x      the x-coordinate of the button.
	 * @param y      the y-coordinate of the button.
	 * @param width  the width of the button.
	 * @param height the height of the button.
	 * @param label  the label of the button.
	 * @return an instance of a button.
	 */
	IButton createButton(int id, int x, int y, int width, int height, String label);

	/**
	 * Creates an instance of a button.
	 *
	 * @param id      the id of the button.
	 * @param x       the x-coordinate of the button.
	 * @param y       the y-coordinate of the button.
	 * @param width   the width of the button.
	 * @param height  the height of the button.
	 * @param label   the label of the button.
	 * @param enabled true, if the button should be enabled (/clickable).
	 * @return an instance of a button.
	 */
	IButton createButton(int id, int x, int y, int width, int height, String label, boolean enabled);

	/**
	 * Creates an instance of a button. This button will only render its label and not its full GUI texture.
	 *
	 * @param id    the id of the button.
	 * @param x     the x-coordinate of the button.
	 * @param y     the y-coordinate of the button.
	 * @param label the label of the button.
	 * @return an instance of a button.
	 */
	IButton createStringButton(int id, int x, int y, String label);

	/**
	 * Creates an instance of a button. This button will only render its label and not its full GUI texture.
	 *
	 * @param id     the id of the button.
	 * @param x      the x-coordinate of the button.
	 * @param y      the y-coordinate of the button.
	 * @param width  the width of the button.
	 * @param height the height of the button.
	 * @param label  the label of the button.
	 * @return an instance of a button.
	 */
	IButton createStringButton(int id, int x, int y, int width, int height, String label);

	/**
	 * Creates an instance of a button. This button is used to record audio files from the clients microphone whenever the user clicks on it.
	 *
	 * @param id       the id of the button.
	 * @param x        the x-coordinate of the button.
	 * @param y        the y-coordinate of the button.
	 * @param callback a callback that gets called each time the audio record has been finished and saved to hard drive.
	 * @return an instance of a button.
	 */
	IButton createAudioButton(int id, int x, int y, AudioCallback callback);

	/**
	 * Creates an instance of a button. This button does not have a label, but displays a texture instead.
	 *
	 * @param resourceLocation the texture that should be rendered instead of the texture.
	 * @param u                the u-coordinate of the texture.
	 * @param v                the v-coordinate of the texture.
	 * @param id               the id of the button.
	 * @param x                the x-coordinate of the button.
	 * @param y                the y-coordinate of the button.
	 * @return an instance of a button.
	 */
	IButton createIconButton(IResourceLocation resourceLocation, int u, int v, int id, int x, int y);

	/**
	 * Creates an instance of a textfield.
	 *
	 * @param id     the id of the textfield.
	 * @param x      the x-coordinate of the textfield.
	 * @param y      the y-coordinate of the textfield.
	 * @param width  the width of the textfield.
	 * @param height the height of the textfield.
	 * @return an instance of a textfield.
	 */
	ITextfield createTextfield(int id, int x, int y, int width, int height);

	/**
	 * Creates an instance of a textfield.
	 *
	 * @param id              the id of the textfield.
	 * @param x               the x-coordinate of the textfield.
	 * @param y               the y-coordinate of the textfield.
	 * @param width           the width of the textfield.
	 * @param height          the height of the textfield.
	 * @param maxStringLength the maximum string length of the textfield.
	 * @return an instance of a textfield.
	 */
	ITextfield createTextfield(int id, int x, int y, int width, int height, int maxStringLength);

	/**
	 * Creates an instance of a textfield.
	 *
	 * @param placeholder a placeholder text that will be displayed whenever the textfield is not focused.
	 * @param id          the id of the textfield.
	 * @param x           the x-coordinate of the textfield.
	 * @param y           the y-coordinate of the textfield.
	 * @param width       the width of the textfield.
	 * @param height      the height of the textfield.
	 * @return an instance of a textfield.
	 */
	IPlaceholderTextfield createTextfield(String placeholder, int id, int x, int y, int width, int height);

	/**
	 * Creates an instance of a textfield.
	 *
	 * @param placeholder     a placeholder text that will be displayed whenever the textfield is not focused.
	 * @param id              the id of the textfield.
	 * @param x               the x-coordinate of the textfield.
	 * @param y               the y-coordinate of the textfield.
	 * @param width           the width of the textfield.
	 * @param height          the height of the textfield.
	 * @param maxStringLength the maximum string length of the textfield.
	 * @return an instance of a textfield.
	 */
	IPlaceholderTextfield createTextfield(String placeholder, int id, int x, int y, int width, int height, int maxStringLength);

	/**
	 * Wrapps a Minecraft textfield into an {@link IWrappedTextfield}.
	 *
	 * @param handle the Minecraft textfield.
	 * @return a wrapped instance of the Minecraft textfield.
	 */
	IWrappedTextfield createWrappedTextfield(Object handle);

	/**
	 * Creates an instance of a GUI-list. This instance can be used to draw all entries of a list onto the screen by adding a scrolling functionality and allows the exact specification of
	 * how an entry should be drawn.
	 *
	 * @param clickable a callback that gets called every time the user either performs a single or a double click onto an entry.
	 * @param width     the width of the screen (usually {@link Gui#getWidth()}).
	 * @param height    the height of the screen (usually {@link Gui#getHeight()}).
	 * @param top       the top y-position of the list.
	 * @param bottom    the bottom y-position of the list.
	 * @param left      the left x-position of the list.
	 * @param right     the right x-position of the list.
	 * @param rows      a list that contains all rows. The elements in this list are allowed to be changed in the client thread.
	 * @param <E>       a class that extends {@link Row} and which is responsible of drawing all rows of the specified list.
	 * @return an instance of a GUI-list.
	 */
	<E extends Row> IGuiList<E> createGuiList(Clickable<E> clickable, int width, int height, int top, int bottom, int left, int right, List<E> rows);

	/**
	 * Creates an instance of a GUI-list. This instance can be used to draw all entries of a list onto the screen by adding a scrolling functionality and allows the exact specification of
	 * how an entry should be drawn. This method is only used for creating a chat list, as this class contains code for changing the background of the panel and allows the user to open
	 * URL-links.
	 *
	 * @param width    the width of the screen (usually {@link Gui#getWidth()}).
	 * @param height   the height of the screen (usually {@link Gui#getHeight()}).
	 * @param top      the top y-position of the list.
	 * @param bottom   the bottom y-position of the list.
	 * @param left     the left x-position of the list.
	 * @param right    the right x-position of the list.
	 * @param scrollx  the x-position of the scrollbar.
	 * @param rows     a list that contains all rows. The elements in this list are allowed to be changed in the client thread.
	 * @param callback a callback class that is used to get some config values and is called whenever a chat line has been clicked.
	 * @param <E>      a class that extends {@link Row} and which is responsible of drawing all rows of the specified list.
	 * @return an instance of a GUI-list.
	 */
	<E extends Row> IGuiList<E> createGuiListChat(int width, int height, int top, int bottom, int left, int right, int scrollx, List<E> rows, GuiListChatCallback callback);

	/**
	 * Creates a file selector GUI element, which allows the client to navigate through his file system.
	 *
	 * @param currentDir the start directory of the file selector.
	 * @param width      the width of the screen (usually {@link Gui#getWidth()}).
	 * @param height     the height of the screen (usually {@link Gui#getHeight()}).
	 * @param left       the left x-position of the file selector.
	 * @param right      the right x-position of the file selector.
	 * @param top        the top y-position of the file selector.
	 * @param bottom     the bottom y-position of the file selector.
	 * @param callback   a callback that will be called when the player has selected a file through the file selector.
	 * @return an instance of a file selector.
	 */
	IFileSelector createFileSelector(File currentDir, int width, int height, int left, int right, int top, int bottom, Callback<File> callback);

	/**
	 * Creates an instance of a slider.
	 *
	 * @param id             the id of the slider.
	 * @param x              the x-position of the slider.
	 * @param y              the y-position of the slider.
	 * @param sliderCallback a callback class that is used to get some config values and gets called whenever the player clicks on the slider.
	 * @return an instance of a slider.
	 */
	IButton createSlider(int id, int x, int y, SliderCallback sliderCallback);

	/**
	 * Creates an instance of a slider.
	 *
	 * @param id             the id of the slider.
	 * @param x              the x-position of the slider.
	 * @param y              the y-position of the slider.
	 * @param width          the width of the slider.
	 * @param height         the height of the slider.
	 * @param sliderCallback a callback class that is used to get some config values and gets called whenever the player clicks on the slider.
	 * @return an instance of a slider.
	 */
	IButton createSlider(int id, int x, int y, int width, int height, SliderCallback sliderCallback);

	/**
	 * Creates a color selector button.
	 *
	 * @param id       the id of the button.
	 * @param x        the x-coordinate of the button.
	 * @param y        the y-coordinate of the button.
	 * @param width    the width of the button.
	 * @param height   the height of the button.
	 * @param label    the label of the button.
	 * @param callback a callback class that is used to get and set the current color of the button.
	 * @return an instance of a color selector button.
	 */
	IColorSelector createColorSelector(int id, int x, int y, int width, int height, String label, ColorSelectorCallback callback);

	/**
	 * @return a new overlay that will be displayed in the top right corner of the screen.
	 */
	IOverlay newOverlay();

	/**
	 * Updates the maximum overlay count.
	 *
	 * @param count the amount of overlays that should be displayed at the same time.
	 */
	void updateOverlayCount(int count);

	/**
	 * Renders all overlays onto the screen.
	 */
	void renderOverlay();

	/**
	 * Creates a wrapped GUI for a Minecraft internal screen.
	 *
	 * @param lastScreen the screen that should be wrapped.
	 * @return a wrapped GUI.
	 */
	IWrappedGui createWrappedGui(Object lastScreen);

	/**
	 * Creates a custom keybinding.
	 *
	 * @param description the description of the keybinding (also used as key in the options file).
	 * @param keyCode     the default {@link org.lwjgl.input.Keyboard}-code of the key.
	 * @param category    the category of the keybinding.
	 * @return the created keybinding.
	 */
	IKeybinding createKeybinding(String description, int keyCode, String category);

	/**
	 * @return the second ingame chat.
	 */
	IGui2ndChat get2ndChat();

	/**
	 * @return true, if the chat is currently opened.
	 */
	boolean isChatOpened();

	/**
	 * @return the text of the chat box.
	 */
	String getChatBoxText();

	/**
	 * Types some text into the current ingame chat GUI.
	 *
	 * @param text the text that should be typed.
	 */
	void typeInChatGUI(String text);

	/**
	 * Creates a new chat component with given prefix.
	 *
	 * @param prefix                the prefix that should be added to the chat component.
	 * @param originalChatComponent the original chat component.
	 * @return a new chat component.
	 */
	Object getChatComponentWithPrefix(String prefix, Object originalChatComponent);

	/**
	 * @return true, if the sign GUI is currently opened.
	 */
	boolean isSignGUIOpened();

	/**
	 * Registers a list of custom keybindings.
	 *
	 * @param keybindings a list of keybindings.
	 */
	void registerKeybindings(List<IKeybinding> keybindings);

	/**
	 * Plays a sound to the player.
	 *
	 * @param sound the resource key of the sound.
	 * @param pitch the pitch value of the sound.
	 */
	void playSound(String sound, float pitch);

	/**
	 * Plays a sound to the player.
	 *
	 * @param domain the resource domain of the sound.
	 * @param sound  the resource key of the sound.
	 * @param pitch  the pitch value of the sound.
	 */
	void playSound(String domain, String sound, float pitch);

	/**
	 * @return the font-height of the font used by Minecraft. A constant value of {@code 9} pixels, as of today.
	 */
	int getFontHeight();

	/**
	 * Resets the internally stored server value.
	 */
	void resetServer();

	/**
	 * @return the address of the current Minecraft server the client is playing on in {@code <host>:<port>}-format
	 */
	String getServer();

	/**
	 * @return the amount of players on the current Minecraft server the client is playing on.
	 */
	List<NetworkPlayerInfo> getServerPlayers();

	/**
	 * @return true, if the player list is currently shown.
	 */
	boolean isPlayerListShown();

	/**
	 * Sets the field-of-view for the player.
	 *
	 * @param fov the new field-of-view.
	 */
	void setFOV(float fov);

	/**
	 * @return the current field-of-view of the player.
	 */
	float getFOV();

	/**
	 * Enables or disables the smooth camera.
	 *
	 * @param smoothCamera true, if the smooth camera should be enabled.
	 */
	void setSmoothCamera(boolean smoothCamera);

	/**
	 * @return true, if the smooth camera is currently enabled.
	 */
	boolean isSmoothCamera();

	/**
	 * Translates a String to minecraft language
	 *
	 * @param location The String key
	 * @return String translated String
	 */
	String translate(String location, Object... values);

	/**
	 * Displays a new GUI screen.
	 *
	 * @param gui the new gui that should be displayed.
	 */
	void displayScreen(Gui gui);

	/**
	 * Displays a new GUI screen.
	 *
	 * @param gui the new gui that should be displayed.
	 */
	void displayScreen(Object gui);

	/**
	 * Connects the client to a new Minecraft server.
	 *
	 * @param host the host of the server.
	 * @param port the port of the server.
	 */
	void joinServer(String host, int port);

	/**
	 * Connects the client to a new Minecraft server.
	 *
	 * @param parentScreen the parent screen.
	 * @param serverData   the Minecraft server data.
	 */
	void joinServer(Object parentScreen, Object serverData);

	/**
	 * Disconnects the client from the world.
	 */
	void disconnectFromWorld();

	/**
	 * @return a new server pinger instance.
	 */
	IServerPinger getServerPinger();

	/**
	 * @return the sytem time in Milliseconds.
	 */
	long getSystemTime();

	/**
	 * @return true, if the player is currently not spectating any other entity.
	 */
	boolean isSpectatingSelf();

	/**
	 * @return the game mode of the player.
	 */
	PlayerGameMode getGameMode();

	/**
	 * @return the title of the currently opened container or {@code null}, if no container is currently opened.
	 */
	String getOpenContainerTitle();

	/**
	 * Closes the currently opened container (eg. chest or player inventory.)
	 */
	void closeContainer();

	/**
	 * @return the session id of the player.
	 */
	String getSession();

	/**
	 * @return the username of the player.
	 */
	String getUsername();

	/**
	 * @return a proxy, if specified by the client.
	 */
	Proxy getProxy();

	/**
	 * @return the game profile of the current player.
	 */
	GameProfile getGameProfile();

	/**
	 * @return the amount of rendered frames during the last second.
	 */
	String getFPS();

	/**
	 * @return true, if the player instance is currently {@code null}.
	 */
	boolean isPlayerNull();

	/**
	 * @return true, if the world is currently loading.
	 */
	boolean isTerrainLoading();

	/**
	 * @return the current x-position of the player or the entity he is currently spectating.
	 */
	double getPlayerPosX();

	/**
	 * @return the current y-position of the player or the entity he is currently spectating.
	 */
	double getPlayerPosY();

	/**
	 * @return the current z-position of the player or the entity he is currently spectating.
	 */
	double getPlayerPosZ();

	/**
	 * @return the current yaw rotation of the player or the entity he is currently spectating.
	 */
	float getPlayerRotationYaw();

	/**
	 * @return the current pitch rotation of the player or the entity he is currently spectating.
	 */
	float getPlayerRotationPitch();

	/**
	 * @return the x-coordinate of the chunk of the player.
	 */
	int getPlayerChunkX();

	/**
	 * @return the y-coordinate of the chunk of the player.
	 */
	int getPlayerChunkY();

	/**
	 * @return the z-coordinate of the chunk of the player.
	 */
	int getPlayerChunkZ();

	/**
	 * @return the relative x-coordinate of the chunk of the player.
	 */
	int getPlayerChunkRelX();

	/**
	 * @return the relative y-coordinate of the chunk of the player.
	 */
	int getPlayerChunkRelY();


	/**
	 * @return the relative z-coordinate of the chunk of the player.
	 */
	int getPlayerChunkRelZ();

	/**
	 * @return true, if the player is looking at a block and the block is in range.
	 */
	boolean hasTargetBlock();

	/**
	 * @return the x-coordinate of the target block.
	 */
	int getTargetBlockX();

	/**
	 * @return the y-coordinate of the target block.
	 */
	int getTargetBlockY();

	/**
	 * @return the z-coordinate of the target block.
	 */
	int getTargetBlockZ();

	/**
	 * @return the name of the target block.
	 */
	IResourceLocation getTargetBlockName();

	/**
	 * @return true, if fancy graphics have been enabled by the player.
	 */
	boolean isFancyGraphicsEnabled();

	/**
	 * @return the current biome of the player or {@code null}, if the player is outside the world.
	 */
	String getBiome();

	/**
	 * @return the current light level or {@code 0}, if the player is outside the world.
	 */
	int getLightLevel();

	/**
	 * @return the visible and total amount of entities in the current world.
	 */
	String getEntityCount();

	/**
	 * @return true, if the player is currently riding an entity.
	 */
	boolean isRidingEntity();

	/**
	 * @return the current food level of the player.
	 */
	int getFoodLevel();

	/**
	 * @return the current saturation value of the player.
	 */
	float getSaturation();

	/**
	 * Gets the health of an entity.
	 *
	 * @param entity the entity.
	 * @return the health of the entity.
	 */
	float getHealth(Object entity);

	/**
	 * @return the health of the player.
	 */
	float getPlayerHealth();

	/**
	 * @return the maximum health of the player.
	 */
	float getPlayerMaxHealth();

	/**
	 * @return the current armor value of the player.
	 */
	int getPlayerArmor();

	/**
	 * @return the remaining air-time of the player.
	 */
	int getAir();

	/**
	 * @return true, if the player is currently inside water.
	 */
	boolean isPlayerInsideWater();

	/**
	 * @return the amount of damage the armor and potion effects of the player absorb.
	 */
	float getResistanceFactor();

	/**
	 * @return the potion effect that should be used when rendering the potion effect vignette.
	 */
	PotionEffect getPotionForVignette();

	/**
	 * @return a list that contains all active potion effects of the player.
	 */
	List<? extends PotionEffect> getActivePotionEffects();

	/**
	 * @return a list that contains some dummy potion effects.
	 */
	List<? extends PotionEffect> getDummyPotionEffects();

	/**
	 * @return the height of the potion effect indicator that has been added in Minecraft 1.9. Used to offset all mod render items.
	 */
	int getPotionEffectIndicatorHeight();

	/**
	 * @return true, if a hunger potion is currently active.
	 */
	boolean isHungerPotionActive();

	/**
	 * @return the item that the player is currently holding in his main hand.
	 */
	ItemStack getItemInMainHand();

	/**
	 * @return the item that the player is currently holding in his off hand.
	 */
	ItemStack getItemInOffHand();

	/**
	 * Gets an item by its armor slot.
	 *
	 * @param slot the armor slot id. Must be a value between zero and three.
	 * @return the item of the specified armor slot.
	 */
	ItemStack getItemInArmorSlot(int slot);

	/**
	 * Creates an item stack from its type.
	 *
	 * @param resourceName the resource key of the item stack.
	 * @return a new item stack.
	 */
	ItemStack getItemByName(String resourceName);

	/**
	 * Creates an item stack from its type.
	 *
	 * @param resourceName the resource key of the item stack.
	 * @param amount       the amount of the item stack.
	 * @return a new item stack.
	 */
	ItemStack getItemByName(String resourceName, int amount);

	/**
	 * Counts how many items of a specified type are in the players inventory.
	 *
	 * @param key the resource key of the item.
	 * @return the amount of found items.
	 */
	int getItemCount(String key);

	/**
	 * @return the selected hotbar slot. Is a value between zero and eight.
	 */
	int getSelectedHotbarSlot();

	/**
	 * Selects a hotbar slot.
	 *
	 * @param slot the new slot that should be selected. Must be a value between zero and eight.
	 */
	void setSelectedHotbarSlot(int slot);

	/**
	 * Executes a "right click" on the current item of the player in his inventory.
	 */
	void onRightClickMouse();

	/**
	 * Updates the stored scaled resolution. Called every frame to match window size.
	 */
	void updateScaledResolution();

	/**
	 * @return the width of the window in pixels.
	 */
	int getWidth();

	/**
	 * @return the height of the window in pixels.
	 */
	int getHeight();

	/**
	 * @return the scaled width of the window in pixels.
	 */
	int getScaledWidth();

	/**
	 * @return the scaled height of the window in pixels.
	 */
	int getScaledHeight();

	/**
	 * @return the current scale factor of the window.
	 */
	int getScaleFactor();

	/**
	 * @return true, if the debug screen is currently visible.
	 */
	boolean showDebugScreen();

	/**
	 * @return true, if the player is spectating another entity.
	 */
	boolean isPlayerSpectating();

	/**
	 * @return true, if the ingame HUD currently gets drawn.
	 */
	boolean shouldDrawHUD();

	/**
	 * @return an array containing the keybindings of all hotbar slots in {@link org.lwjgl.input.Keyboard}-ids.
	 */
	String[] getHotbarKeys();

	/**
	 * Gets the display name of a key
	 *
	 * @param key the keycode
	 * @return the display name of the key.
	 */
	String getKeyDisplayStringShort(int key);

	/**
	 * Draws a textured rectangle into the ingame GUI.
	 *
	 * @param x      the x-position of the rectangle.
	 * @param y      the y-position of the rectangle.
	 * @param u      the u-coordinate of the texture.
	 * @param v      the v-coordinate of the texture.
	 * @param width  the width of the rectangle.
	 * @param height the height of the rectangle.
	 */
	void drawIngameTexturedModalRect(int x, int y, int u, int v, int width, int height);

	/**
	 * Adds a chat message to the client's chat.
	 *
	 * @param message the message that should be added.
	 */
	void messagePlayer(String message);

	/**
	 * Sends a chat message from the client to the current server (or single player world).
	 *
	 * @param message the message that should be sent.
	 */
	void sendMessage(String message);

	/**
	 * @return true, if the current network manager is not {@code null}.
	 */
	boolean hasNetworkManager();

	/**
	 * Sends a custom payload over the Minecraft plugin channel to a server.
	 *
	 * @param channel the name of the plugin channel.
	 * @param payload a {@link ByteBuf} containing the custom payload.
	 */
	void sendCustomPayload(String channel, ByteBuf payload);

	/**
	 * @return true, if the current world is a local world.
	 */
	boolean isLocalWorld();

	/**
	 * @return the current screen or a wrapper for it, if the screen is a Minecraft internal class.
	 */
	Gui getCurrentScreen();

	/**
	 * @return the current screen.
	 */
	Object getMinecraftScreen();

	/**
	 * Links a buffered image to a resource location.
	 *
	 * @param name  the resource key of the resource location.
	 * @param image the buffered image.
	 * @return the linked resource location.
	 */
	Object loadDynamicImage(String name, BufferedImage image);

	/**
	 * Creates a new resource location.
	 *
	 * @param resourcePath the resource key.
	 * @return a new instance of a resource location.
	 */
	IResourceLocation createResourceLocation(String resourcePath);

	/**
	 * Creates a new resource location.
	 *
	 * @param resourceDomain the resource domain.
	 * @param resourcePath   the resource key.
	 * @return a new instance of a resource location.
	 */
	IResourceLocation createResourceLocation(String resourceDomain, String resourcePath);

	/**
	 * Binds a resource location.
	 *
	 * @param resourceLocation the resource location that should be bound.
	 */
	void bindTexture(Object resourceLocation);

	/**
	 * Deletes a texture.
	 *
	 * @param resourceLocation the resource location of the texture that should be deleted.
	 */
	void deleteTexture(Object resourceLocation);

	/**
	 * Creates a new dynamic image with a fixed width and height.
	 *
	 * @param resourceLocation the resource location of the dynamic image.
	 * @param width            the width of the dynamic image.
	 * @param height           the height of the dynamic image.
	 * @return a new dynamic image.
	 */
	Object createDynamicImage(Object resourceLocation, int width, int height);

	/**
	 * Gets a dynamic image by its resource location.
	 *
	 * @param resourceLocation the resource location of the dynamic image.
	 * @return the dynamic image.
	 */
	Object getTexture(Object resourceLocation);

	/**
	 * Draws a dynamic image to the screen.
	 *
	 * @param dynamicImage the dynamic image.
	 * @param image        the buffered image that should be rendered.
	 */
	void fillDynamicImage(Object dynamicImage, BufferedImage image);

	/**
	 * Renders the icon of a potion.
	 *
	 * @param index the index of the potion.
	 */
	void renderPotionIcon(int index);

	/**
	 * Renders a rectangle using the default Minecraft dirt texture.
	 *
	 * @param x1 the left x-position of the rectangle.
	 * @param x2 the right x-position of the rectangle.
	 * @param y1 the top y-position of the rectangle.
	 * @param y2 the bottom y-position of the rectangle.
	 */
	void renderTextureOverlay(int x1, int x2, int y1, int y2);

	/**
	 * Sets the ingame focues.
	 */
	void setIngameFocus();

	/**
	 * Executes a world ray trace.
	 *
	 * @param maxDistance the maximum distance of the ray trace.
	 * @return the object in sight.
	 */
	MouseOverObject calculateMouseOverDistance(double maxDistance);

	/**
	 * @return the current side-board of the player scoreboard or {@code null}, if the player doesn't have an active side-board.
	 */
	Scoreboard getScoreboard();

	/**
	 * @return an instance of the resource manager, which handles the loading of capes and drawing of custom sword models.
	 */
	IResourceManager getResourceManager();

	/**
	 * @return the current battery status of a Windows laptop.
	 */
	ISystemPowerStatus getBatteryStatus();

	/**
	 * @return a 16x16 or (if available) a 32x32 Minecraft icon.
	 */
	InputStream getMinecraftIcon() throws Exception;

	/**
	 * @return true, if the current thread is the main client thread.
	 */
	boolean isMainThread();

	/**
	 * @return the main Minecraft directory.
	 */
	File getMinecraftDataDirectory();

	/**
	 * Dispatches any new key presses.
	 */
	void dispatchKeypresses();

	/**
	 * Stops the Minecraft client.
	 */
	void shutdown();

	abstract class CapeCallback {

		public BufferedImage parseImage(BufferedImage image) {
			return image;
		}

		public abstract void callback(Object capeLocation);

	}

	class MouseOverObject {

		private ObjectType type;
		private Object object;
		private double distance;

		public MouseOverObject(ObjectType type, Object object, double distance) {
			this.type = type;
			this.object = object;
			this.distance = distance;
		}

		/**
		 * @return the type of the object in sight.
		 */
		public ObjectType getType() {
			return type;
		}

		/**
		 * @return an instance of an entity, if {@link #getType()} is {@link ObjectType#ENTITY}.
		 */
		public Object getObject() {
			return object;
		}

		/**
		 * @return the distance to the object or the specified maximum distance, if {@link #getType()} is {@link ObjectType#MISS}.
		 */
		public double getDistance() {
			return distance;
		}

	}

	enum ObjectType {

		/**
		 * No object in sight.
		 */
		MISS, /**
		 * A block is in sight.
		 */
		BLOCK, /**
		 * An entity is in sight.
		 */
		ENTITY
	}

}
