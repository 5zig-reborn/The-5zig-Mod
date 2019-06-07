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

package eu.the5zig.teamspeak.event;

import java.util.regex.*;
import eu.the5zig.teamspeak.net.*;
import java.util.*;
import eu.the5zig.teamspeak.util.*;
import eu.the5zig.teamspeak.api.*;
import eu.the5zig.teamspeak.impl.*;

public class EventManager
{
    private static final Pattern P_FILTER_TAGS;
    private final TeamSpeakNetworkManager networkManager;
    
    public EventManager(final TeamSpeakNetworkManager networkManager) {
        this.networkManager = networkManager;
    }
    
    public void createEvent(final EventType eventType, final PropertyMap propertyMap) throws Throwable {
        switch (eventType) {
            case CURRENT_SERVER_CONNECTION_CHANGED: {
                this.createServerConnectionChangedEvent(propertyMap);
                break;
            }
            case CONNECT_STATUS_CHANGE: {
                this.createConnectStatusChangeEvent(propertyMap);
                break;
            }
            case CLIENT_ENTERED_VIEW: {
                this.createClientEnteredViewEvent(propertyMap);
                break;
            }
            case CLIENT_LEFT_VIEW: {
                this.createClientLeftViewEvent(propertyMap);
                break;
            }
            case CLIENT_MOVED: {
                this.createClientMovedEvent(propertyMap);
                break;
            }
            case CLIENT_UPDATED: {
                this.clientUpdated(propertyMap);
                break;
            }
            case CHANNEL_CREATED: {
                this.createChannelCreatedEvent(propertyMap);
                break;
            }
            case CHANNEL_EDITED: {
                this.onChannelEdited(propertyMap);
                break;
            }
            case CHANNEL_MOVED: {
                this.onChannelMoved(propertyMap);
                break;
            }
            case CHANNEL_DELETED: {
                this.createChannelDeletedEvent(propertyMap);
                break;
            }
            case TEXT_MESSAGE: {
                this.createTextMessageEvent(propertyMap);
                break;
            }
            case CLIENT_POKE: {
                this.createClientPokeEvent(propertyMap);
                break;
            }
            case TALK_STATUS_CHANGE: {
                this.createTalkStatusEvent(propertyMap);
                break;
            }
            case SERVER_GROUP_LIST: {
                this.serverGroupList(propertyMap);
                break;
            }
            case CHANNEL_GROUP_LIST: {
                this.channelGroupList(propertyMap);
                break;
            }
            case CHANNEL_GROUP_CHANNGED: {
                this.channelGroupChanged(propertyMap);
                break;
            }
            case CHANNEL_SUBSCRIBED: {
                this.channelSubscribed(propertyMap);
                break;
            }
        }
    }
    
    private void createServerConnectionChangedEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (tab == null) {
            tab = new ServerTabImpl(this.networkManager, tabId);
            this.networkManager.getTeamSpeakClient().addServerTab(tab);
        }
        this.networkManager.trySelectTab(tabId, new EmptyRunnable());
        final ServerConnectionHandlerChangeEvent event = new ServerConnectionHandlerChangeEvent(tab, this.networkManager.getTeamSpeakClient().getSelectedTab());
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createConnectStatusChangeEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        final TeamSpeakConnectStatus status = TeamSpeakConnectStatus.byName(propertyMap.get("status"));
        if (status == TeamSpeakConnectStatus.DISCONNECTED) {
            final List<? extends ServerTab> serverTabs = this.networkManager.getTeamSpeakClient().getServerTabs();
            if (serverTabs.size() == 1) {
                this.networkManager.clearTabInfo(tab);
            }
            else {
                this.networkManager.getTeamSpeakClient().removeServerTab(tab);
            }
        }
        else {
            ServerTabImpl serverTab;
            if (tab == null) {
                serverTab = new ServerTabImpl(this.networkManager, tabId);
                this.networkManager.getTeamSpeakClient().addServerTab(serverTab);
            }
            else {
                serverTab = tab;
            }
            if (status == TeamSpeakConnectStatus.CONNECTION_ESTABLISHED) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EventManager.this.networkManager.getTabInfo(serverTab, new EmptyRunnable());
                    }
                }, "TeamSpeak Connect Thread").start();
            }
        }
    }
    
    private void createClientEnteredViewEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl from = tab.getChannel(propertyMap.getInt("cfid"));
        final ChannelImpl to = tab.getChannel(propertyMap.getInt("ctid"));
        final ClientImpl client = new ClientImpl(this.networkManager, propertyMap.getInt("clid"), propertyMap.getInt("client_database_id"), propertyMap.get("client_unique_identifier"), propertyMap.get("client_nickname"), to);
        client.updateProperties(propertyMap);
        client.setChannel(to);
        tab.addClient(client);
        final ClientEnteredViewEvent.Reason reason = ClientEnteredViewEvent.Reason.byId(propertyMap.getInt("reasonid"));
        final ClientEnteredViewEvent event = new ClientEnteredViewEvent(tab, client, from, to, reason);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createClientLeftViewEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl client = tab.getClient(propertyMap.getInt("clid"));
        final ChannelImpl from = tab.getChannel(propertyMap.getInt("cfid"));
        final ChannelImpl to = tab.getChannel(propertyMap.getInt("ctid"));
        final ClientLeftViewEvent.Reason reason = ClientLeftViewEvent.Reason.byId(propertyMap.getInt("reasonid"));
        final String reasonMessage = propertyMap.get("reasonmsg");
        tab.removeClient(client);
        final ClientLeftViewEvent event = new ClientLeftViewEvent(tab, client, from, to, reason, reasonMessage);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createClientMovedEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl client = tab.getClient(propertyMap.getInt("clid"));
        final ChannelImpl from = client.getChannel();
        final ChannelImpl to = tab.getChannel(propertyMap.getInt("ctid"));
        if (tab.getSelf().equals(client)) {
            to.setSubscribed(true);
        }
        final ClientMovedEvent.Reason reason = ClientMovedEvent.Reason.byId(propertyMap.getInt("reasonid"));
        ClientImpl invoker = null;
        if (propertyMap.contains("invokerid")) {
            invoker = tab.getClient(propertyMap.getInt("invokerid"));
        }
        final String reasonMessage = propertyMap.get("reasonmsg");
        final ClientMovedEvent event = new ClientMovedEvent(tab, client, from, to, reason, invoker, reasonMessage);
        from.removeClient(client);
        to.addClient(client);
        client.setChannel(to);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void clientUpdated(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl client = tab.getClient(propertyMap.getInt("clid"));
        client.updateProperties(propertyMap);
    }
    
    private void createChannelCreatedEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl channel = new ChannelImpl(tab, propertyMap);
        channel.updateProperties(propertyMap);
        tab.addChannel(channel);
        final int invokerId = propertyMap.getInt("invokerid");
        final ClientImpl invoker = (invokerId <= 0) ? null : tab.getClient(invokerId);
        final ChannelCreatedEvent event = new ChannelCreatedEvent(tab, channel, invoker);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void onChannelEdited(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl channel = tab.getChannel(propertyMap.getInt("cid"));
        if (propertyMap.contains("channel_order")) {
            final int orderId = propertyMap.getInt("channel_order");
            if (channel.getParent() == null) {
                tab.removeChannel(channel);
                channel.setOrder(orderId);
                tab.addChannel(channel);
            }
            else {
                tab.removeChild(channel.getParent(), channel);
                channel.setOrder(orderId);
                tab.addChild(channel.getParent(), channel);
            }
        }
        channel.updateProperties(propertyMap);
    }
    
    private void onChannelMoved(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl channel = tab.getChannel(propertyMap.getInt("cid"));
        final ChannelImpl parent = tab.getChannel(propertyMap.getInt("cpid"));
        final int orderId = propertyMap.getInt("order");
        if (channel.getParent() == null) {
            tab.removeChannel(channel);
        }
        else {
            tab.removeChild(channel.getParent(), channel);
        }
        channel.setParent(parent);
        channel.setOrder(orderId);
        if (channel.getParent() == null) {
            tab.addChannel(channel);
        }
        else {
            tab.addChild(channel.getParent(), channel);
        }
    }
    
    private void createChannelDeletedEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl channel = tab.getChannel(propertyMap.getInt("cid"));
        tab.removeChannel(channel);
        final int invokerId = propertyMap.getInt("invokerid");
        final ClientImpl invoker = (invokerId <= 0) ? null : tab.getClient(invokerId);
        final ChannelDeletedEvent event = new ChannelDeletedEvent(tab, channel, invoker);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createTextMessageEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final MessageTargetMode targetMode = MessageTargetMode.byId(propertyMap.getInt("targetmode"));
        String msg = propertyMap.get("msg");
        msg = EventManager.P_FILTER_TAGS.matcher(msg).replaceAll("");
        int target = 0;
        if (propertyMap.contains("target")) {
            target = propertyMap.getInt("target");
        }
        final ClientImpl invoker = tab.getClient(propertyMap.getInt("invokerid"));
        if (targetMode == MessageTargetMode.CLIENT) {
            final ClientImpl targetClient = (invoker == tab.getSelf()) ? tab.getClient(target) : invoker;
            final PrivateChatImpl privateChat = tab.getPrivateChat((Client)targetClient);
            privateChat.addMessage(new MessageImpl(Utils.getChatTimeString() + "\"" + invoker.getName() + "\": " + msg, System.currentTimeMillis()));
        }
        final TextMessageEvent event = new TextMessageEvent(tab, targetMode, msg, target, invoker);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createClientPokeEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl invoker = tab.getClient(propertyMap.getInt("invokerid"));
        String message = propertyMap.get("msg");
        message = EventManager.P_FILTER_TAGS.matcher(message).replaceAll("");
        tab.getPokeChat().addMessage(new MessageImpl(Utils.getChatTimeString() + "\"" + invoker.getName() + "\": " + message, System.currentTimeMillis()));
        final ClientPokeEvent event = new ClientPokeEvent(tab, invoker, message);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void createTalkStatusEvent(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl client = tab.getClient(propertyMap.getInt("clid"));
        final TalkStatus talkStatus = TalkStatus.byId(propertyMap.getInt("status"));
        final boolean whispering = propertyMap.getBool("isreceivedwhisper");
        if (client == null) {
            throw new IllegalArgumentException("Invalid client id!");
        }
        client.setTalking(talkStatus == TalkStatus.TALKING);
        client.setWhispering(whispering);
        final TalkStatusChangeEvent event = new TalkStatusChangeEvent(tab, talkStatus, whispering, client);
        TeamSpeakEventDispatcher.dispatch(event);
    }
    
    private void serverGroupList(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        final GroupImpl serverGroup = new GroupImpl(tab, propertyMap.getInt("sgid"));
        serverGroup.setName(propertyMap.get("name"));
        serverGroup.setShowPrefix(propertyMap.getBool("namemode"));
        serverGroup.setType(propertyMap.getInt("type"));
        serverGroup.setIconId(propertyMap.getInt("iconid"));
        serverGroup.setSaveDb(propertyMap.getBool("savedb"));
        serverGroup.setSortId(propertyMap.getInt("sortid"));
        serverGroup.setModifyPower(propertyMap.getInt("n_modifyp"));
        serverGroup.setMemberAddPower(propertyMap.getInt("n_member_addp"));
        serverGroup.setMemberRemovePower(propertyMap.getInt("n_member_removep"));
        tab.addServerGroup(serverGroup);
    }
    
    private void channelGroupList(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        final GroupImpl channelGroup = new GroupImpl(tab, propertyMap.getInt("cgid"));
        channelGroup.setName(propertyMap.get("name"));
        channelGroup.setShowPrefix(propertyMap.getBool("namemode"));
        channelGroup.setType(propertyMap.getInt("type"));
        channelGroup.setIconId(propertyMap.getInt("iconid"));
        channelGroup.setSaveDb(propertyMap.getBool("savedb"));
        channelGroup.setSortId(propertyMap.getInt("sortid"));
        channelGroup.setModifyPower(propertyMap.getInt("n_modifyp"));
        channelGroup.setMemberAddPower(propertyMap.getInt("n_member_addp"));
        channelGroup.setMemberRemovePower(propertyMap.getInt("n_member_removep"));
        tab.addChannelGroup(channelGroup);
    }
    
    private void channelGroupChanged(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ClientImpl client = tab.getClient(propertyMap.getInt("clid"));
        final int groupId = propertyMap.getInt("cgid");
        final GroupImpl channelGroup = tab.getChannelGroup(groupId);
        client.setChannelGroup(channelGroup);
    }
    
    private void channelSubscribed(final PropertyMap propertyMap) {
        final int tabId = propertyMap.getInt("schandlerid");
        final ServerTabImpl tab = this.networkManager.getTeamSpeakClient().getServerTab(tabId);
        if (!tab.isLoaded()) {
            return;
        }
        final ChannelImpl channel = tab.getChannel(propertyMap.getInt("cid"));
        channel.setSubscribed(true);
    }
    
    static {
        P_FILTER_TAGS = Pattern.compile("\\[[^\\]]*\\]");
    }
}
