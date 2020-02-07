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
import eu.the5zig.mod.Version;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class RewardsCache {
    private static Cache<String, Reward> cachedRewards;

    private static final String PATREON_URL = "https://secure.5zigreborn.eu/rewards/";

    private static boolean operate = true;

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
        if(!operate) return null;

        Reward reward = cachedRewards.getIfPresent(uuid);
        if(reward == null) {
            downloadReward(uuid);
        }
        return reward == null ? null : reward.getDisplayString();
    }

    private static void downloadReward(String uuid) {
        if(!operate) return;

        Reward reward = new Reward((String) null);
        loadPlayerIntoCache(uuid, reward);
        new Thread(() -> {
            try {
                URL url = new URL(PATREON_URL + uuid);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.addRequestProperty("User-Agent", "5zig/" + Version.VERSION);
                if(conn.getResponseCode() != 404) {
                    reward.setDisplayString(IOUtils.toString(conn.getInputStream(), StandardCharsets.UTF_8));
                }
                conn.disconnect();
            }
            catch (IOException ex) {
                The5zigMod.logger.error("Couldn't fetch patreon info.");
            }
        }).start();
    }
}
