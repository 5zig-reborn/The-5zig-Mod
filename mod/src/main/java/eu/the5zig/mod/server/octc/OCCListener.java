/*
 * Copyright (c) 2019-2020 5zig Reborn
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

package eu.the5zig.mod.server.octc;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;

public class OCCListener extends AbstractGameListener<ServerOCC.Arena> {
    private String lastSubtitle;

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
            gameMode.setKills(0);
            gameMode.setDeaths(0);
            gameMode.setKillStreak(0);
            gameMode.setTeam(null);
        }
        else if(key.startsWith("kill_")) {
            String killer = match.get(1);
            if(killer != null && killer.equals(The5zigAPI.getAPI().getGameProfile().getName())) {
                gameMode.setKills(gameMode.getKills() + 1);
                gameMode.setKillStreak(gameMode.getKillStreak() + 1);
            }
        }
    }

    @Override
    public void onTitle(ServerOCC.Arena gameMode, String title, String subTitle) {
        if("§r§a§r§a§r§aLeft click to respawn in §r§b0.2§r§as§r".equals(subTitle)
                || "§r§a§r§a§r§aRespawning in §r§b0.2§r§as§r".equals(subTitle) && !subTitle.equals(lastSubtitle)) {
            gameMode.setDeaths(gameMode.getDeaths() + 1);
            gameMode.setKillStreak(0);
        }
        lastSubtitle = subTitle;
    }
}
