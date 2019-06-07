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

import java.awt.image.*;

import com.google.common.base.Objects;
import com.google.common.collect.*;
import java.util.*;
import com.google.common.base.*;
import eu.the5zig.teamspeak.response.*;
import eu.the5zig.teamspeak.util.*;
import eu.the5zig.teamspeak.request.*;
import eu.the5zig.teamspeak.api.*;

public class ChannelImpl implements Channel
{
    private final ServerTabImpl serverTab;
    private final int id;
    private int order;
    private String name;
    private String formattedName;
    private ChannelType type;
    private int parentId;
    private ChannelImpl parent;
    private int iconId;
    private final Object LOCK;
    private final List<ChannelImpl> children;
    private final List<ClientImpl> clients;
    private final Map<Integer, ClientImpl> clientLookup;
    private String topic;
    private String description;
    private boolean subscribed;
    private boolean defaultChannel;
    private boolean requiresPassword;
    private boolean permanent;
    private boolean semiPermanent;
    private ChannelCodec codec;
    private int codecQuality;
    private int neededTalkPower;
    private int maxClients;
    private boolean maxClientsUnlimited;
    private int maxFamilyClients;
    private boolean maxFamilyClientsUnlimited;
    
    public ChannelImpl(final ServerTabImpl serverTab, final PropertyMap propertyMap) {
        this.LOCK = new Object();
        this.children = new ArrayList<>();
        this.clients = new ArrayList<>();
        this.clientLookup = new HashMap<>();
        this.serverTab = serverTab;
        this.id = propertyMap.getInt("cid");
    }
    
    public void updateProperties(final PropertyMap propertyMap) {
        this.order = propertyMap.getInt("channel_order", this.order);
        if (propertyMap.contains("channel_name")) {
            this.name = propertyMap.get("channel_name", this.name);
            this.type = ChannelType.byName(this.name);
            this.formattedName = this.type.formatName(this.name);
        }
        this.topic = propertyMap.get("channel_topic", this.topic);
        this.description = propertyMap.get("channel_description", this.description);
        this.subscribed = propertyMap.getBool("channel_flag_are_subscribed", this.subscribed);
        this.defaultChannel = propertyMap.getBool("channel_flag_default", this.defaultChannel);
        this.requiresPassword = propertyMap.getBool("channel_flag_password", this.requiresPassword);
        this.permanent = propertyMap.getBool("channel_flag_permanent", this.permanent);
        this.semiPermanent = propertyMap.getBool("channel_flag_semi_permanent", this.semiPermanent);
        this.codec = ChannelCodec.byId(propertyMap.getInt("channel_codec", (this.codec == null) ? 0 : this.codec.getId()));
        this.codecQuality = propertyMap.getInt("channel_codec_quality", this.codecQuality);
        this.neededTalkPower = propertyMap.getInt("channel_needed_talk_power", this.neededTalkPower);
        this.maxClients = propertyMap.getInt("channel_maxclients", this.maxClients);
        this.maxClientsUnlimited = propertyMap.getBool("channel_flag_maxclients_unlimited", this.maxClientsUnlimited);
        this.maxFamilyClients = propertyMap.getInt("channel_maxfamilyclients", this.maxFamilyClients);
        this.maxFamilyClientsUnlimited = propertyMap.getBool("channel_flag_maxfamilyclients_unlimited", this.maxFamilyClientsUnlimited);
        if (propertyMap.contains("pid")) {
            this.parentId = propertyMap.getInt("pid", this.parentId);
        }
        else if (propertyMap.contains("cpid")) {
            this.parentId = propertyMap.getInt("cpid", this.parentId);
        }
        this.iconId = propertyMap.getInt("channel_icon_id", this.iconId);
    }
    
    @Override
    public ServerTabImpl getServerTab() {
        return this.serverTab;
    }
    
    public ServerInfoImpl getServerInfo() {
        return this.serverTab.getServerInfo();
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public void setOrder(final int order) {
        this.order = order;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getFormattedName() {
        return this.formattedName;
    }
    
    @Override
    public ChannelType getType() {
        return this.type;
    }
    
    public int getParentId() {
        return this.parentId;
    }
    
    @Override
    public int getIconId() {
        return this.iconId;
    }
    
    @Override
    public BufferedImage getIcon() {
        return (this.getServerInfo() == null || this.getServerInfo().getUniqueId() == null) ? null : ImageManager.resolveIcon(this.getServerInfo().getUniqueId(), this.getIconId());
    }
    
    @Override
    public ChannelImpl getParent() {
        return this.parent;
    }
    
    public void setParent(final ChannelImpl parent) {
        this.parent = parent;
    }
    
    @Override
    public Channel getAbove() {
        return this.serverTab.getChannel(this.order);
    }
    
    @Override
    public List<ChannelImpl> getChildren() {
        synchronized (this.LOCK) {
            return (List<ChannelImpl>)ImmutableList.copyOf((Collection)this.children);
        }
    }
    
    void addChild(final ChannelImpl channel) {
        synchronized (this.LOCK) {
            for (final ChannelImpl ch : this.children) {
                if (ch.getOrder() == channel.getOrder()) {
                    ch.setOrder(channel.getId());
                    break;
                }
            }
            this.children.add(channel);
        }
    }
    
    void removeChild(final ChannelImpl channel) {
        synchronized (this.LOCK) {
            this.children.remove(channel);
            for (final ChannelImpl other : this.children) {
                if (other.getOrder() == channel.getId()) {
                    other.setOrder(channel.getOrder());
                }
            }
        }
    }
    
    @Override
    public List<ClientImpl> getClients() {
        synchronized (this.LOCK) {
            return (List<ClientImpl>)ImmutableList.copyOf((Collection)this.clients);
        }
    }
    
    public void addClient(final ClientImpl client) {
        synchronized (this.LOCK) {
            this.clients.add(client);
            this.clientLookup.put(client.getId(), client);
            Collections.sort(this.clients);
        }
    }
    
    public void removeClient(final ClientImpl client) {
        synchronized (this.LOCK) {
            this.clients.remove(client);
            this.clientLookup.remove(client.getId());
            Collections.sort(this.clients);
        }
    }
    
    @Override
    public ClientImpl getClient(final int id) {
        synchronized (this.LOCK) {
            return this.clientLookup.get(id);
        }
    }
    
    @Override
    public String getTopic() {
        return this.topic;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public boolean hasSubscribed() {
        return this.subscribed;
    }
    
    public void setSubscribed(final boolean subscribed) {
        this.subscribed = subscribed;
    }
    
    @Override
    public boolean isDefault() {
        return this.defaultChannel;
    }
    
    @Override
    public boolean requiresPassword() {
        return this.requiresPassword;
    }
    
    @Override
    public boolean isPermanent() {
        return this.permanent;
    }
    
    @Override
    public boolean isSemiPermanent() {
        return this.semiPermanent;
    }
    
    @Override
    public ChannelCodec getCodec() {
        return this.codec;
    }
    
    @Override
    public int getCodecQuality() {
        return this.codecQuality;
    }
    
    @Override
    public int getNeededTalkPower() {
        return this.neededTalkPower;
    }
    
    @Override
    public int getMaxClients() {
        return this.maxClientsUnlimited ? -1 : this.maxClients;
    }
    
    @Override
    public int getMaxFamilyClients() {
        return this.maxFamilyClientsUnlimited ? -1 : this.maxFamilyClients;
    }
    
    @Override
    public void moveBelow(final Channel channel) {
        if (Objects.equal((Object)this.parent, (Object)channel.getParent())) {
            this.serverTab.networkManager.sendRequest(new ChannelEditRequest(this.getId(), channel.getId()), new EmptyCallback<TeamSpeakCommandResponse>());
        }
        else {
            this.serverTab.networkManager.sendRequest(new ChannelMoveRequest(this.getId(), (channel.getParent() == null) ? 0 : channel.getParent().getId(), channel.getId()), new EmptyCallback<TeamSpeakCommandResponse>());
        }
    }
    
    @Override
    public void moveInside(final Channel channel) {
        this.moveInside(channel, null);
    }
    
    @Override
    public void moveInside(final Channel channel, final Channel above) {
        this.serverTab.networkManager.sendRequest(new ChannelMoveRequest(this.getId(), channel.getId(), (above == null) ? 0 : above.getId()), new EmptyCallback<TeamSpeakCommandResponse>());
    }
    
    @Override
    public String toString() {
        return "ChannelImpl{id=" + this.id + ", name='" + this.name + '\'' + '}';
    }
    
    static void sort(final List<ChannelImpl> channels) {
        sort0(channels);
        for (final ChannelImpl channel : channels) {
            sort(channel.children);
        }
    }
    
    private static void sort0(final List<ChannelImpl> channels) {
        final Map<Integer, ChannelImpl> map = new HashMap<>();
        for (final ChannelImpl channel : channels) {
            if (map.containsKey(channel.getOrder())) {
                throw new IllegalArgumentException("duplicate order ids: " + channel.getOrder());
            }
            map.put(channel.getOrder(), channel);
        }
        final List<ChannelImpl> sorted = new ArrayList<>();
        for (int i = 0; i < channels.size(); ++i) {
            final ChannelImpl e = (i == 0) ? map.get(0) : map.get(sorted.get(i - 1).getId());
            if (e == null) {
                throw new NullPointerException("map " + map + " does not contain element with id " + ((i == 0) ? 0 : sorted.get(i - 1).getId()) + "!");
            }
            sorted.add(e);
        }
        channels.clear();
        channels.addAll(sorted);
    }
}
