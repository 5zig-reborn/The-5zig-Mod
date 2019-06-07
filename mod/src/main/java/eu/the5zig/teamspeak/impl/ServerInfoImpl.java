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

package eu.the5zig.teamspeak.impl;

import eu.the5zig.teamspeak.tslogs.*;
import eu.the5zig.teamspeak.api.*;
import eu.the5zig.teamspeak.util.*;
import java.awt.image.*;

public class ServerInfoImpl implements ServerInfo
{
    private final ServerTabImpl serverTab;
    private final String ip;
    private final int port;
    private String name;
    private String uniqueId;
    private String platform;
    private String version;
    private long created;
    private String bannerURL;
    private String bannerImageURL;
    private int bannerImageInterval;
    private String hostButtonTooltip;
    private String hostButtonURL;
    private String hostButtonImageURL;
    private float prioritySpeakerDimmModificator;
    private String phoneticName;
    private int iconId;
    private LogFileParseManager parseManager;
    
    public ServerInfoImpl(final ServerTabImpl serverTab, final String ip, final int port) {
        this.serverTab = serverTab;
        this.ip = ip;
        this.port = port;
    }
    
    public ServerTabImpl getServerTab() {
        return this.serverTab;
    }
    
    @Override
    public String getIp() {
        return this.ip;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }
    
    public void setUniqueId(final String uniqueId) {
        this.uniqueId = uniqueId;
        if (this.parseManager != null) {
            this.parseManager.stop();
            this.parseManager = null;
        }
        this.parseManager = new LogFileParseManager(this);
    }
    
    @Override
    public String getPlatform() {
        return this.platform;
    }
    
    public void setPlatform(final String platform) {
        this.platform = platform;
    }
    
    @Override
    public String getVersion() {
        return this.version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    @Override
    public long getTimeCreated() {
        return this.created;
    }
    
    public void setTimeCreated(final long created) {
        this.created = created;
    }
    
    @Override
    public ServerImage getServerBanner() {
        return ImageManager.resolveServerImage(this.bannerURL, this.bannerImageURL);
    }
    
    @Override
    public ServerImage getServerHostButton() {
        return ImageManager.resolveServerImage(this.hostButtonURL, this.hostButtonImageURL);
    }
    
    public void setBannerURL(final String bannerURL) {
        this.bannerURL = bannerURL;
    }
    
    public void setBannerImageURL(final String bannerImageURL) {
        this.bannerImageURL = bannerImageURL;
    }
    
    public void setBannerImageInterval(final int bannerImageInterval) {
        this.bannerImageInterval = bannerImageInterval;
    }
    
    public float getPrioritySpeakerDimmModificator() {
        return this.prioritySpeakerDimmModificator;
    }
    
    public void setPrioritySpeakerDimmModificator(final float prioritySpeakerDimmModificator) {
        this.prioritySpeakerDimmModificator = prioritySpeakerDimmModificator;
    }
    
    public void setHostButtonTooltip(final String hostButtonTooltip) {
        this.hostButtonTooltip = hostButtonTooltip;
    }
    
    public void setHostButtonURL(final String hostButtonURL) {
        this.hostButtonURL = hostButtonURL;
    }
    
    public void setHostButtonImageURL(final String hostButtonImageURL) {
        this.hostButtonImageURL = hostButtonImageURL;
    }
    
    public void setPhoneticName(final String phoneticName) {
        this.phoneticName = phoneticName;
    }
    
    public void setIconId(final int iconId) {
        this.iconId = iconId;
    }
    
    @Override
    public BufferedImage getIcon() {
        return ImageManager.resolveIcon(this.uniqueId, this.iconId);
    }
    
    public LogFileParseManager getParseManager() {
        return this.parseManager;
    }
    
    @Override
    public String toString() {
        return "ServerInfo{ip='" + this.ip + '\'' + ", port=" + this.port + '}';
    }
}
