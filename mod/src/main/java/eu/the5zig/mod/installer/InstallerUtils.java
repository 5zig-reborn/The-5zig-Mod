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

import eu.the5zig.util.Utils;
import eu.the5zig.util.io.FileUtils;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;

public class InstallerUtils {

	private static final StringBuilder log = new StringBuilder();

	public static File getMinecraftDirectory() {
		String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (Utils.getPlatform()) {
			case LINUX:
			case SOLARIS:
				workingDirectory = new File(userHome, ".minecraft/");
				break;
			case WINDOWS:
				String applicationData = System.getenv("APPDATA");
				if (applicationData != null) {
					workingDirectory = new File(applicationData, ".minecraft/");
				} else {
					workingDirectory = new File(userHome, ".minecraft/");
				}
				break;
			case MAC:
				workingDirectory = new File(userHome, "Library/Application Support/minecraft");
				break;
			default:
				workingDirectory = new File(userHome, "minecraft/");
		}
		return workingDirectory;
	}

	public static void checkMD5(File thisFile, String checkPath) {
		try {
			String md5 = FileUtils.md5(thisFile);
			log("Calculated MD5: " + md5);
			String url = "http://5zig.net/md5/" + checkPath;
			String checksum = Utils.downloadFile(url, 2500);
			if (checksum == null) {
				log("Could not download checksum! Continuing installation at own risk!");
				return;
			}
			log("Checksum: " + checksum);
			if (!md5.equals(checksum)) {
				log("Calculated MD5-String does not match checksum from " + url + "!");
				int installWithMods = JOptionPane.showOptionDialog(null,
						"The File you downloaded could not be verified as secure!\nThis means that this Modification has been probably modified by another person.\nDo you still want to" +
								" " + "continue installation?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Images.iconImage,
						new String[]{"Yes, I understand the risks.", "No, Exit Installer."}, "default");
				if (installWithMods == JOptionPane.NO_OPTION) {
					System.exit(0);
				}
			} else {
				log("Checksum does match calculated md5 String!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String text) {
		System.out.println(text);
		log.append(text).append(Utils.lineSeparator());
	}

	public static void exitWithException(Throwable throwable, String modVersion, String minecraftVersion, File[] selectedMods) {
		throwable.printStackTrace();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		throwable.printStackTrace(ps);
		String content = baos.toString();
		JTextArea textArea = new JTextArea("The 5zig Mod v" + modVersion, 20, 100);
		textArea.setText("An error occurred while installing The 5zig Mod!" + Utils.lineSeparator() + "Please contact the5zig@gmail.com and paste the contents of this window!" +
				Utils.lineSeparator(3) + content + seperator() + "User Info:" + Utils.lineSeparator() + "Version:\t" + minecraftVersion + "_" + modVersion + Utils.lineSeparator() +
				"Selected mods:\t" + Arrays.toString(selectedMods) + Utils.lineSeparator() + "OS Name:\t" + Utils.getOSName() + Utils.lineSeparator() + "Java Version:\t" + Utils.getJava() + Utils.lineSeparator() + "User Home:\t" + System.getProperty("user.home") +
				seperator() + Utils.lineSeparator() + "Installer Log:" + Utils.lineSeparator() + log.toString());
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setCaretPosition(0);
		textArea.setEditable(false);

		JOptionPane.showMessageDialog(null, new JScrollPane(textArea), "The 5zig Mod", JOptionPane.PLAIN_MESSAGE);
		System.exit(1);
	}

	private static String seperator() {
		StringBuilder sb = new StringBuilder(Utils.lineSeparator());
		for (int i = 0; i < 100; i++) {
			sb.append("=");
		}
		return sb.append(Utils.lineSeparator()).toString();
	}
}
