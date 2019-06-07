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

package eu.the5zig.teamspeak.response;

import java.util.*;
import com.google.common.collect.*;
import eu.the5zig.teamspeak.util.*;

public class TeamSpeakResponse
{
    private final String message;
    
    protected TeamSpeakResponse(final String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return this.message.replace("\n", "").replace("\r", "");
    }
    
    public HashMap<String, String> getParsedResponse() {
        return parse(this.getMessage());
    }
    
    public String getRawMessage() {
        return this.message;
    }
    
    public static HashMap<String, String> parse(final String message) {
        final HashMap<String, String> parsedResponse = new HashMap<>();
        for (final String arg : message.split(" ")) {
            final int index = arg.indexOf("=");
            if (index == -1) {
                parsedResponse.put(arg, "");
            }
            else {
                final String key = arg.substring(0, index);
                final String value = EscapeUtil.unescape(arg.substring(index + 1, arg.length()));
                parsedResponse.put(key, value);
            }
        }
        return parsedResponse;
    }
    
    @Override
    public String toString() {
        return "TeamSpeakResponse{message='" + this.getMessage() + '\'' + '}';
    }
}
