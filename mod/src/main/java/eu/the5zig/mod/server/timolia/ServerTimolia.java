/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.util.Vector3f;

import java.util.Map;

public class ServerTimolia {

	public static class Splun extends GameMode {

		@Override
		public String getName() {
			return "Splun";
		}
	}

	public static class DNA extends GameMode {

		private int startHeight;
		private double height;

		public int getStartHeight() {
			return startHeight;
		}

		public void setStartHeight(int startHeight) {
			this.startHeight = startHeight;
		}

		public double getHeight() {
			return height;
		}

		public void setHeight(double height) {
			this.height = height;
		}

		@Override
		public String getName() {
			return "DNA";
		}
	}

	public static class TSpiele extends GameMode {

		@Override
		public String getName() {
			return "TSpiele";
		}
	}

	public static class Arena extends GameMode {

		private int round;

		public int getRound() {
			return round;
		}

		public void setRound(int round) {
			this.round = round;
		}

		@Override
		public String getName() {
			return "4rena";
		}
	}

	public static class BrainBow extends GameMode {

		private String team;
		private int score;

		public BrainBow() {
			setRespawnable(true);
		}

		public String getTeam() {
			return team;
		}

		public void setTeam(String team) {
			this.team = team;
		}

		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		@Override
		public String getName() {
			return "BrainBow";
		}
	}

	public static class PvP extends GameMode {

		private long winTime;
		private String opponent;
		private OpponentStats opponentStats;
		private int winStreak;
		private String winMessage;
		private PvPTournament tournament;

		public long getWinTime() {
			return winTime;
		}

		public void setWinTime(long winTime) {
			this.winTime = winTime;
		}

		public String getOpponent() {
			return opponent;
		}

		public void setOpponent(String opponent) {
			this.opponent = opponent;
		}

		public OpponentStats getOpponentStats() {
			return opponentStats;
		}

		public void setOpponentStats(OpponentStats opponentStats) {
			this.opponentStats = opponentStats;
		}

		public int getWinStreak() {
			return winStreak;
		}

		public void setWinStreak(int winStreak) {
			this.winStreak = winStreak;
		}

		public String getWinMessage() {
			return winMessage;
		}

		public void setWinMessage(String winMessage) {
			this.winMessage = winMessage;
		}

		public PvPTournament getTournament() {
			return tournament;
		}

		public void setTournament(PvPTournament tournament) {
			this.tournament = tournament;
		}

		@Override
		public String getName() {
			return "1vs1";
		}
	}

	public static class OpponentStats {

		private int gamesTotal;
		private int gamesWon;
		private double killDeathRatio;

		public OpponentStats(int gamesTotal, int gamesWon, double killDeathRatio) {
			this.gamesTotal = gamesTotal;
			this.gamesWon = gamesWon;
			this.killDeathRatio = killDeathRatio;
		}

		public int getGamesTotal() {
			return gamesTotal;
		}

		public void setGamesTotal(int gamesTotal) {
			this.gamesTotal = gamesTotal;
		}

		public int getGamesWon() {
			return gamesWon;
		}

		public void setGamesWon(int gamesWon) {
			this.gamesWon = gamesWon;
		}

		public double getKillDeathRatio() {
			return killDeathRatio;
		}

		public void setKillDeathRatio(double killDeathRatio) {
			this.killDeathRatio = killDeathRatio;
		}
	}

	public static class PvPTournament {

		private String host;
		private int participants;
		private int qualificationRounds;
		private long qualificationDuration;
		private int qualificationFirstTo;
		private int roundFirstTo;
		private long qualificationRoundTime;
		private long roundTime;
		private int currentRound;
		private String kit;
		private int roundWins;
		private int roundLoses;

		private boolean inventoryRequested = false;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getParticipants() {
			return participants;
		}

		public void setParticipants(int participants) {
			this.participants = participants;
		}

		public int getQualificationRounds() {
			return qualificationRounds;
		}

		public void setQualificationRounds(int qualificationRounds) {
			this.qualificationRounds = qualificationRounds;
		}

		public long getQualificationDuration() {
			return qualificationDuration;
		}

		public void setQualificationDuration(long qualificationDuration) {
			this.qualificationDuration = qualificationDuration;
		}

		public int getQualificationFirstTo() {
			return qualificationFirstTo;
		}

		public void setQualificationFirstTo(int qualificationFirstTo) {
			this.qualificationFirstTo = qualificationFirstTo;
		}

		public int getRoundFirstTo() {
			return roundFirstTo;
		}

		public void setRoundFirstTo(int roundFirstTo) {
			this.roundFirstTo = roundFirstTo;
		}

		public long getQualificationRoundTime() {
			return qualificationRoundTime;
		}

		public void setQualificationRoundTime(long qualificationRoundTime) {
			this.qualificationRoundTime = qualificationRoundTime;
		}

		public long getRoundTime() {
			return roundTime;
		}

		public void setRoundTime(long roundTime) {
			this.roundTime = roundTime;
		}

		public int getCurrentRound() {
			return currentRound;
		}

		public void setCurrentRound(int currentRound) {
			this.currentRound = currentRound;
		}

		public String getKit() {
			return kit;
		}

		public void setKit(String kit) {
			this.kit = kit;
		}

		public int getRoundWins() {
			return roundWins;
		}

		public void setRoundWins(int roundWins) {
			this.roundWins = roundWins;
		}

		public int getRoundLoses() {
			return roundLoses;
		}

		public void setRoundLoses(int roundLoses) {
			this.roundLoses = roundLoses;
		}

		public boolean isInventoryRequested() {
			return inventoryRequested;
		}

		public void setInventoryRequested(boolean inventoryRequested) {
			this.inventoryRequested = inventoryRequested;
		}

	}

	public static class TournamentQualificationRound {

		private final int round;
		/**
		 * Team -> won/lost/running
		 */
		private final Map<String, GameResult> teams;

		public TournamentQualificationRound(int round, Map<String, GameResult> teams) {
			this.round = round;
			this.teams = teams;
		}

		public int getRound() {
			return round;
		}

		public Map<String, GameResult> getTeams() {
			return teams;
		}
	}

	public static class TournamentTeam implements Comparable<TournamentTeam> {

		private int wins;
		private String name;

		public TournamentTeam(int wins, String name) {
			this.wins = wins;
			this.name = name;
		}

		public int getWins() {
			return wins;
		}

		public void setWins(int wins) {
			this.wins = wins;
		}

		public String getName() {
			return name;
		}

		@Override
		public int compareTo(TournamentTeam o) {
			return Integer.valueOf(o.wins).compareTo(wins);
		}

		@Override
		public String toString() {
			return "TournamentTeam{" +
					"wins=" + wins +
					", name='" + name + '\'' +
					'}';
		}
	}

	public enum GameResult {
		WON, LOST, RUNNING
	}

	public static class InTime extends GameMode {

		private boolean invincible;
		private long invincibleTimer;
		private long loot;
		private long lootTimer;
		private boolean spawnRegeneration;
		private long spawnRegenerationTimer;

		public InTime() {
			invincible = true;
			invincibleTimer = -1;
			loot = -1;
			lootTimer = -1;
			spawnRegeneration = false;
			spawnRegenerationTimer = -1;
		}

		public boolean isInvincible() {
			return invincible;
		}

		public void setInvincible(boolean invincible) {
			this.invincible = invincible;
		}

		public long getInvincibleTimer() {
			return invincibleTimer;
		}

		public void setInvincibleTimer(long invincibleTimer) {
			this.invincibleTimer = invincibleTimer;
		}

		public long getLoot() {
			return loot;
		}

		public void setLoot(long loot) {
			this.loot = loot;
		}

		public long getLootTimer() {
			return lootTimer;
		}

		public void setLootTimer(long lootTimer) {
			this.lootTimer = lootTimer;
		}

		public boolean isSpawnRegeneration() {
			return spawnRegeneration;
		}

		public void setSpawnRegeneration(boolean spawnRegeneration) {
			this.spawnRegeneration = spawnRegeneration;
		}

		public long getSpawnRegenerationTimer() {
			return spawnRegenerationTimer;
		}

		public void setSpawnRegenerationTimer(long spawnRegenerationTimer) {
			this.spawnRegenerationTimer = spawnRegenerationTimer;
		}

		@Override
		public String getName() {
			return "InTime";
		}
	}

	public static class Arcade extends GameMode {

		private String currentMiniGame;
		private String nextMiniGame;

		public String getCurrentMiniGame() {
			return currentMiniGame;
		}

		public void setCurrentMiniGame(String currentMiniGame) {
			this.currentMiniGame = currentMiniGame;
		}

		public String getNextMiniGame() {
			return nextMiniGame;
		}

		public void setNextMiniGame(String nextMiniGame) {
			this.nextMiniGame = nextMiniGame;
		}

		@Override
		public String getName() {
			return "Arcade";
		}
	}

	public static class Advent extends GameMode {

		private String parkourName;
		private int currentCheckpoint;
		private Vector3f currentCheckpointCoordinates;
		private int fails;
		private long timeGold;
		private long timeSilver;
		private long timeBronze;
		private Vector3f startCoordinates;

		public Advent() {
		}

		public String getParkourName() {
			return parkourName;
		}

		public void setParkourName(String parkourName) {
			this.parkourName = parkourName;
		}

		public void setCurrentCheckpoint(int currentCheckpoint) {
			this.currentCheckpoint = currentCheckpoint;
		}

		public int getCurrentCheckpoint() {
			return currentCheckpoint;
		}

		public Vector3f getCurrentCheckpointCoordinates() {
			return currentCheckpointCoordinates;
		}

		public void setCurrentCheckpointCoordinates(Vector3f currentCheckpointCoordinates) {
			this.currentCheckpointCoordinates = currentCheckpointCoordinates;
		}

		public int getFails() {
			return fails;
		}

		public void setFails(int fails) {
			this.fails = fails;
		}

		public long getTimeGold() {
			return timeGold;
		}

		public void setTimeGold(long timeGold) {
			this.timeGold = timeGold;
		}

		public long getTimeSilver() {
			return timeSilver;
		}

		public void setTimeSilver(long timeSilver) {
			this.timeSilver = timeSilver;
		}

		public long getTimeBronze() {
			return timeBronze;
		}

		public void setTimeBronze(long timeBronze) {
			this.timeBronze = timeBronze;
		}

		public Vector3f getStartCoordinates() {
			return startCoordinates;
		}

		public void setStartCoordinates(Vector3f startCoordinates) {
			this.startCoordinates = startCoordinates;
		}

		@Override
		public String getName() {
			return "Adventskalender";
		}
	}

	public static class JumpWorld extends GameMode {

		private int checkpoints;
		private Vector3f lastCheckpoint;
		private int fails;

		public int getCheckpoints() {
			return checkpoints;
		}

		public void setCheckpoints(int checkpoints) {
			this.checkpoints = checkpoints;
		}

		public Vector3f getLastCheckpoint() {
			return lastCheckpoint;
		}

		public void setLastCheckpoint(Vector3f lastCheckpoint) {
			this.lastCheckpoint = lastCheckpoint;
		}

		public int getFails() {
			return fails;
		}

		public void setFails(int fails) {
			this.fails = fails;
		}

		@Override
		public String getName() {
			return "JumpWorld";
		}
	}

}
