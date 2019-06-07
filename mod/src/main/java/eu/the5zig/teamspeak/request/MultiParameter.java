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

public class MultiParameter extends Parameter
{
    private Parameter[][] parameters;
    
    public MultiParameter(final Parameter[][] parameters) {
        this.parameters = parameters;
    }
    
    @Override
    public String serialize() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, parametersLength = this.parameters.length; i < parametersLength; ++i) {
            final Parameter[] parameter = this.parameters[i];
            if (i != 0) {
                stringBuilder.append("|");
            }
            for (int j = 0, parameterLength = parameter.length; j < parameterLength; ++j) {
                final Parameter parameter2 = parameter[j];
                if (j != 0) {
                    stringBuilder.append(" ");
                }
                stringBuilder.append(parameter2.serialize());
            }
        }
        return stringBuilder.toString();
    }
}
