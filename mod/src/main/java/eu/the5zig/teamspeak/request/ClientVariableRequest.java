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

public class ClientVariableRequest extends Request
{
    private static final Parameter[] PARAMS;
    
    public ClientVariableRequest(final int... clientIds) {
        super("clientvariable", new Parameter[] { build(clientIds) });
    }
    
    private static MultiParameter build(final int... clientIds) {
        if (clientIds.length == 0) {
            throw new IllegalArgumentException("Must provide at least one client id!");
        }
        final Parameter[][] result = new Parameter[clientIds.length][ClientVariableRequest.PARAMS.length + 1];
        for (int i = 0; i < clientIds.length; ++i) {
            final Parameter[] parameters = new Parameter[ClientVariableRequest.PARAMS.length + 1];
            parameters[0] = Request.value("clid", clientIds[i]);
            System.arraycopy(ClientVariableRequest.PARAMS, 0, parameters, 1, ClientVariableRequest.PARAMS.length);
            result[i] = parameters;
        }
        return new MultiParameter(result);
    }
    
    static {
        PARAMS = new Parameter[] { Request.array("client_unique_identifier", "client_nickname", "client_input_muted", "client_output_muted", "client_outputonly_muted", "client_input_hardware", "client_output_hardware", "client_meta_data", "client_is_recording", "client_database_id", "client_channel_group_id", "client_servergroups", "client_away", "client_away_message", "client_type", "client_flag_avatar", "client_talk_power", "client_talk_request", "client_talk_request_msg", "client_description", "client_is_talker", "client_is_priority_speaker", "client_unread_messages", "client_nickname_phonetic", "client_needed_serverquery_view_power", "client_icon_id", "client_is_channel_commander", "client_country", "client_channel_group_inherited_channel_id", "client_flag_talking", "client_is_muted", "client_volume_modificator", "client_version", "client_platform", "client_login_name", "client_created", "client_lastconnected", "client_totalconnections", "client_month_bytes_uploaded", "client_month_bytes_downloaded", "client_total_bytes_uploaded", "client_total_bytes_downloaded", "client_input_deactivated") };
    }
}
