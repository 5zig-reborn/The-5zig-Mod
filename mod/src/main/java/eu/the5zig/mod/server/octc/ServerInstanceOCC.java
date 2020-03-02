package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.ServerInstance;

import java.util.Locale;

public class ServerInstanceOCC extends ServerInstance {
    @Override
    public void registerListeners() {
        getGameListener().registerListener(new OCCServerListener());
        getGameListener().registerListener(new OCCListener());
    }

    @Override
    public String getName() {
        return "Oc.tc";
    }

    @Override
    public String getConfigName() {
        return "octc";
    }

    @Override
    public boolean handleServer(String host, int port) {
        return host.toLowerCase(Locale.ROOT).endsWith("oc.tc");
    }

    private static class OCCServerListener extends AbstractGameListener<GameMode> {

        @Override
        public Class<GameMode> getGameMode() {
            return null;
        }

        @Override
        public boolean matchLobby(String lobby) {
            return false;
        }

        @Override
        public void onServerJoin() {
            getGameListener().switchLobby("Arena");
        }
    }
}
