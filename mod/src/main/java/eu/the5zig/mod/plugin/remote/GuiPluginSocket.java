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

package eu.the5zig.mod.plugin.remote;

import eu.the5zig.mod.I18n;
import eu.the5zig.mod.MinecraftFactory;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.gui.Gui;
import eu.the5zig.mod.gui.elements.IButton;
import eu.the5zig.mod.render.Base64Renderer;

import java.io.IOException;

public class GuiPluginSocket extends Gui {

    private DownloadState state;
    private PluginSocket socket;
    private RemotePluginDownloader downloader;
    private RemotePlugin preview;
    private String error;

    private Base64Renderer renderer = new Base64Renderer(
            MinecraftFactory.getVars().createResourceLocation("the5zigmod", "textures/plugindummy.png"), 1024, 1024);

    private IButton bottomButton;
    private IButton yesButton, noButton;

    public GuiPluginSocket(Gui lastScreen) {
        super(lastScreen);
        state = DownloadState.STARTING_SERVER;
    }

    public void setSocket(PluginSocket socket) {
        this.socket = socket;
    }

    public void setDownloader(RemotePluginDownloader downloader) {
        this.downloader = downloader;
    }

    @Override
    protected void guiClosed() {
        if(socket != null) socket.stop();
        super.guiClosed();
    }

    @Override
    public void initGui() {
        addButton(bottomButton = The5zigMod.getVars().createButton(1, getWidth() / 2 - 100, getHeight() / 2 + 80, The5zigMod.getVars().translate("gui.cancel")));
        addButton(yesButton = The5zigMod.getVars().createButton(2, getWidth() / 2 - 50, getHeight() / 2 + 50, 50, 20, The5zigMod.getVars().translate("gui.yes")));
        addButton(noButton = The5zigMod.getVars().createButton(3, getWidth() / 2 + 10, getHeight() / 2 + 50, 50, 20, The5zigMod.getVars().translate("gui.no")));
        yesButton.setVisible(false);
        noButton.setVisible(false);
    }

    @Override
    protected void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawCenteredString(state.getDisplay(), getWidth() / 2, getHeight() / 2 - 40);
        if(error != null) {
            Gui.drawCenteredString(I18n.translate(error), getWidth() / 2, getHeight() / 2 - 20);
        }
        if(preview != null) {
            if(preview.getImageBase64() != null) {
                renderer.setHeight(preview.getImageBase64().height);
                renderer.setWidth(preview.getImageBase64().width);
                renderer.setBase64String(preview.getImageBase64().base64, "plugin_icons/" + preview.getId());
            }
            int imgX = getWidth() / 2 - 50;
            int imgY = getHeight() / 2;
            renderer.renderImage(imgX, imgY, 32, 32);
            The5zigMod.getVars().drawString(preview.getName(), imgX + 40, imgY + 2);
            The5zigMod.getVars().drawString("by " + preview.getAuthor(), imgX + 40, imgY + 12);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(IButton button) {
        if(button.getId() == 1 || button.getId() == 3) {
            if(socket != null) socket.stop();
            The5zigMod.getVars().displayScreen(lastScreen);
        }
        else if(button.getId() == 2) {
            yesButton.setVisible(false);
            noButton.setVisible(false);
            try {
                downloader.downloadPlugin(preview, this);
            } catch (IOException e) {
                downloadComplete("IO Error: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public void setState(DownloadState state) {
        this.state = state;
    }

    public void setPreview(RemotePlugin preview) {
        this.preview = preview;
        yesButton.setVisible(true);
        noButton.setVisible(true);
    }

    void downloadComplete(String error) {
        state = DownloadState.DOWNLOAD_COMPLETE;
        bottomButton.setLabel(The5zigMod.getVars().translate("gui.done"));
        this.error = error;
    }

    enum DownloadState {
        STARTING_SERVER("plugin.conn.starting"),
        LISTENING("plugin.conn.listening"),
        DOWNLOADING_INFO("plugin.conn.info"),
        DOWNLOADING_PLUGIN("plugin.conn.download"),
        DOWNLOAD_COMPLETE("plugin.conn.downloaded");

        private String translationKey;

        DownloadState(String translKey) {
            this.translationKey = translKey;
        }

        public String getDisplay() {
            return I18n.translate(translationKey);
        }
    }
}
