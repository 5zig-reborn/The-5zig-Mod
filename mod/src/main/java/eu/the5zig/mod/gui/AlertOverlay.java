/*
 * Copyright (c) 2019-2020 5zig Reborn
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
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.util.GLUtil;
import eu.the5zig.util.minecraft.ChatColor;

public class AlertOverlay {
    private IButton closeButton;
    private boolean state;
    private String translationKey, titleKey;
    private int width, height;
    private Gui parent;

    public AlertOverlay(Gui parent, String translationKey, String titleKey) {
        this.translationKey = translationKey;
        this.state = false;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
        this.parent = parent;
        this.titleKey = titleKey;
        this.closeButton = The5zigMod.getVars().createButton(50, width / 2 - 75, (height - 200) / 2 + 135,
                150, 20, The5zigMod.getVars().translate("gui.done"));
    }

    public void draw(int mouseX, int mouseY) {
        if(state) {
            GLUtil.color(1, 1, 1, 1);
            The5zigMod.getVars().bindTexture(The5zigMod.DEMO_BACKGROUND);
            parent.drawTexturedModalRect((width - 247) / 2, (height - 200) / 2, 0, 0, 256, 256);
            if(titleKey != null)
                The5zigMod.getVars().drawCenteredString(ChatColor.BOLD + I18n.translate(titleKey), width / 2, (height - 200) / 2 + 10);
            int y = 0;
            for (String line : The5zigMod.getVars().splitStringToWidth(I18n.translate(translationKey), 236)) {
                Gui.drawCenteredString(ChatColor.WHITE + line, width / 2, (height - 200) / 2 + 30 + y);
                y += 10;
            }
            closeButton.draw(mouseX, mouseY);
        }
    }

    public void onClick(int x, int y) {
        if(this.closeButton.mouseClicked(x, y)) {
            this.closeButton.playClickSound();
            state = false;
        }
    }

    public void tick() {
        if(state)
            this.closeButton.tick();
    }

    public void open() {
        state = true;
    }

    public boolean getState() {
        return state;
    }

    public void mouseReleased(int x, int y, int mouseState) {
        if(state) this.closeButton.callMouseReleased(x, y, mouseState);
    }
}
