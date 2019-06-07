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

public class ChannelEditRequest extends Request
{
    public ChannelEditRequest(final Channel channel, final String name, final String encodedPassword, final String topic, final String description, final ChannelLifespan lifespan, final boolean defaultChannel, final Channel parentChannel, final Channel orderChannel, final boolean bottomPosition, final int neededTalkPower, final ChannelCodec codec, final int codecQuality, final int maxClients) {
        super("channeledit", new Parameter[0]);
        this.addParam(Request.value("cid", channel.getId()));
        if (!Objects.equal((Object)channel.getName(), (Object)name)) {
            this.addParam(Request.value("channel_name", name));
        }
        if (!Strings.isNullOrEmpty(encodedPassword)) {
            this.addParam(Request.value("channel_password", encodedPassword));
        }
        else if (channel.requiresPassword()) {
            this.addParam(Request.value("channel_password", ""));
        }
        if (!Objects.equal((Object)(Strings.isNullOrEmpty(channel.getTopic()) ? null : channel.getTopic()), (Object)(Strings.isNullOrEmpty(topic) ? null : topic))) {
            this.addParam(Request.value("channel_topic", (topic == null) ? "" : topic));
        }
        if (!Objects.equal((Object)(Strings.isNullOrEmpty(channel.getDescription()) ? null : channel.getDescription()), (Object)(Strings.isNullOrEmpty(description) ? null : description))) {
            this.addParam(Request.value("channel_description", (description == null) ? "" : description));
        }
        if (lifespan != null) {
            switch (lifespan) {
                case SEMI_PERMANENT: {
                    if (!channel.isSemiPermanent()) {
                        this.addParam(Request.value("channel_flag_semi_permanent", true));
                    }
                    if (channel.isPermanent()) {
                        this.addParam(Request.value("channel_flag_permanent", false));
                        break;
                    }
                    break;
                }
                case PERMANENT: {
                    if (!channel.isPermanent()) {
                        this.addParam(Request.value("channel_flag_permanent", true));
                    }
                    if (channel.isSemiPermanent()) {
                        this.addParam(Request.value("channel_flag_semi_permanent", false));
                        break;
                    }
                    break;
                }
                default: {
                    if (channel.isSemiPermanent()) {
                        this.addParam(Request.value("channel_flag_semi_permanent", false));
                    }
                    if (channel.isPermanent()) {
                        this.addParam(Request.value("channel_flag_permanent", false));
                        break;
                    }
                    break;
                }
            }
        }
        if (channel.isDefault() != defaultChannel) {
            this.addParam(Request.value("channel_flag_default", defaultChannel));
        }
        if (!Objects.equal((Object)channel.getParent(), (Object)parentChannel)) {
            this.addParam(Request.value("cpid", (parentChannel == null) ? 0 : parentChannel.getId()));
        }
        if (!bottomPosition && !Objects.equal((Object)channel.getAbove(), (Object)orderChannel)) {
            this.addParam(Request.value("channel_order", (orderChannel == null) ? 0 : orderChannel.getId()));
        }
        if (channel.getNeededTalkPower() != neededTalkPower) {
            this.addParam(Request.value("channel_needed_talk_power", neededTalkPower));
        }
        if (channel.getCodec() != codec) {
            this.addParam(Request.value("channel_codec", (codec == null) ? ChannelCodec.OPUS_VOICE.getId() : codec.getId()));
        }
        if (channel.getCodecQuality() != codecQuality) {
            this.addParam(Request.value("channel_codec_quality", codecQuality));
        }
        if (channel.getMaxClients() != maxClients) {
            this.addParam(Request.value("channel_maxclients", maxClients));
            if (maxClients == -1) {
                this.addParam(Request.value("channel_flag_maxclients_unlimited", true));
            }
            else {
                this.addParam(Request.value("channel_flag_maxclients_unlimited", false));
            }
        }
    }
    
    public ChannelEditRequest(final int channelId, final int orderId) {
        super("channeledit", new Parameter[0]);
        this.addParam(Request.value("cid", channelId));
        this.addParam(Request.value("channel_order", orderId));
    }
}
