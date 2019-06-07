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

public class ServerVariableRequest extends Request
{
    private static final Parameter[] PARAMS;
    
    public ServerVariableRequest() {
        super("servervariable", ServerVariableRequest.PARAMS);
    }
    
    static {
        PARAMS = new Parameter[] { Request.array("virtualserver_name", "virtualserver_unique_identifier", "virtualserver_platform", "virtualserver_version", "virtualserver_created", "virtualserver_codec_encryption_mode", "virtualserver_default_server_group", "virtualserver_default_channel_group", "virtualserver_hostbanner_url", "virtualserver_hostbanner_gfx_url", "virtualserver_hostbanner_gfx_interval", "virtualserver_priority_speaker_dimm_modificator", "virtualserver_id", "virtualserver_hostbutton_tooltip", "virtualserver_hostbutton_url", "virtualserver_hostbutton_gfx_url", "virtualserver_name_phonetic", "virtualserver_icon_id", "virtualserver_ip", "virtualserver_ask_for_privilegekey", "virtualserver_hostbanner_mode") };
    }
}
