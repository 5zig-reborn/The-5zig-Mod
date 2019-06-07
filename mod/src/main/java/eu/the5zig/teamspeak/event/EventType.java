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

public enum EventType
{
    ANY("any"), 
    TALK_STATUS_CHANGE("notifytalkstatuschange"), 
    MESSAGE("notifymessage"), 
    MESSAGE_LIST("notifymessagelist"), 
    COMPLAIN_LIST("notifycomplainlist"), 
    BAN_LIST("notifybanlist"), 
    CLIENT_MOVED("notifyclientmoved"), 
    CLIENT_LEFT_VIEW("notifyclientleftview"), 
    CLIENT_ENTERED_VIEW("notifycliententerview"), 
    CLIENT_POKE("notifyclientpoke"), 
    CLIENT_CHAT_CLOSED("notifyclientchatclosed"), 
    CLIENT_CHAT_COMPOSING("notifyclientchatcomposing"), 
    CLIENT_UPDATED("notifyclientupdated"), 
    CLIENT_IDS("notifyclientids"), 
    CLIENT_DB_ID_FROM_UID("notifyclientdbidfromuid"), 
    CLIENT_NAME_FROM_UID("notifyclientnamefromuid"), 
    CLIENT_NAME_FROM_DB_ID("notifyclientnamefromdbid"), 
    CLIENT_UID_FROM_CLID("notifyclientuidfromclid"), 
    CONNECTION_INFO("notifyconnectioninfo"), 
    CHANNEL_CREATED("notifychannelcreated"), 
    CHANNEL_EDITED("notifychanneledited"), 
    CHANNEL_DELETED("notifychanneldeleted"), 
    CHANNEL_DESCRIPTION_CHANGED("notifychanneldescriptionchanged"), 
    CHANNEL_MOVED("notifychannelmoved"), 
    SERVER_EDITED("notifyserveredited"), 
    SERVER_UPDATED("notifyserverupdated"), 
    CHANNEL_LIST("channellist"), 
    CHANNEL_LIST_FINISHED("channellistfinished"), 
    TEXT_MESSAGE("notifytextmessage"), 
    CURRENT_SERVER_CONNECTION_CHANGED("notifycurrentserverconnectionchanged"), 
    CONNECT_STATUS_CHANGE("notifyconnectstatuschange"), 
    CHANNEL_GROUP_CHANNGED("notifyclientchannelgroupchanged"), 
    CLIENT_NEEDED_PERMISSIONS("notifyclientneededpermissions"), 
    SERVER_GROUP_CLIENT_ADDED("notifyservergroupclientadded"), 
    SERVER_GROUP_CLIENT_REMOVED("notifyservergroupclientdeleted"), 
    CHANNEL_PASSWORD_CHANGED("notifychannelpasswordchanged"), 
    CHANNEL_SUBSCRIBED("notifychannelsubscribed"), 
    START_DOWNLOAD("notifystartdownload"), 
    STATUS_FILE_TRANSFER("notifystatusfiletransfer"), 
    SERVER_GROUP_LIST("notifyservergrouplist"), 
    CHANNEL_GROUP_LIST("notifychannelgrouplist");
    
    private String name;
    
    private EventType(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public static EventType byName(final String name) {
        for (final EventType eventType : values()) {
            if (eventType.getName().equals(name)) {
                return eventType;
            }
        }
        return null;
    }
}
