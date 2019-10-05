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

package eu.the5zig.mod.manager.spotify;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class SpotifyWebAPI {

    private static final String BASE_URL = "https://api.spotify.com/v1";
    private static final int TIMEOUT = 10000;
    private static final Pattern JS_PATTERN = Pattern.compile("\\s+Spotify\\.Entity = (.*);");

    private final Cache<String, String> TRACK_IMAGE_LOOKUP = CacheBuilder.newBuilder().maximumSize(500).build();
    private final Cache<String, SpotifyTrack> SPOTIFY_SEARCH_RESULTS = CacheBuilder.newBuilder().maximumSize(500).build();

    public boolean resolveTrackImage(final SpotifyNewTrack track) throws IOException {
        URL url = new URL(track.getImageUrl());
        BufferedImage image = ImageIO.read(url);
        BufferedImage image1 = new BufferedImage(128, 128, image.getType());
        Graphics graphics = image1.getGraphics();
        try {
            graphics.drawImage(image, 0, 0, image1.getWidth(), image1.getHeight(), null);
        } finally {
            graphics.dispose();
        }
        // Converting Image byte array into Base64 String
        ByteBuf localByteBuf1 = Unpooled.buffer();
        ImageIO.write(image1, "PNG", new ByteBufOutputStream(localByteBuf1));
        ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
        String imageDataString = localByteBuf2.toString(Charsets.UTF_8);

        track.setImage(imageDataString);
        TRACK_IMAGE_LOOKUP.put(track.getId(), imageDataString);

        return true;
    }

}
