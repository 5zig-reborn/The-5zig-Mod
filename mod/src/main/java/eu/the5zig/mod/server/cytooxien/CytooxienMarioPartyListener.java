/*
 * Original: Copyright (c) 2015-2019 5zig [MIT]
 * Current: Copyright (c) 2019 5zig Reborn [GPLv3+]
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

package eu.the5zig.mod.server.cytooxien;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.gui.ingame.Scoreboard;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class CytooxienMarioPartyListener extends AbstractGameListener<ServerCytooxien.MarioParty> {

	@Override
	public Class<ServerCytooxien.MarioParty> getGameMode() {
		return ServerCytooxien.MarioParty.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.toLowerCase().startsWith("mario party");
	}

	@Override
	public void onMatch(ServerCytooxien.MarioParty gameMode, String key, IPatternResult match) {
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("marioparty.first")) {
				gameMode.minigames++;
				if (gameMode.minigames > 0 && !gameMode.getMinigameQueue().isEmpty()) {
					gameMode.getMinigameQueue().remove(0);
				}
				String[] players = match.get(0).split(", ");
				for (String player : players) {
					if (player.equals(The5zigMod.getDataManager().getUsername())) {
						gameMode.first++;
					}
				}
				String winner = match.get(0);
				gameMode.setWinner(winner);
				gameMode.setWinTime(System.currentTimeMillis());
			} else if (key.equals("marioparty.second")) {
				String[] players = match.get(0).split(", ");
				for (String player : players) {
					if (player.equals(The5zigMod.getDataManager().getUsername())) {
						gameMode.second++;
					}
				}
			} else if (key.equals("marioparty.third")) {
				String[] players = match.get(0).split(", ");
				for (String player : players) {
					if (player.equals(The5zigMod.getDataManager().getUsername())) {
						gameMode.third++;
					}
				}
			} else if (key.equals("marioparty.minigame.announcement")) {
				if (!("\u00dcbersicht").equals(The5zigMod.getVars().getOpenContainerTitle())) {
					gameMode.inventoryRequested = true;
					The5zigMod.getVars().closeContainer();
					int selectedSlot = The5zigMod.getVars().getSelectedHotbarSlot();
					The5zigMod.getVars().setSelectedHotbarSlot(4);
					The5zigMod.getVars().onRightClickMouse();
					The5zigMod.getVars().setSelectedHotbarSlot(selectedSlot);
				}
			}
		}
	}

	@Override
	public void onChestSetSlot(ServerCytooxien.MarioParty gameMode, String containerTitle, int slot, ItemStack itemStack) {
		if (gameMode.inventoryRequested && gameMode.getState() == GameState.LOBBY && containerTitle.equals("Minispiel-\u00dcbersicht")) {
			if (slot < 9 * 3) {
				String stripped = ChatColor.stripColor(itemStack.getDisplayName());
				if (!gameMode.getMinigameQueue().contains(stripped)) {
					gameMode.getMinigameQueue().add(stripped);
				}
			} else {
				gameMode.inventoryRequested = false;
				The5zigMod.getVars().closeContainer();
			}
		}
		if (gameMode.getState() == GameState.GAME && containerTitle.equals("\u00dcbersicht")) {
			if (slot < 9 * 3) {
				String player = ChatColor.stripColor(itemStack.getDisplayName());
				if (The5zigMod.getDataManager().getUsername().equals(player)) {
					gameMode.setPlace(slot + 1);
				}
				if (slot == 0) {
					String field = ChatColor.stripColor(itemStack.getLore().get(2)).split("braucht | Felder")[1];
					gameMode.setFirstPlayer(player + " (" + field + ")");
				}
			} else {
				if (gameMode.inventoryRequested) {
					The5zigMod.getVars().closeContainer();
					The5zigMod.getScheduler().postToMainThread(new Runnable() {
						@Override
						public void run() {
							The5zigMod.getVars().closeContainer();
						}
					}, true);
				}
				gameMode.inventoryRequested = false;
			}
		}
	}

	@Override
	public void onTitle(ServerCytooxien.MarioParty gameMode, String title, String subTitle) {
	}

	@Override
	public void onTick(ServerCytooxien.MarioParty gameMode) {
//		if (gameMode.getState() == GameState.LOBBY && !gameMode.inventoryRequested && gameMode.getMinigameQueue().isEmpty() && The5zigMod.getVars().getMinecraftScreen() == null &&
//				The5zigMod.getVars().getServerPlayers().size() > 0) {
//			gameMode.inventoryRequested = true;
//			The5zigMod.getVars().closeContainer();
//			int selectedSlot = The5zigMod.getVars().getSelectedHotbarSlot();
//			The5zigMod.getVars().setSelectedHotbarSlot(1);
//			The5zigMod.getVars().onRightClickMouse();
//			The5zigMod.getVars().setSelectedHotbarSlot(selectedSlot);
//		}
		if (gameMode.getState() == GameState.GAME && gameMode.getWinner() != null && gameMode.getWinTime() != -1 && System.currentTimeMillis() - gameMode.getWinTime() > 5000) {
			gameMode.setWinner(null);
			gameMode.setWinTime(-1);
		}
		if (gameMode.getState() == GameState.LOBBY) {
			Scoreboard scoreboard = The5zigMod.getVars().getScoreboard();
			if (scoreboard == null) return;
			if (scoreboard.getLines().containsKey(ChatColor.GRAY + "Startfeld")) {
				gameMode.setState(GameState.GAME);
			}
		}
		if (gameMode.getState() != GameState.GAME) return;
		Scoreboard scoreboard = The5zigMod.getVars().getScoreboard();
		if (scoreboard == null) return;
		HashMap<Integer, String> reverse = new HashMap<Integer, String>();
		for (Map.Entry<String, Integer> e : scoreboard.getLines().entrySet()) reverse.put(e.getValue(), e.getKey());
		if (!(reverse.containsKey(1) && reverse.containsKey(2) && reverse.containsKey(5) && reverse.containsKey(4)))
			return;
		if (ChatColor.stripColor(reverse.get(2)).equals("Dein Feld") && ChatColor.stripColor(reverse.get(1)).split("/").length == 2) {
			gameMode.setRemainingFields(Integer.parseInt(ChatColor.stripColor(reverse.get(1)).split("/")[1]) - Integer.parseInt(ChatColor.stripColor(reverse.get(1)).split("/")[0]));
		} else if (ChatColor.stripColor(reverse.get(2)).equals("Dein Feld") && ChatColor.stripColor(reverse.get(1)).contains("Felder")) {
			gameMode.setRemainingFields(Integer.parseInt(ChatColor.stripColor(reverse.get(1)).split(" ")[0]) * -1);
		}
		if (ChatColor.stripColor(reverse.get(5)).equals("Deine Platzierung") && !reverse.get(4).contains(".")) {
			gameMode.setPlace(Integer.parseInt(ChatColor.stripColor(reverse.get(4)).replace("#", "")));
		}
	}
}
