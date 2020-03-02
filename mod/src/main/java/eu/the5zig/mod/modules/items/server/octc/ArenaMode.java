package eu.the5zig.mod.modules.items.server.octc;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.octc.ServerOCC;

public class ArenaMode extends GameModeItem<ServerOCC.Arena> {

    public ArenaMode() {
        super(ServerOCC.Arena.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return dummy ? "CTW" : getGameMode().getSubMode();
    }

    @Override
    public String getTranslation() {
        return "ingame.mode";
    }
}
