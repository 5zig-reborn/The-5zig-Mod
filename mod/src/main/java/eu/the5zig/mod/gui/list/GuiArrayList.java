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

package eu.the5zig.mod.gui.list;

import eu.the5zig.mod.gui.elements.IGuiList;
import eu.the5zig.mod.gui.elements.Row;

import java.util.ArrayList;
import java.util.Collection;

public class GuiArrayList<E extends Row> extends ArrayList<E> {
    private IGuiList parentList;

    public GuiArrayList() {
    }

    public GuiArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public GuiArrayList(Collection<? extends E> c) {
        super(c);
    }

    public IGuiList getParentList() {
        return parentList;
    }

    public void setParentList(IGuiList parentList) {
        this.parentList = parentList;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        if(parentList != null) parentList.addEntry(index, element);
    }

    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        if(!result) return false;
        if(parentList != null) parentList.addEntry(indexOf(e), e);
        return true;
    }

    @Override
    public E set(int index, E element) {
        E result = super.set(index, element);
        if(parentList != null) {
            if(result != null) parentList.removeEntry(result);
            parentList.addEntry(index, element);
        }
        return result;
    }

    @Override
    public E remove(int index) {
        E removed = super.remove(index);
        if(parentList != null) parentList.removeEntry(removed);
        return removed;
    }

    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        if(parentList != null) parentList.removeEntry((E) o);
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        if(parentList != null) parentList.doClearEntries();
    }
}
