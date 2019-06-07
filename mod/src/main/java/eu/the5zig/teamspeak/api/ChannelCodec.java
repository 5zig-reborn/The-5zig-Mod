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

package eu.the5zig.teamspeak.api;

public enum ChannelCodec
{
    SPEEX_NARROWBAND(0), 
    SPEEX_WIDEBAND(1), 
    SPEEX_ULTRA_WIDEBAND(2), 
    CELT_MONO(3), 
    OPUS_VOICE(4), 
    OPUS_MUSIC(5);
    
    private int id;
    
    private ChannelCodec(final int id) {
        this.id = id;
    }
    
    public int getId() {
        return this.id;
    }
    
    public static ChannelCodec byId(final int id) {
        for (final ChannelCodec channelCodec : values()) {
            if (channelCodec.getId() == id) {
                return channelCodec;
            }
        }
        return ChannelCodec.SPEEX_NARROWBAND;
    }
}
