/*
 * Copyright (c) 2019-2020 5zig Reborn
 * Copyright (c) 2015-2019 5zig
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

package eu.the5zig.mod.crashreport;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.util.EnvironmentUtils;
import eu.the5zig.util.Utils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CrashHopper {

	private static final File HOPPER_JAR_FILE = new File(The5zigMod.getModDirectory(), "crash-hopper.jar");

	private static boolean extracted = false;

	private CrashHopper() {
	}

	public static void init() {
		InputStream resourceInputStream = null;
		OutputStream outputStream = null;
		try {
			if (HOPPER_JAR_FILE.exists()) {
				// Validate
				resourceInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("crash-hopper.jar");
				if (resourceInputStream == null) {
					throw new RuntimeException("Could not find original Jar File of Crash Hopper!");
				}
				byte[] validMD5 = DigestUtils.md5(resourceInputStream);

				InputStream jarInputStream = null;
				try {
					byte[] jarMD5 = DigestUtils.md5(jarInputStream = new FileInputStream(HOPPER_JAR_FILE));

					if (Arrays.equals(validMD5, jarMD5)) {
						// Files are the same, no need to extract the original file.
						extracted = true;
						return;
					}

				} finally {
					IOUtils.closeQuietly(jarInputStream);
					IOUtils.closeQuietly(resourceInputStream);
				}
			}
			resourceInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("crash-hopper.jar");
			if (resourceInputStream == null) {
				throw new RuntimeException("Could not find original Jar File of Crash Hopper!");
			}

			outputStream = new FileOutputStream(HOPPER_JAR_FILE);

			// Extract Crash Hopper.
			IOUtils.copy(resourceInputStream, outputStream);

			The5zigMod.logger.info("Extracted new Crash Hopper!");

			extracted = true;
		} catch (Exception e) {
			The5zigMod.logger.warn("Could not extract Crash Hopper!", e);
		} finally {
			IOUtils.closeQuietly(resourceInputStream);
			IOUtils.closeQuietly(outputStream);
		}
	}

	public static void launch(Throwable cause, File crashFile) {
		if (!extracted) {
			return;
		}

		ZipFile zipFile = EnvironmentUtils.getModFile();
		if (zipFile == null) {
			return;
		}
		The5zigMod.logger.info("Using zip file " + zipFile.getName());

		boolean containsModFile = false;
		Throwable throwable = cause;
		do {
			for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.isDirectory())
						continue;

					String entryName = entry.getName().replace('/', '.').replace(".class", "");
					if (stackTraceElement.getClassName().equals(entryName)) {
						containsModFile = true;
						The5zigMod.logger.info("Found " + stackTraceElement.getClassName() + " in stacktrace (" + entryName + "). Has this crash been caused by the 5zig mod?");
						break;
					}
				}
			}
		} while ((throwable = throwable.getCause()) != null);

		if (!containsModFile) {
			return;
		}

		The5zigMod.logger.info("Launching crash hopper...");
		try {
			boolean autoReport = true;
			if (The5zigMod.getAPI() != null && The5zigMod.getAPI().getPluginManager() != null && !The5zigMod.getAPI().getPluginManager().getPlugins().isEmpty()) {
				autoReport = false;
			} else if (The5zigMod.getConfig() != null) {
				autoReport = The5zigMod.getConfig().getBool("reportCrashes");
			}
			new ProcessBuilder(Utils.getJavaExecutable(), "-jar", HOPPER_JAR_FILE.getAbsolutePath(), String.valueOf(autoReport), crashFile.getAbsolutePath(),
					new File(The5zigMod.getModDirectory().getParentFile(), "logs/latest.log").getAbsolutePath()).start();
		} catch (IOException ignored) {
		}
	}

}
