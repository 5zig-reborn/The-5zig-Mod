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

package eu.the5zig.mod.manager.iloveradio;

import com.google.common.base.Charsets;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ILoveRadioManager {

	public static final int ILOVERADIO_COLOR_BACKGROUND = 0xFF111111;

	private static final String CHANNEL_LIST_PATH = "http://www.iloveradio.de/fileadmin/templates/js/ilr3.main.js";
	private static final String CHANNEL_INFO_PATH = "http://www.iloveradio.de/xmlparser.php?allchannels=";

	private final List<ILoveRadioChannel> CHANNELS = Lists.newArrayList();
	private final Cache<String, String> TRACK_IMAGE_LOOKUP = CacheBuilder.newBuilder().maximumSize(1500).build();

	private int lastUpdated = 100;

	public ILoveRadioManager() {
		The5zigMod.getListener().registerListener(this);
	}

	@EventHandler
	public void onTick(TickEvent event) {
		if (The5zigMod.getModuleMaster().isItemActive("ILOVERADIO")) {
			if (++lastUpdated >= 100) {
				lastUpdated = 0;
				The5zigMod.getAsyncExecutor().execute(new Runnable() {
					@Override
					public void run() {
						updateChannelInfo();
					}
				});
			}
		}
	}

	public ILoveRadioChannel getChannelById(int id) {
		synchronized (CHANNELS) {
			for (ILoveRadioChannel iLoveRadioChannel : CHANNELS) {
				if (iLoveRadioChannel.getId() == id) {
					return iLoveRadioChannel;
				}
			}
		}
		return null;
	}

	public int getChannelSize() {
		synchronized (CHANNELS) {
			return CHANNELS.size();
		}
	}

	private void loadChannelList() {
		InputStream inputStream = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(CHANNEL_LIST_PATH).openConnection();
			connection.setConnectTimeout(20000);
			connection.setReadTimeout(60000);
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				return;
			}
			inputStream = connection.getInputStream();
			List<String> lines = IOUtils.readLines(inputStream);
			boolean foundChannelVar = false;
			synchronized (CHANNELS) {
				for (String line : lines) {
					if (foundChannelVar && !line.contains("//")) {
						CHANNELS.add(new ILoveRadioChannel(CHANNELS.size() + 1, line.split("'")[1]));
						if (!line.endsWith(",")) {
							break;
						}
					} else if (line.contains("channelname: [")) {
						foundChannelVar = true;
					}
				}
			}
		} catch (Exception e) {
			The5zigMod.logger.error("Could not load iLoveRadio channels!");
			List<String> asList = Arrays.asList("iloveradio", "ilove2dance", "ilovebravotubestars", "ilovethebattle", "ilovemashup", "ilovebravoparty", "iloveaboutberlin", "penny",
					"ilovebravocharts", "ilovebravolove", "ilovebigfmnitrox", "ilovebigfmurbanclubbeats", "ilovebigfmgroovenight");
			synchronized (CHANNELS) {
				for (int i = 0, asListSize = asList.size(); i < asListSize; i++) {
					String channel = asList.get(i);
					CHANNELS.add(new ILoveRadioChannel(i + 1, channel));
				}
			}
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	private void updateChannelInfo() {
		int channelSize = getChannelSize();
		if (channelSize == 0) {
			loadChannelList();
			return;
		}
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(CHANNEL_INFO_PATH + channelSize);

			NodeList channelList = doc.getElementsByTagName("ilr_trackinfo");
			for (int i = 0; i < channelList.getLength(); i++) {
				Element channelNode = (Element) channelList.item(i);
				int id = Integer.parseInt(channelNode.getAttribute("channel"));
				String artist = channelNode.getElementsByTagName("artist").item(0).getTextContent();
				String title = channelNode.getElementsByTagName("title").item(0).getTextContent();
				String image = ((Element) channelNode.getElementsByTagName("image").item(0)).getAttribute("src");
				ILoveRadioTrack track = new ILoveRadioTrack(title, artist);
				ILoveRadioChannel channelById = getChannelById(id);
				if (channelById != null) {
					ILoveRadioTrack currentTrack = channelById.getCurrentTrack();
					if (currentTrack == null || !currentTrack.equals(track)) {
						channelById.setCurrentTrack(track);
						String base64 = resolveCover(image);
						if (base64 != null) {
							track.setImage(base64);
						}
					}
				}
			}
		} catch (Exception e) {
			The5zigMod.logger.warn("Could not update iLoveRadio.de channel list!", e);
		}
	}

	private String resolveCover(final String path) throws ExecutionException {
		return TRACK_IMAGE_LOOKUP.get(path, new Callable<String>() {
			@Override
			public String call() throws Exception {
				URL url = new URL(path);
				BufferedImage image = ImageIO.read(url);
				BufferedImage image1 = new BufferedImage(60, 60, image.getType());
				Graphics graphics = image1.getGraphics();
				try {
					graphics.drawImage(image, 0, 0, image1.getWidth(), image1.getHeight(), null);
				} finally {
					graphics.dispose();
				}
				// Converting Image byte array into Base64 String
				ByteBuf localByteBuf1 = Unpooled.buffer();
				ImageIO.write(image1, "PNG", new ByteBufOutputStream(localByteBuf1));
				ByteBuf localByteBuf2 = Base64.encode(localByteBuf1);
				return localByteBuf2.toString(Charsets.UTF_8);
			}
		});
	}
}
