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

package eu.the5zig.mod.listener;

import com.google.common.collect.Maps;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.api.ServerAPIListener;
import eu.the5zig.mod.api.SettingListener;
import eu.the5zig.mod.chat.ChatTypingListener;
import eu.the5zig.mod.chat.NetworkTickListener;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.discord.The5zigRichPresence;
import eu.the5zig.mod.event.*;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.manager.ChatFilterManager;
import eu.the5zig.mod.manager.TextMacroManager;
import eu.the5zig.mod.manager.TextReplacementManager;
import eu.the5zig.mod.server.Server;
import eu.the5zig.mod.util.TabList;
import eu.the5zig.util.minecraft.ChatColor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;
import java.util.*;

public class EventListener {

	private final Map<Class<? extends Event>, List<RegisteredEventHandler>> registeredEvents = Maps.newHashMap();

	private String previousTitle;
	private String previousSubTitle;

	private EasterListener easterListener = new EasterListener();

	public EventListener() {
		registerListener(new ServerAPIListener());

		registerListener(new KeybindingListener());
		registerListener(new NetworkTickListener());
		registerListener(new DisplayFocusListener());
		registerListener(new ChatTypingListener());
		registerListener(new ChatFilterManager());
		registerListener(new TextReplacementManager());
		registerListener(new TextMacroManager());
		registerListener(new SettingListener());
		registerListener(new ZoomListener());
		registerListener(new ChatUsernameListener());
		registerListener(new JoinTextListener());
		registerListener(easterListener);
	}

	public void registerListener(Object listener) {
		try {
			Class<?> clazz = listener.getClass();
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(EventHandler.class) && method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
					Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
					EventHandler eventHandler = method.getAnnotation(EventHandler.class);
					if (!registeredEvents.containsKey(eventClass)) {
						registeredEvents.put(eventClass, new ArrayList<RegisteredEventHandler>());
					}
					List<RegisteredEventHandler> eventHandlers = registeredEvents.get(eventClass);
					eventHandlers.add(new RegisteredEventHandler(listener, method, eventHandler));
					Collections.sort(eventHandlers);
				}
			}
		} catch (Throwable throwable) {
			throw new RuntimeException("Could not register listener!", throwable);
		}
	}

	public void unregisterListener(Object listener) {
		try {
			Class<?> clazz = listener.getClass();
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(EventHandler.class) && method.getParameterTypes().length == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
					Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
					List<RegisteredEventHandler> eventHandlers = registeredEvents.get(eventClass);
					for (Iterator<RegisteredEventHandler> iterator = eventHandlers.iterator(); iterator.hasNext(); ) {
						RegisteredEventHandler registeredEventHandler = iterator.next();
						if (registeredEventHandler.getInstance() == listener && registeredEventHandler.getMethod().equals(method)) {
							iterator.remove();
						}
					}
					Collections.sort(eventHandlers);
				}
			}
		} catch (Throwable throwable) {
			throw new RuntimeException("Could not unregister listener!", throwable);
		}
	}

	public <T extends Event> T fireEvent(T event) {
		try {
			List<RegisteredEventHandler> eventHandlers = registeredEvents.get(event.getClass());
			if (eventHandlers == null) {
				return event;
			}
			for (RegisteredEventHandler eventHandler : eventHandlers) {
				if (event instanceof Cancelable && ((Cancelable) event).isCancelled() && eventHandler.getEventHandler().ignoreCancelled()) {
					continue;
				}
				try {
					eventHandler.getMethod().invoke(eventHandler.getInstance(), event);
				} catch (Throwable throwable) {
					The5zigMod.logger.warn("Could not fire event for handler " + eventHandler.getInstance().getClass().getSimpleName(), throwable);
				}
			}
		} catch (Throwable throwable) {
			The5zigMod.logger.warn("Could not fire event " + event.getClass().getSimpleName() + "!", throwable);
		}
		return event;
	}

	public void onServerConnect(String host, int port) {
		if (The5zigMod.getNetworkManager().isConnected()) {
			The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.SERVER, host + ":" + port));
		}

		fireEvent(new ServerJoinEvent(host, port));
		fireEvent(new ServerConnectEvent());
		// Server erst danach setzen, damit in der Config nachgeschaut werden kann.
		if (The5zigMod.getDataManager().getServer() == null)
			The5zigMod.getDataManager().setServer(new Server(host, port));

		if (The5zigMod.getDataManager().tabList != null) {
			onPlayerListHeaderFooter(The5zigMod.getDataManager().tabList);
		}

		The5zigRichPresence presence = new The5zigRichPresence();
		presence.setText("Playing on");
		presence.setState(host + (port == 25565 ? "" : ":" + port));
		The5zigMod.getDiscordRPCManager().setPresence(presence);
	}

	public void onRenderOverlay() {
		The5zigMod.getVars().renderOverlay();
		easterListener.getEasterRenderer().render();
	}

	private boolean hadNetworkManager = false;

	public void onTick() {
		fireEvent(TickEvent.INSTANCE);
		if (!The5zigMod.getVars().isPlayerNull() && !The5zigMod.getVars().isTerrainLoading()) {
			fireEvent(WorldTickEvent.INSTANCE);
		}

		if (hadNetworkManager && !The5zigMod.getVars().hasNetworkManager()) {
			The5zigMod.getVars().getResourceManager().cleanupTextures();
			The5zigMod.getDataManager().setDeathLocation(null);
		}
		hadNetworkManager = The5zigMod.getVars().hasNetworkManager();
		if (The5zigMod.getDataManager().getServer() != null && (The5zigMod.getVars().getServer() == null || The5zigMod.getVars().isPlayerNull())) {
			The5zigMod.getDataManager().resetServer();
		}
	}

	public void onSendChatMessage(String message) {
		ChatSendEvent event = fireEvent(new ChatSendEvent(message));
		if (!event.isCancelled()) {
			The5zigMod.getVars().sendMessage(event.getMessage());
		}
	}

	/**
	 * Handles all received chat messages.
	 *
	 * @param message       The message that has been received.
	 * @param chatComponent The original Chat component.
	 * @return True, if the message should be ignored.
	 */
	public boolean onServerChat(String message, Object chatComponent) {
		if (message == null)
			return false;

		ChatEvent event = fireEvent(new ChatEvent(message, chatComponent));
		if (event.getAlteredMessage() != null && !event.isCancelled()) {
			The5zigMod.getVars().messagePlayer(event.getAlteredMessage());
			return true;
		}
		return event.isCancelled();
	}

	public boolean onActionBar(String message) {
		if (message == null)
			return false;

		ActionBarEvent event = fireEvent(new ActionBarEvent(message));
		return event.isCancelled();
	}

	public void handlePluginMessage(String channel, ByteBuf packetData) {
		if ("MC|Brand".equals(channel) || "minecraft:brand".equals(channel)) {
			if (The5zigMod.getDataManager().getServer() == null) {
				if (The5zigMod.getVars().getServer() != null) {
					String ip = The5zigMod.getVars().getServer();
					String host = ip;
					int port = 25565;
					if (host.contains(":")) {
						String[] split = ip.split(":");
						host = split[0];
						try {
							port = split.length == 1 ? 25565 : Integer.parseInt(split[1]);
						} catch (NumberFormatException ignored) {
							port = 25565;
						}
					}
					onServerConnect(host, port);
				}
			} else {
				fireEvent(new ServerConnectEvent());
			}
		}
		fireEvent(new PayloadEvent(channel, packetData));
	}

	public void onPlayerListHeaderFooter(TabList tabList) {
		The5zigMod.getDataManager().tabList = tabList;

		String headerString = tabList.getHeader().replace(ChatColor.RESET.toString(), "");
		String footerString = tabList.getFooter().replace(ChatColor.RESET.toString(), "");
		fireEvent(new PlayerListEvent(headerString, footerString));
	}

	public void onTitle(String title, String subTitle) {
		if (title == null && subTitle == null) {
			previousTitle = null;
			previousSubTitle = null;
			return;
		}
		if (title != null) {
			previousTitle = title;
		}
		if (subTitle != null) {
			previousSubTitle = subTitle;
		}
		if (previousTitle != null) {
			fireEvent(new TitleEvent(previousTitle, previousSubTitle));
		}
	}

	public void onTeleport(double x, double y, double z, float yaw, float pitch) {
		fireEvent(new PlayerTeleportEvent(x, y, z, yaw, pitch));
	}

	public void onInventorySetSlot(int slot, ItemStack itemStack) {
		String containerTitle = The5zigMod.getVars().getOpenContainerTitle();
		if (containerTitle != null) {
			fireEvent(new ChestSetSlotEvent(containerTitle, slot, itemStack));
		}
	}

	public String[] onSignEdited(String[] lines) {
		SignEditEvent event = fireEvent(new SignEditEvent(lines));
		return event.getLines();
	}

}