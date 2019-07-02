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

package eu.the5zig.mod.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.MinecraftFactory;

import java.util.List;
import java.util.Locale;

public class ChatHighlighting {
    public static boolean shouldHighlight(String text) {
        System.out.println(text);
        List<String> highlightWords;

        String chatSearchText = MinecraftFactory.getClassProxyCallback().getChatSearchText();
        if (!Strings.isNullOrEmpty(chatSearchText)) {
            highlightWords = ImmutableList.of(chatSearchText);
        } else {
            highlightWords = MinecraftFactory.getClassProxyCallback().getHighlightWords();
        }
        if (highlightWords.isEmpty()) {
            return false;
        }

        for (String search : highlightWords) {
            search = search.replace("%player%", MinecraftFactory.getVars().getGameProfile().getName()).toLowerCase(Locale.ROOT);
            if(text.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}