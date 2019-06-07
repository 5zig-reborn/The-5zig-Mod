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

package eu.the5zig.teamspeak;

import eu.the5zig.teamspeak.listener.*;
import eu.the5zig.teamspeak.net.*;
import eu.the5zig.teamspeak.impl.*;
import org.apache.logging.log4j.*;
import eu.the5zig.teamspeak.util.*;
import eu.the5zig.teamspeak.api.*;
import com.google.common.collect.*;
import java.util.*;

public class TeamSpeakClientImpl implements TeamSpeakClient
{
    private static final String HOST = "localhost";
    private static final int PORT = 25639;
    private final List<ConnectListener> CONNECT_LISTENERS;
    private final List<DisconnectListener> DISCONNECT_LISTENERS;
    private boolean connected;
    private boolean autoReconnect;
    private TeamSpeakNetworkManager networkManager;
    private final List<ServerTabImpl> tabs;
    
    TeamSpeakClientImpl() {
        this.CONNECT_LISTENERS = new ArrayList<>();
        this.DISCONNECT_LISTENERS = new ArrayList<>();
        this.connected = false;
        this.autoReconnect = true;
        this.tabs = new ArrayList<>();
    }
    
    @Override
    public void connect() {
        this.connect(null);
    }
    
    @Override
    public void connect(final String authKey) {
        if (this.connected) {
            throw new IllegalStateException("TeamSpeak API is already connected to Client Query!");
        }
        this.connected = false;
        this.networkManager = new TeamSpeakNetworkManager(this, authKey);
        new Runnable() {
            private final Object LOCK = new Object();
            private List<Integer> result;
            
            @Override
            public void run() {
                try {
                    TeamSpeakClientImpl.this.networkManager.connect("localhost", 25639);
                    final int currentTab = TeamSpeakClientImpl.this.networkManager.getSelectedTab();
                    TeamSpeakClientImpl.this.networkManager.getTabs(new Callback<List<Integer>>() {
                        @Override
                        public void onDone(final List<Integer> tabIds) {
                            result = tabIds;
                            synchronized (LOCK) {
                                LOCK.notifyAll();
                            }
                        }
                    });
                    synchronized (this.LOCK) {
                        this.LOCK.wait();
                    }
                    for (final Integer tabId : this.result) {
                        final ServerTabImpl serverTab = new ServerTabImpl(TeamSpeakClientImpl.this.networkManager, tabId);
                        synchronized (TeamSpeakClientImpl.this.tabs) {
                            TeamSpeakClientImpl.this.tabs.add(serverTab);
                        }
                        TeamSpeakClientImpl.this.networkManager.getTabInfo(serverTab, new Runnable() {
                            @Override
                            public void run() {
                                synchronized (LOCK) {
                                    LOCK.notifyAll();
                                }
                            }
                        });
                        synchronized (this.LOCK) {
                            this.LOCK.wait();
                        }
                    }
                    TeamSpeakClientImpl.this.connected = true;
                    TeamSpeakClientImpl.this.getServerTab(currentTab).setSelected(true);
                    TeamSpeakClientImpl.this.networkManager.trySelectTab(currentTab, new BlockingRunnable());
                    synchronized (TeamSpeakClientImpl.this.CONNECT_LISTENERS) {
                        for (final ConnectListener listener : TeamSpeakClientImpl.this.CONNECT_LISTENERS) {
                            listener.onConnected();
                        }
                    }
                }
                catch (Throwable throwable) {
                    if (TeamSpeakClientImpl.this.networkManager == null) {
                        TeamSpeakClientImpl.this.disconnect(throwable);
                    }
                    else {
                        TeamSpeakClientImpl.this.networkManager.disconnect(throwable);
                    }
                }
            }
        }.run();
    }
    
    @Override
    public void disconnect() {
        if (this.networkManager != null) {
            this.networkManager.disconnect(null);
        }
    }
    
    @Override
    public boolean isConnected() {
        return this.networkManager != null && this.networkManager.isConnected();
    }
    
    public void disconnect(final Throwable cause) {
        if (TeamSpeak.isDebugMode()) {
            LogManager.getLogger().info("Disconnected from TeamSpeak ClientQuery: " + cause);
        }
        this.connected = false;
        for (final ServerTabImpl tab : this.tabs) {
            this.networkManager.clearTabInfo(tab);
        }
        this.tabs.clear();
        this.networkManager = null;
        synchronized (this.DISCONNECT_LISTENERS) {
            for (final DisconnectListener listener : this.DISCONNECT_LISTENERS) {
                listener.onDisconnect(cause);
            }
        }
        if (this.autoReconnect && cause != null) {
            new Thread("TeamSpeak Reconnect Thread") {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000L);
                    }
                    catch (InterruptedException ex) {}
                    if (TeamSpeak.isDebugMode()) {
                        LogManager.getLogger().info("Reconnecting to TeamSpeak ClientQuery...");
                    }
                    TeamSpeakClientImpl.this.connect();
                }
            }.start();
        }
    }
    
    public void addServerTab(final ServerTabImpl tab) {
        synchronized (this.tabs) {
            this.tabs.add(tab);
        }
    }
    
    public void removeServerTab(final ServerTabImpl tab) {
        synchronized (this.tabs) {
            this.networkManager.clearTabInfo(tab);
            this.tabs.remove(tab);
            if (!this.tabs.isEmpty() && this.networkManager.getSelectedTab() == tab.getId()) {
                this.networkManager.trySelectTab(this.tabs.get(this.tabs.size() - 1).getId(), new EmptyRunnable());
            }
        }
    }
    
    @Override
    public void addConnectListener(final ConnectListener listener) {
        synchronized (this.CONNECT_LISTENERS) {
            this.CONNECT_LISTENERS.add(listener);
        }
    }
    
    @Override
    public void removeConnectListener(final ConnectListener listener) {
        synchronized (this.CONNECT_LISTENERS) {
            this.CONNECT_LISTENERS.remove(listener);
        }
    }
    
    @Override
    public void addDisconnectListener(final DisconnectListener listener) {
        synchronized (this.DISCONNECT_LISTENERS) {
            this.DISCONNECT_LISTENERS.add(listener);
        }
    }
    
    @Override
    public void removeDisconnectListener(final DisconnectListener listener) {
        synchronized (this.DISCONNECT_LISTENERS) {
            this.DISCONNECT_LISTENERS.remove(listener);
        }
    }
    
    @Override
    public void setAutoReconnect(final boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }
    
    @Override
    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }
    
    @Override
    public List<? extends ServerTab> getServerTabs() {
        synchronized (this.tabs) {
            return (List<? extends ServerTab>)ImmutableList.copyOf((Collection)this.tabs);
        }
    }
    
    @Override
    public ServerTabImpl getServerTab(final int id) {
        synchronized (this.tabs) {
            for (final ServerTabImpl tab : this.tabs) {
                if (tab.getId() == id) {
                    return tab;
                }
            }
        }
        return null;
    }
    
    @Override
    public ServerTabImpl getSelectedTab() {
        return (this.networkManager == null) ? null : this.getServerTab(this.networkManager.getSelectedTab());
    }
    
    public TeamSpeakNetworkManager getNetworkManager() {
        return this.networkManager;
    }
}
