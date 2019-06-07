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

import eu.the5zig.teamspeak.api.*;
import java.awt.image.*;
import eu.the5zig.teamspeak.util.*;
import com.google.common.primitives.*;

public class GroupImpl implements Group
{
    private ServerTabImpl serverTab;
    private final int id;
    private String name;
    private boolean showPrefix;
    private int type;
    private int iconId;
    private boolean saveDb;
    private int sortId;
    private int modifyPower;
    private int memberAddPower;
    private int memberRemovePower;
    
    public GroupImpl(final ServerTabImpl serverTab, final int id) {
        this.serverTab = serverTab;
        this.id = id;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public boolean isShowPrefix() {
        return this.showPrefix;
    }
    
    public void setShowPrefix(final boolean showPrefix) {
        this.showPrefix = showPrefix;
    }
    
    @Override
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    @Override
    public BufferedImage getIcon() {
        final ServerInfoImpl serverInfo = this.serverTab.getServerInfo();
        return (serverInfo == null || serverInfo.getUniqueId() == null) ? null : ImageManager.resolveIcon(serverInfo.getUniqueId(), this.iconId);
    }
    
    @Override
    public int getIconId() {
        return this.iconId;
    }
    
    public void setIconId(final int iconId) {
        this.iconId = iconId;
    }
    
    @Override
    public boolean isPersistent() {
        return this.saveDb;
    }
    
    public void setSaveDb(final boolean saveDb) {
        this.saveDb = saveDb;
    }
    
    @Override
    public int getSortId() {
        return this.sortId;
    }
    
    public void setSortId(final int sortId) {
        this.sortId = sortId;
    }
    
    @Override
    public int getModifyPower() {
        return this.modifyPower;
    }
    
    public void setModifyPower(final int modifyPower) {
        this.modifyPower = modifyPower;
    }
    
    @Override
    public int getMemberAddPower() {
        return this.memberAddPower;
    }
    
    public void setMemberAddPower(final int memberAddPower) {
        this.memberAddPower = memberAddPower;
    }
    
    @Override
    public int getMemberRemovePower() {
        return this.memberRemovePower;
    }
    
    public void setMemberRemovePower(final int memberRemovePower) {
        this.memberRemovePower = memberRemovePower;
    }
    
    @Override
    public int compareTo(final Group o) {
        return Ints.compare(this.getSortId(), o.getSortId());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final GroupImpl group = (GroupImpl)o;
        return this.id == group.id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
}
