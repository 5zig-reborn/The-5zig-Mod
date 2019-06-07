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

import java.text.*;
import java.util.*;
import java.awt.image.*;
import java.net.*;
import javax.imageio.*;
import org.apache.commons.io.*;
import java.io.*;

public class Utils
{
    private static final File TEAMSPEAK_DIRECTORY;
    private static final DateFormat timeFormatter;
    
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    
    public static Platform getPlatform() {
        final String osName = getOSName().toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            return Platform.WINDOWS;
        }
        if (osName.contains("mac")) {
            return Platform.MAC;
        }
        if (osName.contains("linux") || osName.contains("sunos") || osName.contains("unix")) {
            return Platform.LINUX;
        }
        if (osName.contains("solaris")) {
            return Platform.SOLARIS;
        }
        return Platform.UNKNOWN;
    }
    
    public static File getTeamspeakDirectory() {
        return Utils.TEAMSPEAK_DIRECTORY;
    }
    
    public static String getChatTimeString() {
        return "<" + Utils.timeFormatter.format(new Date(System.currentTimeMillis())) + "> ";
    }
    
    public static BufferedImage readImage(final String imageUrl) throws IOException {
        final URL url = new URL(imageUrl);
        final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            return ImageIO.read(inputStream);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
    
    private static int findHKey(final String key) {
        if (existsHKey(-2147483646, key)) {
            return -2147483646;
        }
        if (existsHKey(-2147483647, key)) {
            return -2147483647;
        }
        return -1;
    }
    
    private static boolean existsHKey(final int hkey, final String key) {
        try {
            WinRegistry.readString(hkey, key, "");
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }
    
    private static File getTeamSpeakDirectoryInAppData() {
        final String userHome = System.getProperty("user.home", ".");
        final String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (osName.contains("win")) {
            final String applicationData = System.getenv("APPDATA");
            if (applicationData != null) {
                return new File(applicationData, "TS3Client/");
            }
            return new File(userHome, "TS3Client/");
        }
        else {
            if (osName.contains("mac")) {
                return new File(userHome, "Library/Application Support/TeamSpeak 3");
            }
            if (osName.contains("linux") || osName.contains("sunos") || osName.contains("unix") || osName.contains("solaris")) {
                return new File(userHome, "TeamSpeak 3/");
            }
            return new File(userHome, "TeamSpeak 3");
        }
    }
    
    static {
        timeFormatter = DateFormat.getTimeInstance(2);
        File TEAMSPEAK_DIRECTORY2;
        if (getPlatform() == Platform.WINDOWS) {
            final String registryKey = "Software\\TeamSpeak 3 Client";
            final int hKey = findHKey(registryKey);
            if (hKey == -1) {
                TEAMSPEAK_DIRECTORY2 = getTeamSpeakDirectoryInAppData();
            }
            else {
                try {
                    final String configLocation = WinRegistry.readString(hKey, registryKey, "ConfigLocation");
                    if ("0".equals(configLocation)) {
                        TEAMSPEAK_DIRECTORY2 = getTeamSpeakDirectoryInAppData();
                    }
                    else {
                        final String installPath = WinRegistry.readString(hKey, registryKey, "");
                        final File possibleConfigDirectory = new File(installPath, "config");
                        if (possibleConfigDirectory.exists()) {
                            TEAMSPEAK_DIRECTORY2 = possibleConfigDirectory;
                        }
                        else {
                            TEAMSPEAK_DIRECTORY2 = getTeamSpeakDirectoryInAppData();
                        }
                    }
                }
                catch (Exception ignored) {
                    TEAMSPEAK_DIRECTORY2 = getTeamSpeakDirectoryInAppData();
                }
            }
        }
        else {
            TEAMSPEAK_DIRECTORY2 = getTeamSpeakDirectoryInAppData();
        }
        TEAMSPEAK_DIRECTORY = TEAMSPEAK_DIRECTORY2;
    }
    
    public enum Platform
    {
        WINDOWS, 
        MAC, 
        LINUX, 
        SOLARIS, 
        UNKNOWN;
    }
}
