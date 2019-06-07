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

import com.google.common.collect.*;
import java.util.*;

public class Request
{
    private String command;
    private List<Parameter> params;
    
    public Request(final String command, final Parameter... params) {
        this.params = new ArrayList<>();
        this.command = command;
        Collections.addAll(this.params, params);
    }
    
    protected void addParam(final Parameter parameter) {
        this.params.add(parameter);
    }
    
    protected static ArrayParameter array(final String... array) {
        return new ArrayParameter(array);
    }
    
    protected static OptionParameter option(final String option) {
        return new OptionParameter(option);
    }
    
    protected static ValueParameter value(final String key, final Object value) {
        return new ValueParameter(key, value);
    }
    
    public List<Parameter> getParams() {
        return this.params;
    }
    
    @Override
    public String toString() {
        String result = this.command;
        for (final Parameter parameter : this.params) {
            result = result + " " + parameter.serialize();
        }
        return result;
    }
}
