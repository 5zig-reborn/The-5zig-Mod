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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.items.ConfigItem;
import eu.the5zig.mod.config.items.ListItem;
import eu.the5zig.mod.config.items.NonConfigItem;
import eu.the5zig.mod.config.items.StringItem;
import eu.the5zig.mod.gui.elements.ButtonRow;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.util.Keyboard;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.List;
import java.util.Map;

public class GuiSettingsSearch extends Gui {

	private final List<ButtonRow> searchRows = Lists.newArrayList();
	private IGuiList<ButtonRow> resultList;
	private String keyword = "";
	private Map<IButton, ConfigItem> configItemMap = Maps.newHashMap();

	private long lastMouseMoved;
	private int lastMouseX, lastMouseY;

	public GuiSettingsSearch(Gui lastScreen) {
		super(lastScreen);
		actionOnTextFieldReturn = false;
	}

	@Override
	public void initGui() {
		resultList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 64, getHeight() - 32, 0, getWidth(), searchRows);
		resultList.setDrawSelection(false);
		resultList.setRowWidth(310);
		resultList.setBottomPadding(2);
		resultList.setScrollX(getWidth() / 2 + 160);
		addGuiList(resultList);
		addTextField(The5zigMod.getVars().createTextfield(I18n.translate("gui.search"), 1, getWidth() / 2 - 100, 32, 200, 20, 128));
		getTextfieldById(1).callSetText(keyword);
		onKeyType('\u0000', 0);

		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 - 100, getHeight() - 27, The5zigMod.getVars().translate("gui.done")));
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);

		if (lastMouseX != mouseX || lastMouseY != mouseY) {
			lastMouseX = mouseX;
			lastMouseY = mouseY;
			lastMouseMoved = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - lastMouseMoved > 700) {
			IButton hovered = getHoveredButton(mouseX, mouseY);
			if (hovered != null) {
				ConfigItem item = configItemMap.get(hovered);
				if (item != null) {
					String hoverText = item.getHoverText();

					List<String> lines = Lists.newArrayList();
					lines.add(hovered.getLabel());
					if (item instanceof ListItem) {
						int size = ((ListItem<?>) item).get().size();
						if (size == 1) {
							lines.add(I18n.translate("config.list.entry"));
						} else {
							lines.add(I18n.translate("config.list.entries", size));
						}
					}
					lines.addAll(The5zigMod.getVars().splitStringToWidth(hoverText, 150));
					if (!(item instanceof NonConfigItem) && Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
						lines.add(ChatColor.DARK_GRAY.toString() + ChatColor.ITALIC + I18n.translate("config.click_to_reset"));
					}
					lines.add("");
					if (I18n.has("config." + item.getCategory() + ".title")) {
						lines.add(ChatColor.BLUE + I18n.translate("config.list.category") + ": " + I18n.translate("config." + item.getCategory() + ".title"));
					}
					drawHoveringText(lines, mouseX, mouseY);
				}
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		tryReset();
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		super.mouseReleased(x, y, state);
		tryReset();
	}

	private void tryReset() {
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
			IButton hovered = getHoveredButton(lastMouseX, lastMouseY);
			if (hovered != null && !(hovered instanceof NonConfigItem)) {
				ConfigItem item = configItemMap.get(hovered);
				item.reset();
				hovered.setLabel(item.translate());
				if (item.hasChanged())
					The5zigMod.getConfig().save();
			}
		}
	}

	private IButton getHoveredButton(int mouseX, int mouseY) {
		for (ButtonRow button : searchRows) {
			if (button.button1 != null && isHovered(button.button1, mouseX, mouseY)) {
				return button.button1;
			} else if (button.button2 != null && isHovered(button.button2, mouseX, mouseY)) {
				return button.button2;
			}
		}
		return null;
	}

	private boolean isHovered(IButton button, int lastMouseX, int lastMouseY) {
		return lastMouseX >= button.getX() && lastMouseX <= button.getX() + button.getWidth() && lastMouseY >= button.getY() && lastMouseY <= button.getY() + button.getHeight();
	}

	@Override
	protected void actionPerformed(final IButton button) {
		if (configItemMap.containsKey(button)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)) {
				return;
			}
			ConfigItem item = configItemMap.get(button);
			if (item instanceof StringItem) {
				final StringItem stringItem = (StringItem) item;
				The5zigMod.getVars().displayScreen(new GuiCenteredTextfield(this, new CenteredTextfieldCallback() {
					@Override
					public void onDone(String text) {
						stringItem.set(text);
						stringItem.action();
						button.setLabel(stringItem.translate());
						if (stringItem.hasChanged())
							The5zigMod.getConfig().save();
					}

					@Override
					public String title() {
						return stringItem.translate();
					}
				}, stringItem.get(), stringItem.getMinLength(), stringItem.getMaxLength()));
			} else if (item instanceof ListItem) {
				The5zigMod.getVars().displayScreen(new GuiSettingsList(this, (ListItem<?>) item));
			} else {
				item.next();
				item.action();
				button.setLabel(item.translate());
				if (item.hasChanged())
					The5zigMod.getConfig().save();
			}
		}
	}

	@Override
	protected void tick() {
		for (Map.Entry<IButton, ConfigItem> entry : configItemMap.entrySet()) {
			entry.getKey().setEnabled(!entry.getValue().isRestricted());
		}
	}

	@Override
	protected void onKeyType(char character, int key) {
		searchRows.clear();
		configItemMap.clear();
		keyword = getTextfieldById(1).callGetText();
		String searchText = keyword.toLowerCase();
		if (searchText.isEmpty()) {
			return;
		}
		ButtonRow row = null;
		int count = 0;
		for (ConfigItem configItem : The5zigMod.getConfig().getItems()) {
			String translate = configItem.translate();
			if (Strings.isNullOrEmpty(configItem.getCategory()) || !translate.toLowerCase().contains(searchText)) {
				continue;
			}
			IButton button = GuiSettings.getButton(configItem, 1000 + count, getWidth());
			if (button == null) {
				continue;
			}
			button.setEnabled(!configItem.isRestricted());
			configItemMap.put(button, configItem);
			if (row == null) {
				row = new ButtonRow(button, null);
				searchRows.add(row);
			} else {
				row.button2 = button;
				row = null;
			}
			count++;
		}
	}

	@Override
	public String getTitleKey() {
		return "gui.search";
	}
}
