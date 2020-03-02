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

package eu.the5zig.mod.config;

import com.google.common.collect.Lists;
import com.google.gson.*;
import eu.the5zig.mod.server.Server;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by 5zig.
 * All rights reserved © 2015
 */
public class ServerAdapter implements JsonSerializer<List<Server>>, JsonDeserializer<List<Server>> {

	private static final String CLASSNAME = "Name";
	private static final String VALUE = "Value";

	@Override
	public JsonElement serialize(List<Server> servers, Type type, JsonSerializationContext context) {
		JsonArray array = new JsonArray();
		for (Server server : servers) {
			JsonObject retValue = new JsonObject();
			String className = server.getClass().getCanonicalName();
			retValue.addProperty(CLASSNAME, className);
			JsonElement elem = context.serialize(server, server.getClass());
			retValue.add(VALUE, elem);
			array.add(retValue);
		}
		return array;
	}

	@Override
	public List<Server> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		List<Server> servers = Lists.newArrayList();
		JsonArray array = jsonElement.getAsJsonArray();
		for (JsonElement element : array) {
			JsonObject jsonObject = element.getAsJsonObject();
			JsonPrimitive prim = (JsonPrimitive) jsonObject.get(CLASSNAME);
			String className = prim.getAsString();
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
				if (!Server.class.isAssignableFrom(clazz))
					throw new IllegalArgumentException();
			} catch (Throwable ignored) {
				return servers;
			}
			servers.add(context.<Server>deserialize(jsonObject.get(VALUE), clazz));
		}
		return servers;
	}
}
