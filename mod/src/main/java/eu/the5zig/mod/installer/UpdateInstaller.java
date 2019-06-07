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

package eu.the5zig.mod.installer;

import java.io.File;

public class UpdateInstaller extends InstallerNew {

	public UpdateInstaller(String modVersion, String minecraftVersion, String currentModVersion, File sourceFile) throws MinecraftNotFoundException {
		super(new File("").getAbsoluteFile(), modVersion, minecraftVersion);

		if (currentModVersion != null) {
			// same Minecraft version
			File oldModJarDirectory = new File(versionsDirectory, getVersionName(currentModVersion));
			File oldModJarFile = new File(oldModJarDirectory, getVersionName(currentModVersion) + ".jar");
			File oldModJsonFile = new File(oldModJarDirectory, getVersionName(currentModVersion) + ".json");

			if (oldModJarFile.exists() && oldModJsonFile.exists()) {
				minecraftJarDirectory = oldModJarDirectory;
				minecraftJarFile = oldModJarFile;
			}

			File oldOtherModsLibraryDirectory = new File(librariesDirectory, "eu" + File.separator + "the5zig" + File.separator + "Mods" + File.separator + minecraftVersion + "_" +
					currentModVersion);
			File oldOtherModsLibraryFile = new File(oldOtherModsLibraryDirectory, "Mods-" + minecraftVersion + "_" + currentModVersion + ".jar");
			if (oldOtherModsLibraryFile.exists()) {
				otherMods = new File[] {oldOtherModsLibraryFile};
			}
		}
		this.sourceFile = sourceFile;
	}

	@Override
	protected void applyOptifinePatch(ProcessCallback callback) {
		// Don't apply any patches.
	}
}
