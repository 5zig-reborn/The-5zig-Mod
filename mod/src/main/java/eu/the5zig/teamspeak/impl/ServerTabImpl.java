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

package eu.the5zig.teamspeak.impl;

import eu.the5zig.teamspeak.net.*;
import eu.the5zig.teamspeak.tslogs.*;
import com.google.common.collect.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.*;
import com.google.common.base.*;
import eu.the5zig.teamspeak.response.*;
import eu.the5zig.teamspeak.util.*;
import org.apache.commons.io.*;
import org.apache.commons.codec.binary.*;
import eu.the5zig.teamspeak.request.*;
import java.util.*;
import eu.the5zig.teamspeak.api.*;

public class ServerTabImpl implements ServerTab
{
    final TeamSpeakNetworkManager networkManager;
    private final int id;
    private boolean loaded;
    private boolean selected;
    private ServerInfoImpl serverInfo;
    private final Object LOCK;
    private final List<ChannelImpl> channels;
    private final Map<Integer, ChannelImpl> channelLookup;
    private final Map<Integer, ClientImpl> clientLookup;
    private int selfId;
    private OwnClientImpl self;
    private GroupImpl defaultServerGroup;
    private final List<GroupImpl> serverGroups;
    private final Map<Integer, GroupImpl> serverGroupLookup;
    private GroupImpl defaultChannelGroup;
    private final List<GroupImpl> channelGroups;
    private final Map<Integer, GroupImpl> channelGroupLookup;
    private final ServerChatImpl serverChat;
    private final ChannelChatImpl channelChat;
    private final PokeChatImpl pokeChat;
    private final List<PrivateChatImpl> privateChats;
    private final Map<Integer, PrivateChatImpl> privateChatLookup;
    
    public ServerTabImpl(final TeamSpeakNetworkManager networkManager, final int id) {
        this.LOCK = new Object();
        this.channels = new ArrayList<>();
        this.channelLookup = new HashMap<>();
        this.clientLookup = new HashMap<>();
        this.serverGroups = new ArrayList<>();
        this.serverGroupLookup = new HashMap<>();
        this.channelGroups = new ArrayList<>();
        this.channelGroupLookup = new HashMap<>();
        this.privateChats = new ArrayList<>();
        this.privateChatLookup = new HashMap<>();
        this.networkManager = networkManager;
        this.id = id;
        this.serverChat = new ServerChatImpl(networkManager);
        this.channelChat = new ChannelChatImpl(networkManager);
        this.pokeChat = new PokeChatImpl(networkManager);
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public boolean isSelected() {
        return this.selected;
    }
    
    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
    
    @Override
    public void setSelected() {
        if (!this.selected) {
            this.networkManager.trySelectTab(this.id, new EmptyRunnable());
        }
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public void setLoaded(final boolean loaded) {
        this.loaded = loaded;
    }
    
    @Override
    public ServerInfoImpl getServerInfo() {
        return this.serverInfo;
    }
    
    public void setServerInfo(final ServerInfoImpl serverInfo) {
        if (this.serverInfo != null) {
            final LogFileParseManager parseManager = this.serverInfo.getParseManager();
            if (parseManager != null) {
                parseManager.stop();
            }
        }
        this.serverInfo = serverInfo;
    }
    
    public void setChannels(final Map<Integer, ChannelImpl> channelMap) {
        synchronized (this.LOCK) {
            this.channels.clear();
            for (final ChannelImpl channel : channelMap.values()) {
                if (channel == null) {
                    throw new NullPointerException();
                }
                if (channel.getParentId() == 0) {
                    this.channels.add(channel);
                }
                else {
                    final ChannelImpl parent = channelMap.get(channel.getParentId());
                    channel.setParent(parent);
                    parent.addChild(channel);
                }
            }
            this.channelLookup.clear();
            this.channelLookup.putAll(channelMap);
            ChannelImpl.sort(this.channels);
        }
    }
    
    public void addChannel(final ChannelImpl channel) {
        if (channel == null) {
            throw new NullPointerException();
        }
        synchronized (this.LOCK) {
            if (channel.getParentId() == 0) {
                for (final ChannelImpl ch : this.channels) {
                    if (ch.getOrder() == channel.getOrder()) {
                        ch.setOrder(channel.getId());
                        break;
                    }
                }
                this.channels.add(channel);
            }
            else {
                final ChannelImpl parent = this.channelLookup.get(channel.getParentId());
                channel.setParent(parent);
                parent.addChild(channel);
            }
            this.channelLookup.put(channel.getId(), channel);
            ChannelImpl.sort(this.channels);
        }
    }
    
    public void removeChannel(final ChannelImpl channel) {
        synchronized (this.LOCK) {
            if (channel.getParentId() == 0) {
                this.channels.remove(channel);
                for (final ChannelImpl other : this.channels) {
                    if (other.getOrder() == channel.getId()) {
                        other.setOrder(channel.getOrder());
                    }
                }
            }
            else {
                this.channelLookup.get(channel.getParentId()).removeChild(channel);
                for (final ChannelImpl child : channel.getChildren()) {
                    this.removeChannel(child);
                }
            }
            this.channelLookup.remove(channel.getId());
        }
    }
    
    public void addChild(final ChannelImpl parent, final ChannelImpl child) {
        synchronized (this.LOCK) {
            parent.addChild(child);
            this.channelLookup.put(child.getId(), child);
            ChannelImpl.sort(this.channels);
        }
    }
    
    public void removeChild(final ChannelImpl parent, final ChannelImpl child) {
        synchronized (this.LOCK) {
            parent.removeChild(child);
            this.channelLookup.remove(child.getId());
            ChannelImpl.sort(this.channels);
        }
    }
    
    @Override
    public List<ChannelImpl> getChannels() {
        synchronized (this.LOCK) {
            return (List<ChannelImpl>)ImmutableList.copyOf((Collection)this.channels);
        }
    }
    
    @Override
    public void createChannel(final String name, final String password, final String topic, final String description, final ChannelLifespan lifespan, final boolean defaultChannel, final Channel parentChannel, final Channel orderChannel, final boolean bottomPosition, final int neededTalkPower, final ChannelCodec codec, final int codecQuality, final int maxClients) {
        Validate.notEmpty((CharSequence)name, "Channel name cannot be empty!", new Object[0]);
        if (!Strings.isNullOrEmpty(password)) {
            this.networkManager.sendRequest(new HashPasswordRequest(password), new Callback<TeamSpeakCommandResponse>() {
                @Override
                public void onDone(final TeamSpeakCommandResponse response) {
                    final PropertyMap propertyMap = new PropertyMap(response.getParsedResponse());
                    final String hash = propertyMap.get("passwordhash");
                    ServerTabImpl.this.networkManager.sendRequest(new ChannelCreateRequest(name, hash, topic, description, lifespan, defaultChannel, parentChannel, orderChannel, bottomPosition, neededTalkPower, codec, codecQuality, maxClients), new EmptyCallback<TeamSpeakCommandResponse>());
                }
            });
        }
        else {
            this.networkManager.sendRequest(new ChannelCreateRequest(name, null, topic, description, lifespan, defaultChannel, parentChannel, orderChannel, bottomPosition, neededTalkPower, codec, codecQuality, maxClients), new EmptyCallback<TeamSpeakCommandResponse>());
        }
    }
    
    @Override
    public void updateChannelProperties(final Channel channel, final String name, final String password, final String topic, final String description, final ChannelLifespan lifespan, final boolean defaultChannel, final Channel parentChannel, final Channel orderChannel, final boolean bottomPosition, final int neededTalkPower, final ChannelCodec codec, final int codecQuality, final int maxClients) {
        Validate.notNull((Object)channel, "Channel cannot be null!", new Object[0]);
        Validate.notEmpty((CharSequence)name, "Channel name cannot be empty!", new Object[0]);
        if (!Strings.isNullOrEmpty(password)) {
            this.networkManager.sendRequest(new HashPasswordRequest(password), new Callback<TeamSpeakCommandResponse>() {
                @Override
                public void onDone(final TeamSpeakCommandResponse response) {
                    final PropertyMap propertyMap = new PropertyMap(response.getParsedResponse());
                    final String hash = propertyMap.get("passwordhash");
                    final String base64hash = Base64.encodeBase64String(hash.getBytes(Charsets.UTF_8));
                    final ChannelEditRequest command = new ChannelEditRequest(channel, name, base64hash, topic, description, lifespan, defaultChannel, parentChannel, orderChannel, bottomPosition, neededTalkPower, codec, codecQuality, maxClients);
                    if (command.getParams().size() > 1) {
                        ServerTabImpl.this.networkManager.sendRequest(command, new EmptyCallback<TeamSpeakCommandResponse>());
                    }
                }
            });
        }
        else {
            final ChannelEditRequest command = new ChannelEditRequest(channel, name, null, topic, description, lifespan, defaultChannel, parentChannel, orderChannel, bottomPosition, neededTalkPower, codec, codecQuality, maxClients);
            if (command.getParams().size() > 1) {
                this.networkManager.sendRequest(command, new EmptyCallback<TeamSpeakCommandResponse>());
            }
        }
    }
    
    @Override
    public void deleteChannel(final Channel channel, final boolean force) {
        this.networkManager.sendRequest(new ChannelDeleteRequest(channel.getId(), force), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    public ChannelImpl getChannel(final int channelId) {
        synchronized (this.LOCK) {
            return this.channelLookup.get(channelId);
        }
    }
    
    @Override
    public OwnClientImpl getSelf() {
        return this.self;
    }
    
    public void setClients(final List<ClientImpl> clients) {
        synchronized (this.LOCK) {
            this.clientLookup.clear();
            for (final ClientImpl client : clients) {
                client.getChannel().addClient(client);
                this.clientLookup.put(client.getId(), client);
            }
        }
    }
    
    public void addClient(final ClientImpl client) {
        synchronized (this.LOCK) {
            client.getChannel().addClient(client);
            this.clientLookup.put(client.getId(), client);
        }
    }
    
    public void removeClient(final ClientImpl client) {
        synchronized (this.LOCK) {
            client.getChannel().removeClient(client);
            this.clientLookup.remove(client.getId());
        }
    }
    
    public ClientImpl getClient(final int id) {
        synchronized (this.LOCK) {
            return this.clientLookup.get(id);
        }
    }
    
    public void setSelf(final OwnClientImpl client) {
        this.self = client;
    }
    
    public void setSelfId(final int selfId) {
        this.selfId = selfId;
    }
    
    public int getSelfId() {
        return this.selfId;
    }
    
    @Override
    public List<GroupImpl> getServerGroups() {
        synchronized (this.LOCK) {
            return (List<GroupImpl>)ImmutableList.copyOf((Collection)this.serverGroups);
        }
    }
    
    public void addServerGroup(final GroupImpl serverGroup) {
        synchronized (this.LOCK) {
            if (this.serverGroups.contains(serverGroup)) {
                this.serverGroups.remove(serverGroup);
            }
            if (this.serverGroupLookup.containsKey(serverGroup.getId())) {
                this.serverGroupLookup.remove(serverGroup.getId());
            }
            this.serverGroups.add(serverGroup);
            this.serverGroupLookup.put(serverGroup.getId(), serverGroup);
            Collections.sort(this.serverGroups);
        }
    }
    
    @Override
    public GroupImpl getServerGroup(final int id) {
        synchronized (this.LOCK) {
            return this.serverGroupLookup.get(id);
        }
    }
    
    @Override
    public GroupImpl getDefaultServerGroup() {
        return this.defaultServerGroup;
    }
    
    public void setDefaultServerGroup(final GroupImpl defaultServerGroup) {
        this.defaultServerGroup = defaultServerGroup;
    }
    
    public void clearServerGroups() {
        synchronized (this.LOCK) {
            this.serverGroups.clear();
            this.serverGroupLookup.clear();
        }
    }
    
    @Override
    public List<GroupImpl> getChannelGroups() {
        synchronized (this.LOCK) {
            return (List<GroupImpl>)ImmutableList.copyOf((Collection)this.channelGroups);
        }
    }
    
    public void addChannelGroup(final GroupImpl channelGroup) {
        synchronized (this.LOCK) {
            if (this.channelGroups.contains(channelGroup)) {
                this.channelGroups.remove(channelGroup);
            }
            if (this.channelGroupLookup.containsKey(channelGroup.getId())) {
                this.channelGroupLookup.remove(channelGroup.getId());
            }
            this.channelGroups.add(channelGroup);
            this.channelGroupLookup.put(channelGroup.getId(), channelGroup);
            Collections.sort(this.channelGroups);
        }
    }
    
    @Override
    public GroupImpl getChannelGroup(final int id) {
        synchronized (this.LOCK) {
            return this.channelGroupLookup.get(id);
        }
    }
    
    @Override
    public GroupImpl getDefaultChannelGroup() {
        return this.defaultChannelGroup;
    }
    
    public void setDefaultChannelGroup(final GroupImpl defaultChannelGroup) {
        this.defaultChannelGroup = defaultChannelGroup;
    }
    
    public void clearChannelGroups() {
        synchronized (this.LOCK) {
            this.channelGroups.clear();
            this.channelGroupLookup.clear();
        }
    }
    
    @Override
    public ServerChatImpl getServerChat() {
        return this.serverChat;
    }
    
    @Override
    public ChannelChatImpl getChannelChat() {
        return this.channelChat;
    }
    
    @Override
    public PokeChatImpl getPokeChat() {
        return this.pokeChat;
    }
    
    @Override
    public void resetPokeChat() {
        this.pokeChat.reset();
    }
    
    @Override
    public List<PrivateChatImpl> getPrivateChats() {
        synchronized (this.LOCK) {
            return (List<PrivateChatImpl>)ImmutableList.copyOf((Collection)this.privateChats);
        }
    }
    
    @Override
    public PrivateChatImpl getPrivateChat(final Client client) {
        PrivateChatImpl privateChat;
        synchronized (this.LOCK) {
            privateChat = this.privateChatLookup.get(client.getId());
        }
        if (privateChat == null) {
            privateChat = new PrivateChatImpl(this.networkManager, (ClientImpl)client);
            this.addPrivateChat(privateChat);
        }
        return privateChat;
    }
    
    public void addPrivateChat(final PrivateChatImpl privateChat) {
        synchronized (this.LOCK) {
            this.privateChats.add(privateChat);
            this.privateChatLookup.put(privateChat.getClient().getId(), privateChat);
        }
    }
    
    @Override
    public void removePrivateChat(final PrivateChat privateChat) {
        synchronized (this.LOCK) {
            final PrivateChatImpl impl = (PrivateChatImpl)privateChat;
            this.privateChats.remove(impl);
            this.privateChatLookup.remove(privateChat.getClient().getId());
        }
    }
    
    @Override
    public String toString() {
        return "ServerTab{id=" + this.id + ", serverInfo=" + this.serverInfo + '}';
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final ServerTabImpl serverTab = (ServerTabImpl)o;
        return this.id == serverTab.id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
}
