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

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.elements.Clickable;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.StaticModulePreviewRow;
import eu.the5zig.mod.modules.Module;
import eu.the5zig.mod.modules.ModuleLocation;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;

import java.util.Collections;
import java.util.List;

public class GuiModules extends Gui implements Clickable<Module> {

	private IGuiList<Module> guiList;
	private int lastSelected;

	private StaticModulePreviewRow previewRow;

	private boolean displayHelp;
	private IButton closeHelpButton;

	public GuiModules(Gui lastScreen) {
		super(lastScreen);
	}

	@Override
	public void initGui() {
		addBottomDoneButton();

		guiList = The5zigMod.getVars().createGuiList(this, getWidth(), getHeight(), 50, getHeight() - 32 - 48, getWidth() / 2 - 180, getWidth() / 2 - 10,
				The5zigMod.getModuleMaster().getModules());
		guiList.setRowWidth(160);
		guiList.setLeftbound(true);
		guiList.setScrollX(getWidth() / 2 - 15);
		guiList.setSelectedId(lastSelected);
		guiList.callSetHeaderPadding(The5zigMod.getVars().getFontHeight());
		guiList.setHeader(I18n.translate("modules.list.title"));
		addGuiList(guiList);

		IGuiList modulePreviewList = The5zigMod.getVars().createGuiList(null, getWidth(), getHeight(), 50, getHeight() - 50 - 48, getWidth() / 2 + 10, getWidth() / 2 + 180,
				Collections.singletonList(previewRow = new StaticModulePreviewRow(guiList.getSelectedRow(), getWidth() / 2 + 10, 51, 170, getHeight() - 150)));
		modulePreviewList.setLeftbound(true);
		modulePreviewList.setDrawSelection(false);
		modulePreviewList.setScrollX(getWidth() / 2 + 175);
		addGuiList(modulePreviewList);


		String helpText = I18n.translate("modules.help");
		addButton(The5zigMod.getVars().createStringButton(99, getWidth() / 2 - 180, 30,
				The5zigMod.getVars().getStringWidth(ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString() + helpText), 10,
				ChatColor.ITALIC.toString() + ChatColor.UNDERLINE.toString() + helpText));
		String resetText = I18n.translate("modules.reset");
		addButton(The5zigMod.getVars()
				.createStringButton(98, getWidth() / 2 + 180 - The5zigMod.getVars().getStringWidth(resetText), 30, The5zigMod.getVars().getStringWidth(resetText), 10, resetText));

		addButton(The5zigMod.getVars().createButton(1, getWidth() / 2 - 180, getHeight() - 48 - 20, 145, 20, I18n.translate("modules.add")));
		addButton(The5zigMod.getVars().createButton(2, getWidth() / 2 - 30, getHeight() - 48 - 20, 20, 20, "-"));
		addButton(The5zigMod.getVars().createButton(5, getWidth() / 2 + 10, getHeight() - 48 - 44, 80, 20, I18n.translate("modules.settings")));
		addButton(The5zigMod.getVars().createButton(6, getWidth() / 2 + 100, getHeight() - 48 - 44, 80, 20, I18n.translate("modules.enable")));
		addButton(The5zigMod.getVars().createButton(10, getWidth() / 2 + 10, getHeight() - 48 - 20, 80, 20, I18n.translate("modules.move_up")));
		addButton(The5zigMod.getVars().createButton(11, getWidth() / 2 + 100, getHeight() - 48 - 20, 80, 20, I18n.translate("modules.move_down")));

		closeHelpButton = The5zigMod.getVars().createButton(50, getWidth() / 2 - 75, (getHeight() - 200) / 2 + 135, 150, 20, The5zigMod.getVars().translate("gui.done"));
	}

	@Override
	protected void actionPerformed(IButton button) {
		if (button.getId() == 1) {
			int currentIndex = guiList.getSelectedId();
			StringBuilder moduleId = new StringBuilder("new-module");
			boolean noMatch = true;
			while (true) {
				for (Module module : The5zigMod.getModuleMaster().getModules()) {
					if (moduleId.toString().equals(module.getId())) {
						moduleId.append("-");
						noMatch = false;
						break;
					}
				}
				if (noMatch) {
					break;
				}
				noMatch = true;
			}
			Module module = new Module(moduleId.toString(), null, null, null, true, ModuleLocation.TOP_LEFT, null, 0, 0, 1.0f, 0.0f);
			The5zigMod.getModuleMaster().getModules().add(currentIndex, module);
			The5zigMod.getModuleMaster().save();
			onSelect(currentIndex, module, false);
		}
		if (button.getId() == 2) {
			int currentIndex = guiList.getSelectedId();
			List<Module> modules = The5zigMod.getModuleMaster().getModules();
			if (!modules.isEmpty()) {
				modules.remove(currentIndex);
				The5zigMod.getModuleMaster().save();
			}
			onSelect(guiList.getSelectedId(), guiList.getSelectedRow(), false);
		}
		if (button.getId() == 5) {
			Module module = guiList.getSelectedRow();
			if (module != null) {
				The5zigMod.getVars().displayScreen(new GuiModuleSettings(this, module));
			}
		}
		if (button.getId() == 6) {
			Module module = guiList.getSelectedRow();
			if (module != null) {
				module.setEnabled(!module.isEnabled());
				getButtonById(6).setLabel(!module.isEnabled() ? I18n.translate("modules.enable") : I18n.translate("modules.disable"));
				The5zigMod.getModuleMaster().save();
			}
		}
		if (button.getId() == 10) {
			if (move(-1)) {
				The5zigMod.getModuleMaster().save();
			}
		}
		if (button.getId() == 11) {
			if (move(1)) {
				The5zigMod.getModuleMaster().save();
			}
		}
		if (button.getId() == 98) {
			The5zigMod.getVars().displayScreen(new GuiYesNo(this, new YesNoCallback() {
				@Override
				public void onDone(boolean yes) {
					if (yes) {
						try {
							The5zigMod.getModuleMaster().getModules().clear();
							The5zigMod.getModuleMaster().createDefault();
						} catch (Throwable e) {
							The5zigMod.logger.error("Could not reset modules!", e);
						}
					}
				}

				@Override
				public String title() {
					return I18n.translate("modules.reset.title");
				}
			}));
		}
		if (button.getId() == 99) {
			displayHelp = true;
		}
	}

	@Override
	public void handleMouseInput0() {
		if (!displayHelp) {
			super.handleMouseInput0();
		}
	}

	@Override
	public void mouseClicked0(int x, int y, int button) {
		if (displayHelp) {
			if (closeHelpButton.mouseClicked(x, y)) {
				closeHelpButton.playClickSound();
				displayHelp = false;
			}
			return;
		}
		super.mouseClicked0(x, y, button);
	}

	@Override
	protected void mouseReleased(int x, int y, int state) {
		if (displayHelp) {
			closeHelpButton.callMouseReleased(x, y);
		}
	}

	@Override
	protected void tick() {
		getButtonById(2).setEnabled(guiList.getSelectedRow() != null);
		getButtonById(10).setEnabled(guiList.getSelectedId() > 0);
		getButtonById(11).setEnabled(guiList.getSelectedId() < The5zigMod.getModuleMaster().getModules().size() - 1);
		if (displayHelp) {
			closeHelpButton.tick();
		}
		getButtonById(6).setEnabled(guiList.getSelectedRow() != null);
		getButtonById(6).setLabel(guiList.getSelectedRow() != null && !guiList.getSelectedRow().isEnabled() ? I18n.translate("modules.enable") : I18n.translate("modules.disable"));
	}

	@Override
	public void drawScreen0(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen0(mouseX, mouseY, partialTicks);
		if (displayHelp) {
			GLUtil.color(1, 1, 1, 1);
			The5zigMod.getVars().bindTexture(The5zigMod.DEMO_BACKGROUND);
			drawTexturedModalRect((getWidth() - 247) / 2, (getHeight() - 200) / 2, 0, 0, 256, 256);
			The5zigMod.getVars().drawCenteredString(ChatColor.BOLD + I18n.translate("modules.help"), getWidth() / 2, (getHeight() - 200) / 2 + 10);
			int y = 0;
			for (String line : The5zigMod.getVars().splitStringToWidth(I18n.translate("modules.help.display"), 236)) {
				drawCenteredString(ChatColor.WHITE + line, getWidth() / 2, (getHeight() - 200) / 2 + 30 + y);
				y += 10;
			}
			closeHelpButton.draw(mouseX, mouseY);
		}
	}

	@Override
	public void onSelect(int id, Module module, boolean doubleClick) {
		lastSelected = id;
		previewRow.setModule(module);
	}

	@Override
	public String getTitleKey() {
		return "modules.title";
	}

	private boolean move(int pos) {
		Module module = guiList.getSelectedRow();
		if (module == null) {
			return false;
		}
		List<Module> modules = The5zigMod.getModuleMaster().getModules();

		int currentIndex = modules.indexOf(module);
		int nextIndex = currentIndex + pos;

		if (nextIndex >= 0 && nextIndex < modules.size()) {
			Collections.swap(modules, currentIndex, nextIndex);
			guiList.setSelectedId(nextIndex);
			return true;
		}
		return false;
	}
}
