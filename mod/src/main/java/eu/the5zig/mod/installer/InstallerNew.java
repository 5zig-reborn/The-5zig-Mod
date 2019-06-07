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

import eu.the5zig.mod.util.InputStreamReaderThread;
import eu.the5zig.util.Callback;
import eu.the5zig.util.Utils;
import eu.the5zig.util.io.NotifiableFileCopier;
import eu.the5zig.util.io.NotifiableJarCopier;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Base64;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class InstallerNew {

	protected final String modVersion;
	protected final String minecraftVersion;

	protected File minecraftDirectory;
	protected File versionsDirectory;
	protected File librariesDirectory;

	protected File minecraftJarDirectory;
	protected File minecraftJarFile;
	protected File minecraftJsonFile;

	protected File modJarDirectory;
	protected File modJarFile;
	protected File modJsonFile;
	protected File modLibraryDirectory;
	protected File modLibraryFile;
	protected File otherModsLibraryDirectory;
	protected File otherModsLibraryFile;

	protected File sourceFile;
	protected File[] otherMods;

	public InstallerNew(File installDirectory, String modVersion, String minecraftVersion) throws MinecraftNotFoundException {
		this.modVersion = modVersion;
		this.minecraftVersion = minecraftVersion;

		minecraftDirectory = installDirectory;
		versionsDirectory = new File(minecraftDirectory, "versions");
		librariesDirectory = new File(minecraftDirectory, "libraries");
		if (!versionsDirectory.exists() || !librariesDirectory.exists())
			throw new MinecraftNotFoundException();

		minecraftJarDirectory = new File(versionsDirectory, minecraftVersion);
		minecraftJarFile = new File(minecraftJarDirectory, minecraftVersion + ".jar");
		minecraftJsonFile = new File(minecraftJarDirectory, minecraftVersion + ".json");
		if (!minecraftJarFile.exists() || !minecraftJsonFile.exists())
			throw new MinecraftNotFoundException();

		modJarDirectory = new File(versionsDirectory, getVersionName());
		modJarFile = new File(modJarDirectory, getVersionName() + ".jar");
		modJsonFile = new File(modJarDirectory, getVersionName() + ".json");
		modLibraryDirectory = new File(librariesDirectory, "eu" + File.separator + "the5zig" + File.separator + "The5zigMod" + File.separator + minecraftVersion + "_" + modVersion);
		modLibraryFile = new File(modLibraryDirectory, "The5zigMod-" + minecraftVersion + "_" + modVersion + ".jar");
		otherModsLibraryDirectory = new File(librariesDirectory, "eu" + File.separator + "the5zig" + File.separator + "Mods" + File.separator + minecraftVersion + "_" + modVersion);
		otherModsLibraryFile = new File(otherModsLibraryDirectory, "Mods-" + minecraftVersion + "_" + modVersion + ".jar");

		sourceFile = Utils.getRunningJar();
	}

	public void setOtherMods(File[] otherMods) {
		this.otherMods = otherMods;
	}

	public void install() throws Exception {
		install(null);
	}

	public void install(ProcessCallback callback) throws Exception {
		if (otherMods == null || otherMods.length == 0) {
			Stage.COPY_MINECRAFT.setStartPercentage(0.6f);
			Stage.APPLY_OPTIFINE_PATCHES.setStartPercentage(0.95f);
			Stage.COPY_OTHER_MODS.setStartPercentage(0.95f);
		}

		extractSourcesToLib(callback);
		copyMinecraftVersion(callback);
		copyOtherModsIntoMinecraftJar(callback);
		updateLauncherJson(callback);

		if (callback != null)
			callback.message("Done.");
		if (callback != null)
			callback.progress(1);
	}

	protected void extractSourcesToLib(final ProcessCallback callback) throws IOException {
		if (!modLibraryDirectory.exists() && !modLibraryDirectory.mkdirs())
			throw new IOException("Could not create a new Mod Library File!");
		if (sourceFile == null)
			throw new RuntimeException("Could not find locating of running Jar!");
		if (sourceFile.equals(modLibraryFile))
			return;

		if (callback != null)
			callback.setStage(Stage.EXTRACT_SOURCES);
		NotifiableFileCopier.copy(new Callback<Float>() {
			@Override
			public void call(Float f) {
				if (callback != null)
					callback.setProgress(f);
			}
		}, new File[]{sourceFile}, modLibraryFile);
	}

	protected void copyMinecraftVersion(final ProcessCallback callback) throws IOException, ParseException {
		if (!modJarDirectory.exists() && !modJarDirectory.mkdirs())
			throw new IOException("Could not create a new Minecraft Version Directory!");

		if (callback != null)
			callback.setStage(Stage.COPY_MINECRAFT);
		if (!minecraftJarFile.equals(modJarFile)) {
			NotifiableFileCopier.copy(new Callback<Float>() {
				@Override
				public void call(Float f) {
					if (callback != null)
						callback.setProgress(f);
				}
			}, new File[]{minecraftJarFile}, modJarFile);
			NotifiableFileCopier.copy(new Callback<Float>() {
				@Override
				public void call(Float f) {
					if (callback != null)
						callback.setProgress(f);
				}
			}, new File[]{minecraftJsonFile}, modJsonFile);
		}

		updateMinecraftJson();
	}

	protected void copyOtherModsIntoMinecraftJar(final ProcessCallback callback) throws IOException {
		if (otherMods == null || otherMods.length == 0)
			return;

		if ((!otherModsLibraryDirectory.exists() && !otherModsLibraryDirectory.mkdirs()) || (!otherModsLibraryFile.exists() && !otherModsLibraryFile.createNewFile()))
			throw new IOException("Could not create mod directory or file!");

		// fix optifine installation.
		if (callback != null)
			callback.setStage(Stage.APPLY_OPTIFINE_PATCHES);
		applyOptifinePatch(callback);

		if (callback != null)
			callback.setStage(Stage.COPY_OTHER_MODS);
		NotifiableJarCopier.copy(otherMods, otherModsLibraryFile, new Callback<Float>() {
			@Override
			public void call(Float f) {
				if (callback != null)
					callback.setProgress(f);
			}
		});
	}

	protected void applyOptifinePatch(final ProcessCallback callback) {
		for (int i = 0, otherModsLength = otherMods.length; i < otherModsLength; i++) {
			File otherMod = otherMods[i];
			try {
				JarFile jarFile = null;
				ZipEntry entry;
				try {
					jarFile = new JarFile(otherMod);
					entry = jarFile.getEntry("optifine/Patcher.class");
				} finally {
					if (jarFile != null) {
						jarFile.close();
					}
				}

				if (entry != null) {
					callback.log("Found Optifine!");

					File temp = File.createTempFile(otherMod.getName(), null);
					temp.deleteOnExit();

					callback.log("Starting Optifine patch Process...");
					ProcessBuilder builder = new ProcessBuilder(Utils.getJavaExecutable(), "-cp", otherMod.getAbsolutePath(), "optifine.Patcher", minecraftJarFile.getAbsolutePath(),
							otherMod.getAbsolutePath(), temp.getAbsolutePath());
					Process process = builder.start();
					InputStreamReaderThread input = new InputStreamReaderThread("Input", process.getInputStream(), callback);
					input.start();
					InputStreamReaderThread error = new InputStreamReaderThread("Error", process.getErrorStream(), callback);
					error.start();
					int exitCode = process.waitFor();
					callback.log("Optifine process exited with code " + exitCode);
					if (exitCode != 0) {
						throw new RuntimeException("Could not patch Optifine file! Exit code=" + exitCode);
					}
					otherMods[i] = temp;
					input.join();
					error.join();
					return;
				}
			} catch (Exception e) {
				throw new RuntimeException("Could not patch Optifine file!", e);
			}
		}
	}

	protected void updateMinecraftJson() throws ParseException, IOException {
		String json = Utils.loadJson(modJsonFile);

		JSONParser jp = new JSONParser();

		JSONObject root = (JSONObject) jp.parse(json);
		root.put("id", getVersionName());
		root.put("mainClass", "net.minecraft.launchwrapper.Launch");
		if (root.containsKey("arguments")) {
//		if (Utils.versionCompare(minecraftVersion, "1.13") >= 0) {
			JSONObject args = (JSONObject) root.get("arguments");
			JSONArray game = (JSONArray) args.get("game");
			game.add("--tweakClass");
			game.add("eu.the5zig.mod.asm.ClassTweaker");
			args.put("game", game);
		} else {
			root.put("minecraftArguments", root.get("minecraftArguments") + " --tweakClass eu.the5zig.mod.asm.ClassTweaker");
		}

		JSONArray libraries = (JSONArray) root.get("libraries");
		for (Iterator iterator = libraries.iterator(); iterator.hasNext(); ) {
			Object o = iterator.next();
			JSONObject library = (JSONObject) o;
			String name = library.get("name").toString();
			if (name.startsWith("net.minecraft:launchwrapper:") || name.startsWith("eu.the5zig:The5zigMod:") || name.startsWith("eu.the5zig:mods:")) {
				iterator.remove();
			}
		}

		JSONObject launchWrapper = new JSONObject();
		launchWrapper.put("name", "net.minecraft:launchwrapper:1.7");
		libraries.add(0, launchWrapper);
		JSONObject mod = new JSONObject();
		mod.put("name", "eu.the5zig:The5zigMod:" + minecraftVersion + "_" + modVersion);
		libraries.add(0, mod);
		if (otherMods != null && otherMods.length > 0) {
			JSONObject mods2 = new JSONObject();
			mods2.put("name", "eu.the5zig:Mods:" + minecraftVersion + "_" + modVersion);
			libraries.add(1, mods2);
		}

		root.put("libraries", libraries);

		writeToFile(root, modJsonFile);
	}

	protected void updateLauncherJson(ProcessCallback callback) throws ParseException, IOException {
		if (callback != null)
			callback.setStage(Stage.UPDATE_LAUNCHER_FILES);

		File fileJson = new File(minecraftDirectory, "launcher_profiles.json");
		String json = Utils.loadJson(fileJson);
		JSONParser jp = new JSONParser();

		JSONObject root = (JSONObject) jp.parse(json);
		JSONObject profiles = (JSONObject) root.get("profiles");
		JSONObject prof = (JSONObject) profiles.get(getVersionName());
		if (prof == null) {
			prof = new JSONObject();
			prof.put("name", getVersionName());
			profiles.put(getVersionName(), prof);
		}
		prof.put("lastVersionId", getVersionName());
		prof.put("icon", getBase64Icon());
		prof.put("type", "custom");
		root.put("selectedProfile", getVersionName());

		writeToFile(root, fileJson);
	}

	private String getBase64Icon() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream is = InstallerNew.class.getResourceAsStream("/images/5zig-icon.png");
		int read;
		byte[] data = new byte[1024];
		try {
			while ((read = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Diamond_Block";
		}
		return "data:image/png;base64," + Base64.getEncoder().encodeToString(buffer.toByteArray()); // TODO: Backwards Java compatibility
	}

	protected void writeToFile(JSONObject object, File file) throws IOException {
		FileWriter fwJson = null;
		try {
			fwJson = new FileWriter(file);
			JSONWriter writer = new JSONWriter(fwJson);
			writer.writeObject(object);
			fwJson.flush();
		} finally {
			if (fwJson != null) {
				try {
					fwJson.close();
				} catch (IOException ignored) {
				}
			}
		}
	}

	public String getVersionName() {
		return getVersionName(minecraftVersion);
	}

	public static String getVersionName(String minecraftVersion) {
		return minecraftVersion + " - 5zig Mod";
	}

}
