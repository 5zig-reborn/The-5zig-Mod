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

package eu.the5zig.mod.manager;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.ChatBackgroundManager;
import eu.the5zig.mod.chat.NetworkStats;
import eu.the5zig.mod.chat.entity.Friend;
import eu.the5zig.mod.chat.entity.Profile;
import eu.the5zig.mod.chat.entity.Rank;
import eu.the5zig.mod.chat.network.filetransfer.FileTransferManager;
import eu.the5zig.mod.chat.network.packets.PacketFriendStatus;
import eu.the5zig.mod.config.items.SelectColorItem;
import eu.the5zig.mod.event.ServerQuitEvent;
import eu.the5zig.mod.listener.ChatSearchManager;
import eu.the5zig.mod.listener.CrossHairDistanceListener;
import eu.the5zig.mod.listener.TeamSpeakListener;
import eu.the5zig.mod.listener.TeamSpeakReconnectListener;
import eu.the5zig.mod.manager.einslive.EinsLiveManager;
import eu.the5zig.mod.manager.iloveradio.ILoveRadioManager;
import eu.the5zig.mod.manager.itunes.ITunesManager;
import eu.the5zig.mod.manager.keyboard.KeyboardManager;
import eu.the5zig.mod.manager.spotify.SpotifyManager;
import eu.the5zig.mod.render.BracketsFormatting;
import eu.the5zig.mod.render.SnowRenderer;
import eu.the5zig.mod.server.GameServer;
import eu.the5zig.mod.server.Server;
import eu.the5zig.mod.server.ServerInstance;
import eu.the5zig.mod.server.ServerInstanceRegistry;
import eu.the5zig.mod.server.bergwerk.ServerInstanceBergwerk;
import eu.the5zig.mod.server.cytooxien.ServerInstanceCytooxien;
import eu.the5zig.mod.server.gomme.ServerInstanceGommeHD;
import eu.the5zig.mod.server.hypixel.ServerInstanceHypixel;
import eu.the5zig.mod.server.mineplex.ServerInstanceMineplex;
import eu.the5zig.mod.server.octc.ServerInstanceOCC;
import eu.the5zig.mod.server.playminity.ServerInstancePlayMinity;
import eu.the5zig.mod.server.simplehg.ServerInstanceSimpleHG;
import eu.the5zig.mod.server.timolia.ServerInstanceTimolia;
import eu.the5zig.mod.server.venicraft.ServerInstanceVenicraft;
import eu.the5zig.mod.util.TabList;
import eu.the5zig.teamspeak.TeamSpeak;
import eu.the5zig.teamspeak.event.TeamSpeakEventDispatcher;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataManager {

	public TabList tabList;

	private Server server;
	private Profile profile;
	private final String session = The5zigMod.getVars().getSession();
	private final String username = The5zigMod.getVars().getUsername();
	private final UUID uuid;
	private final GameProfile gameProfile;

	private CoordinateClipboard coordinatesClipboard = new CoordinateClipboard(null);
	private DeathLocation deathLocation = null;

	// Network
	private final NetworkStats networkStats = new NetworkStats();
	private final ChatTypingManager chatTypingManager = new ChatTypingManager();
	private final AFKManager afkManager = new AFKManager();
	private final ChatBackgroundManager chatBackgroundManager = new ChatBackgroundManager();

	private final FPSCalculator fpsCalculator = new FPSCalculator();
	private final CPSManager cpsManager = new CPSManager();
	private final SpeedCalculator speedCalculator = new SpeedCalculator();

	private final FileTransferManager fileTransferManager = new FileTransferManager();

	private final AutoReconnectManager autoReconnectManager = new AutoReconnectManager();
	private final SearchManager searchManager = new SearchManager();
	private final CrossHairDistanceListener crossHairDistanceListener = new CrossHairDistanceListener();
	private final WeatherManager weatherManager = new WeatherManager();
	private final SnowRenderer snowRenderer = new SnowRenderer();

	private final ServerInstanceRegistry serverInstanceRegistry = new ServerInstanceRegistry();

	private final SpotifyManager spotifyManager = new SpotifyManager();
	private final ILoveRadioManager iLoveRadioManager = new ILoveRadioManager();
	private final EinsLiveManager einsLiveManager = new EinsLiveManager();
	private final ITunesManager iTunesManager = new ITunesManager();
	private final KeyboardManager keyboardManager = new KeyboardManager();
	private final TimerManager timerManager = new TimerManager();
	private final ChatSearchManager chatSearchManager = new ChatSearchManager();

	private final ServerSettings serverSettings = new ServerSettings();

	private boolean tsRequiresAuth = false;
	private final TeamSpeakReconnectListener teamSpeakReconnectListener;

	public DataManager() {
		gameProfile = The5zigMod.getVars().getGameProfile();
		UUID id = gameProfile.getId();
		uuid = id == null ? UUID.randomUUID() : id;
		profile = new Profile(0, Lists.newArrayList(Rank.USER), System.currentTimeMillis(), "Hey there, I'm using The 5zig Mod!", Friend.OnlineStatus.ONLINE, true, true, true, false, true, ChatColor.RESET);

		registerServerInstance(new ServerInstanceTimolia());
		registerServerInstance(new ServerInstanceGommeHD());
		registerServerInstance(new ServerInstancePlayMinity());
		registerServerInstance(new ServerInstanceBergwerk());
		registerServerInstance(new ServerInstanceMineplex());
		registerServerInstance(new ServerInstanceHypixel());
		registerServerInstance(new ServerInstanceVenicraft());
		registerServerInstance(new ServerInstanceCytooxien());
		registerServerInstance(new ServerInstanceSimpleHG());
		registerServerInstance(new ServerInstanceOCC());

		The5zigMod.getListener().registerListener(teamSpeakReconnectListener = new TeamSpeakReconnectListener());
		TeamSpeakListener teamSpeakListener = new TeamSpeakListener();
		TeamSpeakEventDispatcher.registerListener(teamSpeakListener);
		TeamSpeak.getClient().addDisconnectListener(teamSpeakListener);
		TeamSpeak.setDebugMode(The5zigMod.DEBUG);
		TeamSpeak.getClient().setAutoReconnect(false);
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public String getColoredName() {
		return profile.getRank().get(0).getColorCode() + username;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public String getUniqueIdWithoutDashes() {
		return Utils.getUUIDWithoutDashes(uuid);
	}

	public GameProfile getGameProfile() {
		return gameProfile;
	}

	public void resetServer() {
		The5zigMod.getListener().fireEvent(new ServerQuitEvent());
		server = null;
		tabList = null;
		deathLocation = null;
		The5zigMod.getServerAPIBackend().reset();
		The5zigMod.getVars().resetServer();
		The5zigMod.getVars().get2ndChat().clear();
		if (The5zigMod.getNetworkManager().isConnected()) {
			The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.SERVER, ""));
		}
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public CoordinateClipboard getCoordinatesClipboard() {
		return coordinatesClipboard;
	}

	public void setCoordinatesClipboard(CoordinateClipboard coordinatesClipboard) {
		this.coordinatesClipboard = coordinatesClipboard;
	}

	public DeathLocation getDeathLocation() {
		return deathLocation;
	}

	public void setDeathLocation(DeathLocation deathLocation) {
		this.deathLocation = deathLocation;
	}

	public NetworkStats getNetworkStats() {
		return networkStats;
	}

	public ChatTypingManager getChatTypingManager() {
		return chatTypingManager;
	}

	public AFKManager getAfkManager() {
		return afkManager;
	}

	public ChatBackgroundManager getChatBackgroundManager() {
		return chatBackgroundManager;
	}

	public FPSCalculator getFpsCalculator() {
		return fpsCalculator;
	}

	public CPSManager getCpsManager() {
		return cpsManager;
	}

	public SpeedCalculator getSpeedCalculator() {
		return speedCalculator;
	}

	public FileTransferManager getFileTransferManager() {
		return fileTransferManager;
	}

	public AutoReconnectManager getAutoReconnectManager() {
		return autoReconnectManager;
	}

	public SearchManager getSearchManager() {
		return searchManager;
	}

	public CrossHairDistanceListener getCrossHairDistanceListener() {
		return crossHairDistanceListener;
	}

	public WeatherManager getWeatherManager() {
		return weatherManager;
	}

	public SnowRenderer getSnowRenderer() {
		return snowRenderer;
	}

	public void registerServerInstance(ServerInstance serverInstance) {
		serverInstanceRegistry.registerServerInstance(serverInstance);
	}

	public ServerInstanceRegistry getServerInstanceRegistry() {
		return serverInstanceRegistry;
	}

	public void updateCurrentLobby() {
		Server server = The5zigMod.getDataManager().getServer();
		if (The5zigMod.getDataManager().getServer() != null) {
			The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.SERVER,
					The5zigMod.getDataManager().getServer().getHost() + ":" + The5zigMod.getDataManager().getServer().getPort()));
			if (server instanceof GameServer) {
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.LOBBY, ((GameServer) server).getLobbyString()));
			} else if (The5zigMod.getServerAPIBackend().getLobby() != null) {
				The5zigMod.getNetworkManager().sendPacket(new PacketFriendStatus(PacketFriendStatus.FriendStatus.LOBBY, The5zigMod.getServerAPIBackend().getLobby()));
			}
		}
	}

	public SpotifyManager getSpotifyManager() {
		return spotifyManager;
	}

	public ILoveRadioManager getiLoveRadioManager() {
		return iLoveRadioManager;
	}

	public EinsLiveManager getEinsLiveManager() {
		return einsLiveManager;
	}

	public ITunesManager getITunesManager() {
		return iTunesManager;
	}

	public KeyboardManager getKeyboardManager() {
		return keyboardManager;
	}

	public TimerManager getTimerManager() {
		return timerManager;
	}

	public ChatSearchManager getChatSearchManager() {
		return chatSearchManager;
	}

	public Object getChatComponentWithTime(Object originalChatComponent) {
		ChatColor mainColor = The5zigMod.getConfig().get("chatTimePrefixColor", SelectColorItem.class).get();
		ChatColor bracketsColor = The5zigMod.getConfig().get("chatTimePrefixBracketsColor", SelectColorItem.class).get();
		BracketsFormatting bracketsFormatting = The5zigMod.getConfig().getEnum("chatTimePrefixBracketsFormatting", BracketsFormatting.class);
		String timeFormat = The5zigMod.getConfig().getString("chatTimePrefixTimeFormat");
		try {
			String prefix = bracketsColor + bracketsFormatting.getFirst() + mainColor + new SimpleDateFormat(timeFormat).format(new Date()) +
					bracketsColor + bracketsFormatting.getLast() + " ";
			return The5zigMod.getVars().getChatComponentWithPrefix(prefix, originalChatComponent);
		} catch (Exception e) {
			The5zigMod.logger.warn("Failed to parse time format " + timeFormat, e);
			return originalChatComponent;
		}
	}

	public boolean isTsRequiresAuth() {
		return tsRequiresAuth;
	}

	public void setTsRequiresAuth(boolean tsRequiresAuth) {
		this.tsRequiresAuth = tsRequiresAuth;
	}

	public TeamSpeakReconnectListener getTeamSpeakReconnectListener() {
		return teamSpeakReconnectListener;
	}

	public ServerSettings getServerSettings() {
		return serverSettings;
	}
}