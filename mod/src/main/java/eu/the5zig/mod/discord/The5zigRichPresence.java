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

import eu.the5zig.mod.I18n;

import java.time.OffsetDateTime;

public class The5zigRichPresence {
	
    private String details, state, smallImage, smallImageText, largeImage, largeImageText;
    private OffsetDateTime startTimestamp, endTimestamp;

    public static The5zigRichPresence getDefault() {
        The5zigRichPresence presence = new The5zigRichPresence();
        presence.details = "Minecraft";
        presence.state = I18n.translate("discord.main_menu");
        presence.largeImage = "unknown_server";
        presence.largeImageText = I18n.translate("discord.main_menu");
        presence.smallImage = "default";
        presence.smallImageText = "5zig Reborn";
        presence.startTimestamp = OffsetDateTime.now();
        presence.endTimestamp = null;
        return presence;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }
    
    public void setSmallImageText(String smallImageText) {
    	this.smallImageText = smallImageText;
    }
    
    public void setLargeImage(String largeImage) {
    	this.largeImage = largeImage;
    }
    
    public void setLargeImageText(String largeImageText) {
    	this.largeImageText = largeImageText;
    }
    
    public void setStartTimestamp(OffsetDateTime startTimestamp) {
    	this.startTimestamp = startTimestamp;
    }
    
    public void setEndTimestamp(OffsetDateTime endTimestamp) {
    	this.endTimestamp = endTimestamp;
    }
    

    RichPresence build() {
        RichPresence.Builder builder = new RichPresence.Builder();
        return builder.setDetails(details)
                .setState(state)
                .setSmallImage(smallImage, smallImageText)
                .setStartTimestamp(startTimestamp)
                .setEndTimestamp(endTimestamp)
                .setLargeImage(largeImage, largeImageText)
                .build();
    }
}
