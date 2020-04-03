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

import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

public class Version {

    public static final String VERSION;
    public static final String MCVERSION;
    public static final int LANGVERSION = 44;
    public static final int PROTOCOL = 6;
    public static final int APIVERSION = 4;

    static {
        String version = "DEV", mcVersion = "unknown";

        try {
            Enumeration<URL> resources = Version.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                Manifest manifest = new Manifest(resources.nextElement().openStream());
                if (manifest.getMainAttributes().getValue("5zig-Version") != null) {
					version = manifest.getMainAttributes().getValue("5zig-Version");
					mcVersion = manifest.getMainAttributes().getValue("Minecraft-Version");
                }
            }
        } catch (Exception ex) {
            System.out.println("Corrupted file/Dev env");
        }
        finally {
        	VERSION = version;
        	MCVERSION = mcVersion;
		}
    }

}