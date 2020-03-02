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

package eu.the5zig.mod.discord;

import com.jagrosh.discordipc.entities.RichPresence;

import java.time.OffsetDateTime;

public class The5zigRichPresence {
    private String text, state, smallImage;

    static The5zigRichPresence getDefault() {
        The5zigRichPresence presence = new The5zigRichPresence();
        presence.text = "Menu";
        presence.state = "Menu";
        return presence;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    RichPresence build() {
        RichPresence.Builder builder = new RichPresence.Builder();
        return builder.setDetails(text)
                .setState(state)
                .setSmallImage(smallImage)
                .setStartTimestamp(OffsetDateTime.now())
                .setLargeImage(DiscordRPCManager.IMG_LOGO)
                .build();
    }
}
