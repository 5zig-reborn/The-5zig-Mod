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

package eu.the5zig.mod;

import eu.the5zig.util.BrowseUrl;

import java.awt.*;
import java.net.URI;

public class SocialUrls {

    public static final String DISCORD = "https://l.5zigreborn.eu/discord";
    public static final String REDDIT = "https://reddit.com/r/5zig";
    public static final String PATREON = "https://patreon.com/5zig";
    public static final String TWITTER = "https://twitter.com/The5zigMod";
    public static final String GITHUB = "https://github.com/5zig-reborn/The-5zig-Mod";

    public static void open(String urlString) {
        try {
            URI url = new URI(urlString);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(url);
            } else {
                BrowseUrl.get().openURL(url.toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
