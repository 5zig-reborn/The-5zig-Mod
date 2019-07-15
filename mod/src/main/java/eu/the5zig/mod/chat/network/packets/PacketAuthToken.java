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

import eu.the5zig.mod.The5zigMod;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class PacketAuthToken implements Packet {

    private String token;
    private boolean remember;

    public PacketAuthToken(boolean remember) {
        this.remember = remember;
    }

    public PacketAuthToken() {}

    @Override
    public void read(ByteBuf buffer) throws IOException {
        remember = buffer.readBoolean();
        token = PacketBuffer.readString(buffer);
    }

    @Override
    public void write(ByteBuf buffer) throws IOException {
        buffer.writeBoolean(remember);
    }

    @Override
    public void handle() {
        if(Desktop.isDesktopSupported()) {
            try {
                String baseUrl = The5zigMod.DEBUG ? "http://localhost:8080" : "https://secure.5zigreborn.eu";
                Desktop.getDesktop().browse(new URI(baseUrl + "/login?token=" + token + "&remember=" + remember));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
