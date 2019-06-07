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
import com.google.common.collect.Maps;
import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.*;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.RegisteredItem;
import eu.the5zig.util.Callable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GuiModuleAddItem extends Gui implements Clickable<GuiModuleAddItem.CategoryRow> {

	private final HashMap<String, List<String>> CATEGORIES = Maps.newHashMap();

	private final Module module;
	private final List<CategoryRow> categoryList = Lists.newArrayList();
	private IGuiList<ItemRow> guiListItems;
	private final List<ItemRow> itemList = Lists.newArrayList();

	private static int categoryIndex, itemIndex;

	public GuiModuleAddItem(Gui lastScreen, Module module) {
		super(lastScreen);
		this.module = module;

		for (String category : The5zigMod.getModuleItemRegistry().getItemCategories()) {
			List<String> items = Lists.newArrayList();
			for (RegisteredItem item : The5zigMod.getModuleItemRegistry().getRegisteredItems()) {
				if (item.getCategory().equals(category)) {
					items.add(item.getKey());
				}
			}
			CATEGORIES.put(category, items);
		}
	}

	@Override
	public void initGui() {
		addButton(The5zigMod.getVars().createButton(100, getWidth() / 2 - 155, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.cancel")));
		addButton(The5zigMod.getVars().createButton(200, getWidth() / 2 + 5, getHeight() - 32, 150, 20, The5zigMod.getVars().translate("gui.done")));

		IGuiList<CategoryRow> guiListCategory = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 32, getHeight() - 48, getWidth() / 2 - 180, getWidth() / 2 - 10,
				categoryList);
		guiListCategory.setLeftbound(true);
		guiListCategory.setRowWidth(160);
		guiListCategory.setScrollX(getWidth() / 2 - 15);
		addGuiList(guiListCategory);
		categoryList.clear();
		for (String category : The5zigMod.getModuleItemRegistry().getItemCategories()) {
			categoryList.add(new CategoryRow(category));
		}

		guiListItems = The5zigMod.getVars().createGuiList(new Clickable<ItemRow>() {
			@Override
			public void onSelect(int id, ItemRow row, boolean doubleClick) {
				itemIndex = id;
				if (doubleClick) {
					actionPerformed0(getButtonById(200));
				}
			}
		}, getWidth(), getHeight(), 32, getHeight() - 48, getWidth() / 2 + 10, getWidth() / 2 + 180, itemList);
		guiListItems.setLeftbound(true);
		guiListItems.setRowWidth(160);
		guiListItems.setScrollX(getWidth() / 2 + 175);
		addGuiList(guiListItems);

		guiListCategory.setSelectedId(categoryIndex);
		guiListCategory.onSelect(guiListCategory.getSelectedId(), guiListCategory.getSelectedRow(), false);
		guiListItems.setSelectedId(itemIndex);
		guiListItems.onSelect(guiListItems.getSelectedId(), guiListItems.getSelectedRow(), false);
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 100) {
			The5zigMod.getVars().displayScreen(lastScreen);
		}
		if (button.getId() == 200) {
			ItemRow row = guiListItems.getSelectedRow();
			if (row != null) {
				try {
					module.addItem(The5zigMod.getModuleItemRegistry().create(The5zigMod.getModuleItemRegistry().byKey(row.item)));
					The5zigMod.getModuleMaster().save();
				} catch (Exception e) {
					The5zigMod.logger.error("Could not add item " + row.item + "!", e);
				}
			}
		}
	}

	@Override
	public void onSelect(int id, CategoryRow row, boolean doubleClick) {
		categoryIndex = id;
		itemIndex = 0;
		itemList.clear();

		if (row != null) {
			for (String key : CATEGORIES.get(row.category)) {
				if ("DUMMY".equals(key) && !The5zigMod.DEBUG)
					continue;
				itemList.add(new ItemRow(key));
			}
		}
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);
		ItemRow hover = guiListItems.getHoverItem(mouseX, mouseY);
		if (hover != null && hover.item != null) {
			String key = "modules.item." + hover.item.toLowerCase(Locale.ROOT);
			if (I18n.has(key + ".desc")) {
				The5zigMod.getVars().getCurrentScreen().drawHoveringText(The5zigMod.getVars().splitStringToWidth(I18n.translate(key) +
						"\n" + I18n.translate(key + ".desc"), 160), mouseX, mouseY);
			}
		}
	}

	public class CategoryRow extends BasicRow {

		private final String category;

		public CategoryRow(final String category) {
			super(new Callable<String>() {
				@Override
				public String call() {
					return I18n.translate("modules.category." + category.toLowerCase(Locale.ROOT));
				}
			}, 145);
			this.category = category;
		}

		@Override
		public int getLineHeight() {
			return super.getLineHeight() + 4;
		}
	}

	public class ItemRow implements Row {

		private final String item;

		public ItemRow(final String item) {
			this.item = item;
		}

		@Override
		public void draw(int x, int y) {
			The5zigMod.getVars().drawString(The5zigMod.getVars().shortenToWidth(I18n.translate("modules.item." + item.toLowerCase(Locale.ROOT)), 170), x + 2, y + 2);
		}

		@Override
		public int getLineHeight() {
			return 16;
		}
	}
}
