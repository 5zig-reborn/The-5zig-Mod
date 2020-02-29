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

import java.util.concurrent.TimeUnit;

public class RewardsCache {
    private static Cache<String, Reward> cachedRewards;
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
        return reward == null ? null : reward.getDisplayString();
    }

    public static void putReward(String uuid, String reward) {
        Reward result = new Reward(reward);
        loadPlayerIntoCache(uuid, result);
    }
}
