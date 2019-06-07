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

import javax.swing.*;

public class Images {

	public static ImageIcon iconImage;
	public static ImageIcon backgroundImage;
	public static ImageIcon installImage;
	public static ImageIcon installHoverImage;
	public static ImageIcon installDisabledImage;
	public static ImageIcon installImage2;
	public static ImageIcon installHoverImage2;
	public static ImageIcon installDisabledImage2;
	public static ImageIcon installBox;

	public static void load() {
		InstallerUtils.log("Loading Images...");
		iconImage = new ImageIcon(Images.class.getResource("/images/5zig-icon.png"));
		backgroundImage = new ImageIcon(Images.class.getResource("/images/background.jpg"));
		installImage = new ImageIcon(Images.class.getResource("/images/install.png"));
		installHoverImage = new ImageIcon(Images.class.getResource("/images/install_hover.png"));
		installDisabledImage = new ImageIcon(Images.class.getResource("/images/install_disabled.png"));
		installImage2 = new ImageIcon(Images.class.getResource("/images/install2.png"));
		installHoverImage2 = new ImageIcon(Images.class.getResource("/images/install_hover2.png"));
		installDisabledImage2 = new ImageIcon(Images.class.getResource("/images/install_disabled2.png"));
		installBox = new ImageIcon(Images.class.getResource("/images/install-box.png"));
	}

}
