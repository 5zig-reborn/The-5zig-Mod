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

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.render.PNGUtils;

public class RemotePlugin {
    private String name, author, logo_url, file_name;
    private int id, download_count;

    private PNGUtils.PNGData imageBase64;

    public String getFileName() {
        return file_name;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public int getDownloadCount() {
        return download_count;
    }

    public PNGUtils.PNGData getImageBase64() {
        return imageBase64;
    }

    public int getId() {
        return id;
    }

    void downloadImage() {
        final RemotePlugin plg = this;
        new Thread(() -> plg.imageBase64 = PNGUtils.downloadBase64PNGComplex(
                (The5zigMod.DEBUG ? "http://localhost:3000" : "https://plugins.5zigreborn.eu") + logo_url)).start();
    }
}
