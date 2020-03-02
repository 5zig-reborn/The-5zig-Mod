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

package eu.the5zig.mod.gui.ingame.resource;

public class ItemModelResource {

	private Object resourceLocation;

	private Object item;
	private Render render;
	private Object bakedModel;
	private Object simpleTexture;

	public ItemModelResource(Object resourceLocation, Object item, Render render, Object bakedModel, Object simpleTexture) {
		this.resourceLocation = resourceLocation;
		this.item = item;
		this.render = render;
		this.bakedModel = bakedModel;
		this.simpleTexture = simpleTexture;
	}

	public Object getResourceLocation() {
		return resourceLocation;
	}

	public void setResourceLocation(Object resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public Object getItem() {
		return item;
	}

	public Render getRender() {
		return render;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public Object getBakedModel() {
		return bakedModel;
	}

	public void setBakedModel(Object bakedModel) {
		this.bakedModel = bakedModel;
	}

	public Object getSimpleTexture() {
		return simpleTexture;
	}

	public void setSimpleTexture(Object simpleTexture) {
		this.simpleTexture = simpleTexture;
	}

	public enum Render {

		BOW_PULLING_0, BOW_PULLING_1, BOW_PULLING_2, FISHING_ROD_CAST

	}

}
