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

import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.chat.entity.ConversationChat;
import eu.the5zig.mod.chat.entity.ImageMessage;
import eu.the5zig.mod.chat.entity.Message;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.GuiConversations;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.util.GLUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ImageChatLine extends FileChatLine {

	private static final HashMap<String, CachedImage> resourceLocations = Maps.newHashMap();

	private final int maxImageWidth = 160, maxImageHeight = 90, minImageWidth = 60, minImageHeight = 35;

	private File file;
	private Object resourceLocation;
	private BufferedImage bufferedImage;
	private boolean hoverImage = false;

	public ImageChatLine(Message message) {
		super(message);
		updateImage();
	}

	private ImageMessage getImageMessage() {
		return (ImageMessage) getMessage();
	}

	private ImageMessage.ImageData getImageData() {
		return (ImageMessage.ImageData) getImageMessage().getFileData();
	}

	public void updateImage() {
		final String hash = getImageMessage().getFileData().getHash();
		if (hash == null || resourceLocations.containsKey(hash)) {
			if (hash != null && resourceLocations.containsKey(hash))
				setImageWidthAndHeight(resourceLocations.get(hash).width, resourceLocations.get(hash).height);
			return;
		}
		resourceLocations.put(hash, new CachedImage(null, 100, 50, 0, 0));
		new Thread() {
			@Override
			public void run() {
				file = new File(The5zigMod.getModDirectory(),
						"media/" + The5zigMod.getDataManager().getUniqueId() + "/" + ((ConversationChat) getMessage().getConversation()).getFriendUUID().toString() + "/" + hash);
				try {
					BufferedImage bufferedImage = ImageIO.read(file);
					if (bufferedImage == null)
						throw new IOException("Image could not be loaded!");
					double imageWidth = bufferedImage.getWidth();
					double imageHeight = bufferedImage.getHeight();
					while (imageWidth > maxImageWidth || imageHeight > maxImageHeight) {
						imageWidth /= 1.1;
						imageHeight /= 1.1;
					}
					while (imageWidth < minImageWidth || imageHeight < minImageHeight) {
						imageWidth *= 1.1;
						imageHeight *= 1.1;
					}
					resourceLocations.get(hash).realWidth = bufferedImage.getWidth();
					resourceLocations.get(hash).realHeight = bufferedImage.getHeight();
					getImageData().setRealHeight(bufferedImage.getHeight());
					getImageData().setRealWidth(bufferedImage.getWidth());
					setImageWidthAndHeight((int) imageWidth, (int) imageHeight);
					ImageChatLine.this.bufferedImage = bufferedImage;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void setImageWidthAndHeight(int width, int height) {
		int currentHeight = getImageData().getHeight();
		getImageData().setWidth(width);
		getImageData().setHeight(height);
		getImageMessage().saveData();
		if (The5zigMod.getVars().getCurrentScreen() instanceof GuiConversations) {
			GuiConversations tab = (GuiConversations) The5zigMod.getVars().getCurrentScreen();
			tab.chatList.scrollTo(tab.chatList.getCurrentScroll() + height - currentHeight);
		}
		The5zigMod.getConversationManager().updateMessageText(getImageMessage());
	}

	@Override
	protected String getName() {
		return "Image";
	}

	@Override
	protected void preDraw(int x, int y, int width, int height, int mouseX, int mouseY) {
		String hash = getImageMessage().getFileData().getHash();
		if (resourceLocation == null && hash != null && resourceLocations.containsKey(hash) && resourceLocations.get(hash).resourceLocation != null) {
			resourceLocation = resourceLocations.get(hash).resourceLocation;
			getImageData().setRealWidth(resourceLocations.get(hash).realWidth);
			getImageData().setRealHeight(resourceLocations.get(hash).realHeight);
			setImageWidthAndHeight(resourceLocations.get(hash).width, resourceLocations.get(hash).height);
		} else if (bufferedImage != null && resourceLocation == null) {
			resourceLocation = The5zigMod.getVars().loadDynamicImage(hash, bufferedImage);
			resourceLocations.put(hash, new CachedImage(resourceLocation, getWidth(), getHeight(), getImageData().getRealWidth(), getImageData().getRealHeight()));
			setImageWidthAndHeight(resourceLocations.get(hash).width, resourceLocations.get(hash).height);
		}
	}

	@Override
	protected boolean drawOverlay() {
		return resourceLocation != null;
	}

	@Override
	protected void drawBackground(int x, int y, int width, int height, int mouseX, int mouseY) {
		The5zigMod.getVars().bindTexture(resourceLocation);
		GLUtil.color(1, 1, 1, 1);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
		hoverImage = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		if (hoverImage)
			drawRect(x, y, width, height, true);
	}

	@Override
	protected void postDraw(int x, int y, int width, int height, int mouseX, int mouseY) {
		if (resourceLocation != null)
			return;
		hoverImage = false;
		drawRect(x, y, width, height, false);
		drawStatus(I18n.translate("chat.image.not_found"), x, y, width, height, .8f);
	}

	@Override
	public IButton mousePressed(int mouseX, int mouseY) {
		IButton result = super.mousePressed(mouseX, mouseY);
		if (hoverImage) {
			The5zigMod.getVars().displayScreen(new GuiViewImage(The5zigMod.getVars().getCurrentScreen(), resourceLocation, getImageData(),
					"the5zigmod/media/" + The5zigMod.getDataManager().getUniqueId() + "/" + ((ConversationChat) getMessage().getConversation()).getFriendUUID().toString()));
		}
		return result;
	}

	@Override
	protected int getWidth() {
		return getImageData().getWidth();
	}

	@Override
	protected int getHeight() {
		return getImageData().getHeight();
	}

	@Override
	public int getLineHeight() {
		return getImageData().getHeight() + MESSAGE_HEIGHT + 10;
	}

	private class CachedImage {

		private Object resourceLocation;
		private int width;
		private int height;
		private int realWidth;
		private int realHeight;

		public CachedImage(Object resourceLocation, int width, int height, int realWidth, int realHeight) {
			this.resourceLocation = resourceLocation;
			this.width = width;
			this.height = height;
			this.realWidth = realWidth;
			this.realHeight = realHeight;
		}

	}
}
