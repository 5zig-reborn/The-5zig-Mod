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

package eu.the5zig.mod.chat.gui;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.AudioMessage;
import eu.the5zig.mod.chat.entity.ConversationChat;
import eu.the5zig.mod.chat.entity.FileMessage;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.util.Utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import java.io.File;

public class AudioChatLine extends FileChatLine {

	private Clip clip;
	private boolean clipLoaded = false;
	private int lastFrame;

	private boolean hoverPlay = false;
	private boolean hoverSlider = false;
	private int sliderX;

	public AudioChatLine(Message message) {
		super(message);
	}

	private AudioMessage getAudioMessage() {
		return (AudioMessage) getMessage();
	}

	private AudioMessage.AudioData getAudioData() {
		return (AudioMessage.AudioData) getAudioMessage().getFileData();
	}

	@Override
	protected String getName() {
		return I18n.translate("chat.audio.name");
	}

	@Override
	protected int getWidth() {
		return 100;
	}

	@Override
	protected int getHeight() {
		return getLineHeight() - 28;
	}

	@Override
	protected boolean drawOverlay() {
		return false;
	}

	@Override
	protected void preDraw(int x, int y, int width, int height, int mouseX, int mouseY) {
		if (!clipLoaded && (getAudioData().getStatus() == FileMessage.Status.UPLOADED || getAudioData().getStatus() == FileMessage.Status.DOWNLOADED))
			loadClip();

	}

	@Override
	protected void postDraw(int x, int y, int width, int height, int mouseX, int mouseY) {
		Gui.drawRect(x, y, x + width, y + height, 0xffaaaaaa);
		Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, 0xff222222);
		hoverPlay = false;
		if (clipLoaded && clip == null) {
			drawStatus(I18n.translate("chat.audio.not_found"), x, y, width, height, .8f);
			return;
		}
		hoverPlay = mouseX >= x + 8 && mouseX <= x + 15 && mouseY >= y + 11 && mouseY <= y + 17;
		int frame = lastFrame;
		if (!clip.isRunning()) {
			The5zigMod.getVars().drawString("|>", x + 8, y + 11, hoverPlay ? 0x444444 : 0xffffff);
		} else {
			The5zigMod.getVars().drawString("||", x + 8, y + 11, hoverPlay ? 0x444444 : 0xffffff);
			frame = clip.getFramePosition();
		}
		double totalSeconds = clip.getMicrosecondLength() / 1000000.0;
		double currentSecond = ((double) frame / (double) clip.getFrameLength()) * totalSeconds;
		The5zigMod.getVars().drawString(Utils.getShortenedDouble(currentSecond, 1) + "/" + Utils.getShortenedDouble(totalSeconds, 1) + " sec", x + 8, y + 22);
		Gui.drawRect(x + 21, y + 14, x + 91, y + 18, 0xff111111); // bar
		Gui.drawRect(x + 20, y + 13, x + 90, y + 17, 0xffaaaaaa); // bar shadow
		hoverSlider = mouseX >= x + 20 && mouseX <= x + 90 && mouseY >= y + 14 && mouseY <= y + 18;
		sliderX = x + 20;
		int left = frame == 0 ? x + 20 : x + 20 + (int) (((double) frame / (double) clip.getFrameLength()) * 70.0);
		Gui.drawRect(left, y + 10, left + 1, y + 20, 0xffcccccc); // slider
	}

	@Override
	public IButton mousePressed(int mouseX, int mouseY) {
		IButton button = super.mousePressed(mouseX, mouseY);
		if (clip == null)
			return button;
		if (hoverPlay) {
			if (clip.isRunning()) {
				lastFrame = clip.getFramePosition();
				clip.stop();
			} else {
				if (lastFrame < clip.getFrameLength()) {
					clip.setFramePosition(lastFrame);
				} else {
					clip.setFramePosition(0);
				}
				lastFrame = 0;
				clip.start();
			}
		} else if (hoverSlider) {
			int sliderPos = mouseX - sliderX;
			int maxSliderPos = 70;
			int maxFrame = clip.getFrameLength();
			int framePosition = (int) Math.ceil(((double) sliderPos / (double) maxSliderPos) * (double) maxFrame);
			if (clip.isRunning()) {
				clip.stop();
				clip.setFramePosition(framePosition);
				clip.start();
			} else {
				lastFrame = framePosition;
			}
		}
		return button;
	}

	private void loadClip() {
		clipLoaded = true;
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(The5zigMod.getModDirectory(),
					"media/" + The5zigMod.getDataManager().getUniqueId().toString() + "/" + ((ConversationChat) getMessage().getConversation()).getFriendUUID().toString() + "/" +
							getAudioData().getHash()));
			clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
			clip.open(audioInputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (clip == null)
			return;
		if (clip.isRunning())
			clip.stop();
		clip.close();
	}

	@Override
	public int getLineHeight() {
		return 70;
	}

}
