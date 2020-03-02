package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;

public class OCCListener extends AbstractGameListener<ServerOCC.Arena> {
    @Override
    public Class<ServerOCC.Arena> getGameMode() {
        return ServerOCC.Arena.class;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return "Arena".equals(lobby);
    }

    @Override
    public void onTick(ServerOCC.Arena gameMode) {
        if(The5zigAPI.getAPI().getSideScoreboard() != null) {
            gameMode.setSubMode(ChatColor.stripColor(The5zigAPI.getAPI().getSideScoreboard().getTitle()));
        }
    }

    @Override
    public void onMatch(ServerOCC.Arena gameMode, String key, IPatternResult match) {
        if("team".equals(key)) {
            gameMode.setTeam(match.get(0));
        }
        else if("map".equals(key)) {
            gameMode.setMap(match.get(0).trim());
        }
    }
}
