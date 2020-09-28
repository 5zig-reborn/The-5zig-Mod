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

package eu.the5zig.mod.installer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

public class ModListPanel extends JPanel implements ActionListener {

	private static final Pattern OPTIFINE_NAMING_PATTERN = Pattern.compile("OptiFine_(\\d+\\.\\d+(\\.\\d+)?)_HD_U_.\\d+\\.(jar|zip)");

	private final Frame frame;

	private Set<File> mods = new HashSet<File>();

	private JList jModList;
	private JButton addMod;
	private JLabel addModLbl;
	private JButton removeMod;
	private JLabel removeModLbl;
	private JButton install;
	private JLabel installlbl;
	private JLabel descriptionlbl;

	public ModListPanel(Frame frame) {
		this.frame = frame;
		init();
	}

	private void init() {
		setSize(500, 350);
		setLayout(null);

		JLabel title = new JLabel("The 5zig Mod b" + Frame.modVersion, SwingConstants.CENTER);
		title.setBounds(0, 10, 500, 35);
		title.setFont(new Font("Verdana", Font.BOLD, 26));
		add(title);

		JLabel subTitle = new JLabel("Install additional mods", SwingConstants.CENTER);
		subTitle.setBounds(0, 36, 500, 35);
		subTitle.setFont(new Font("Verdana", Font.BOLD, 20));
		add(subTitle);

		descriptionlbl = new JLabel("<html>Click on \"Add...\" and select additional<br>Mods that should be installed as well.</html>", SwingConstants.CENTER);
		descriptionlbl.setBounds(0, 120, 500, 50);
		descriptionlbl.setFont(new Font("Helvetica", Font.BOLD, 18));
		add(descriptionlbl);

		jModList = new JList();
		jModList.setFixedCellHeight(30);
		jModList.setFont(new Font("Verdana", Font.PLAIN, 16));
		jModList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					removeMod.setEnabled(true);
				}
			}
		});
		JScrollPane listScroller = new JScrollPane(jModList);
		listScroller.setBounds(0, 74, 494, 160);
		add(listScroller);


		addMod = new JButton(Images.installImage2);
		addMod.setRolloverIcon(Images.installHoverImage2);
		addMod.setRolloverSelectedIcon(Images.installHoverImage2);
		addMod.setPressedIcon(Images.installHoverImage2);
		addMod.setSelectedIcon(Images.installHoverImage2);
		addMod.setDisabledIcon(Images.installDisabledImage2);
		addMod.setBounds(30, 260, 100, 40);
		addMod.setBorderPainted(false);
		addMod.setFocusPainted(false);
		addMod.setContentAreaFilled(false);
		addMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				descriptionlbl.setVisible(false);
				JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + File.separator + "Downloads");
				chooser.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("ZIP and JAR Archives", "zip", "jar");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == 0) {
					for (File file : chooser.getSelectedFiles()) {
						boolean shouldAddMod = true;
						shouldAddMod &= checkOptifineVersion(file);
						shouldAddMod &= checkForgeMod(file);
						if (shouldAddMod) {
							mods.add(file);
						}
					}
					jModList.setListData(mods.toArray());
					removeMod.setEnabled(jModList.getSelectedValue() != null);
				}
			}
		});
		addModLbl = new JLabel("Add", SwingConstants.CENTER);
		addModLbl.setBounds(30, 260, 100, 40);
		addModLbl.setFont(new Font("Verdana", Font.PLAIN, 20));
		addModLbl.setForeground(Color.WHITE);
		add(addModLbl);
		add(addMod);

		removeMod = new JButton(Images.installImage2);
		removeMod.setRolloverIcon(Images.installHoverImage2);
		removeMod.setRolloverSelectedIcon(Images.installHoverImage2);
		removeMod.setPressedIcon(Images.installHoverImage2);
		removeMod.setSelectedIcon(Images.installHoverImage2);
		removeMod.setDisabledIcon(Images.installDisabledImage2);
		removeMod.setBounds(140, 260, 100, 40);
		removeMod.setBorderPainted(false);
		removeMod.setFocusPainted(false);
		removeMod.setContentAreaFilled(false);
		removeMod.setEnabled(false);
		removeMod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] selectedValues = jModList.getSelectedValues();
				if (selectedValues != null) {
					mods.removeAll(Arrays.asList(selectedValues));
					jModList.setListData(mods.toArray());
					removeMod.setEnabled(jModList.getSelectedValue() != null);
				}
			}
		});
		removeModLbl = new JLabel("Remove", SwingConstants.CENTER);
		removeModLbl.setBounds(140, 260, 100, 40);
		removeModLbl.setFont(new Font("Verdana", Font.PLAIN, 20));
		removeModLbl.setForeground(Color.WHITE);
		add(removeModLbl);
		add(removeMod);

		install = new JButton(Images.installImage);
		install.setRolloverIcon(Images.installHoverImage);
		install.setRolloverSelectedIcon(Images.installHoverImage);
		install.setPressedIcon(Images.installHoverImage);
		install.setSelectedIcon(Images.installHoverImage);
		install.setDisabledIcon(Images.installDisabledImage);
		install.setBounds(260, 260, 200, 40);
		install.setBorderPainted(false);
		install.setFocusPainted(false);
		install.setContentAreaFilled(false);
		install.addActionListener(this);
		installlbl = new JLabel("Install", SwingConstants.CENTER);
		installlbl.setBounds(260, 260, 200, 40);
		installlbl.setFont(new Font("Verdana", Font.PLAIN, 20));
		installlbl.setForeground(Color.WHITE);
		add(installlbl);
		add(install);

		JLabel backgroundImage = new JLabel(Images.backgroundImage);
		backgroundImage.setBounds(0, 0, 500, 350);
		add(backgroundImage);
	}

	private boolean checkOptifineVersion(File file) {
		Matcher matcher = OPTIFINE_NAMING_PATTERN.matcher(file.getName());
		if (matcher.matches()) {
			String optifineVersion = matcher.group(1);
			if (!Frame.minecraftVersion.equals(optifineVersion) && !(Frame.minecraftVersion + ".0").equals(optifineVersion)) {
				int confirmationResult = JOptionPane.showConfirmDialog(null, "Warning, mod version mismatch!\n"
						+ "It seems like you are trying to add a version of\nOptiFine (" + file.getName() + ") that uses a\ndifferent version than the 5zig mod (" + optifineVersion + " vs " + Frame.minecraftVersion + ")!\n"
						+ "The installation may not be successful if you continue.\nAre you sure you want to add this file?", "The 5zig Mod b" + Frame.modVersion, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				return confirmationResult == JOptionPane.YES_OPTION;
			}
		}
		return true;
	}

	private boolean checkForgeMod(File file) {
		try {
			ZipFile jarFile = new ZipFile(file);
			if (jarFile.getEntry("mcmod.info") != null) {
				int confirmationResult = JOptionPane.showConfirmDialog(null, "Warning, forge mod found! (" + file.getName() + ")\n"
						+ "It seems like you are trying to add a forge mod to your installation!\n"
						+ "This installer does *not* use forge and the installation may not be successful if you continue.\n"
						+ "If you want to install the 5zig mod using forge, copy all mods (including this installer) into the forge mods folder.\n"
						+ "Are you sure you want to add this file and continue the installation *without* forge?", "The 5zig Mod b" + Frame.modVersion, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				return confirmationResult == JOptionPane.YES_OPTION;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.setContentPane(new InstallPanel(frame, mods));
	}

}
