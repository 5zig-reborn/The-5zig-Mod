package eu.the5zig.mod.modules.items.server.octc;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.octc.ServerOCC;

public class ArenaTeam extends GameModeItem<ServerOCC.Arena> {

    public ArenaTeam() {
        super(ServerOCC.Arena.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return dummy ? "Red" : getGameMode().getTeam();
    }

    @Override
    public String getTranslation() {
        return "ingame.team";
    }
}
