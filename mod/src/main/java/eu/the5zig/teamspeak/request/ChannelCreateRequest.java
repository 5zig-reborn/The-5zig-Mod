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

package eu.the5zig.teamspeak.request;

import eu.the5zig.teamspeak.api.*;
import com.google.common.base.*;

public class ChannelCreateRequest extends Request
{
    public ChannelCreateRequest(final String name, final String encodedPassword, final String topic, final String description, final ChannelLifespan lifespan, final boolean defaultChannel, final Channel parentChannel, final Channel orderChannel, final boolean bottomPosition, final int neededTalkPower, final ChannelCodec codec, final int codecQuality, final int maxClients) {
        super("channelcreate", new Parameter[0]);
        this.addParam(Request.value("channel_name", name));
        if (!Strings.isNullOrEmpty(encodedPassword)) {
            this.addParam(Request.value("channel_password", encodedPassword));
        }
        if (!Strings.isNullOrEmpty(topic)) {
            this.addParam(Request.value("channel_topic", topic));
        }
        if (!Strings.isNullOrEmpty(description)) {
            this.addParam(Request.value("channel_description", description));
        }
        if (lifespan != null) {
            switch (lifespan) {
                case SEMI_PERMANENT: {
                    this.addParam(Request.value("channel_flag_semi_permanent", true));
                    break;
                }
                case PERMANENT: {
                    this.addParam(Request.value("channel_flag_permanent", true));
                    break;
                }
            }
        }
        if (defaultChannel) {
            this.addParam(Request.value("channel_flag_default", true));
        }
        this.addParam(Request.value("cpid", (parentChannel == null) ? 0 : parentChannel.getId()));
        if (!bottomPosition) {
            this.addParam(Request.value("channel_order", (orderChannel == null) ? 0 : orderChannel.getId()));
        }
        if (neededTalkPower != 0) {
            this.addParam(Request.value("channel_needed_talk_power", neededTalkPower));
        }
        if (codec != null) {
            this.addParam(Request.value("channel_codec", codec.getId()));
        }
        this.addParam(Request.value("channel_codec_quality", codecQuality));
        this.addParam(Request.value("channel_maxclients", maxClients));
        if (maxClients == -1) {
            this.addParam(Request.value("channel_flag_maxclients_unlimited", true));
        }
        else {
            this.addParam(Request.value("channel_flag_maxclients_unlimited", false));
        }
    }
}
