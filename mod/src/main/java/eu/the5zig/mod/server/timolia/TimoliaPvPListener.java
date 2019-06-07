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

package eu.the5zig.mod.server.timolia;

import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IMultiPatternResult;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.util.Callback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;

public class TimoliaPvPListener extends AbstractGameListener<ServerTimolia.PvP> {

	@Override
	public Class<ServerTimolia.PvP> getGameMode() {
		return ServerTimolia.PvP.class;
	}

	@Override
	public boolean matchLobby(String lobby) {
		return lobby.startsWith("pvp");
	}

	@Override
	public void onMatch(final ServerTimolia.PvP gameMode, String key, IPatternResult match) {
		// Default PvP Fight
		if (gameMode.getState() == GameState.LOBBY || gameMode.getState() == GameState.STARTING) {
			if (key.equals("pvp.starting") && "3".equals(match.get(1))) {
				gameMode.setState(GameState.STARTING);
				gameMode.setOpponent(match.get(0));
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(1)) * 1000L);

				getGameListener().sendAndIgnoreMultiple("/stats " + gameMode.getOpponent(), "\u2591\u2592\u2593Stats von " + gameMode.getOpponent() + "\u2593\u2592\u2591",
						"\u255a\u2550\u2550\u2550\u2550\u2550", new Callback<IMultiPatternResult>() {
							@Override
							public void call(IMultiPatternResult callback) {
								IPatternResult gamesTotal = callback.parseKey("pvp.stats.games.total");
								IPatternResult gamesWon = callback.parseKey("pvp.stats.games.won");
								IPatternResult killDeathRatio = callback.parseKey("pvp.stats.kill_death_ratio");
								if ((gameMode.getState() == GameState.STARTING || gameMode.getState() == GameState.GAME) && gamesTotal != null && gamesWon != null &&
										killDeathRatio != null) {
									gameMode.setOpponentStats(new ServerTimolia.OpponentStats(Integer.parseInt(gamesTotal.get(0)), Integer.parseInt(gamesWon.get(0)),
											Double.parseDouble(killDeathRatio.get(0))));
								}
							}
						});
			}
			if (key.equals("pvp.start")) {
				gameMode.setState(GameState.GAME);
				if (gameMode.getTournament() != null) {
					gameMode.setTime(System.currentTimeMillis() +
							(gameMode.getTournament().getCurrentRound() <= gameMode.getTournament().getQualificationRounds() ? gameMode.getTournament().getQualificationRoundTime() :
									gameMode.getTournament().getRoundTime()));
				} else {
					gameMode.setTime(System.currentTimeMillis());
				}
			}
		}
		if (gameMode.getState() == GameState.GAME) {
			if (key.equals("pvp.ending")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
		}
		if (gameMode.getState() == GameState.STARTING || gameMode.getState() == GameState.GAME) {
			if (key.equals("pvp.win.single") || key.equals("pvp.lose.single") || key.equals("pvp.win.team") || key.equals("pvp.lose.team")) {
				if (key.equals("pvp.win.single") || key.equals("pvp.win.team")) {
					gameMode.setWinner(The5zigMod.getDataManager().getUsername());
					if (gameMode.getTournament() == null) {
						gameMode.setWinStreak(gameMode.getWinStreak() + 1);
					}
					if (gameMode.getTournament() != null) {
						gameMode.getTournament().setRoundWins(gameMode.getTournament().getRoundWins() + 1);
					}
				}
				if (key.equals("pvp.lose.single") || key.equals("pvp.lose.team")) {
					gameMode.setWinner(gameMode.getOpponent());
					if (gameMode.getTournament() == null) {
						gameMode.setWinStreak(0);
					}
					if (gameMode.getTournament() != null) {
						gameMode.getTournament().setRoundLoses(gameMode.getTournament().getRoundLoses() + 1);
					}
				}
				gameMode.setState(GameState.FINISHED);
				gameMode.setWinTime(System.currentTimeMillis() + 2900);
			}
		}
		// Tournament
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("pvp.tournament.starting")) {
				gameMode.setTime(System.currentTimeMillis() + Integer.parseInt(match.get(0)) * 1000L);
			}
		}
		if (gameMode.getState() == GameState.LOBBY) {
			if (key.equals("pvp.tournament.round")) {
				int currentRound = Integer.parseInt(match.get(0));
				if (currentRound == 1) {
					ServerTimolia.PvPTournament tournament = new ServerTimolia.PvPTournament();
					gameMode.setTournament(tournament);
					gameMode.getTournament().setInventoryRequested(true);
					The5zigMod.getVars().sendMessage("/t");
				}
				gameMode.getTournament().setRoundWins(0);
				gameMode.getTournament().setRoundLoses(0);
				gameMode.getTournament().setCurrentRound(currentRound);
			}
			if (gameMode.getTournament() != null) {
				if (key.equals("pvp.tournament.participants.single") || key.equals("pvp.tournament.participants.team")) {
					gameMode.getTournament().setParticipants(Integer.parseInt(match.get(0)));
				}
			}
			if (key.equals("pvp.tournament.end")) {
				gameMode.setTournament(null);
			}
		}
		if (key.equals("pvp.tournament.leave") || key.equals("pvp.team.leave") || key.equals("pvp.team.leave2")) {
			gameMode.setTournament(null);
			gameMode.setState(GameState.LOBBY);
		}
		if (key.equals("pvp.streak")) {
			gameMode.setWinStreak(Integer.parseInt(match.get(0)));
		}
	}

	@Override
	public void onChestSetSlot(ServerTimolia.PvP gameMode, String containerTitle, int slot, ItemStack itemStack) {
		ServerTimolia.PvPTournament tournament = gameMode.getTournament();
		if (tournament == null || itemStack == null || !"Turnierinfos".equals(containerTitle)) {
			return;
		}
		if (slot == 10) {
			if (!"Informationen".equals(ChatColor.stripColor(itemStack.getDisplayName()))) {
				return;
			}
			List<String> formattedLore = itemStack.getLore();
			List<String> unformattedLore = Lists.newArrayList();
			for (String s : formattedLore) {
				unformattedLore.add(ChatColor.stripColor(s));
			}
			unformattedLore.remove(0);
			String host = unformattedLore.remove(0).substring("Turnierleiter: ".length());
			tournament.setHost(host);
			String kit = unformattedLore.remove(0).substring("Kit: ".length());
			tournament.setKit(kit);
			String playerString = unformattedLore.remove(0);
			int players = Integer.parseInt(playerString.substring(playerString.lastIndexOf(" ") + 1));
			tournament.setParticipants(players);
			unformattedLore.remove(0);
			String qualificationType = unformattedLore.remove(0);
			boolean qualification = true;
			if (qualificationType.equals("Normale Qualifikation")) {
				String roundString = unformattedLore.remove(0);
				int qualificationRounds = Integer.parseInt(roundString.substring(roundString.lastIndexOf(" ") + 1));
				tournament.setQualificationRounds(qualificationRounds);
			} else if (qualificationType.equals("Zeitliche Qualifikation")) {
				String roundString = unformattedLore.remove(0);
				long duration = Utils.parseTimeFormatToMillis(roundString.substring(roundString.lastIndexOf(" ") + 1), "mm:ss");
				tournament.setQualificationDuration(duration);
			} else {
				qualification = false;
			}
			if (qualification) {
				String bestOfString = unformattedLore.remove(0);
				int bestOf = Integer.parseInt(bestOfString.substring(bestOfString.lastIndexOf(" ") + 1));
				tournament.setQualificationFirstTo(bestOf);
				String timeString = unformattedLore.remove(0);
				long time = Utils.parseTimeFormatToMillis(timeString.substring(timeString.lastIndexOf(" ") + 1), "mm:ss");
				tournament.setQualificationRoundTime(time);

				unformattedLore.remove(0);
			}
			unformattedLore.remove(0);

			String bestOfString = unformattedLore.remove(0);
			int bestOf = Integer.parseInt(bestOfString.substring(bestOfString.lastIndexOf(" ") + 1));
			tournament.setRoundFirstTo(bestOf);
			String timeString = unformattedLore.remove(0);
			long time = Utils.parseTimeFormatToMillis(timeString.substring(timeString.lastIndexOf(" ") + 1), "mm:ss");
			tournament.setRoundTime(time);

			if (tournament.isInventoryRequested()) {
				tournament.setInventoryRequested(false);
				The5zigMod.getVars().closeContainer();
			}
		}
	}

	@Override
	public void onTick(ServerTimolia.PvP gameMode) {
		if (gameMode.getWinTime() != -1 && gameMode.getWinTime() - System.currentTimeMillis() < 0) {
			gameMode.setWinTime(-1);
			gameMode.setKills(0);
			gameMode.setDeaths(0);
			gameMode.setWinner(null);
			gameMode.setTime(-1);
			gameMode.setOpponent(null);
			gameMode.setOpponentStats(null);
			gameMode.setWinMessage(null);
			gameMode.setState(GameState.LOBBY);
		}
	}
}
