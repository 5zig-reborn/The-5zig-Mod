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

package eu.the5zig.mod.gui;

import com.google.common.collect.Lists;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.ConfigItem;
import eu.the5zig.mod.config.items.INonConfigItem;
import eu.the5zig.mod.config.items.SelectColorItem;
import eu.the5zig.mod.gui.elements.ButtonRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;
import eu.the5zig.mod.util.ColorSelectorCallback;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiCustomLocations extends Gui {

	private static List<ConfigItem> items = Lists.newArrayList();

	private static final int HOVER_WIDTH = 15;
	private static final int BOX_WIDTH = 180;

	private IGuiList<Row> guiList;
	private OpeningState state = OpeningState.CLOSED;
	private long lastDelta;
	private float value;

	private int visibleTitleTicks = 0;

	static {
		for (ConfigItem item : The5zigMod.getConfig().getItems("custom_display")) {
			items.add(item);
		}
	}

	public GuiCustomLocations(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		The5zigMod.getVars().updateScaledResolution();

		visibleTitleTicks = 0;

		state = OpeningState.CLOSED;
		List<Row> rows = Lists.newArrayList();
		guiList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 0, getHeight(), getWidth(), getWidth(), rows);
		guiList.setScrollX(getWidth());

		for (int i = 0, locationItemsSize = items.size(); i < locationItemsSize; i++) {
			final ConfigItem item = items.get(i);
			if (item instanceof INonConfigItem)
				continue;
			IButton button;
			if (item instanceof SelectColorItem) {
				button = The5zigMod.getVars().createColorSelector(i, getWidth(), 0, BOX_WIDTH - 30, 20, The5zigMod.getVars().shortenToWidth(item.translate(), 155),
						new ColorSelectorCallback() {
							@Override
							public ChatColor getColor() {
								return ((SelectColorItem) item).get();
							}

							@Override
							public void setColor(ChatColor color) {
								SelectColorItem colorItem = (SelectColorItem) item;
								colorItem.set(color);
							}
						});
			} else {
				button = The5zigMod.getVars().createButton(i, getWidth(), 0, BOX_WIDTH - 30, 20, The5zigMod.getVars().shortenToWidth(item.translate(), BOX_WIDTH - 45));
			}
			rows.add(new ButtonRow(button, null));
		}
		rows.add(new ButtonRow(The5zigMod.getVars().createButton(50, getWidth(), 0, BOX_WIDTH - 30, 20, I18n.translate("config.custom_display.reset")), null));

	}

	@Override
	protected void onEscapeType() {
		The5zigMod.getVars().displayScreen(lastScreen);
	}

	@Override
	protected void guiClosed() {
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() < items.size()) {
			ConfigItem item = items.get(button.getId());
			item.next();
			item.action();
			button.setLabel(item.translate());
			if (item.hasChanged())
				The5zigMod.getConfig().save();
		} else if (button.getId() == 50) {
			for (ConfigItem item : items) {
				item.reset();
			}
			The5zigMod.getConfig().save();
		}
	}

	@Override
	protected void tick() {
		visibleTitleTicks++;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		guiList.mouseClicked(x, y);
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		guiList.mouseReleased(x, y, state);
	}

	@Override
	protected void handleMouseInput() {
		guiList.callHandleMouseInput();
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		if (The5zigMod.getVars().isPlayerNull()) {
			drawMenuBackground();
			The5zigMod.getRenderer().drawScreen();
		}
		guiList.callDrawScreen(mouseX, mouseY, partialTicks);

		if (state == OpeningState.CLOSED) {
			drawRect(getWidth() - HOVER_WIDTH, 0, getWidth(), getHeight(), 0x99000000);
			drawCenteredString("...", getWidth() - HOVER_WIDTH / 2, getHeight() / 2);
		}
		if ((state == OpeningState.CLOSED || state == OpeningState.CLOSING) && mouseX >= getWidth() - HOVER_WIDTH) {
			state = OpeningState.OPENING;
		} else {
			if ((state == OpeningState.OPENED || state == OpeningState.OPENING) && mouseX < getWidth() - BOX_WIDTH) {
				state = OpeningState.CLOSING;

				guiList.setLeft(getWidth());
				guiList.setScrollX(getWidth());
				for (Row row : guiList.getRows()) {
					((ButtonRow) row).button1.setX(getWidth());
				}
			}
		}
		updateTimer();

		for (Row row : guiList.getRows()) {
			IButton button = ((ButtonRow) row).button1;
			if (button.getId() < items.size()) {
				ConfigItem item = items.get(button.getId());
				button.setLabel(item.translate());
			}
		}

		if (visibleTitleTicks <= 75) {
			GLUtil.enableBlend();
			GLUtil.pushMatrix();
			GLUtil.translate(getWidth() / 2, getHeight() / 2 - 10, 1);
			float scale = 2.5f;
			GLUtil.scale(scale, scale, scale);
			GLUtil.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_FALSE, GL11.GL_TRUE);
			int frequencyTotal = 30;
			int currentCountTotal = visibleTitleTicks % frequencyTotal;

			int alpha = (currentCountTotal > frequencyTotal / 2 ? frequencyTotal / 2 - currentCountTotal : currentCountTotal + frequencyTotal / 2) * 0xff / frequencyTotal;
			MinecraftFactory.getVars().drawCenteredString(I18n.translate("config.custom_display.escape"), 0, 0, 0xffffff | alpha << 24);
			GLUtil.popMatrix();
			GLUtil.disableBlend();
		}
	}

	private void updateTimer() {
		long systemTime = The5zigMod.getVars().getSystemTime();
		float delta = (systemTime - lastDelta) / 100f;
		if (state == OpeningState.OPENING) {
			float add = (1 - value) * delta;
			if (add < 0.001)
				add = 0.001f;
			value += add;
			if (value >= 1) {
				value = 1;
				state = OpeningState.OPENED;
			}
			guiList.setLeft(getWidth() - (int) (BOX_WIDTH * value));
			for (Row row : guiList.getRows()) {
				((ButtonRow) row).button1.setX(getWidth() - (int) ((BOX_WIDTH - 10) * value));
			}
			guiList.setScrollX(getWidth() - (int) (BOX_WIDTH * value) + BOX_WIDTH - 12);
		} else if (state == OpeningState.CLOSING) {
			value = 0;
			state = OpeningState.CLOSED;
		}
		lastDelta = systemTime;
	}

	public boolean isOpened() {
		return state != OpeningState.CLOSED;
	}

	private enum OpeningState {
		OPENED, OPENING, CLOSED, CLOSING
	}

}
