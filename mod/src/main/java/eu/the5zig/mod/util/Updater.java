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

package eu.the5zig.mod.util;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.Version;
import eu.the5zig.mod.asm.Transformer;
import eu.the5zig.mod.installer.ProcessCallback;
import eu.the5zig.mod.installer.UpdateInstaller;
import eu.the5zig.util.Utils;
import eu.the5zig.util.io.FileUtils;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.ZipFile;

public class Updater implements Runnable {

	private Updater() {
	}

	public static void check() {
		new Thread(new Updater(), "Update Thread").start();
	}

	@Override
	public void run() {
//		final UpdateType updateType = The5zigMod.getConfig().getEnum("autoUpdate", UpdateType.class);
//		if (updateType == UpdateType.NEVER)
//			return;
		try {
//			String latestVersion = IOUtils.toString(
//					new URL("http://5zig.net/api/update?mc=" + Version.MCVERSION + "&same-version=" + (updateType == UpdateType.SAME_VERSION ? 1 : 0)).toURI());
			String latestVersion = IOUtils.toString(
					new URL("http://5zig.net/api/update?mc=" + Version.MCVERSION + "&same-version=0").toURI());
			Download download = The5zigMod.gson.fromJson(latestVersion, Download.class);
			if (!"DEV".equals(Version.VERSION) && !Version.VERSION.contains("_b") && Utils.versionCompare(Version.VERSION, download.name) < 0 && Utils.versionCompare(Version.MCVERSION, download.mc) <= 0) {
				The5zigMod.logger.info("Found new update of The 5zig Mod (v" + download.name + ")!");
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("update.1"));
				The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.YELLOW + I18n.translate("update.2"));

//				boolean sameMinecraftVersion = Version.MCVERSION.equals(download.mc);
//				if (updateType == UpdateType.SAME_VERSION && !sameMinecraftVersion) {
//					return;
//				}

//				if (!sameMinecraftVersion) {
//					File minecraftDir = new File("").getAbsoluteFile();
//					File versionsDir = new File(minecraftDir, "versions");
//					File minecraftJarDirectory = new File(versionsDir, download.mc);
//					File minecraftJarFile = new File(minecraftJarDirectory, download.mc + ".jar");
//					if (!minecraftJarFile.exists()) {
//						sameMinecraftVersion = true;
//						The5zigMod.logger.info("New Minecraft version doesn't exist yet! Trying to download most recent mod file for Minecraft " + Version.MCVERSION);
//						latestVersion = IOUtils.toString(new URL("http://5zig.net/api/update?mc=" + Version.MCVERSION + "&same-version=1").toURI());
//						download = The5zigMod.gson.fromJson(latestVersion, Download.class);
//						if (download.name == null || Utils.versionCompare(Version.VERSION, download.name) >= 0) {
//							The5zigMod.logger.info("No new download available for this Minecraft version!");
//							return;
//						}
//					}
//				}
//
//				File modLibraryDirectory = new File(InstallerUtils.getMinecraftDirectory(),
//						"libraries" + File.separator + "eu" + File.separator + "the5zig" + File.separator + "The5zigMod" + File.separator + download.mc + "_" + download.name);
//				if (modLibraryDirectory.exists()) {
//					The5zigMod.logger.info("Update already has been downloaded! Aborting...");
//					return;
//				}
//
//				downloadLatest(download.url, download.md5, download.name, download.mc, sameMinecraftVersion);
			} else {
				The5zigMod.logger.info("The 5zig Mod is up to date!");
			}
		} catch (Exception e) {
			The5zigMod.logger.error("Could not check for latest 5zig Mod Version!", e);
		}
	}

	private void downloadLatest(String url, String md5, String version, String mcVersion, boolean sameMinecraftVersion) throws Exception {
		File minecraftDir = The5zigMod.getVars().getMinecraftDataDirectory();

		if (Transformer.FORGE && sameMinecraftVersion) {
			ZipFile zipFile = EnvironmentUtils.getModFile();
			File modsFile = zipFile != null ? new File(zipFile.getName()) : new File(new File(minecraftDir, "mods"), "The5zigMod-" + mcVersion + "_" + version + ".jar");
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(modsFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			String downloadedMD5 = FileUtils.md5(modsFile);
			if (!downloadedMD5.equals(md5)) {
				org.apache.commons.io.FileUtils.deleteQuietly(modsFile);
				throw new RuntimeException("Invalid Downloaded File MD5!");
			}
		} else {
			File libraryDir = new File(minecraftDir, "libraries" + File.separator + "eu" + File.separator + "the5zig" + File.separator + "The5zigMod" + File.separator + mcVersion + "_" +
					version);
			if (!libraryDir.exists() && !libraryDir.mkdirs())
				throw new IOException("Could not create directory at " + libraryDir);

			File libraryFile = new File(libraryDir, "The5zigMod-" + mcVersion + "_" + version + ".jar");
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(libraryFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			String downloadedMD5 = FileUtils.md5(libraryFile);
			if (!downloadedMD5.equals(md5)) {
				org.apache.commons.io.FileUtils.deleteQuietly(libraryDir);
				throw new RuntimeException("Invalid Downloaded File MD5!");
			}

			UpdateInstaller installer = new UpdateInstaller(version, mcVersion, sameMinecraftVersion ? Version.VERSION : null, libraryFile);
			installer.install(new ProcessCallback() {
				@Override
				public void progress(float percentage) {
				}

				@Override
				public void message(String message) {
					The5zigMod.logger.debug(message);
				}
			});
		}

		The5zigMod.logger.info("Downloaded and installed the latest version of The 5zig Mod!");
		The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.GREEN + I18n.translate("new_update.1"));
		The5zigMod.getOverlayMessage().displayMessageAndSplit(ChatColor.GREEN + I18n.translate("new_update.2"));
	}

	private static class Download {

		public String name;
		public String mc;
		public String url;
		public String md5;

	}

	public enum UpdateType {

		ALWAYS, SAME_VERSION, NEVER

	}

}
