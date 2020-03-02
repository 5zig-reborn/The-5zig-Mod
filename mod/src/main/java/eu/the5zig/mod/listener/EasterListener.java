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

package eu.the5zig.mod.listener;

import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.render.EasterRenderer;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static eu.the5zig.mod.util.Keyboard.*;

public class EasterListener {

	private boolean running = false;
	private BufferedImage bufferedImage;

	private final EasterRenderer easterRenderer = new EasterRenderer(this);

	@EventHandler
	public void onTick(TickEvent event) {
		if (!running) {
			if (isKeyDown(KEY_L) && isKeyDown(KEY_NUMPAD0) && isKeyDown(KEY_Z) && isKeyDown(KEY_SPACE)) {
				start();
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

	private void start() {
		running = true;
		The5zigMod.logger.info("NYANMODE ACTIVATED!!!!!!!!!!!!!");
		new Thread("Easter") {
			@Override
			public void run() {
				// download frames
				if (bufferedImage == null) {
					try {
						BufferedImage bufferedImage = ImageIO.read(new URL("http://5zig.net/dl/nyan.jpg"));
						if (bufferedImage == null)
							throw new IOException("Image could not be loaded!");
						EasterListener.this.bufferedImage = bufferedImage;
					} catch (IOException e) {
						e.printStackTrace();
						running = false;
						return;
					}
				}
				// start audio
				AudioInputStream din = null;
				try {
					AudioInputStream in = AudioSystem.getAudioInputStream(new URL("http://5zig.net/dl/nyan.wav"));
					AudioFormat baseFormat = in.getFormat();
					AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2,
							baseFormat.getSampleRate(), false);
					din = AudioSystem.getAudioInputStream(decodedFormat, in);
					DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
					SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
					if (line != null) {
						line.open(decodedFormat);
						byte[] data = new byte[4096];
						// Start
						line.start();

						int nBytesRead;
						while ((nBytesRead = din.read(data, 0, data.length)) != -1) {
							line.write(data, 0, nBytesRead);
						}
						// Stop
						line.drain();
						line.stop();
						line.close();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (din != null) {
						try {
							din.close();
						} catch (IOException ignored) {
						}
					}
					running = false;
				}
			}
		}.start();
	}

	public EasterRenderer getEasterRenderer() {
		return easterRenderer;
	}
}
