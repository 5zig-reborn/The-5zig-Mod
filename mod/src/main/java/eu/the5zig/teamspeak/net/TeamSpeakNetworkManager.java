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

package eu.the5zig.teamspeak.net;

import io.netty.handler.codec.*;
import java.util.concurrent.atomic.*;
import com.google.common.base.*;
import com.google.common.collect.*;
import io.netty.bootstrap.*;
import io.netty.channel.socket.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;
import eu.the5zig.teamspeak.*;
import eu.the5zig.teamspeak.event.*;
import eu.the5zig.teamspeak.response.*;
import eu.the5zig.teamspeak.request.*;
import eu.the5zig.teamspeak.impl.*;
import java.util.*;
import eu.the5zig.teamspeak.util.*;
import org.apache.logging.log4j.*;
import com.google.common.util.concurrent.*;
import io.netty.channel.nio.*;

public class TeamSpeakNetworkManager
{
    public static final Marker logMarkerPackets;
    private TeamSpeakClientImpl teamSpeakClient;
    private String authKey;
    private final Object AUTH_LOCK;
    private boolean authFailed;
    private static final EventLoopGroup EVENT_LOOP_GROUP;
    private final TeamSpeakHandler CHILD_HANDLER;
    private final LineBasedFrameDecoder LINE_FRAMER;
    private final TeamSpeakDecoder DECODER;
    private final TeamSpeakEncoder ENCODER;
    private volatile boolean connected;
    private KeepAliveThread keepAliveThread;
    private final EventManager eventManager;
    private final List<TeamSpeakRequest> SENT_REQUESTS;
    private final AtomicInteger CURRENT_TAB;
    
    public TeamSpeakNetworkManager(final TeamSpeakClientImpl teamSpeakClient, final String authKey) {
        this.AUTH_LOCK = new Object();
        this.authFailed = false;
        this.CHILD_HANDLER = new TeamSpeakHandler(this);
        this.LINE_FRAMER = new LineBasedFrameDecoder(1000000);
        this.DECODER = new TeamSpeakDecoder(Charsets.UTF_8);
        this.ENCODER = new TeamSpeakEncoder(Charsets.UTF_8);
        this.connected = false;
        this.eventManager = new EventManager(this);
        this.SENT_REQUESTS = new ArrayList<>();
        this.CURRENT_TAB = new AtomicInteger();
        this.teamSpeakClient = teamSpeakClient;
        this.authKey = authKey;
    }
    
    public void connect(final String host, final int port) throws TeamSpeakException, TeamSpeakConnectException {
        if (this.connected) {
            throw new TeamSpeakException("Already connected to ClientQuery!");
        }
        this.connected = true;
        try {
            final Bootstrap bootstrap = new Bootstrap();
            ((Bootstrap)((Bootstrap)bootstrap.group(TeamSpeakNetworkManager.EVENT_LOOP_GROUP)).channel((Class)NioSocketChannel.class)).handler((ChannelHandler)new ChannelInitializer<SocketChannel>() {
                protected void initChannel(final SocketChannel socketChannel) throws Exception {
                    final ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new ChannelHandler[] { TeamSpeakNetworkManager.this.LINE_FRAMER });
                    pipeline.addLast(new ChannelHandler[] { TeamSpeakNetworkManager.this.DECODER });
                    pipeline.addLast(new ChannelHandler[] { TeamSpeakNetworkManager.this.ENCODER });
                    pipeline.addLast(new ChannelHandler[] { TeamSpeakNetworkManager.this.CHILD_HANDLER });
                }
            });
            bootstrap.connect(host, port).sync();
            (this.keepAliveThread = new KeepAliveThread(this)).start();
            synchronized (this.AUTH_LOCK) {
                this.AUTH_LOCK.wait();
            }
            if (this.authFailed) {
                throw new TeamSpeakAuthException();
            }
        }
        catch (Exception e) {
            throw new TeamSpeakConnectException(e);
        }
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void disconnect(final Throwable cause) {
        if (this.connected) {
            this.connected = false;
            if (this.keepAliveThread != null) {
                this.keepAliveThread.shutdown();
            }
            this.CHILD_HANDLER.closeChannel();
            this.teamSpeakClient.disconnect(cause);
        }
    }
    
    public RequestChain sendRequest(final Request command, final Callback<TeamSpeakCommandResponse> callback) throws TeamSpeakException {
        if (!this.connected) {
            return new RequestChain(this);
        }
        final RequestChain chain = new RequestChain(this);
        final TeamSpeakRequest request = new TeamSpeakRequest(command, callback, chain);
        this.CHILD_HANDLER.send(command, new GenericFutureListener() {
            public void operationComplete(final Future future) throws Exception {
                if (TeamSpeak.isDebugMode()) {
                    LogManager.getLogger().info(TeamSpeakNetworkManager.logMarkerPackets, "OUT|" + request.getRequest());
                }
                synchronized (TeamSpeakNetworkManager.this.SENT_REQUESTS) {
                    TeamSpeakNetworkManager.this.SENT_REQUESTS.add(request);
                }
            }
        });
        if (callback instanceof BlockingCallback) {
            ((BlockingCallback)callback).onStart();
        }
        return chain;
    }
    
    void sendRequest(final TeamSpeakRequest request) throws TeamSpeakException {
        if (!this.connected) {
            throw new TeamSpeakException("Not connected to ClientQuery!");
        }
        this.CHILD_HANDLER.send(request.getRequest(), new GenericFutureListener() {
            public void operationComplete(final Future future) throws Exception {
                if (TeamSpeak.isDebugMode()) {
                    LogManager.getLogger().info(TeamSpeakNetworkManager.logMarkerPackets, "OUT|" + request.getRequest());
                }
                synchronized (TeamSpeakNetworkManager.this.SENT_REQUESTS) {
                    TeamSpeakNetworkManager.this.SENT_REQUESTS.add(request);
                }
            }
        });
    }
    
    void handleResponse(final TeamSpeakResponse response) {
        if (!this.connected) {
            return;
        }
        if (TeamSpeak.isDebugMode()) {
            LogManager.getLogger().info(TeamSpeakNetworkManager.logMarkerPackets, "IN |" + response.getMessage());
        }
        if (response instanceof TeamSpeakServerConnectionResponse) {
            final int serverConnectionHandlerId = ((TeamSpeakServerConnectionResponse)response).getServerConnectionHandlerId();
            TeamSpeakRequest request = null;
            if (this.CURRENT_TAB.get() != 0) {
                synchronized (this.SENT_REQUESTS) {
                    if (!this.SENT_REQUESTS.isEmpty()) {
                        request = this.SENT_REQUESTS.remove(0);
                    }
                }
            }
            this.CURRENT_TAB.set(serverConnectionHandlerId);
            if (((TeamSpeakServerConnectionResponse)response).requiresAuth()) {
                if (this.authKey == null) {
                    synchronized (this.AUTH_LOCK) {
                        this.authFailed = true;
                        this.AUTH_LOCK.notifyAll();
                    }
                }
                else {
                    this.sendRequest(new AuthRequest(this.authKey), new Callback<TeamSpeakCommandResponse>() {
                        @Override
                        public void onDone(final TeamSpeakCommandResponse response) {
                            TeamSpeakNetworkManager.this.sendRequest(new ClientNotifyRegisterRequest(EventType.ANY), new Callback<TeamSpeakCommandResponse>() {
                                @Override
                                public void onDone(final TeamSpeakCommandResponse response) {
                                    synchronized (TeamSpeakNetworkManager.this.AUTH_LOCK) {
                                        TeamSpeakNetworkManager.this.AUTH_LOCK.notifyAll();
                                    }
                                }
                            });
                        }
                        
                        @Override
                        public void exceptionCaught(final TeamSpeakException exception) {
                            synchronized (TeamSpeakNetworkManager.this.AUTH_LOCK) {
                                TeamSpeakNetworkManager.this.authFailed = true;
                                TeamSpeakNetworkManager.this.AUTH_LOCK.notifyAll();
                            }
                        }
                    });
                }
            }
            else {
                this.sendRequest(new ClientNotifyRegisterRequest(EventType.ANY), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        synchronized (TeamSpeakNetworkManager.this.AUTH_LOCK) {
                            TeamSpeakNetworkManager.this.AUTH_LOCK.notifyAll();
                        }
                    }
                });
                if (request != null) {
                    request.getCallback().onDone(null);
                }
            }
        }
        else if (response instanceof TeamSpeakCommandResponse) {
            synchronized (this.SENT_REQUESTS) {
                if (this.SENT_REQUESTS.isEmpty()) {
                    LogManager.getLogger().warn("Got response without having sent a command: " + response.getRawMessage());
                    return;
                }
                final TeamSpeakRequest request = this.SENT_REQUESTS.remove(0);
                final TeamSpeakCommandResponse commandResponse = (TeamSpeakCommandResponse)response;
                if (commandResponse.getErrorId() == 0) {
                    try {
                        request.getCallback().onDone(commandResponse);
                    }
                    catch (Throwable throwable) {
                        request.getCallback().exceptionCaught(new TeamSpeakException("Could not handle command response of " + request.getRequest(), throwable));
                    }
                    request.getChain().sendNextRequest();
                }
                else {
                    final ServerTabImpl selectedTab = this.teamSpeakClient.getSelectedTab();
                    if (selectedTab != null) {
                        selectedTab.getServerChat().addMessage(new MessageImpl(Utils.getChatTimeString() + "An error occurred: " + commandResponse.getErrorMsg() + " (" + commandResponse.getErrorId() + ")", System.currentTimeMillis()));
                    }
                    request.getCallback().exceptionCaught(new TeamSpeakException(commandResponse));
                }
            }
        }
        else if (response instanceof TeamSpeakEventResponse) {
            final TeamSpeakEventResponse eventResponse = (TeamSpeakEventResponse)response;
            final List<PropertyMap> propertyMaps = new ArrayList<PropertyMap>();
            for (final String s : eventResponse.getMessage().split("\\|")) {
                final HashMap<String, String> parse = TeamSpeakResponse.parse(s);
                if (!propertyMaps.isEmpty()) {
                    final Map<String, String> firstProperties = propertyMaps.get(0).getProperties();
                    for (final String key : firstProperties.keySet()) {
                        if (!parse.containsKey(key)) {
                            parse.put(key, firstProperties.get(key));
                        }
                    }
                }
                propertyMaps.add(new PropertyMap(parse));
            }
            if (this.teamSpeakClient.isConnected()) {
                for (final PropertyMap propertyMap : propertyMaps) {
                    try {
                        this.eventManager.createEvent(eventResponse.getType(), propertyMap);
                    }
                    catch (Throwable throwable2) {
                        LogManager.getLogger().error("Could not handle event " + eventResponse.getType() + "!", throwable2);
                    }
                }
            }
        }
        else {
            LogManager.getLogger().error("Got unhandled response: " + response);
        }
    }
    
    public void trySelectTab(final int tabId, final Runnable runnable) {
        if (tabId == 0) {
            throw new IllegalArgumentException("Invalid tab id!");
        }
        if (this.CURRENT_TAB.get() != tabId) {
            this.sendRequest(new UseRequest(tabId), new Callback<TeamSpeakCommandResponse>() {
                @Override
                public void onDone(final TeamSpeakCommandResponse response) {
                    final ServerTabImpl selectedTab = TeamSpeakNetworkManager.this.teamSpeakClient.getSelectedTab();
                    if (selectedTab != null) {
                        selectedTab.setSelected(false);
                    }
                    TeamSpeakNetworkManager.this.teamSpeakClient.getServerTab(tabId).setSelected(true);
                    TeamSpeakNetworkManager.this.CURRENT_TAB.set(tabId);
                    runnable.run();
                }
            });
            this.checkNeedsBlocking(runnable);
        }
        else {
            runnable.run();
        }
    }
    
    public int getSelectedTab() {
        return this.CURRENT_TAB.get();
    }
    
    public void getTabs(final Callback<List<Integer>> callback) {
        this.sendRequest(new ServerConnectionHandlerListRequest(), new Callback<TeamSpeakCommandResponse>() {
            @Override
            public void onDone(final TeamSpeakCommandResponse response) {
                final List<Integer> result = new ArrayList<>();
                for (final String tab : response.getMessage().split("\\|")) {
                    result.add(Integer.valueOf(tab.split("=")[1]));
                }
                callback.onDone(result);
            }
        });
    }
    
    public void getTabInfo(final ServerTabImpl serverTab, final Runnable runnable) {
        this.trySelectTab(serverTab.getId(), new Runnable() {
            @Override
            public void run() {
                TeamSpeakNetworkManager.this.sendRequest(new ServerConnectInfoRequest(), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        final HashMap<String, String> parsedResponse = response.getParsedResponse();
                        serverTab.setServerInfo(new ServerInfoImpl(serverTab, parsedResponse.get("ip"), Integer.parseInt(parsedResponse.get("port"))));
                    }
                    
                    @Override
                    public void exceptionCaught(final TeamSpeakException exception) {
                        TeamSpeakNetworkManager.this.clearTabInfo(serverTab);
                        runnable.run();
                    }
                }).sendThen(new WhoAmIRequest(), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        final HashMap<String, String> parsedResponse = response.getParsedResponse();
                        final int clientId = Integer.parseInt(parsedResponse.get("clid"));
                        serverTab.setSelfId(clientId);
                    }
                }).sendThen(new ServerGroupListRequest(), new EmptyCallback<TeamSpeakCommandResponse>()).sendThen(new ChannelGroupListRequest(), new EmptyCallback<TeamSpeakCommandResponse>()).sendThen(new ServerVariableRequest(), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        final ServerInfoImpl serverInfo = serverTab.getServerInfo();
                        final PropertyMap propertyMap = new PropertyMap(response.getParsedResponse());
                        serverInfo.setName(propertyMap.get("virtualserver_name"));
                        serverInfo.setUniqueId(propertyMap.get("virtualserver_unique_identifier"));
                        serverInfo.setPlatform(propertyMap.get("virtualserver_platform"));
                        serverInfo.setVersion(propertyMap.get("virtualserver_version"));
                        serverInfo.setTimeCreated(propertyMap.getLong("virtualserver_created"));
                        serverInfo.setBannerURL(propertyMap.get("virtualserver_hostbanner_url"));
                        serverInfo.setBannerImageURL(propertyMap.get("virtualserver_hostbanner_gfx_url"));
                        serverInfo.setBannerImageInterval(propertyMap.getInt("virtualserver_hostbanner_gfx_interval"));
                        serverInfo.setPrioritySpeakerDimmModificator(propertyMap.getFloat("virtualserver_priority_speaker_dimm_modificator"));
                        serverInfo.setHostButtonTooltip(propertyMap.get("virtualserver_hostbutton_tooltip"));
                        serverInfo.setHostButtonURL(propertyMap.get("virtualserver_hostbutton_url"));
                        serverInfo.setHostButtonImageURL(propertyMap.get("virtualserver_hostbutton_gfx_url"));
                        serverInfo.setPhoneticName(propertyMap.get("virtualserver_name_phonetic"));
                        serverInfo.setIconId(propertyMap.getInt("virtualserver_icon_id"));
                        serverTab.setDefaultServerGroup(serverTab.getServerGroup(propertyMap.getInt("virtualserver_default_server_group")));
                        serverTab.setDefaultChannelGroup(serverTab.getChannelGroup(propertyMap.getInt("virtualserver_default_channel_group")));
                    }
                }).sendThen(new ChannelListRequest(), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        final Map<Integer, ChannelImpl> map = new HashMap<Integer, ChannelImpl>();
                        for (final String c : response.getMessage().split("\\|")) {
                            final HashMap<String, String> parse = TeamSpeakResponse.parse(c);
                            final PropertyMap propertyMap = new PropertyMap(parse);
                            final ChannelImpl channel = new ChannelImpl(serverTab, propertyMap);
                            channel.updateProperties(propertyMap);
                            map.put(channel.getId(), channel);
                        }
                        serverTab.setChannels(map);
                    }
                }).sendThen(new ClientListRequest(), new Callback<TeamSpeakCommandResponse>() {
                    @Override
                    public void onDone(final TeamSpeakCommandResponse response) {
                        final List<ClientImpl> clients = new ArrayList<ClientImpl>();
                        for (final String c : response.getMessage().split("\\|")) {
                            final HashMap<String, String> parse = TeamSpeakResponse.parse(c);
                            final ChannelImpl channel = serverTab.getChannel(Integer.parseInt(parse.get("cid")));
                            final PropertyMap propertyMap = new PropertyMap(parse);
                            final int clientId = propertyMap.getInt("clid");
                            ClientImpl client;
                            if (clientId == serverTab.getSelfId()) {
                                final OwnClientImpl ownClient = new OwnClientImpl(TeamSpeakNetworkManager.this, clientId, propertyMap.getInt("client_database_id"), propertyMap.get("client_unique_identifier"), propertyMap.get("client_nickname"), channel);
                                serverTab.setSelf(ownClient);
                                client = ownClient;
                            }
                            else {
                                client = new ClientImpl(TeamSpeakNetworkManager.this, clientId, propertyMap.getInt("client_database_id"), propertyMap.get("client_unique_identifier"), propertyMap.get("client_nickname"), channel);
                            }
                            client.updateProperties(propertyMap);
                            clients.add(client);
                        }
                        serverTab.setClients(clients);
                        serverTab.setLoaded(true);
                        runnable.run();
                    }
                });
            }
        });
    }
    
    public void clearTabInfo(final ServerTabImpl serverTab) {
        serverTab.setChannels(Collections.emptyMap());
        serverTab.setClients(Collections.emptyList());
        serverTab.setServerInfo(null);
        serverTab.setSelfId(0);
        serverTab.setSelf(null);
        serverTab.clearServerGroups();
        serverTab.clearChannelGroups();
    }
    
    private void checkNeedsBlocking(final Runnable runnable) {
        if (runnable instanceof BlockingRunnable) {
            ((BlockingRunnable)runnable).onStart();
        }
    }
    
    public TeamSpeakClientImpl getTeamSpeakClient() {
        return this.teamSpeakClient;
    }
    
    static {
        logMarkerPackets = MarkerManager.getMarker("TEAMSPEAK_PACKETS");
        EVENT_LOOP_GROUP = (EventLoopGroup)new NioEventLoopGroup(2, new ThreadFactoryBuilder().setNameFormat("TeamSpeak Netty Client").setDaemon(true).build());
    }
}
