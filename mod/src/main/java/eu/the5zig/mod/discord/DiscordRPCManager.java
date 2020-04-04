/*
 * Copyright (c) 2019-2020 5zig Reborn
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

package eu.the5zig.mod.discord;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.User;
import com.jagrosh.discordipc.exceptions.NoDiscordClientException;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.ServerQuitEvent;

public class DiscordRPCManager {

    public static final long CLIENT_ID = 587300876440305685L;

    private IPCClient client;

    public void init() throws NoDiscordClientException {
        if(!The5zigMod.getConfig().getBool("discord"))
            return;

        client = new IPCClient(CLIENT_ID);

        client.setListener(new IPCListener() {
            @Override
            public void onReady(IPCClient client, User user) {
                The5zigMod.logger.info("Connected to Discord as {}#{} ({}).", user.getName(), user.getDiscriminator(),
                        user.getId());
            }
        });

        client.connect();
        setDefault();

        The5zigMod.getListener().registerListener(this);
    }

    public void clearPresence() {
        if(client != null)
            client.sendRichPresence(null);
    }

    public void close() {
        if(client != null)
            client.close();
    }

    public void disable() {
        clearPresence();
        close();
        client = null;
    }

    public void setDefault() {
        setPresence(The5zigRichPresence.getDefault());
    }

    public void setPresence(The5zigRichPresence presence) {
        if(client != null)
            client.sendRichPresence(presence.build());
    }

    @EventHandler
    public void onDisconnect(ServerQuitEvent event) {
        setDefault();
    }

}
