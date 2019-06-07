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

package eu.the5zig.mod.modules;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import eu.the5zig.mod.The5zigMod;
import eu.the5zig.mod.config.ModuleData;
import eu.the5zig.mod.config.items.ConfigItem;
import eu.the5zig.util.minecraft.ChatColor;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;

public class ModuleMaster {

	private File file;
	private File parent;

	private final List<Module> modules = Lists.newArrayList();
	private List<String> defaultModules = Lists.newArrayList();

	private final List<String> newAvailableItems = Lists.newArrayList();
	private final List<String> activeItems = Lists.newArrayList();

	public ModuleMaster(File parent) {
		this.file = new File(parent, "modules.json");
		this.parent = parent;
	}

	public File getFile() {
		return file;
	}

	public void loadInitial() {
		try {
			if (file.exists()) {
				loadModules();
			} else {
				createDefault();
			}
		} catch (Throwable e) {
			The5zigMod.logger.warn("Error loading modules!", e);
			try {
				File backupFile = new File(parent, "modules.old.json");
				if (backupFile.exists()) {
					backupFile.delete();
				}
				FileUtils.moveFile(file, backupFile);
				createDefault();
			} catch (Throwable e1) {
				The5zigMod.logger.error("Could not create default modules file!", e1);
			}
		}
	}

	private JsonElement parseJson(String json) throws Throwable {
		try {
			JsonReader jsonReader = new JsonReader(new StringReader(json));
			jsonReader.setLenient(true);
			return new JsonParser().parse(jsonReader);
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse json!\n" + json);
			throw e;
		}
	}

	private JsonObject getDefaultRoot() throws Throwable {
		String json;
		JsonElement element;
		try {
			json = CharStreams.toString(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("core/modules.json")));
			element = parseJson(json);
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not load default module data!");
			throw e;
		}

		return element.getAsJsonObject();
	}

	private ModuleData parse(JsonObject root) {
		List<String> defaultModules = Lists.newArrayList();
		JsonArray defaultModulesArray = root.get("defaultModules").getAsJsonArray();
		for (JsonElement element : defaultModulesArray) {
			defaultModules.add(element.getAsString());
		}

		List<String> availableItems = Lists.newArrayList();
		if (root.has("availableItems")) {
			JsonArray availableItemsArray = root.get("availableItems").getAsJsonArray();
			for (JsonElement element : availableItemsArray) {
				availableItems.add(element.getAsString());
			}
		} else {
			for (RegisteredItem registeredItem : The5zigMod.getModuleItemRegistry().getRegisteredItems()) {
				availableItems.add(registeredItem.getKey());
			}
		}

		return new ModuleData(defaultModules, availableItems);
	}

	public void loadModules() throws Throwable {
		String json;
		try {
			json = FileUtils.readFileToString(file);
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not load module file " + file + "!");
			throw e;
		}
		JsonElement element;
		try {
			element = parseJson(json);
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse json of " + file + "!");
			throw e;
		}

		JsonObject root;
		JsonArray modulesArray;
		try {
			root = element.getAsJsonObject();
			modulesArray = root.get("modules").getAsJsonArray();
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse module list!");
			throw e;
		}
		this.modules.clear();
		this.activeItems.clear();

		try {
			JsonObject defaultRoot = getDefaultRoot();
			ModuleData fileData = parse(root);
			ModuleData resourceData = parse(defaultRoot);
			for (String defaultModule : resourceData.getDefaultModules()) {
				if (!fileData.getDefaultModules().contains(defaultModule)) {
					for (JsonElement moduleElement : defaultRoot.getAsJsonArray("modules")) {
						JsonObject moduleObject = moduleElement.getAsJsonObject();
						if (moduleObject.has("id") && defaultModule.equals(moduleObject.get("id").getAsString())) {
							modulesArray.add(moduleObject);
							fileData.getDefaultModules().add(defaultModule);
						}
					}
				}
			}
			for (String availableItem : resourceData.getAvailableItems()) {
				if (!fileData.getAvailableItems().contains(availableItem)) {
					tryCopyItem(availableItem, defaultRoot, modulesArray);
					newAvailableItems.add(availableItem);
				}
			}
			if (!newAvailableItems.isEmpty()) {
				The5zigMod.logger.info("Found " + newAvailableItems.size() + " new available module item(s)!");
			}
			JsonArray array = new JsonArray();
			for (String s : fileData.getDefaultModules()) {
				array.add(new JsonPrimitive(s));
			}
			root.add("defaultModules", array);
			defaultModules = fileData.getDefaultModules();
			JsonArray availableItems = new JsonArray();
			for (RegisteredItem registeredItem : The5zigMod.getModuleItemRegistry().getRegisteredItems()) {
				availableItems.add(new JsonPrimitive(registeredItem.getKey()));
			}
			root.add("availableItems", availableItems);
		} catch (Throwable e) {
			The5zigMod.logger.warn("Could not parse default module file!", e);
		}

		for (JsonElement moduleElement : modulesArray) {
			JsonObject moduleObject;
			String moduleId;
			try {
				moduleObject = moduleElement.getAsJsonObject();
				moduleId = moduleObject.get("id").getAsString();
			} catch (Throwable e) {
				The5zigMod.logger.error("Could not load unknown module!", e);
				continue;
			}

			boolean alreadyLoaded = false;
			for (Module module : this.modules) {
				if (module.getId().equals(moduleId)) {
					alreadyLoaded = true;
					The5zigMod.logger.warn("Module with id " + moduleId + " already has been loaded!");
					break;
				}
			}
			if (alreadyLoaded) {
				continue;
			}

			try {
				this.modules.add(parseModule(moduleObject, moduleId));
			} catch (Throwable e) {
				The5zigMod.logger.error("Could not load module with id \"" + moduleId + "\"!", e);
			}
		}

		try {
			String toJson = The5zigMod.prettyGson.toJson(root);
			FileWriter writer = new FileWriter(file);
			writer.write(toJson);
			writer.close();
		} catch (IOException e) {
			The5zigMod.logger.warn("Could not update Modules!", e);
		}

		int items = 0;
		for (Module module : modules) {
			items += module.getItems().size();
		}
		The5zigMod.logger.info("Loaded " + modules.size() + " modules containing " + items + " items!");
	}

	private void tryCopyItem(String availableItem, JsonObject defaultRoot, JsonArray modulesArray) {
		if (!defaultRoot.has("modules")) {
			return;
		}
		for (JsonElement defaultModuleElement : defaultRoot.getAsJsonArray("modules")) {
			JsonObject defaultModuleObject = defaultModuleElement.getAsJsonObject();
			if (!defaultModuleObject.has("items")) {
				continue;
			}
			for (JsonElement defaultItemElement : defaultModuleObject.getAsJsonArray("items")) {
				JsonObject defaultItemObject = defaultItemElement.getAsJsonObject();
				if (!defaultItemObject.has("type") || !defaultItemObject.get("type").getAsString().equals(availableItem)) {
					continue;
				}
				for (JsonElement moduleElement : modulesArray) {
					JsonObject moduleObject = moduleElement.getAsJsonObject();
					if (!moduleObject.get("id").getAsString().equals(defaultModuleObject.get("id").getAsString())) {
						continue;
					}
					JsonArray itemsArray = moduleObject.getAsJsonArray("items");
					for (JsonElement itemElement : itemsArray) {
						JsonObject item = itemElement.getAsJsonObject();
						if (item.has("type") && item.get("type").getAsString().equals(availableItem)) {
							return;
						}
					}
					itemsArray.add(defaultItemObject);
					return;
				}
			}
		}
	}

	private Module parseModule(JsonObject moduleObject, String moduleId) {
		String moduleName = moduleObject.has("name") ? moduleObject.get("name").getAsString() : null;
		String moduleTranslation = moduleObject.has("translation") ? moduleObject.get("translation").getAsString() : null;
		ModuleLocation location;
		AnchorPoint anchorPoint = null;
		float locationX = 0, locationY = 0;
		try {
			if (moduleObject.has("location") && moduleObject.get("location").isJsonPrimitive()) {
				String locationString = moduleObject.get("location").getAsString();
				location = ModuleLocation.valueOf(locationString);
				if (location == ModuleLocation.CUSTOM) {
					anchorPoint = AnchorPoint.TOP_LEFT;
					try {
						if (moduleObject.has("locationX") && moduleObject.has("locationY")) {
							locationX = moduleObject.get("locationX").getAsFloat();
							locationY = moduleObject.get("locationY").getAsFloat();
						} else {
							The5zigMod.logger.warn("Could not find custom x and y location for module with id \"" + moduleId + "\"!");
						}
						if (moduleObject.has("anchor")) {
							anchorPoint = AnchorPoint.valueOf(moduleObject.get("anchor").getAsString());
						} else {
							The5zigMod.logger.warn("Could not find custom anchor point for module with id \"" + moduleId + "\"!");
						}
					} catch (Throwable e) {
						The5zigMod.logger.error("Could not parse location \"" + locationString + "\" for module with id \"" + moduleId + "\"!", e);
					}
				}
			} else {
				location = ModuleLocation.TOP_LEFT;
			}
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse location for module with id \"" + moduleId + "\"!", e);
			location = ModuleLocation.TOP_LEFT;
		}
		String server = moduleObject.has("server") ? moduleObject.get("server").getAsString() : null;
		boolean showLabel = !moduleObject.has("showLabel") || moduleObject.get("showLabel").getAsBoolean();
		float scale = !moduleObject.has("scale") ? 1.0f : moduleObject.get("scale").getAsFloat();
		float boxOpacity = !moduleObject.has("boxOpacity") ? 0.0f : moduleObject.get("boxOpacity").getAsFloat();
		Module module = new Module(moduleId, moduleName, moduleTranslation, server, showLabel, location, anchorPoint, locationX, locationY, scale, boxOpacity);
		Module.RenderType renderType = null;
		try {
			if (moduleObject.has("render")) {
				renderType = Module.RenderType.valueOf(moduleObject.get("render").getAsString());
			}
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse render type!", e);
		}
		module.setRenderType(renderType);
		if (moduleObject.has("color")) {
			try {
				JsonObject colorObject = moduleObject.get("color").getAsJsonObject();
				ChatColor mainFormatting = colorObject.has("mainFormatting") ? ChatColor.valueOf(colorObject.get("mainFormatting").getAsString()) : null;
				ChatColor mainColor = colorObject.has("mainColor") ? ChatColor.valueOf(colorObject.get("mainColor").getAsString()) : null;
				module.setLabelFormatting(new ModuleLabelFormatting(mainFormatting, mainColor));
			} catch (Throwable e) {
				The5zigMod.logger.error("Could not parse color for module \"" + module.getId() + "\"!", e);
			}
		}
		if (moduleObject.has("enabled")) {
			module.setEnabled(moduleObject.get("enabled").getAsBoolean());
		}

		JsonArray items = moduleObject.get("items").getAsJsonArray();
		for (JsonElement jsonElement : items) {
			JsonObject itemObject = jsonElement.getAsJsonObject();
			String typeString = itemObject.get("type").getAsString();
			RegisteredItem registeredItem = The5zigMod.getModuleItemRegistry().byKey(typeString);
			if (registeredItem == null) {
				The5zigMod.logger.error("Could not parse item type \"" + typeString + "\"!");
				continue;
			}
			try {
				AbstractModuleItem item = parseItem(itemObject, registeredItem);
				if (item != null) {
					module.addItem(item);
					this.activeItems.add(registeredItem.getKey());
				}
			} catch (Throwable e) {
				The5zigMod.logger.error("Could not load item \"" + typeString + "\"!", e);
			}
		}
		return module;
	}

	private AbstractModuleItem parseItem(JsonObject itemObject, RegisteredItem registeredItem) {
		AbstractModuleItem item;
		try {
			item = The5zigMod.getModuleItemRegistry().create(registeredItem);
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse item type \"" + registeredItem.getKey() + "\"!", e);
			return null;
		}
		try {
			parseSettings(item, itemObject);
			if (itemObject.has("color")) {
				try {
					JsonObject colorObject = itemObject.get("color").getAsJsonObject();
					ChatColor prefixFormatting = colorObject.has("prefixFormatting") ? ChatColor.valueOf(colorObject.get("prefixFormatting").getAsString()) : null;
					ChatColor prefixColor = colorObject.has("prefixColor") ? ChatColor.valueOf(colorObject.get("prefixColor").getAsString()) : null;
					ChatColor mainFormatting = colorObject.has("mainFormatting") ? ChatColor.valueOf(colorObject.get("mainFormatting").getAsString()) : null;
					ChatColor mainColor = colorObject.has("mainColor") ? ChatColor.valueOf(colorObject.get("mainColor").getAsString()) : null;
					item.properties.setFormatting(new ModuleItemFormattingImpl(prefixFormatting, prefixColor, mainFormatting, mainColor));
				} catch (Throwable e) {
					The5zigMod.logger.error("Could not parse color for item \"" + registeredItem.getKey() + "\"!", e);
				}
			}
			if (itemObject.has("showPrefix")) {
				try {
					boolean showPrefix = itemObject.get("showPrefix").getAsBoolean();
					item.properties.setShowPrefix(showPrefix);
				} catch (Throwable e) {
					The5zigMod.logger.error("Could not parse showPrefix for item \"" + registeredItem.getKey() + "\"!", e);
				}
			}
		} catch (Throwable e) {
			The5zigMod.logger.error("Could not parse type \"" + registeredItem.getKey() + "\"", e);
		}
		return item;
	}

	private void parseSettings(AbstractModuleItem item, JsonObject itemObject) {
		JsonArray settingsArray = null;
		if (itemObject.has("settings")) {
			settingsArray = itemObject.get("settings").getAsJsonArray();
		}
		List<ConfigItem> missingSettings = Lists.newArrayList();
		for (ConfigItem configItem : ((ModuleItemPropertiesImpl)item.getProperties()).getSettings()) {
			if (settingsArray != null) {
				boolean contains = false;
				try {
					for (JsonElement settingsElement : settingsArray) {
						JsonObject settingsObject = settingsElement.getAsJsonObject();
						String settingsName = settingsObject.get("name").getAsString();
						if (configItem.getKey().equals(settingsName)) {
							JsonObject settingsValue = settingsObject.get("value").getAsJsonObject();
							try {
								configItem.deserialize(settingsValue);
							} catch (Throwable e) {
								The5zigMod.logger.warn("Could not deserialize setting " + settingsName + "!", e);
								configItem.reset();
								configItem.serialize(settingsValue);
							}
							contains = true;
							break;
						}
					}
				} catch (Throwable e) {
					The5zigMod.logger.warn("Could not parse setting \"" + configItem.getKey() + "\" for item \"" + The5zigMod.getModuleItemRegistry().byItem(item.getClass()) + "\"!", e);
				}
				if (!contains) {
					configItem.reset();
					missingSettings.add(configItem);
				}
			} else {
				configItem.reset();
				missingSettings.add(configItem);
			}
		}
		if (!missingSettings.isEmpty()) {
			boolean wasNull = settingsArray == null;
			if (settingsArray == null)
				settingsArray = new JsonArray();
			for (ConfigItem missingSetting : missingSettings) {
				try {
					JsonObject missingElement = new JsonObject();
					missingElement.addProperty("name", missingSetting.getKey());
					JsonObject missingValue = new JsonObject();
					missingSetting.serialize(missingValue);
					missingElement.add("value", missingValue);
					settingsArray.add(missingElement);
				} catch (Throwable e) {
					The5zigMod.logger.warn("Could not add missing setting \"" + missingSetting.getKey() + "\" for item \"" + The5zigMod.getModuleItemRegistry().byItem(item.getClass()) + "\"");
				}
			}
			if (wasNull) {
				itemObject.add("settings", settingsArray);
			}
		}
	}

	public void createDefault() throws Throwable {
		FileUtils.copyInputStreamToFile(Thread.currentThread().getContextClassLoader().getResourceAsStream("core/modules.json"), file);
		The5zigMod.logger.info("Created default module config!");
		loadModules();
	}

	public void save() {
		The5zigMod.getAsyncExecutor().execute(new Runnable() {
			@Override
			public void run() {
				try {
					JsonObject root = new JsonObject();
					JsonArray moduleArray = new JsonArray();
					activeItems.clear();
					List<Module> modules = Lists.newArrayList(ModuleMaster.this.modules);
					for (Module module : modules) {
						JsonObject moduleObject = new JsonObject();
						moduleObject.addProperty("id", module.getId());
						if (module.getName() != null) {
							moduleObject.addProperty("name", module.getName());
						}
						if (module.getTranslation() != null) {
							moduleObject.addProperty("translation", module.getTranslation());
						}
						if (module.getServer() != null) {
							moduleObject.addProperty("server", module.getServer());
						}
						moduleObject.addProperty("showLabel", module.isShowLabel());
						moduleObject.addProperty("scale", module.getScale());
						moduleObject.addProperty("boxOpacity", module.getBoxOpacity());
						moduleObject.addProperty("location", module.getLocation().toString());
						if (module.getLocation() == ModuleLocation.CUSTOM) {
							if (module.getAnchorPoint() != null) {
								moduleObject.addProperty("anchor", module.getAnchorPoint().toString());
							}
							if (module.getLocationX() != 0) {
								moduleObject.addProperty("locationX", module.getLocationX());
							}
							if (module.getLocationY() != 0) {
								moduleObject.addProperty("locationY", module.getLocationY());
							}
						}
						if (module.getRenderType() != null) {
							moduleObject.addProperty("render", module.getRenderType().toString());
						}
						if (module.getLabelFormatting() != null) {
							JsonObject colorObject = new JsonObject();
							if (module.getLabelFormatting().getMainFormatting() != null)
								colorObject.addProperty("mainFormatting", module.getLabelFormatting().getMainFormatting().name());
							if (module.getLabelFormatting().getMainColor() != null)
								colorObject.addProperty("mainColor", module.getLabelFormatting().getMainColor().name());
							moduleObject.add("color", colorObject);
						}
						moduleObject.addProperty("enabled", module.isEnabled());

						JsonArray items = new JsonArray();
						for (ActiveModuleItem item : module.getItems()) {
							activeItems.add(The5zigMod.getModuleItemRegistry().byItem(item.getHandle().getClass()).getKey());

							JsonObject itemObject = new JsonObject();
							itemObject.addProperty("type", The5zigMod.getModuleItemRegistry().byItem(item.getHandle().getClass()).getKey());
							JsonArray settingsArray = new JsonArray();
							for (ConfigItem setting : ((ModuleItemPropertiesImpl)item.getHandle().getProperties()).getSettings()) {
								JsonObject settingElement = new JsonObject();
								settingElement.addProperty("name", setting.getKey());
								JsonObject settingValue = new JsonObject();
								setting.serialize(settingValue);
								settingElement.add("value", settingValue);
								settingsArray.add(settingElement);
							}
							if (settingsArray.size() != 0) {
								itemObject.add("settings", settingsArray);
							}
							if (item.getHandle().properties.getFormatting() != null) {
								JsonObject colorObject = new JsonObject();
								if (item.getHandle().properties.getFormatting().getPrefixFormatting() != null)
									colorObject.addProperty("prefixFormatting", item.getHandle().properties.getFormatting().getPrefixFormatting().name());
								if (item.getHandle().properties.getFormatting().getPrefixColor() != null)
									colorObject.addProperty("prefixColor", item.getHandle().properties.getFormatting().getPrefixColor().name());
								if (item.getHandle().properties.getFormatting().getMainFormatting() != null)
									colorObject.addProperty("mainFormatting", item.getHandle().properties.getFormatting().getMainFormatting().name());
								if (item.getHandle().properties.getFormatting().getMainColor() != null)
									colorObject.addProperty("mainColor", item.getHandle().properties.getFormatting().getMainColor().name());
								itemObject.add("color", colorObject);
							}
							if (!item.getHandle().properties.isShowPrefix()) {
								itemObject.addProperty("showPrefix", item.getHandle().properties.isShowPrefix());
							}
							items.add(itemObject);
						}
						moduleObject.add("items", items);
						moduleArray.add(moduleObject);
					}
					root.add("modules", moduleArray);
					JsonArray array = new JsonArray();
					for (String s : defaultModules) {
						array.add(new JsonPrimitive(s));
					}
					root.add("defaultModules", array);
					JsonArray availableItems = new JsonArray();
					for (RegisteredItem registeredItem : The5zigMod.getModuleItemRegistry().getRegisteredItems()) {
						availableItems.add(new JsonPrimitive(registeredItem.getKey()));
					}
					root.add("availableItems", availableItems);

					String json = The5zigMod.prettyGson.toJson(root);
					FileWriter writer = new FileWriter(file);
					writer.write(json);
					writer.close();
				} catch (Throwable e) {
					The5zigMod.logger.warn("Could not save modules!", e);
				}
			}
		});
	}

	public List<Module> getModules() {
		return modules;
	}

	public boolean isItemActive(String key) {
		return activeItems.contains(key);
	}
}
