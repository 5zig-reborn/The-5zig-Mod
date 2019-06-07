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

package eu.the5zig.mod.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.Utils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.Executors;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class TrayManager implements Runnable {

	private boolean isTraySupported;
	private SystemTray tray;
	private TrayIcon trayIcon;
	private Image minecraftIcon;
	private boolean trayInitialized = false;

	public TrayManager() {
		Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Tray").build()).execute(this);
	}

	@Override
	public void run() {
		isTraySupported = Utils.getPlatform() == Utils.Platform.WINDOWS && SystemTray.isSupported();
		if (isTraySupported) {
			Utils.setUI(UIManager.getSystemLookAndFeelClassName());
			tray = SystemTray.getSystemTray();
			loadMinecraftIcon();
			create();
		} else {
			MinecraftFactory.getClassProxyCallback().getLogger().warn("System Tray is NOT Supported!");
		}
	}

	private void loadMinecraftIcon() {
		if (minecraftIcon != null)
			return;
		try {
			minecraftIcon = ImageIO.read(The5zigMod.getVars().getMinecraftIcon());

			trayIcon = new TrayIcon(minecraftIcon, "The 5zig Mod - " + MinecraftFactory.getVars().getUsername());

			PopupMenu popupMenu = new PopupMenu();
			MenuItem about = new MenuItem("About");
			about.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(null, "This Tray Icon is used by The 5zig Mod to notify you, when you receive a new Message.",
							"Minecraft " + MinecraftFactory.getClassProxyCallback().getMinecraftVersion(), JOptionPane.PLAIN_MESSAGE, null);
				}
			});
			popupMenu.add(about);
			popupMenu.addSeparator();
			MenuItem disable = new MenuItem("Disable");
			disable.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					MinecraftFactory.getClassProxyCallback().disableTray();

					destroy();
				}
			});
			popupMenu.add(disable);
			trayIcon.setPopupMenu(popupMenu);
		} catch (Exception e) {
			MinecraftFactory.getClassProxyCallback().getLogger().warn("Could not load Minecraft Icon!", e);
		}
	}

	public void create() {
		if (!isTraySupported || trayInitialized || !MinecraftFactory.getClassProxyCallback().isTrayEnabled() || minecraftIcon == null)
			return;

		MinecraftFactory.getClassProxyCallback().getLogger().info("Setting up Tray Icon...");
		try {
			tray.add(trayIcon);
			trayInitialized = true;
		} catch (Exception e) {
			MinecraftFactory.getClassProxyCallback().getLogger().warn("Could not Setup Tray Icon!", e);
		}
	}

	public boolean isTraySupported() {
		return isTraySupported;
	}

	public void displayMessage(String title, String message) {
		if (Utils.getPlatform() == Utils.Platform.MAC) {
			try {
				Runtime.getRuntime().exec(new String[] { "osascript", "-e",
						"display notification \"" + message + "\" with title \"" + title + "\"" });
			} catch (IOException e) {
				MinecraftFactory.getClassProxyCallback().getLogger().error("Could not display notification", e);
			}
		} else {
			if (trayInitialized) {
				trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
			}
		}
	}

	public void destroy() {
		if (trayInitialized) {
			tray.remove(trayIcon);
			trayInitialized = false;
		}
	}
}
