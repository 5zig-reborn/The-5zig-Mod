package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.server.ServerInstance;

import java.util.Locale;

public class ServerInstanceOCC extends ServerInstance {
    @Override
    public void registerListeners() {

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
}
