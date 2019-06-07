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

public class ClientUpdateRequest extends Request
{
    public ClientUpdateRequest(final Ident ident, final Object value) {
        super("clientupdate", new Parameter[] { Request.value(ident.getName(), value) });
    }
    
    public enum Ident
    {
        NICKNAME("client_nickname"), 
        AWAY("client_away"), 
        AWAY_MESSAGE("away_message"), 
        INPUT_MUTED("client_input_muted"), 
        OUTPUT_MUTED("client_output_muted"), 
        INPUT_DEACTIVATED("client_input_deactivated"), 
        CHANNEL_COMMANDER("client_is_channel_commander"), 
        NICKNAME_PHONETIC("client_nickname_phonetic"), 
        AVATAR("client_flag_avatar"), 
        META_DATA("client_meta_data"), 
        DEFAULT_TOKEN("client_default_token");
        
        private String name;
        
        private Ident(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
