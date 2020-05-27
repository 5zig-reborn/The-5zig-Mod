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

package eu.the5zig.mod.modules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.modules.items.Dummy;
import eu.the5zig.mod.modules.items.SimpleText;
import eu.the5zig.mod.modules.items.player.*;
import eu.the5zig.mod.modules.items.server.*;
import eu.the5zig.mod.modules.items.server.bergwerk.DuelRespawn;
import eu.the5zig.mod.modules.items.server.bergwerk.DuelTeam;
import eu.the5zig.mod.modules.items.server.bergwerk.DuelTeleportMessage;
import eu.the5zig.mod.modules.items.server.cytooxien.MarioPartyFields;
import eu.the5zig.mod.modules.items.server.cytooxien.MarioPartyFirstPlayer;
import eu.the5zig.mod.modules.items.server.cytooxien.MarioPartyPlace;
import eu.the5zig.mod.modules.items.server.gommehd.*;
import eu.the5zig.mod.modules.items.server.hypixel.*;
import eu.the5zig.mod.modules.items.server.octc.ArenaMap;
import eu.the5zig.mod.modules.items.server.octc.ArenaMode;
import eu.the5zig.mod.modules.items.server.octc.ArenaTeam;
import eu.the5zig.mod.modules.items.server.playminity.JumpLeagueCheckpoints;
import eu.the5zig.mod.modules.items.server.playminity.JumpLeagueFails;
import eu.the5zig.mod.modules.items.server.playminity.JumpLeagueLives;
import eu.the5zig.mod.modules.items.server.simplehg.SimpleHGFeast;
import eu.the5zig.mod.modules.items.server.simplehg.SimpleHGKit;
import eu.the5zig.mod.modules.items.server.simplehg.SimpleHGMiniFeast;
import eu.the5zig.mod.modules.items.server.timolia.*;
import eu.the5zig.mod.modules.items.server.venicraft.MineathlonDiscipline;
import eu.the5zig.mod.modules.items.server.venicraft.MineathlonRound;
import eu.the5zig.mod.modules.items.system.*;

import java.util.HashMap;
import java.util.List;

public class ModuleItemRegistry {

	private final List<RegisteredItem> REGISTERED_ITEMS = Lists.newArrayList();
	private final HashMap<String, RegisteredItem> BY_KEY = Maps.newHashMap();
	private final HashMap<Class<? extends AbstractModuleItem>, RegisteredItem> BY_ITEM = Maps.newHashMap();
	private final List<String> ITEM_CATEGORIES = Lists.newArrayList();

	public ModuleItemRegistry() {
		registerItem("TEXT", SimpleText.class, Category.GENERAL);
		registerItem("FPS", FPS.class, Category.GENERAL);
		registerItem("CPS", CPS.class, Category.GENERAL);
		registerItem("AFK_TIME", AFKTime.class, Category.GENERAL);
		registerItem("COORDINATES", Coordinates.class, Category.GENERAL);
		registerItem("CHUNK_COORDINATES", ChunkCoordinates.class, Category.GENERAL);
		registerItem("DEATH_COORDINATES", DeathCoordinates.class, Category.GENERAL);
		registerItem("DIRECTION", Direction.class, Category.GENERAL);
		registerItem("PITCH", Pitch.class, Category.GENERAL);
		registerItem("BIOME", Biome.class, Category.GENERAL);
		registerItem("ENTITIES", EntityCount.class, Category.GENERAL);
		registerItem("POTIONS", Potions.class, Category.GENERAL);
		registerItem("LIGHT_LEVEL", Light.class, Category.GENERAL);
		registerItem("SPEED", Speed.class, Category.GENERAL);
		registerItem("TARGET_BLOCK_COORDINATES", TargetBlockCoordinates.class, Category.GENERAL);
		registerItem("TARGET_BLOCK_NAME", TargetBlockName.class, Category.GENERAL);
		registerItem("DAMAGE_RESISTANCE", DamageResistance.class, Category.GENERAL);
		registerItem("COMBAT_RANGE", CombatRange.class, Category.GENERAL);
		registerItem("UPTIME", Uptime.class, Category.GENERAL);

		registerItem("MAIN_HAND", MainHand.class, Category.EQUIP);
		registerItem("OFF_HAND", OffHand.class, Category.EQUIP);
		registerItem("HELMET", Helmet.class, Category.EQUIP);
		registerItem("CHESTPLATE", Chestplate.class, Category.EQUIP);
		registerItem("LEGGINGS", Leggings.class, Category.EQUIP);
		registerItem("BOOTS", Boots.class, Category.EQUIP);
		registerItem("ARROWS", Arrows.class, Category.EQUIP);
		registerItem("SOUPS", Soups.class, Category.EQUIP);

		registerItem("IP", ServerIP.class, Category.SERVER_GENERAL);
		registerItem("PLAYERS", ServerPlayers.class, Category.SERVER_GENERAL);
		registerItem("PING", ServerPing.class, Category.SERVER_GENERAL);
		registerItem("LOBBY", Lobby.class, Category.SERVER_GENERAL);
		registerItem("COUNTDOWN", Countdown.class, Category.SERVER_GENERAL);
		registerItem("KILLS", Kills.class, Category.SERVER_GENERAL);
		registerItem("KILLSTREAK", Killstreak.class, Category.SERVER_GENERAL);
		registerItem("DEATHS", Deaths.class, Category.SERVER_GENERAL);
		registerItem("WIN_MESSAGE", WinMessage.class, Category.SERVER_GENERAL);
		registerItem("ONLINE_FRIENDS", OnlineFriends.class, Category.SERVER_GENERAL);
		registerItem("PARTY_MEMBERS", PartyMembers.class, Category.SERVER_GENERAL);
		registerItem("NICKNAME", Nickname.class, Category.SERVER_GENERAL);

		registerItem("TIMOLIA_PVP_WINSTREAK", PVPWinStreak.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_PVP_OPPONENT", PVPOpponent.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_PVP_OPPONENT_GAMES", PVPOpponentGames.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_PVP_OPPONENT_WINS", PVPOpponentWins.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_PVP_OPPONENT_KDR", PVPOpponentKDR.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_TOURNAMENT_PARTICIPANTS", TournamentParticipants.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_TOURNAMENT_ROUND", TournamentRound.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_TOURNAMENT_FIRST_TO", TournamentFirstTo.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_TOURNAMENT_QUALIFICATION", TournamentQualification.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_TOURNAMENT_ROUND_SCORE", TournamentRoundScore.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_DNA_HEIGHT", DNAHeight.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ARCADE_CURRENT_MINIGAME", ArcadeCurrentMinigame.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ARCADE_NEXT_MINIGAME", ArcadeNextMinigame.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ARENA_ROUND", ArenaRound.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_BRAINBOW_TEAM", BrainbowTeam.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_BRAINBOW_SCORE", BrainbowScore.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ADVENT_MEDAL", AdventMedal.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ADVENT_PARKOUR", AdventParkour.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ADVENT_CHECKPOINT", AdventCheckpoint.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_ADVENT_FAILS", AdventFails.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_INTIME_INVINCIBILITY", InTimeInvincibility.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_INTIME_REGENERATION", InTimeRegeneration.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_INTIME_LOOT", InTimeLoot.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_INTIME_INVINCIBILITY_MESSAGE", InTimeInvincibilityMessage.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_JUMPWORLD_FAILS", JumpWorldFails.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_JUMPWORLD_CHECKPOINT", JumpWorldCheckpoint.class, Category.SERVER_TIMOLIA);
		registerItem("TIMOLIA_JUMPWORLD_LAST_CHECKPOINT", JumpWorldLastCheckpoint.class, Category.SERVER_TIMOLIA);

		registerItem("GOMMEHD_SG_DEATHMATCH", SGDeathmatch.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_SG_DEATHMATCH_MESSAGE", SGDeathmatchMessage.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_BEDWARS_TEAM", BedWarsTeam.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_BEDWARS_RESPAWN", BedWarsRespawn.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_BEDWARS_GOLD", BedWarsGold.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_BEDWARS_BEDS", BedWarsBeds.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_ENDERGAMES_KIT", EnderGamesKit.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_ENDERGAMES_COINS", EnderGamesCoins.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_SKYWARS_COINS", SkyWarsCoins.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_SKYWARS_TEAM", SkyWarsTeam.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_SKYWARS_KIT", SkyWarsKit.class, Category.SERVER_GOMMEHD);
		registerItem("GOMMEHD_RAGEMODE_EMERALDS", RageModeEmeralds.class, Category.SERVER_GOMMEHD);

		registerItem("PLAYMINITY_JUMPLEAGUE_CHECKPOINTS", JumpLeagueCheckpoints.class, Category.SERVER_PLAYMINITY);
		registerItem("PLAYMINITY_JUMPLEAGUE_FAILS", JumpLeagueFails.class, Category.SERVER_PLAYMINITY);
		registerItem("PLAYMINITY_JUMPLEAGUE_LIVES", JumpLeagueLives.class, Category.SERVER_PLAYMINITY);

		registerItem("BERGWERK_DUEL_TEAM", DuelTeam.class, Category.SERVER_BERGWERK);
		registerItem("BERGWERK_DUEL_RESPAWN", DuelRespawn.class, Category.SERVER_BERGWERK);
		registerItem("BERGWERK_DUEL_TELEPORT_MESSAGE", DuelTeleportMessage.class, Category.SERVER_BERGWERK);

		registerItem("HYPIXEL_PAINTBALL_TEAM", PaintballTeam.class, Category.SERVER_HYPIXEL);
		registerItem("HYPIXEL_BLITZ_KIT", BlitzKit.class, Category.SERVER_HYPIXEL);
		registerItem("HYPIXEL_BLITZ_STAR", BlitzStar.class, Category.SERVER_HYPIXEL);
		registerItem("HYPIXEL_BLITZ_DEATHMATCH", BlitzDeathmatch.class, Category.SERVER_HYPIXEL);
		registerItem("HYPIXEL_BLITZ_STAR_MESSAGE", BlitzStarMessage.class, Category.SERVER_HYPIXEL);
		registerItem("HYPIXEL_BLITZ_DEATHMATCH_MESSAGE", BlitzDeathmatchMessage.class, Category.SERVER_HYPIXEL);

		registerItem("VENICRAFT_MINEATHLON_DISCIPLINE", MineathlonDiscipline.class, Category.SERVER_VENICRAFT);
		registerItem("VENICRAFT_MINEATHLON_ROUND", MineathlonRound.class, Category.SERVER_VENICRAFT);

		registerItem("CYTOOXIEN_MARIOPARTY_PLACE", MarioPartyPlace.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_MARIOPARTY_REMAINING_FIELDS", MarioPartyFields.class, Category.SERVER_CYTOOXIEN);
//		registerItem("CYTOOXIEN_MARIOPARTY_CURRENT_MINIGAME", MarioPartyCurrentMinigame.class, Category.SERVER_CYTOOXIEN);
//		registerItem("CYTOOXIEN_MARIOPARTY_NEXT_MINIGAME", MarioPartyNextMinigame.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_MARIOPARTY_FIRST_PLAYER", MarioPartyFirstPlayer.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_MARIOPARTY_FIRST_SECOND_THIRD", eu.the5zig.mod.modules.items.server.cytooxien.MarioPartyFirstSecondThird.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_MARIOPARTY_MINIGAMES", eu.the5zig.mod.modules.items.server.cytooxien.MarioPartyMinigames.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_BW_BEDS", eu.the5zig.mod.modules.items.server.cytooxien.BedwarsBeds.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_BW_STATS_KILLS", eu.the5zig.mod.modules.items.server.cytooxien.BedwarsStatsKills.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_BW_TEAM", eu.the5zig.mod.modules.items.server.cytooxien.BedwarsTeam.class, Category.SERVER_CYTOOXIEN);
		registerItem("CYTOOXIEN_BW_CAN_RESPAWN", eu.the5zig.mod.modules.items.server.cytooxien.BedwarsCanRespawn.class, Category.SERVER_CYTOOXIEN);

		registerItem("SIMPLEHG_KIT", SimpleHGKit.class, Category.SERVER_SIMPLEHG);
		registerItem("SIMPLEHG_FEAST", SimpleHGFeast.class, Category.SERVER_SIMPLEHG);
		registerItem("SIMPLEHG_MINIFEAST", SimpleHGMiniFeast.class, Category.SERVER_SIMPLEHG);

		registerItem("OCTC_MODE", ArenaMode.class, Category.SERVER_OCC);
		registerItem("OCTC_MAP", ArenaMap.class, Category.SERVER_OCC);
		registerItem("OCTC_TEAM", ArenaTeam.class, Category.SERVER_OCC);

		registerItem("TIME", Time.class, Category.SYSTEM);
		registerItem("DATE", Date.class, Category.SYSTEM);
		registerItem("TIMER", Timer.class, Category.SYSTEM);
		registerItem("MEMORY", Memory.class, Category.SYSTEM);
		registerItem("BATTERY", Battery.class, Category.SYSTEM);
		registerItem("SPOTIFY", Spotify.class, Category.SYSTEM);
		registerItem("ITUNES", ITunes.class, Category.SYSTEM);
		registerItem("ILOVERADIO", ILoveRadio.class, Category.SYSTEM);
		registerItem("EINSLIVE", EinsLive.class, Category.SYSTEM);
		registerItem("TEAMSPEAK", TeamSpeakItem.class, Category.SYSTEM);
		registerItem("COMMAND", CommandOutput.class, Category.SYSTEM);

		registerItem("DUMMY", Dummy.class);
		registerItem("COORDINATES_CLIPBOARD", CoordinatesClipboard.class, Category.OTHER);
		registerItem("CUSTOM_SERVER", CustomServer.class, Category.OTHER);
	}

	public void registerItem(String key, Class<? extends AbstractModuleItem> clazz) {
		registerItem(key, clazz, Category.OTHER);
	}

	public void registerItem(String key, Class<? extends AbstractModuleItem> clazz, Category category) {
		registerItem(key, clazz, category.getName());
	}

	public void registerItem(String key, Class<? extends AbstractModuleItem> clazz, String category) {
		if (BY_KEY.containsKey(key))
			throw new IllegalArgumentException("Item with key \"" + key + "\" already has been registered!");

		RegisteredItem registeredItem = new RegisteredItem(key, clazz, category);
		REGISTERED_ITEMS.add(registeredItem);
		BY_KEY.put(key, registeredItem);
		BY_ITEM.put(clazz, registeredItem);
		if (!ITEM_CATEGORIES.contains(category)) {
			ITEM_CATEGORIES.add(category);
		}
	}

	public void unregisterItem(Class<? extends AbstractModuleItem> clazz) {
		RegisteredItem registeredItem = BY_ITEM.remove(clazz);
		if (registeredItem != null) {
			BY_KEY.remove(registeredItem.getKey());
			REGISTERED_ITEMS.remove(registeredItem);
		}
	}

	public AbstractModuleItem create(RegisteredItem item) throws Exception {
		Class<? extends AbstractModuleItem> clazz = item.getClazz();
		AbstractModuleItem moduleItem = clazz.newInstance();
		moduleItem.properties = new ModuleItemPropertiesImpl(moduleItem);
		moduleItem.registerSettings();
		return moduleItem;
	}

	public RegisteredItem byItem(Class<? extends AbstractModuleItem> clazz) {
		return BY_ITEM.get(clazz);
	}

	public RegisteredItem byKey(String key) {
		return BY_KEY.get(key);
	}

	public List<RegisteredItem> getRegisteredItems() {
		return REGISTERED_ITEMS;
	}

	public List<String> getItemCategories() {
		return ITEM_CATEGORIES;
	}
}
