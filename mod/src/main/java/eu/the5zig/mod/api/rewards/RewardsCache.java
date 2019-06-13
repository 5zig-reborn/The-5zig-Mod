/*
 * Copyright (c) 2019 5zig Reborn
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

package eu.the5zig.mod.api.rewards;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RewardsCache {
    private static Cache<String, Reward> cachedRewards;

    // Currently used for staff rewards
    private static HashMap<String, Reward> permanentRewards = new HashMap<>();

    private static final String PERM_REWARDS_URL = "https://rocco.dev/5zig/rewards.json";
    private static final String PATREON_URL = "https://api.5zigreborn.eu/patreon/fetch?uuid=";

    static {
        cachedRewards = CacheBuilder.newBuilder()
                .maximumSize(2000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .build();
    }

    private static void loadPlayerIntoCache(String uuid, Reward reward) {
        cachedRewards.put(uuid, reward);
    }

    public static String getRewardString(String uuid) {
        Reward reward = cachedRewards.getIfPresent(uuid);
        if(reward == null) {
            Reward permanent = permanentRewards.get(uuid);
            if(permanent == null) {
                downloadReward(uuid);
            }
            else {
                loadPlayerIntoCache(uuid, permanent);
            }
        }
        return reward == null ? null : reward.getDisplayString();
    }

    private static void downloadReward(String uuid) {
        Reward reward = new Reward((String) null);
        loadPlayerIntoCache(uuid, reward);
        new Thread(() -> {
            try {
                URL url = new URL(PATREON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == 302) {
                    reward.setType(RewardType.PATREON);
                }
            }
            catch (IOException ex) {
                The5zigMod.logger.error("Couldn't fetch patreon info.");
            }
        }).start();
    }

    public static void downloadPermanentRewards() {
        try {
            String result = Utils.downloadFile(PERM_REWARDS_URL);

            JSONObject json = (JSONObject) new JSONParser().parse(result);
            HashMap<String, Reward> map = new HashMap<>();

            json.keySet().forEach(k -> {
                String value = (String) json.get(k);
                Reward reward = new Reward(value);
                map.put((String)k, reward);
            });

            permanentRewards = map;

        } catch (Exception e) {
            The5zigMod.logger.error("Couldn't fetch rewards. Offline?");
            e.printStackTrace();
        }
    }
}
