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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainPanel extends JPanel implements ActionListener {

	private final Frame frame;

	private JLabel text;
	private JButton changeDir;
	private JLabel changeDirlbl;
	private JButton install;
	private JLabel installlbl;
	private JButton cancel;
	private JLabel cancellbl;
	private JProgressBar progressBar;

	public MainPanel(Frame frame) {
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

		JLabel subTitle = new JLabel("for Minecraft " + Frame.minecraftVersion, SwingConstants.CENTER);
		subTitle.setBounds(0, 36, 500, 35);
		subTitle.setFont(new Font("Verdana", Font.BOLD, 20));
		add(subTitle);

		text = new JLabel("", SwingConstants.CENTER);
		text.setBounds(40, 50, 420, 170);
		text.setFont(new Font("Helvetica", Font.BOLD, 18));
		text.setText("<html>Welcome to the 5zig Mod installer!<br><br>Click on \"Install\" to install the mod! Select optionally other mods to install them together" +
				".<br>More information on <u>https://www.5zigreborn.eu</u></html>");
		add(text);

		changeDir = new JButton(Images.installBox);
		changeDir.setBounds(30, 220, 430, 30);
		changeDir.setBorderPainted(false);
		changeDir.setFocusPainted(false);
		changeDir.setContentAreaFilled(false);
		changeDirlbl = new JLabel("Installing to " + Frame.installDirectory.getAbsolutePath(), SwingConstants.CENTER);
		changeDirlbl.setBounds(35, 220, 420, 30);
		changeDirlbl.setFont(new Font("Arial", Font.PLAIN, 16));
		changeDirlbl.setForeground(Color.lightGray);
		changeDirlbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				changeDirlbl.setText("Click to change...");
				changeDirlbl.setFont(new Font("Arial", Font.BOLD, 16));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				changeDirlbl.setText("Installing to " + Frame.installDirectory.getAbsolutePath());
				changeDirlbl.setFont(new Font("Arial", Font.PLAIN, 16));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser(Frame.installDirectory);
				chooser.setDialogTitle("Select Minecraft Installation Directory");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					Frame.installDirectory = chooser.getSelectedFile();
					changeDirlbl.setText("Installing to " + Frame.installDirectory.getAbsolutePath());
					changeDirlbl.setFont(new Font("Arial", Font.PLAIN, 16));
				}
			}
		});
		add(changeDirlbl);
		add(changeDir);

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

		cancel = new JButton(Images.installImage);
		cancel.setRolloverIcon(Images.installHoverImage);
		cancel.setRolloverSelectedIcon(Images.installHoverImage);
		cancel.setPressedIcon(Images.installHoverImage);
		cancel.setSelectedIcon(Images.installHoverImage);
		cancel.setDisabledIcon(Images.installDisabledImage);
		cancel.setBounds(30, 260, 200, 40);
		cancel.setBorderPainted(false);
		cancel.setFocusPainted(false);
		cancel.setContentAreaFilled(false);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		cancellbl = new JLabel("Cancel", SwingConstants.CENTER);
		cancellbl.setBounds(30, 260, 200, 40);
		cancellbl.setFont(new Font("Verdana", Font.PLAIN, 20));
		cancellbl.setForeground(Color.WHITE);
		add(cancellbl);
		add(cancel);

		progressBar = new JProgressBar();
		progressBar.setBounds(20, 210, 450, 20);
		progressBar.setVisible(false);
		add(progressBar);

		JLabel backgroundImage = new JLabel(Images.backgroundImage);
		backgroundImage.setBounds(0, 0, 500, 350);
		add(backgroundImage);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.setContentPane(new ModListPanel(frame));
	}
}
