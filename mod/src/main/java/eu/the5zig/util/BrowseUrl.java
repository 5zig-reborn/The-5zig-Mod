/*
 * Copyright (c) 2019 5zig Reborn
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

package eu.the5zig.util;

import eu.the5zig.mod.The5zigMod;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;

public enum BrowseUrl {
    LINUX,
    SOLARIS,
    WINDOWS {
        protected String[] getOpenCommand(URL url) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler", url.toString()};
        }
    },
    OSX {
        protected String[] getOpenCommand(URL url) {
            return new String[]{"open", url.toString()};
        }
    },
    UNKNOWN;

    public void openURL(URL url) {
        try {
            Process process = AccessController.doPrivileged((PrivilegedExceptionAction<Process>)
                    (() -> Runtime.getRuntime().exec(this.getOpenCommand(url))));

            for (String s : IOUtils.readLines(process.getErrorStream())) {
                The5zigMod.logger.error(s);
            }

            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();
        } catch (IOException | PrivilegedActionException ex) {
            The5zigMod.logger.error("Couldn't open url '{}'", url, ex);
        }

    }

    protected String[] getOpenCommand(URL url) {
        String s = url.toString();
        if ("file".equals(url.getProtocol())) {
            s = s.replace("file:", "file://");
        }
        return new String[]{"xdg-open", s};
    }

    public static BrowseUrl get() {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        if (s.contains("win")) {
            return WINDOWS;
        } else if (s.contains("mac")) {
            return OSX;
        } else if (s.contains("solaris")) {
            return SOLARIS;
        } else if (s.contains("sunos")) {
            return SOLARIS;
        } else if (s.contains("linux")) {
            return LINUX;
        } else {
            return s.contains("unix") ? LINUX : UNKNOWN;
        }
    }
}
