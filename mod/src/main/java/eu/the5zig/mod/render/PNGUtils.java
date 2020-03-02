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

package eu.the5zig.mod.render;

import com.google.common.base.Charsets;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

public class PNGUtils {
    public static String downloadBase64PNG(String urlString) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "5zig/" + Version.VERSION);
            BufferedImage image = ImageIO.read(conn.getInputStream());
            // Converting Image byte array into Base64 String
            ByteBuf localByteBuf1 = Unpooled.buffer();
            ImageIO.write(image, "PNG", new ByteBufOutputStream(localByteBuf1));
            ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
            String imageDataString = localByteBuf2.toString(Charsets.UTF_8);
            The5zigMod.logger.debug("Got Base64 encoded image for {}", urlString);
            return imageDataString;
        } catch (Exception e) {
            The5zigMod.logger.warn("Could not get Base64 image for {}", urlString);
            e.printStackTrace();
        }
        finally {
            if(conn != null)
                conn.disconnect();
        }
        return null;
    }
}
