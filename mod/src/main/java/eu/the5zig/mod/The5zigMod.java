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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import eu.the5zig.mod.api.ServerAPIBackend;
import eu.the5zig.mod.api.rewards.RewardsCache;
import eu.the5zig.mod.asm.Transformer;
import eu.the5zig.mod.chat.ConversationManager;
import eu.the5zig.mod.chat.FriendManager;
import eu.the5zig.mod.chat.GroupChatManager;
import eu.the5zig.mod.chat.network.NetworkManager;
import eu.the5zig.mod.chat.party.PartyManager;
import eu.the5zig.mod.config.*;
import eu.the5zig.mod.config.items.IntItem;
import eu.the5zig.mod.crashreport.CrashHopper;
import eu.the5zig.mod.discord.DiscordRPCManager;
import eu.the5zig.mod.gui.IOverlay;
import eu.the5zig.mod.listener.EventListener;
import eu.the5zig.mod.manager.*;
import eu.the5zig.mod.manager.itunes.ITunesWindowsDelegate;
import eu.the5zig.mod.modules.ModuleItemRegistry;
import eu.the5zig.mod.modules.ModuleMaster;
import eu.the5zig.mod.render.DisplayRenderer;
import eu.the5zig.mod.render.GuiIngame;
import eu.the5zig.mod.server.hypixel.api.HypixelAPIManager;
import eu.the5zig.mod.util.*;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.util.Utils;
import eu.the5zig.util.AsyncExecutor;
import eu.the5zig.util.db.Database;
import eu.the5zig.util.db.DummyDatabase;
import eu.the5zig.util.db.FileDatabaseConfiguration;
import eu.the5zig.util.db.exceptions.NoConnectionException;
import eu.the5zig.util.io.FileUtils;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Random;

/**
 * Main mod class.
 */
public class The5zigMod {

	public static final Logger logger = LogManager.getLogger("5zig");
	public static final Marker networkMarker = MarkerManager.getMarker("Net");
	public static final Random random = new Random();
	public static final Gson gson = new Gson();
	public static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

	// Textures
	public static final IResourceLocation ITEMS = MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/items.png");
	public static final IResourceLocation INVENTORY_BACKGROUND = MinecraftFactory.getVars().createResourceLocation("textures/gui/container/inventory.png");
	public static final IResourceLocation STEVE = MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/skin.png");
	public static final IResourceLocation MINECRAFT_ICONS = MinecraftFactory.getVars().createResourceLocation("textures/gui/icons.png");
	public static final IResourceLocation MINECRAFT_UNKNOWN_SERVER = MinecraftFactory.getVars().createResourceLocation("textures/misc/unknown_server.png");
	public static final IResourceLocation DEMO_BACKGROUND = MinecraftFactory.getVars().createResourceLocation("textures/gui/demo_background.png");
	public static final IResourceLocation TRACK_LOCATION = MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/track.png");
	public static final IResourceLocation EINSLIVE_LOCATION = MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/1live.png");
	public static final IResourceLocation TEAMSPEAK_ICONS = MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/teamspeak.png");

	/**
	 * Allows tasks to be executed asynchronously.
	 */
	private static final AsyncExecutor asyncExecutor = new AsyncExecutor();
	public static boolean DEBUG = false;

	// Configurations

	private static File modDirectory;
	/**
	 * Main config.
	 */
	private static ConfigNew config;
	/**
	 * Config for all chat filters.
	 */
	private static ChatFilterConfiguration chatFilterConfig;
	/**
	 * Config that stores recently joined servers.
	 */
	private static LastServerConfiguration lastServerConfig;
	/**
	 * Config that stores all text replacements.
	 */
	private static TextReplacementConfiguration textReplacementConfig;
	/**
	 * Config that stores all text macros.
	 */
	private static TextMacroConfiguration textMacroConfiguration;
	/**
	 * Config that stores all join auto texts.
	 */
	private static JoinTextConfiguration joinTextConfiguration;

	/**
	 * Registry of all module items.
	 */
	private static ModuleItemRegistry moduleItemRegistry;
	/**
	 * Module registry and config class.
	 */
	private static ModuleMaster moduleMaster;

	private static IVariables variables = MinecraftFactory.getVars();
	private static DataManager datamanager;
	private static EventListener listener;
	private static Scheduler scheduler;
	private static DisplayRenderer renderer;
	private static GuiIngame guiIngame;
	private static ServerAPIBackend serverAPIBackend;
	private static TrayManager trayManager;

	private static NetworkManager networkManager;
	private static Database conversationDatabase;
	private static ConversationManager conversationManager;
	private static GroupChatManager groupChatManager;
	private static FriendManager friendManager;
	private static PartyManager partyManager;
	private static SkinManager skinManager;

	private static KeybindingManager keybindingManager;

	private static ModAPIImpl api;
	private static HypixelAPIManager hypixelAPIManager;
	private static MojangAPIManager mojangAPIManager;

	private static DiscordRPCManager discordRPCManager;

	private static boolean initialized = false;

	private The5zigMod() {
	}

	/**
	 * Initializes the 5zig Mod and sets up the workspace.
	 *
	 * @throws IllegalStateException if the method has been called more than once.
	 */
	public static void init() {
		if (initialized) {
			throw new IllegalStateException("The 5zig Mod has been already initialized!");
		}
		initialized = true;

		long start = System.currentTimeMillis();

		logger.info("Initializing the 5zig Mod!");
		if (Transformer.FORGE) {
			logger.info("Forge detected!");
		}

		modDirectory = new File(The5zigMod.getVars().getMinecraftDataDirectory(), "the5zigmod");
		// Workaround to allow Minecraft specific classes the interaction with mod core classes.
		MinecraftFactory.setClassProxyCallback(new ClassProxyCallbackImpl());

		// Initialize Crash Hopper
		CrashHopper.init();

		// Directories and config
		try {
			setupDirs();
		} catch (IOException e) {
			logger.fatal("Could not create Mod directories! Exiting!", e);
			CrashReportUtil.makeCrashReport(e, "Creating Directory.");
		}

		try {
			loadConfig();
		} catch (IOException e) {
			logger.fatal("Could not load Main Configuration!");
			CrashReportUtil.makeCrashReport(e, "Loading Main Configuration.");
		}
		DEBUG = config.getBool("debug");
		setupLogger();
		variables.updateOverlayCount(getConfig().getInt("maxOverlays"));
		chatFilterConfig = new ChatFilterConfiguration(modDirectory);
		lastServerConfig = new LastServerConfiguration(modDirectory);
		textReplacementConfig = new TextReplacementConfiguration(modDirectory);
		textMacroConfiguration = new TextMacroConfiguration(modDirectory);
		joinTextConfiguration = new JoinTextConfiguration(modDirectory);

		// Listener and manager classes.
		listener = new EventListener();
		datamanager = new DataManager();
		keybindingManager = new KeybindingManager();
		scheduler = new Scheduler();
		listener.registerListener(scheduler);
		renderer = new DisplayRenderer();
		serverAPIBackend = new ServerAPIBackend();
		moduleItemRegistry = new ModuleItemRegistry();
		moduleMaster = new ModuleMaster(modDirectory);
		guiIngame = new GuiIngame();

		// Networking stuff
		conversationManager = new ConversationManager();
		groupChatManager = new GroupChatManager();
		friendManager = new FriendManager();
		partyManager = new PartyManager();
		trayManager = new TrayManager();

		newNetworkManager();
		RewardsCache.downloadPermanentRewards();

		skinManager = new SkinManager();

		// API classes
		hypixelAPIManager = new HypixelAPIManager();
		mojangAPIManager = new MojangAPIManager();
		api = new ModAPIImpl();
		api.getPluginManager().loadAll();
		moduleMaster.loadInitial();

		Updater.check();

		discordRPCManager = new DiscordRPCManager();
		try {
			discordRPCManager.init();
		} catch (NoDiscordClientException e) {
			logger.warn("No Discord Client found: disabling Rich Presence.");
		}

		logger.info("Loaded The 5zig Mod v" + Version.VERSION + "! (took {} ms)", System.currentTimeMillis() - start);
	}

	/**
	 * Called from Minecraft internally. Disconnects from the 5zig mod server, destroys the
	 * Tray Icon, finishes the AsyncExecutor and closes the sql connection.
	 */
	public static void shutdown() {
		logger.info("Stopping The 5zig Mod!");
		try {
			if (api != null) {
				api.getPluginManager().unloadAll();
			}
			if (networkManager != null) {
				networkManager.disconnect();
			}
			if (trayManager != null) {
				trayManager.destroy();
			}
			asyncExecutor.finish();
			if (conversationDatabase != null) {
				conversationDatabase.closeConnection();
			}
			if (datamanager != null) {
				if (datamanager.getFileTransferManager() != null) {
					// Cleanup .part files
					datamanager.getFileTransferManager().cleanUp(new File(modDirectory, "media/" + datamanager.getUniqueId().toString()));
				}
				if (datamanager.getKeyboardManager() != null) {
					datamanager.getKeyboardManager().unInit();
				}
				if (datamanager.getITunesManager() != null && datamanager.getITunesManager().getDelegate() instanceof ITunesWindowsDelegate) {
					((ITunesWindowsDelegate) datamanager.getITunesManager().getDelegate()).release();
				}
				TeamSpeak.getClient().disconnect();
			}
		} catch (Throwable throwable) {
			logger.warn("Could not shutdown The 5zig Mod properly!", throwable);
		}
	}

	/**
	 * @return true, if the mod already has been initialized.
	 */
	public static boolean hasBeenInitialized() {
		return initialized;
	}

	/**
	 * @return the main directory of the mod.
	 */
	public static File getModDirectory() {
		return modDirectory;
	}

	/**
	 * @return a class that allows tasks to be executed in one separate Thread.
	 */
	public static AsyncExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	/**
	 * @return an utility class that contains Minecraft specific methods.
	 */
	public static IVariables getVars() {
		return variables;
	}

	/**
	 * Creates some directories if they do not exist.
	 *
	 * @throws IOException if there was a problem creating new directories.
	 */
	private static void setupDirs() throws IOException {
		FileUtils.createDir(new File(modDirectory, "sql/chatlogs"));
		FileUtils.createDir(new File(modDirectory, "lang"));
		FileUtils.createDir(new File(modDirectory, "skins"));
		FileUtils.createDir(new File(modDirectory, "servers/hypixel"));
	}

	/**
	 * Tries to load the Main Configuration File.
	 *
	 * @throws IOException if there was a problem loading the Configuration.
	 */
	private static void loadConfig() throws IOException {
		File configFile = new File(modDirectory, "config.json");
		if (!configFile.exists() && !configFile.createNewFile()) {
			throw new IOException("Could not create Configuration!");
		}

		config = new ConfigNew(configFile);
		IntItem version = config.get("version", IntItem.class);
		if (!version.isDefault()) {
			config.reset();
		}
		The5zigMod.logger.info("Loaded Configurations!");
	}

	/**
	 * Configures the main logger.
	 */
	private static void setupLogger() {
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		org.apache.logging.log4j.core.config.Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
		if (DEBUG) {
			loggerConfig.setLevel(Level.DEBUG);
		}
		ctx.updateLoggers();
		logger.debug("Debug Mode ENABLED!");
	}

	/**
	 * @return the main configuration.
	 */
	public static ConfigNew getConfig() {
		return config;
	}

	/**
	 * @return the chat filter configuration.
	 */
	public static ChatFilterConfiguration getChatFilterConfig() {
		return chatFilterConfig;
	}

	/**
	 * @return a configuration that contains all previously joined servers.
	 */
	public static LastServerConfiguration getLastServerConfig() {
		return lastServerConfig;
	}

	/**
	 * @return a configuration that contains all text replacements.
	 */
	public static TextReplacementConfiguration getTextReplacementConfig() {
		return textReplacementConfig;
	}

	/**
	 * @return a configuration that contains all text macros.
	 */
	public static TextMacroConfiguration getTextMacroConfiguration() {
		return textMacroConfiguration;
	}

	/**
	 * @return a configuration that contains all join auto texts.
	 */
	public static JoinTextConfiguration getJoinTextConfiguration() {
		return joinTextConfiguration;
	}

	/**
	 * @return the main data-manager class.
	 */
	public static DataManager getDataManager() {
		return datamanager;
	}

	/**
	 * @return the keybinding manager.
	 */
	public static KeybindingManager getKeybindingManager() {
		return keybindingManager;
	}

	/**
	 * @return the main event manager class.
	 */
	public static EventListener getListener() {
		return listener;
	}

	/**
	 * @return a scheduler class that is used to post tasks to the main client Thread.
	 */
	public static Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * @return the main renderer class.
	 */
	public static DisplayRenderer getRenderer() {
		return renderer;
	}

	public static ServerAPIBackend getServerAPIBackend() {
		return serverAPIBackend;
	}

	public static ModuleItemRegistry getModuleItemRegistry() {
		return moduleItemRegistry;
	}

	public static ModuleMaster getModuleMaster() {
		return moduleMaster;
	}

	public static GuiIngame getGuiIngame() {
		return guiIngame;
	}

	public static IOverlay getOverlayMessage() {
		return getVars().newOverlay();
	}

	/**
	 * Tries to create and connect to a local H2-database.
	 */
	public static void newConversationDatabase() {
		Database database = null;
		File file = new File(modDirectory, "sql/chatlogs/" + getDataManager().getUniqueId().toString());
		File dbFile = new File(file.getAbsolutePath() + ".mv.db");
		File backupFile = new File(file.getAbsolutePath() + "_backup.mv.db");
		FileDatabaseConfiguration configuration = new FileDatabaseConfiguration(file, "DATABASE_TO_UPPER=FALSE");
		try {
			// check lock
			if (dbFile.exists() && isFileLocked(configuration.getFile().getAbsolutePath() + ".mv.db")) {
				logger.info("Found locked database! Using dummy database!");
				database = new DummyDatabase();
			} else {
				database = new Database(configuration); // "AUTO_SERVER=TRUE"
				try {
					database.closeConnection();
					org.apache.commons.io.FileUtils.copyFile(dbFile, backupFile);
					logger.debug("Created db backup!");
				} catch (Exception e) {
					The5zigMod.logger.warn("Could not create backup of conversations!", e);
				}
			}
		} catch (Throwable throwable) {
			logger.info("Could not load Conversations!", throwable);
			try {
				logger.info("Trying to load backup!");
				org.apache.commons.io.FileUtils.copyFile(backupFile, dbFile);
				database = new Database(configuration);
			} catch (Throwable e2) {
				logger.info("Could not load backup! Deleting...", e2);
				try {
					if ((!backupFile.exists() && !backupFile.delete()) || (!dbFile.exists() && !dbFile.delete()))
						throw new IOException("Could not delete db file...");

					try {
						database = new DummyDatabase();
					} catch (NoConnectionException ignored) {
					}
				} catch (Exception e1) {
					try {
						database = new DummyDatabase();
					} catch (NoConnectionException ignored) {
					}
				}
			}
		}
		conversationDatabase = database;
		conversationManager.init(database);
	}

	/**
	 * Checks if a given file is currently locked by the file system.
	 *
	 * @param fileName the path of the file that should be checked.
	 * @return true, if the file is currently locked by the file system.
	 */
	private static boolean isFileLocked(String fileName) {
		RandomAccessFile f = null;
		try {
			f = new RandomAccessFile(fileName, "r");
			FileLock lock = f.getChannel().tryLock(0, Long.MAX_VALUE, true);
			if (lock != null) {
				lock.release();
				return false;
			}
		} catch (IOException ignored) {
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException ignored) {
				}
			}
		}
		return true;
	}

	/**
	 * @return a database table that stores all mod chat conversations.
	 */
	public static Database getConversationDatabase() {
		return conversationDatabase;
	}

	/**
	 * @return a class that stores all mod chat conversations and messages.
	 */
	public static ConversationManager getConversationManager() {
		return conversationManager;
	}

	/**
	 * @return a class that stores all mod group chats.
	 */
	public static GroupChatManager getGroupChatManager() {
		return groupChatManager;
	}

	/**
	 * @return a class that stores all mod chat friends.
	 */
	public static FriendManager getFriendManager() {
		return friendManager;
	}

	/**
	 * @return a class that stores all party requests as well as the current party of the player.
	 */
	public static PartyManager getPartyManager() {
		return partyManager;
	}

	/**
	 * @return an utility class that is used to retrieve and store Minecraft skin heads using a third-party service.
	 */
	public static SkinManager getSkinManager() {
		return skinManager;
	}

	/**
	 * @return a class that is responsible of creating a system tray.
	 */
	public static TrayManager getTrayManager() {
		return trayManager;
	}

	/**
	 * Creates a new network manager and connects to the mod chat server.
	 */
	public static void newNetworkManager() {
		networkManager = NetworkManager.connect();
	}

	/**
	 * @return the current network manager.
	 */
	public static NetworkManager getNetworkManager() {
		return networkManager;
	}

	public static ModAPIImpl getAPI() {
		return api;
	}

	public static HypixelAPIManager getHypixelAPIManager() {
		return hypixelAPIManager;
	}

	public static MojangAPIManager getMojangAPIManager() {
		return mojangAPIManager;
	}

	public static DiscordRPCManager getDiscordRPCManager() {
		return discordRPCManager;
	}

	/**
	 * Translates a boolean value to either "On" or "Off".
	 *
	 * @param b the value that should be translated.
	 * @return either "On" or "Off", depending on the given value.
	 */
	public static String toBoolean(boolean b) {
		return b ? I18n.translate("gui.on") : I18n.translate("gui.off");
	}

	public static boolean isCtrlKeyDown(boolean isMacOS) {
		return isMacOS && The5zigMod.getConfig().getBool("macCmdDrop") ? Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA)
				: Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
	}

	public static boolean isCtrlKeyDown() {
		return isCtrlKeyDown(Utils.getPlatform() == Utils.Platform.MAC);
	}

	public static boolean isMinecraftVersionAvailable(String version) {
		if ("unknown".equals(Version.MCVERSION)) {
			return false;
		}
		return eu.the5zig.util.Utils.versionCompare(Version.MCVERSION, version) >= 0;
	}

}