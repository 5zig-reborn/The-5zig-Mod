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

package eu.the5zig.mod.render;

import com.google.common.base.Charsets;
import com.google.common.cache.*;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.mod.util.IResourceLocation;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class Base64Renderer {

	private static Cache<String, Base64Renderer> CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).maximumWeight(1000000).weigher(
			new Weigher<String, Base64Renderer>() {
				@Override
				public int weigh(String s, Base64Renderer base64Renderer) {
					String base64String = base64Renderer.getBase64String();
					return base64String == null ? 0 : base64String.length();
				}
			}).removalListener(new RemovalListener<String, Base64Renderer>() {
		@Override
		public void onRemoval(RemovalNotification<String, Base64Renderer> removalNotification) {
			Base64Renderer renderer = removalNotification.getValue();
			if (renderer != null) {
				renderer.reset();
			}
		}
	}).build();

	private String base64String;
	private Object dynamicImage;
	private IResourceLocation resourceLocation;
	private IResourceLocation fallbackResource;
	private boolean interpolateLinear = false;

	private int x;
	private int y;
	private int width;
	private int height;

	public Base64Renderer() {
		this(The5zigMod.STEVE);
	}

	public Base64Renderer(IResourceLocation fallbackResource) {
		this(fallbackResource, 64, 64);
	}

	public Base64Renderer(int width, int height) {
		this(The5zigMod.STEVE, width, height);
	}

	public Base64Renderer(IResourceLocation fallbackResource, int width, int height) {
		this.fallbackResource = fallbackResource;
		this.width = width;
		this.height = height;
	}

	public void renderImage() {
		renderImage(x, y, width, height);
	}

	public void renderImage(int x, int y, int width, int height) {
		renderImage(x, y, width, height, 1, 1, 1, 1);
	}

	public void renderImage(int x, int y, int width, int height, float r, float g, float b, float a) {
		if (dynamicImage == null) {
			if (fallbackResource != null) {
				render(x, y, width, height, fallbackResource, r, g, b, a);
			}
		} else {
			render(x, y, width, height, resourceLocation, r, b, g, a);
		}
	}

	private void render(int x, int y, int width, int height, IResourceLocation resource, float r, float g, float b, float a) {
		if (dynamicImage != null) {
			The5zigMod.getVars().bindTexture(dynamicImage);
		} else {
			The5zigMod.getVars().bindTexture(resource);
		}

		GLUtil.color(r, g, b, a);
		GLUtil.disableBlend();
		if (interpolateLinear) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
		}
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
		if (interpolateLinear) {
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);
		}
	}

	public String getBase64String() {
		return base64String;
	}

	public void setBase64String(String base64String, String resourceLocation) {
		this.base64String = base64String;
		this.resourceLocation = The5zigMod.getVars().createResourceLocation(resourceLocation);
		this.dynamicImage = The5zigMod.getVars().getTexture(this.resourceLocation);
		prepareImage();
	}

	public void setBase64String(String base64String, IResourceLocation resourceLocation) {
		this.base64String = base64String;
		this.resourceLocation = resourceLocation;
		this.dynamicImage = The5zigMod.getVars().getTexture(this.resourceLocation);
		prepareImage();
	}

	public void reset() {
		delete(resourceLocation);
		this.base64String = null;
		this.resourceLocation = null;
		this.dynamicImage = null;
	}

	private void prepareImage() {
		if (base64String == null) {
			delete(resourceLocation);
			this.dynamicImage = null;
			return;
		}
		ByteBuf localByteBuf1 = Unpooled.copiedBuffer(base64String, Charsets.UTF_8);
		ByteBuf localByteBuf2 = null;
		BufferedImage localBufferedImage;
		try {
			localByteBuf2 = Base64.decode(localByteBuf1);
			localBufferedImage = read(new ByteBufInputStream(localByteBuf2));
			Validate.validState(localBufferedImage.getWidth() == width, "Must be " + width + " pixels wide");
			Validate.validState(localBufferedImage.getHeight() == height, "Must be " + height + " pixels high");
		} catch (Exception e) {
			The5zigMod.logger.error("Could not prepare base64 renderer image", e);
			delete(resourceLocation);
			dynamicImage = null;
			return;
		} finally {
			localByteBuf1.release();
			if (localByteBuf2 != null) {
				localByteBuf2.release();
			}
		}
		if (this.dynamicImage == null) {
			this.dynamicImage = The5zigMod.getVars().loadDynamicImage(resourceLocation.callGetResourcePath(), localBufferedImage);
		}
	}

	private static BufferedImage read(InputStream byteBuf) throws IOException {
		try {
			return ImageIO.read(byteBuf);
		} finally {
			IOUtils.closeQuietly(byteBuf);
		}
	}

	private void delete(IResourceLocation resource) {
		The5zigMod.getVars().deleteTexture(resource);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setWidthAndHeight(int size) {
		this.width = size;
		this.height = size;
	}

	public boolean isInterpolateLinear() {
		return interpolateLinear;
	}

	public void setInterpolateLinear(boolean interpolateLinear) {
		this.interpolateLinear = interpolateLinear;
	}

	public static Base64Renderer getRenderer(BufferedImage icon, String id) {
		Base64Renderer renderer = CACHE.getIfPresent(id);
		if (renderer != null) {
			return renderer;
		}
		final Base64Renderer finalRenderer = new Base64Renderer(null, icon.getWidth(), icon.getHeight());
		CACHE.put(id, finalRenderer);
		try {
			ByteBuf decodedBuffer = Unpooled.buffer();
			ImageIO.write(icon, "PNG", new ByteBufOutputStream(decodedBuffer));
			ByteBuf encodedBuffer = Base64.encode(decodedBuffer);
			String imageDataString = encodedBuffer.toString(Charsets.UTF_8);

			finalRenderer.setBase64String(imageDataString, id);
		} catch (Exception e) {
			The5zigMod.logger.error("Could not load icon " + id, e);
		}
		return finalRenderer;
	}

}
