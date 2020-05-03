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

package eu.the5zig.mod.chat.network.packets;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.GuiCenteredText;
import eu.the5zig.util.BrowseUrl;
import io.netty.buffer.ByteBuf;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class PacketAuthToken implements Packet {

    private TokenType type;
    private String token;
    private boolean remember;

    public PacketAuthToken(boolean remember) {
        this.remember = remember;
    }

    public PacketAuthToken() {}

    @Override
    public void read(ByteBuf buffer) throws IOException {
        type = TokenType.values()[buffer.readInt()];
        remember = buffer.readBoolean();
        token = PacketBuffer.readString(buffer);
    }

    @Override
    public void write(ByteBuf buffer) throws IOException {
        buffer.writeBoolean(remember);
    }

    @Override
    public void handle() {
        switch (type) {
            case SITE_LOGIN:
                String baseUrl = The5zigMod.DEBUG ? "http://localhost:8080" : "https://secure.5zigreborn.eu";
                try {
                    URI uri = new URI(baseUrl + "/login?token=" + token + "&remember=" + remember);
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(uri);
                    } else {
                        BrowseUrl.get().openURL(uri.toURL());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                break;
            case SPOTIFY:
                The5zigMod.getConfig().get("refresh_token").set(token);
                The5zigMod.getVars().displayScreen(new GuiCenteredText(null, I18n.translate("spotify.token_get")));
                break;
        }
    }

    private enum TokenType {
        SITE_LOGIN, SPOTIFY
    }
}
