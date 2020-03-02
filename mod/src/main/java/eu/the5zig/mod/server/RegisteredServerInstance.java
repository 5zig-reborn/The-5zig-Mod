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

package eu.the5zig.mod.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.event.*;
import eu.the5zig.mod.util.CommandIgnoreResult;
import eu.the5zig.util.Callback;
import eu.the5zig.util.ExtendedCallback;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegisteredServerInstance implements GameListenerRegistry {

	private final ServerInstance serverInstance;
	private final ClassLoader classLoader;
	private final List<AbstractGameListener<? extends GameMode>> gameListeners = Lists.newArrayList();

	private boolean onServer = false;

	/**
	 * A HashMap containing all message-patterns of the current Server.
	 * The Key is the Pattern key, the Value is the Pattern itself.
	 */
	private final HashMap<String, Pattern> messages = Maps.newHashMap();

	private CommandIgnoreResult ignoreResult = new CommandIgnoreResult();
	private List<MultiLineIgnore> multiLineIgnores = Lists.newArrayList();

	public RegisteredServerInstance(ServerInstance serverInstance, ClassLoader classLoader) {
		The5zigMod.getListener().registerListener(this);
		this.serverInstance = serverInstance;
		this.classLoader = classLoader;
		this.serverInstance.gameListener = this;
		this.serverInstance.registerListeners();
		loadPatterns();
	}

	@Override
	public void registerListener(AbstractGameListener<? extends GameMode> listener) {
		listener.gameListener = this;
		gameListeners.add(listener);
	}

	@Override
	public void switchLobby(String newLobby) {
		final IGameServer server = getServer();
		server.setLobby(newLobby);
		boolean gameModeFound = false;
		if (newLobby != null) {
			for (AbstractGameListener<? extends GameMode> gameListener : gameListeners) {
				if (gameListener.matchLobby(newLobby)) {
					try {
						server.setGameMode(gameListener.getGameMode().newInstance());
					} catch (Throwable throwable) {
						The5zigMod.logger.error("Could not create new instance of game mode " + gameListener.getGameMode());
					}
					gameModeFound = true;
					break;
				}
			}
		}
		if (!gameModeFound) {
			server.setGameMode(null);
		}
		The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.LOBBY, getServer().getLobbyString()));
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onGameModeJoin(server.getGameMode());
			}
		});
	}

	@Override
	public String getCurrentLobby() {
		return getServer().getLobby();
	}

	@Override
	public String getCurrentNick() {
		return getServer().getNickname();
	}

	@Override
	public void setCurrentNick(String nickname) {
		getServer().setNickname(nickname);
	}

	public IGameServer getServer() {
		return isCurrentServerInstance() ? (IGameServer) The5zigMod.getDataManager().getServer() : null;
	}

	private void loadPatterns() {
		String path = "core/messages/" + serverInstance.getConfigName() + ".properties";
		if (!messages.isEmpty()) {
			The5zigMod.logger.debug("Messages Property Map has been already filled with values. {}", messages);
			return;
		}
		The5zigMod.logger.debug("Loading Patterns from {}...", path);
		Properties properties = new Properties();
		try {
			properties.load(classLoader.getResourceAsStream(path));
		} catch (Exception e) {
			The5zigMod.logger.error("Could not load Messages from path " + path, e);
			return;
		}
		Set<Map.Entry<Object, Object>> enumeration = properties.entrySet();
		for (Map.Entry<Object, Object> entry : enumeration) {
			try {
				String regex = String.valueOf(entry.getValue());
				regex = regex.replace("%p", "\\w{1,16}");
				regex = regex.replace("%d", "-?[0-9]+");
				messages.put(String.valueOf(entry.getKey()), Pattern.compile(regex));
			} catch (PatternSyntaxException e) {
				The5zigMod.logger.error("Could not parse Pattern in " + path + "!", e);
			}
		}
	}

	public List<List<String>> match(String message) {
		List<List<String>> matchedKeys = Lists.newArrayList();
		for (String key : messages.keySet()) {
			List<String> match = match(message, key);
			if (match != null) {
				match.add(0, key);
				matchedKeys.add(match);
			}
		}
		return matchedKeys;
	}

	/**
	 * Tries to match the Pattern, returned from {@link RegisteredServerInstance#messages} by the key parameter, and returns, if found, the difference between the pattern and the message, or null, if the
	 * Pattern didn't match.
	 *
	 * @param message The message, that should be searched for.
	 * @param key     The key of the Pattern.
	 * @return The difference between the found pattern and the message or an empty list, if the Pattern didn't match.
	 */
	public List<String> match(String message, String key) {
		if (!messages.containsKey(key))
			return null;
		Pattern pattern = messages.get(key);
		Matcher matcher = pattern.matcher(message);
		if (matcher.matches()) {
			List<String> matches = Lists.newArrayList();
			for (int i = 1; i <= matcher.groupCount(); i++) {
				matches.add(matcher.group(i));
			}
			The5zigMod.logger.debug("Pattern matched with {}", matches);
			return matches;
		}
		return null;
	}

	public boolean isOnServer() {
		return onServer;
	}

	public boolean isCurrentServerInstance() {
		return onServer && The5zigMod.getDataManager().getServer() instanceof IGameServer;
	}

	@Override
	public GameMode getCurrentGameMode() {
		if (isCurrentServerInstance()) {
			return ((IGameServer) The5zigMod.getDataManager().getServer()).getGameMode();
		} else {
			return null;
		}
	}

	@Override
	public Set<String> getOnlineFriends() {
		IGameServer server = getServer();
		return server == null ? Collections.<String>emptySet() : server.getOnlineFriends();
	}

	@Override
	public Set<String> getPartyMembers() {
		IGameServer server = getServer();
		return server == null ? Collections.<String>emptySet() : server.getPartyMembers();
	}

	public ServerInstance getServerInstance() {
		return serverInstance;
	}

	@EventHandler
	public void onServerJoin(ServerJoinEvent event) {
		if (serverInstance.handleServer(event.getHost(), event.getPort())) {
			The5zigMod.getDataManager().setServer(new GameServer(event.getHost(), event.getPort(), serverInstance.getConfigName()));
			for (RegisteredServerInstance instance : The5zigMod.getDataManager().getServerInstanceRegistry().getRegisteredInstances()) {
				instance.onServer = false;
			}
			onServer = true;
			for (AbstractGameListener listener : gameListeners) {
				try {
					listener.onServerJoin();
				} catch (Throwable throwable) {
					The5zigMod.logger.error("Could not call GameListener " + listener + "!", throwable);
				}
			}
		}
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onServerConnect(getCurrentGameMode());
			}
		});
	}

	@EventHandler
	public void onServerDisconnect(ServerQuitEvent event) {
		if (!isCurrentServerInstance())
			return;
		onServer = false;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onServerDisconnect(getCurrentGameMode());
			}
		});
	}

	@EventHandler
	public void onTick(WorldTickEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onTick(getCurrentGameMode());
			}
		});
	}

	@EventHandler
	public void onKeyPress(final KeyPressEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onKeyPress(getCurrentGameMode(), event.getKeyCode());
			}
		});
	}

	@EventHandler
	public void onPayloadReceive(final PayloadEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onPayloadReceive(getCurrentGameMode(), event.getChannel(), event.getPayload());
			}
		});
	}

	@EventHandler
	public void onServerChat(final ChatEvent event) {
		if (!isCurrentServerInstance())
			return;
		final String message = event.getMessage();
		boolean ignore = executeAllBool(new ExtendedCallback<AbstractGameListener, Boolean>() {
			@Override
			@SuppressWarnings("unchecked")
			public Boolean get(AbstractGameListener key) {
				return key.onServerChat(getCurrentGameMode(), message);
			}
		});

		ignore |= tryMatch(message);
		ignore |= tryMultiIgnores(message);
		ignore |= ignoreResult.handle(message);

		event.setCancelled(event.isCancelled() | ignore);
	}

	@EventHandler
	public void onActionBar(final ActionBarEvent event) {
		if (!isCurrentServerInstance())
			return;
		boolean ignore = executeAllBool(new ExtendedCallback<AbstractGameListener, Boolean>() {
			@Override
			@SuppressWarnings("unchecked")
			public Boolean get(AbstractGameListener key) {
				return key.onActionBar(getCurrentGameMode(), event.getMessage());
			}
		});

		ignore |= tryMatch(event.getMessage());

		event.setCancelled(event.isCancelled() | ignore);
	}

	@EventHandler
	public void onPlayerListHeaderFooter(final PlayerListEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onPlayerListHeaderFooter(getCurrentGameMode(), event.getHeader(), event.getFooter());
			}
		});
	}

	@EventHandler
	public void onTitle(final TitleEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onTitle(getCurrentGameMode(), event.getTitle(), event.getSubTitle());
			}
		});
	}

	@EventHandler
	public void onTeleport(final PlayerTeleportEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onTeleport(getCurrentGameMode(), event.getX(), event.getY(), event.getZ(), event.getYaw(), event.getPitch());
			}
		});
	}

	@EventHandler
	public void onChestSetSlot(final ChestSetSlotEvent event) {
		if (!isCurrentServerInstance())
			return;
		executeAll(new Callback<AbstractGameListener>() {
			@Override
			@SuppressWarnings("unchecked")
			public void call(AbstractGameListener callback) {
				callback.onChestSetSlot(getCurrentGameMode(), event.getContainerTitle(), event.getSlot(), event.getItemStack());
			}
		});
	}

	private boolean tryMatch(final String message) {
		final List<List<String>> matchedKeys = match(ChatColor.stripColor(message));
		if (matchedKeys.isEmpty())
			return false;
		boolean ignore = false;
		for (List<String> match : matchedKeys) {
			final String key = match.get(0);
			match.remove(0);
			final PatternResult patternResult = new PatternResult(match);
			executeAll(new Callback<AbstractGameListener>() {
				@Override
				@SuppressWarnings("unchecked")
				public void call(AbstractGameListener callback) {
					callback.onMatch(getCurrentGameMode(), key, patternResult);
				}
			});
			ignore |= patternResult.shouldMessageBeIgnored();
		}
		return ignore;
	}

	private void executeAll(Callback<AbstractGameListener> callback) {
		GameMode gameMode = getCurrentGameMode();
		for (AbstractGameListener<?> listener : gameListeners) {
			if ((listener.getGameMode() == null) ||
					(gameMode != null && listener.getGameMode() != null && listener.getGameMode().isAssignableFrom(
							gameMode.getClass()))) {
				try {
					callback.call(listener);
				} catch (Throwable throwable) {
					The5zigMod.logger.error("Could not call GameListener " + listener + "!", throwable);
				}
				gameMode = getCurrentGameMode();
			}
		}
	}

	private boolean executeAllBool(ExtendedCallback<AbstractGameListener, Boolean> callback) {
		boolean result = false;
		GameMode gameMode = ((IGameServer) The5zigMod.getDataManager().getServer()).getGameMode();
		for (AbstractGameListener<?> listener : gameListeners) {
			if ((gameMode == null && listener.getGameMode() == null) ||
					(gameMode != null && listener.getGameMode() != null && listener.getGameMode().isAssignableFrom(
							gameMode.getClass()))) {
				try {
					result |= callback.get(listener);
				} catch (Throwable throwable) {
					The5zigMod.logger.error("Could not call GameListener " + listener + "!", throwable);
				}
			}
		}
		return result;
	}

	/**
	 * Executes a command from the Player and ignores the result of it.
	 *
	 * @param message The command that should be executed.
	 * @param key     The message key that should be ignored. The key can be found in the xxx.properties file.
	 */
	@Override
	public void sendAndIgnore(String message, String key) {
		ignoreResult.send(message, messages.get(key));
	}

	@Override
	public void sendAndIgnoreMultiple(String message, String start, String end, Callback<IMultiPatternResult> callback) {
		multiLineIgnores.add(new MultiLineIgnore(this, start, end, callback));
		The5zigMod.getVars().sendMessage(message);
	}

	@Override
	public void sendAndIgnoreMultiple(String message, String startKey, int numberOfMessages, String abortKey, Callback<IMultiPatternResult> callback) {
		Pattern pattern = messages.get(startKey);
		Pattern abort = abortKey == null ? null : messages.get(abortKey);
		if (pattern == null)
			throw new IllegalArgumentException("Could not find pattern " + startKey);
		multiLineIgnores.add(new MultiLineIgnore(this, pattern, numberOfMessages, abort, callback));
		The5zigMod.getVars().sendMessage(message);
	}

	private boolean tryMultiIgnores(String message) {
		boolean ignore = false;

		String strippedMessage = ChatColor.stripColor(message);
		for (Iterator<MultiLineIgnore> iterator = multiLineIgnores.iterator(); iterator.hasNext(); ) {
			MultiLineIgnore multiLineIgnore = iterator.next();
			if (multiLineIgnore.getAbort() != null && multiLineIgnore.getAbort().matcher(strippedMessage).matches()) {
				iterator.remove();
				ignore = true;
			} else {
				if (!multiLineIgnore.hasStartedListening()) {
					if ((multiLineIgnore.getStartPattern() != null && multiLineIgnore.getStartPattern().matcher(strippedMessage).matches()) || strippedMessage.equalsIgnoreCase(multiLineIgnore.getStartMessage())) {
						multiLineIgnore.setStartedListening(true);
						multiLineIgnore.add(message);
						ignore = true;
					}
				} else {
					multiLineIgnore.add(message);
					ignore = true;
					if ((multiLineIgnore.getEndMessage() != null && strippedMessage.equalsIgnoreCase(multiLineIgnore.getEndMessage())) ||
							(multiLineIgnore.getNumberOfMessages() > 0 && multiLineIgnore.getCurrentMessageCount() == multiLineIgnore.getNumberOfMessages())) {
						iterator.remove();
						try {
							multiLineIgnore.callCallback();
						} catch (Throwable throwable) {
							The5zigMod.logger.error("Could not call multi-line-ignore-callback", throwable);
						}
					}
				}
			}
		}

		return ignore;
	}

}
