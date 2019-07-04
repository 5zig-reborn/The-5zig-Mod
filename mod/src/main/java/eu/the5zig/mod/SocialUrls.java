package eu.the5zig.mod;

import java.awt.*;
import java.net.URI;

public class SocialUrls {

    public static final String DISCORD = "https://l.5zigreborn.eu/discord";
    public static final String REDDIT = "https://reddit.com/r/5zig";
    public static final String PATREON = "https://patreon.com/5zig";
    public static final String TWITTER = "https://twitter.com/The5zigMod";
    public static final String GITHUB = "https://github.com/5zig-reborn/The-5zig-Mod";

    public static void open(String urlString) {
        if(!Desktop.isDesktopSupported()) return;

        try {
            URI url = new URI(urlString);
            Desktop.getDesktop().browse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
