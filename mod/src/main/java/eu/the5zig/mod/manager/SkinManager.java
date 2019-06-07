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

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.util.Utils;
import eu.the5zig.util.io.FileUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class SkinManager {

	private final ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Skin Download Thread #%d").build());
	private final File dir;
	private final String END = ".skin";
	private HashMap<String, Base64Skin> base64EncodedSkins = new HashMap<String, Base64Skin>();

	public SkinManager() {
		dir = new File(The5zigMod.getModDirectory(), "skins");

		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
			The5zigMod.logger.warn("Could not load Skins!");
		}
	}

	private void load() throws IOException {
		File[] files = dir.listFiles();
		if (files == null)
			return;
		for (File file : files) {
			if (!file.getName().endsWith(END))
				return;
			long diff = new Date().getTime() - file.lastModified();
			if (diff > TimeUnit.DAYS.toMillis(30)) {
				org.apache.commons.io.FileUtils.deleteQuietly(file);
				continue;
			}

			String name = file.getName().substring(0, file.getName().length() - END.length());
			String base64 = FileUtils.readFile(file);
			base64EncodedSkins.put(name, new Base64Skin(base64));
		}
	}

	private void setBase64EncodedSkin(String uuid, String base64) {
		try {
			FileUtils.writeFile(new File(dir, uuid + END), base64);
		} catch (IOException e) {
			e.printStackTrace();
		}
		base64EncodedSkins.put(uuid, new Base64Skin(base64));
		base64EncodedSkins.get(uuid).setUpToDate();
	}

	public String getBase64EncodedSkin(UUID uniqueId) {
		String uuid = Utils.getUUIDWithoutDashes(uniqueId);
		if (base64EncodedSkins.containsKey(uuid)) {
			if (!base64EncodedSkins.get(uuid).isUpToDate()) {
				base64EncodedSkins.get(uuid).setUpToDate();
				downloadBase64Skin(uuid);
			}
			return base64EncodedSkins.get(uuid).getBase64();
		}
		base64EncodedSkins.put(uuid, new Base64Skin(null));
		downloadBase64Skin(uuid);
		return null;
	}

	private void downloadBase64Skin(final String uuid) {
		executorService.submit(new Runnable() {
			@Override
			public void run() {
				try {
					URL url = new URL("https://cravatar.eu/avatar/" + uuid + "/64.png");
					BufferedImage image = ImageIO.read(url);
					// Converting Image byte array into Base64 String
					ByteBuf localByteBuf1 = Unpooled.buffer();
					ImageIO.write(image, "PNG", new ByteBufOutputStream(localByteBuf1));
					ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
					String imageDataString = localByteBuf2.toString(Charsets.UTF_8);
					setBase64EncodedSkin(uuid, imageDataString);
					The5zigMod.logger.debug("Got Base64 encoded skin for {}", uuid);
				} catch (Exception e) {
					The5zigMod.logger.warn("Could not get Base64 skin for " + uuid, e);
				}
			}
		});
	}

	private class Base64Skin {

		private String base64;
		private boolean upToDate;

		private Base64Skin(String base64) {
			this.base64 = base64;
			this.upToDate = false;
		}

		public boolean isUpToDate() {
			return upToDate;
		}

		public void setUpToDate() {
			this.upToDate = true;
		}

		public String getBase64() {
			return base64;
		}
	}

}
