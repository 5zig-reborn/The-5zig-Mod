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

package eu.the5zig.mod.util;

import java.math.BigDecimal;

public class MathUtil {

	private MathUtil() {
	}

	public static BigDecimal binomcdf(int n, double p, int k) {
		BigDecimal result = new BigDecimal(0);
		for (int i = 0; i <= k; i++) {
			result = result.add(binompdf(n, p, i));
		}
		return result;
	}

	public static BigDecimal binompdf(int n, double p, int k) {
		BigDecimal binomialkoeffizient = binomalcoefficient(n, k);
		BigDecimal pp = new BigDecimal(p).pow(k);
		BigDecimal pq = new BigDecimal(1 - p).pow(n - k);

		return binomialkoeffizient.multiply(pp).multiply(pq);
	}

	private static BigDecimal binomalcoefficient(int n, int k) {
		if (k == 0) return new BigDecimal(1);
		if (2 * k > n) return binomalcoefficient(n, n - k);
		BigDecimal result = new BigDecimal(n - k + 1);
		for (int i = 2; i <= k; i++) {
			result = result.multiply(new BigDecimal(n - k + i));
			result = result.divide(new BigDecimal(i));
		}
		return result;
	}

}
