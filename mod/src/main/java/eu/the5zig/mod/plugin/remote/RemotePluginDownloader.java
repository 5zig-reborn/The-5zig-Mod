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

package eu.the5zig.mod.plugin.remote;

import com.google.gson.Gson;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class RemotePluginDownloader {
    public RemotePlugin download(int id) {
        String url = (The5zigMod.DEBUG ? "http://localhost:3000" : "https://plugins.5zigreborn.eu") + "/api/plugin/" + id;
        String text = Utils.downloadFile(url);
        Gson gson = new Gson();
        RemotePlugin plugin = gson.fromJson(text, RemotePlugin.class);
        plugin.downloadImage();
        return plugin;
    }

    public void downloadPlugin(RemotePlugin plugin, final GuiPluginSocket watcher) throws IOException {
        watcher.setState(GuiPluginSocket.DownloadState.DOWNLOADING_PLUGIN);
        String url = (The5zigMod.DEBUG ? "http://localhost:3000" : "https://plugins.5zigreborn.eu") + "/dl/" + plugin.getId();
        File out = new File(The5zigMod.getModDirectory(), "plugins/" + plugin.getFileName() + ".jar");
        if(!out.exists()) {
            if(!out.createNewFile()) {
                watcher.downloadComplete("plugin.conn.error.fs"); // Couldn't create plugin file
                return;
            }
        }
        new Thread(() -> {
            try {
                URL url1 = new URL(url);
                try(InputStream stream = url1.openStream()) {
                    try(ReadableByteChannel channel = Channels.newChannel(stream)) {
                        try(FileOutputStream fos = new FileOutputStream(out)) {
                            fos.getChannel().transferFrom(channel, 0, Integer.MAX_VALUE);
                            watcher.downloadComplete(null);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
