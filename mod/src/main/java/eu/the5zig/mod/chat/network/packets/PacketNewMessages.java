/*
 * Copyright (c) 2019 5zig Reborn
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

package eu.the5zig.mod.chat.network.packets;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.network.util.PacketUtil;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketNewMessages implements Packet {

    private List<Message> messages = new ArrayList<>();

    @Override
    public void read(ByteBuf buffer) throws IOException {
        int size = PacketBuffer.readVarIntFromBuffer(buffer);
        for(int i = 0; i < size; i++) {
            UUID uuid = PacketBuffer.readUUID(buffer);
            String name = PacketBuffer.readString(buffer);
            String text = PacketBuffer.readString(buffer);
            long time = buffer.readLong();

            messages.add(new Message(time, name, uuid, text));
        }
    }

    @Override
    public void write(ByteBuf buffer) throws IOException {

    }

    @Override
    public void handle() {
        PacketUtil.ensureMainThread(this);
        for(Message msg : messages) {
            The5zigMod.getConversationManager().handleBulkMessage(msg.uuid, msg.username, msg.text, msg.timestamp);
        }
        if(messages.size() == 0) return;
        if(The5zigMod.getConfig().getBool("playMessageSounds"))
            The5zigMod.getVars().playSound("the5zigmod", "chat.message.receive", 1);
        if(The5zigMod.getConfig().getBool("showMessages"))
            The5zigMod.getOverlayMessage().displayMessage(I18n.translate("chat.new_messages"), messages.size());
    }

    private static class Message {
        private long timestamp;
        private String username;
        private UUID uuid;
        private String text;

        public Message(long timestamp, String username, UUID uuid, String text) {
            this.timestamp = timestamp;
            this.username = username;
            this.uuid = uuid;
            this.text = text;
        }
    }
}
