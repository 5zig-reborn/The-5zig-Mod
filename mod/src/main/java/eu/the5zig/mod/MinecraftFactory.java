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

package eu.the5zig.mod;

import eu.the5zig.mod.asm.ReflectionNames;
import eu.the5zig.mod.asm.Transformer;
import eu.the5zig.mod.util.ClassProxyCallback;
import eu.the5zig.mod.util.IVariables;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraft.realms.RealmsSharedConstants;

import java.lang.reflect.Field;

public class MinecraftFactory {

	private static final IVariables variables;
	private static ClassProxyCallback classProxyCallback;

	static {
		try {
			String version;
			Field vf;
			String reflName = null;
			try {
				vf = RealmsSharedConstants.class.getField("VERSION_STRING");

				version = (String) vf.get(null);


				switch (version) {
					case "1.8.9":
						reflName = "ReflectionNames189";
						break;
					case "1.12.2":
						reflName = "ReflectionNames1122";
						break;
					case "1.13.2":
						reflName = "ReflectionNames1132";
						break;
				}
			} catch (Exception e) {
				version = "1.14.4";
				reflName = "ReflectionNames1144";
			}
			LogWrapper.info("Minecraft Version: " + version);

			try {
				LogWrapper.finest("Checking for Forge");
				Class.forName("net.minecraftforge.client.GuiIngameForge");
				LogWrapper.info("Forge detected!");
				Transformer.FORGE = true;
			} catch (Exception ignored) {
				LogWrapper.info("Forge not found!");
			}

			try {
				Transformer.REFLECTION = (ReflectionNames) Class.forName("eu.the5zig.mod.asm." + reflName).newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			variables = (IVariables) Class.forName("Variables").newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static IVariables getVars() {
		return variables;
	}

	public static void setClassProxyCallback(ClassProxyCallback classProxyCallback) {
		MinecraftFactory.classProxyCallback = classProxyCallback;
	}

	public static ClassProxyCallback getClassProxyCallback() {
		return classProxyCallback;
	}

}
