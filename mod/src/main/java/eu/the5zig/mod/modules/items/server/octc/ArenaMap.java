package eu.the5zig.mod.modules.items.server.octc;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.octc.ServerOCC;

public class ArenaMap extends GameModeItem<ServerOCC.Arena> {

    public ArenaMap() {
        super(ServerOCC.Arena.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return null;
    }
}
