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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package eu.the5zig.mod.util;

public final class Rectangle {

	private int x;
	private int y;
	private int width;
	private int height;

	public Rectangle() {
	}

	public Rectangle(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setSize(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void setBounds(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			return false;
		} else {
			int x = this.x;
			int y = this.y;
			if (X >= x && Y >= y) {
				w += x;
				h += y;
				return (w < x || w > X) && (h < y || h > Y);
			} else {
				return false;
			}
		}
	}

	public boolean contains(Rectangle r) {
		return this.contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean contains(int X, int Y, int W, int H) {
		int w = this.width;
		int h = this.height;
		if ((w | h | W | H) < 0) {
			return false;
		} else {
			int x = this.x;
			int y = this.y;
			if (X >= x && Y >= y) {
				w += x;
				W += X;
				if (W <= X) {
					if (w >= x || W > w) {
						return false;
					}
				} else if (w >= x && W > w) {
					return false;
				}

				h += y;
				H += Y;
				if (H <= Y) {
					if (h >= y || H > h) {
						return false;
					}
				} else if (h >= y && H > h) {
					return false;
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public boolean intersects(Rectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.getWidth();
		int rh = r.getHeight();
		if (rw > 0 && rh > 0 && tw > 0 && th > 0) {
			int tx = this.x;
			int ty = this.y;
			int rx = r.getX();
			int ry = r.getY();
			rw += rx;
			rh += ry;
			tw += tx;
			th += ty;
			return (rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry);
		} else {
			return false;
		}
	}

	public Rectangle intersection(Rectangle r, Rectangle dest) {
		int tx1 = this.x;
		int ty1 = this.y;
		int rx1 = r.getX();
		int ry1 = r.getY();
		long tx2 = (long)tx1;
		tx2 += (long)this.width;
		long ty2 = (long)ty1;
		ty2 += (long)this.height;
		long rx2 = (long)rx1;
		rx2 += (long)r.getWidth();
		long ry2 = (long)ry1;
		ry2 += (long)r.getHeight();
		if (tx1 < rx1) {
			tx1 = rx1;
		}

		if (ty1 < ry1) {
			ty1 = ry1;
		}

		if (tx2 > rx2) {
			tx2 = rx2;
		}

		if (ty2 > ry2) {
			ty2 = ry2;
		}

		tx2 -= (long)tx1;
		ty2 -= (long)ty1;
		if (tx2 < -2147483648L) {
			tx2 = -2147483648L;
		}

		if (ty2 < -2147483648L) {
			ty2 = -2147483648L;
		}

		if (dest == null) {
			dest = new Rectangle(tx1, ty1, (int)tx2, (int)ty2);
		} else {
			dest.setBounds(tx1, ty1, (int)tx2, (int)ty2);
		}

		return dest;
	}

	public void add(int newx, int newy) {
		int x1 = Math.min(this.x, newx);
		int x2 = Math.max(this.x + this.width, newx);
		int y1 = Math.min(this.y, newy);
		int y2 = Math.max(this.y + this.height, newy);
		this.x = x1;
		this.y = y1;
		this.width = x2 - x1;
		this.height = y2 - y1;
	}

	public void grow(int h, int v) {
		this.x -= h;
		this.y -= v;
		this.width += h * 2;
		this.height += v * 2;
	}

	public boolean isEmpty() {
		return this.width <= 0 || this.height <= 0;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Rectangle)) {
			return super.equals(obj);
		} else {
			Rectangle r = (Rectangle)obj;
			return this.x == r.x && this.y == r.y && this.width == r.width && this.height == r.height;
		}
	}

	public String toString() {
		return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",width=" + this.width + ",height=" + this.height + "]";
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
