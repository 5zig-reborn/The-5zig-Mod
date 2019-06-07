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

package eu.the5zig.teamspeak.util;

import java.io.*;
import java.awt.image.*;
import org.apache.commons.codec.binary.*;
import com.google.common.primitives.*;
import javax.imageio.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.*;
import org.apache.commons.lang3.*;
import eu.the5zig.teamspeak.api.*;
import com.google.common.base.*;
import java.net.*;
import eu.the5zig.teamspeak.impl.*;
import com.google.common.cache.*;
import java.util.concurrent.*;

public class ImageManager
{
    private static final File CACHE_DIRECTORY;
    private static final Cache<Integer, CachedImage> ICON_CACHE;
    private static final Cache<String, CachedImage> AVATAR_CACHE;
    private static final Cache<String, CachedServerImage> SERVER_IMAGE_CACHE;
    private static final char[] TEAMSPEAK_HEX;
    
    public static BufferedImage resolveIcon(final String serverUniqueId, final int iconId) {
        if (iconId == 0) {
            return null;
        }
        CachedImage cachedImage = ImageManager.ICON_CACHE.getIfPresent(iconId);
        if (cachedImage == null) {
            cachedImage = new CachedImage();
            ImageManager.ICON_CACHE.put(iconId, cachedImage);
            final File cacheFolder = new File(ImageManager.CACHE_DIRECTORY, Base64.encodeBase64String(serverUniqueId.getBytes(Charsets.UTF_8)));
            final File iconFolder = new File(cacheFolder, "icons");
            final long unsignedNumber = UnsignedInts.toLong(iconId);
            final File icon = new File(iconFolder, "icon_" + unsignedNumber);
            if (cacheFolder.exists() && iconFolder.exists() && icon.exists()) {
                try {
                    cachedImage.image = ImageIO.read(icon);
                }
                catch (Throwable t) {}
            }
            if (cachedImage.image == null) {
                LogManager.getLogger().error("Could not resolve TS icon " + unsignedNumber);
            }
        }
        return cachedImage.image;
    }
    
    public static BufferedImage resolveAvatar(final String serverUniqueId, final String clientUniqueId) {
        if (StringUtils.isEmpty(clientUniqueId)) {
            return null;
        }
        CachedImage cachedImage = (CachedImage)ImageManager.AVATAR_CACHE.getIfPresent((Object)clientUniqueId);
        if (cachedImage == null) {
            cachedImage = new CachedImage();
            ImageManager.AVATAR_CACHE.put(clientUniqueId, cachedImage);
            final String base64 = clientUniqueId + "=";
            final byte[] bytes = Base64.decodeBase64(base64);
            final char[] hexChars = new char[bytes.length * 2];
            for (int j = 0; j < bytes.length; ++j) {
                final int v = bytes[j] & 0xFF;
                hexChars[j * 2] = ImageManager.TEAMSPEAK_HEX[v >>> 4];
                hexChars[j * 2 + 1] = ImageManager.TEAMSPEAK_HEX[v & 0xF];
            }
            final String avatarName = "avatar_" + new String(hexChars);
            final File cacheFolder = new File(ImageManager.CACHE_DIRECTORY, Base64.encodeBase64String(serverUniqueId.getBytes(Charsets.UTF_8)));
            final File clientFolder = new File(cacheFolder, "clients");
            final File icon = new File(clientFolder, avatarName);
            if (cacheFolder.exists() && clientFolder.exists() && icon.exists()) {
                try {
                    cachedImage.image = ImageIO.read(icon);
                }
                catch (Throwable t) {}
            }
        }
        return cachedImage.image;
    }
    
    public static ServerImage resolveServerImage(final String pointingURL, final String imageURL) {
        if (Strings.isNullOrEmpty(imageURL)) {
            return null;
        }
        CachedServerImage cachedServerImage = (CachedServerImage)ImageManager.SERVER_IMAGE_CACHE.getIfPresent((Object)imageURL);
        if (cachedServerImage == null) {
            cachedServerImage = new CachedServerImage((ServerImage)null);
            ImageManager.SERVER_IMAGE_CACHE.put(imageURL, cachedServerImage);
            final CachedServerImage finalCachedServerImage = cachedServerImage;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final BufferedImage image = Utils.readImage(imageURL);
                        URL url = null;
                        if (!Strings.isNullOrEmpty(pointingURL)) {
                            url = new URL(pointingURL);
                        }
                        finalCachedServerImage.image = new ServerImageImpl(url, image);
                    }
                    catch (Throwable e) {
                        LogManager.getLogger().error("Could not resolve image at " + imageURL, e);
                    }
                }
            }, "TS Image Resolver").start();
        }
        return cachedServerImage.image;
    }
    
    static {
        CACHE_DIRECTORY = new File(Utils.getTeamspeakDirectory(), "cache");
        ICON_CACHE = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).maximumSize(500L).build();
        AVATAR_CACHE = CacheBuilder.newBuilder().expireAfterWrite(10L, TimeUnit.MINUTES).maximumSize(300L).build();
        SERVER_IMAGE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(30L, TimeUnit.MINUTES).maximumSize(10L).build();
        TEAMSPEAK_HEX = "abcdefghijklmnop".toCharArray();
    }
    
    private static class CachedImage
    {
        public BufferedImage image;
    }
    
    private static class CachedServerImage
    {
        public ServerImage image;
        
        private CachedServerImage(final ServerImage image) {
            this.image = image;
        }
    }
}
