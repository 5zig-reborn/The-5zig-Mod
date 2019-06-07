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

package eu.the5zig.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class that allows runnables to be executed in a seperate Thread.
 */
public class AsyncExecutor {

	private ExecutorService service;

	public AsyncExecutor() {
		service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("Async Executor Pool #%1$d").build());
	}

	/**
	 * Executes a runnable in a single async thread. Multiple requests in a row may get queued.
	 *
	 * @param runnable the runnable that should be executed asynchronously.
	 */
	public void execute(Runnable runnable) {
		service.execute(runnable);
	}

	/**
	 * Shuts the async executor service down.
	 */
	public void finish() {
		service.shutdown();
	}

}
