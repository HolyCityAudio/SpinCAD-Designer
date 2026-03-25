/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADJsonSerializer.java
 * Copyright (C) 2026 - Gary Worsham
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.holycityaudio.SpinCAD;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.*;

/**
 * Handles reading and writing SpinCADPatch and SpinCADBank objects
 * in JSON format. Legacy .spcd/.spbk files (Java serialization) are
 * still readable via the existing ObjectInputStream path.
 *
 * JSON format stores:
 *   - Patch/bank metadata (file name, comments, pot values)
 *   - Block list with type, position, and per-subclass parameters
 *   - Connection list (by block index and pin name)
 *
 * ElmProgram runtime state (instructions, memory segments, registers)
 * is NOT stored — it is regenerated at render time.
 */
public class SpinCADJsonSerializer {

	private static final int FORMAT_VERSION = 1;
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	// ========================= PATCH WRITE =========================

	public static void writePatch(SpinCADPatch patch, String filePath) throws IOException {
		JsonObject root = new JsonObject();
		root.addProperty("formatVersion", FORMAT_VERSION);
		root.addProperty("type", "patch");
		root.addProperty("patchFileName", patch.patchFileName);

		// comments
		root.add("comments", serializeCommentBlock(patch.cb));

		// pot values
		JsonArray potVals = new JsonArray();
		for (int i = 0; i < 3; i++) {
			potVals.add(patch.getPotVal(i));
		}
		root.add("potValues", potVals);

		// hex file data (only if present)
		if (patch.isHexFile) {
			root.addProperty("isHexFile", true);
			JsonArray hexArr = new JsonArray();
			for (int h : patch.hexFile) {
				hexArr.add(h);
			}
			root.add("hexFile", hexArr);
		}

		// blocks and connections from the model
		if (patch.patchModel != null && patch.patchModel.getBlockList() != null) {
			serializeModel(patch.patchModel, root);
		}

		try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
			gson.toJson(root, writer);
		}
	}

	// ========================= PATCH READ ==========================

	public static SpinCADPatch readPatch(String filePath) throws IOException {
		JsonObject root;
		try (Reader reader = new BufferedReader(new FileReader(filePath))) {
			root = JsonParser.parseReader(reader).getAsJsonObject();
		}

		SpinCADPatch patch = new SpinCADPatch();
		patch.patchFileName = getStringOrDefault(root, "patchFileName", "Untitled");

		// comments
		if (root.has("comments")) {
			deserializeCommentBlock(root.getAsJsonObject("comments"), patch.cb);
		}

		// pot values
		if (root.has("potValues")) {
			JsonArray potVals = root.getAsJsonArray("potValues");
			for (int i = 0; i < Math.min(3, potVals.size()); i++) {
				patch.setPotVal(i, potVals.get(i).getAsDouble());
			}
		}

		// hex file
		if (root.has("isHexFile") && root.get("isHexFile").getAsBoolean()) {
			patch.isHexFile = true;
			if (root.has("hexFile")) {
				JsonArray hexArr = root.getAsJsonArray("hexFile");
				for (int i = 0; i < Math.min(128, hexArr.size()); i++) {
					patch.hexFile[i] = hexArr.get(i).getAsInt();
				}
			}
		}

		// blocks and connections
		if (root.has("blocks")) {
			deserializeModel(root, patch.patchModel);
		}

		return patch;
	}

	// ========================= BANK WRITE ==========================

	public static void writeBank(SpinCADBank bank, String filePath) throws IOException {
		JsonObject root = new JsonObject();
		root.addProperty("formatVersion", FORMAT_VERSION);
		root.addProperty("type", "bank");
		root.addProperty("bankFileName", bank.bankFileName);
		root.add("comments", serializeCommentBlock(bank.cb));

		JsonArray patches = new JsonArray();
		for (int i = 0; i < 8; i++) {
			patches.add(serializePatchInline(bank.patch[i]));
		}
		root.add("patches", patches);

		try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
			gson.toJson(root, writer);
		}
	}

	// ========================= BANK READ ===========================

	public static SpinCADBank readBank(String filePath) throws IOException {
		JsonObject root;
		try (Reader reader = new BufferedReader(new FileReader(filePath))) {
			root = JsonParser.parseReader(reader).getAsJsonObject();
		}

		SpinCADBank bank = new SpinCADBank();
		bank.bankFileName = getStringOrDefault(root, "bankFileName", "Untitled");

		if (root.has("comments")) {
			deserializeCommentBlock(root.getAsJsonObject("comments"), bank.cb);
		}

		if (root.has("patches")) {
			JsonArray patches = root.getAsJsonArray("patches");
			for (int i = 0; i < Math.min(8, patches.size()); i++) {
				bank.patch[i] = deserializePatchInline(patches.get(i).getAsJsonObject());
			}
		}

		return bank;
	}

	// ==================== COMMENT BLOCK HELPERS ====================

	private static JsonObject serializeCommentBlock(SpinCADCommentBlock cb) {
		JsonObject obj = new JsonObject();
		obj.addProperty("fileName", cb.fileName);
		if (cb.version != null) {
			obj.addProperty("version", cb.version);
		}
		JsonArray lines = new JsonArray();
		for (int i = 0; i < 5; i++) {
			lines.add(cb.line[i] != null ? cb.line[i] : "");
		}
		obj.add("lines", lines);
		return obj;
	}

	private static void deserializeCommentBlock(JsonObject obj, SpinCADCommentBlock cb) {
		if (obj.has("fileName")) cb.fileName = obj.get("fileName").getAsString();
		if (obj.has("version")) cb.version = obj.get("version").getAsString();
		if (obj.has("lines")) {
			JsonArray lines = obj.getAsJsonArray("lines");
			for (int i = 0; i < Math.min(5, lines.size()); i++) {
				cb.line[i] = lines.get(i).getAsString();
			}
		}
	}

	// ==================== MODEL SERIALIZATION ======================

	private static void serializeModel(SpinCADModel model, JsonObject root) {
		ArrayList<SpinCADBlock> blockList = model.getBlockList();
		JsonArray blocksArr = new JsonArray();
		JsonArray connectionsArr = new JsonArray();

		for (int i = 0; i < blockList.size(); i++) {
			SpinCADBlock block = blockList.get(i);
			JsonObject blockObj = new JsonObject();

			// type discriminator — full class name for reliable reconstruction
			blockObj.addProperty("className", block.getClass().getName());
			blockObj.addProperty("blockNum", block.getBlockNum());
			blockObj.addProperty("x", block.getX());
			blockObj.addProperty("y", block.getY());
			if (block.getName() != null) {
				blockObj.addProperty("name", block.getName());
			}

			// per-subclass parameters (fields declared on the concrete class,
			// excluding transient, static, and control panel references)
			JsonObject params = extractBlockParams(block);
			if (params.size() > 0) {
				blockObj.add("params", params);
			}

			blocksArr.add(blockObj);

			// connections: scan input pins for connections
			for (SpinCADPin pin : block.pinList) {
				if (pin.isInputPin() && pin.isConnected()) {
					SpinCADPin sourcePin = pin.getPinConnection();
					SpinCADBlock sourceBlock = pin.getBlockConnection();
					if (sourceBlock != null && sourcePin != null) {
						JsonObject conn = new JsonObject();
						conn.addProperty("fromBlockIndex", blockList.indexOf(sourceBlock));
						conn.addProperty("fromPin", sourcePin.getName());
						conn.addProperty("toBlockIndex", i);
						conn.addProperty("toPin", pin.getName());
						connectionsArr.add(conn);
					}
				}
			}
		}

		root.add("blocks", blocksArr);
		root.add("connections", connectionsArr);
	}

	private static void deserializeModel(JsonObject root, SpinCADModel model) {
		model.newModel();
		JsonArray blocksArr = root.getAsJsonArray("blocks");
		ArrayList<SpinCADBlock> blockList = new ArrayList<>();

		// Phase 1: instantiate all blocks
		for (int i = 0; i < blocksArr.size(); i++) {
			JsonObject blockObj = blocksArr.get(i).getAsJsonObject();
			String className = blockObj.get("className").getAsString();
			int x = blockObj.get("x").getAsInt();
			int y = blockObj.get("y").getAsInt();

			SpinCADBlock block = instantiateBlock(className, x, y);
			if (block == null) {
				System.err.println("WARNING: Could not instantiate block: " + className);
				continue;
			}

			// restore name if saved
			if (blockObj.has("name")) {
				block.setName(blockObj.get("name").getAsString());
			}

			// restore per-subclass parameters
			if (blockObj.has("params")) {
				applyBlockParams(block, blockObj.getAsJsonObject("params"));
			}

			model.addBlock(block);
			blockList.add(block);
		}

		// Phase 2: restore connections
		if (root.has("connections")) {
			JsonArray connectionsArr = root.getAsJsonArray("connections");
			for (int i = 0; i < connectionsArr.size(); i++) {
				JsonObject conn = connectionsArr.get(i).getAsJsonObject();
				int fromIdx = conn.get("fromBlockIndex").getAsInt();
				String fromPinName = conn.get("fromPin").getAsString();
				int toIdx = conn.get("toBlockIndex").getAsInt();
				String toPinName = conn.get("toPin").getAsString();

				if (fromIdx >= 0 && fromIdx < blockList.size() &&
					toIdx >= 0 && toIdx < blockList.size()) {

					SpinCADBlock fromBlock = blockList.get(fromIdx);
					SpinCADBlock toBlock = blockList.get(toIdx);
					SpinCADPin fromPin = fromBlock.getPin(fromPinName);
					SpinCADPin toPin = toBlock.getPin(toPinName);

					if (fromPin != null && toPin != null) {
						toPin.setConnection(fromBlock, fromPin);
					} else {
						System.err.println("WARNING: Could not restore connection " +
							fromPinName + " -> " + toPinName);
					}
				}
			}
		}
	}

	// ==================== INLINE PATCH (for banks) =================

	private static JsonObject serializePatchInline(SpinCADPatch patch) {
		JsonObject obj = new JsonObject();
		obj.addProperty("patchFileName", patch.patchFileName);
		obj.add("comments", serializeCommentBlock(patch.cb));

		JsonArray potVals = new JsonArray();
		for (int i = 0; i < 3; i++) {
			potVals.add(patch.getPotVal(i));
		}
		obj.add("potValues", potVals);

		if (patch.isHexFile) {
			obj.addProperty("isHexFile", true);
			JsonArray hexArr = new JsonArray();
			for (int h : patch.hexFile) {
				hexArr.add(h);
			}
			obj.add("hexFile", hexArr);
		}

		if (patch.patchModel != null && patch.patchModel.getBlockList() != null) {
			serializeModel(patch.patchModel, obj);
		}

		return obj;
	}

	private static SpinCADPatch deserializePatchInline(JsonObject obj) {
		SpinCADPatch patch = new SpinCADPatch();
		patch.patchFileName = getStringOrDefault(obj, "patchFileName", "Untitled");

		if (obj.has("comments")) {
			deserializeCommentBlock(obj.getAsJsonObject("comments"), patch.cb);
		}

		if (obj.has("potValues")) {
			JsonArray potVals = obj.getAsJsonArray("potValues");
			for (int i = 0; i < Math.min(3, potVals.size()); i++) {
				patch.setPotVal(i, potVals.get(i).getAsDouble());
			}
		}

		if (obj.has("isHexFile") && obj.get("isHexFile").getAsBoolean()) {
			patch.isHexFile = true;
			if (obj.has("hexFile")) {
				JsonArray hexArr = obj.getAsJsonArray("hexFile");
				for (int i = 0; i < Math.min(128, hexArr.size()); i++) {
					patch.hexFile[i] = hexArr.get(i).getAsInt();
				}
			}
		}

		if (obj.has("blocks")) {
			deserializeModel(obj, patch.patchModel);
		}

		return patch;
	}

	// ==================== REFLECTION HELPERS ========================

	/**
	 * Extracts non-static, non-transient fields declared on the block's
	 * concrete class (not inherited fields — those are handled by the
	 * constructor and connection restoration).
	 */
	private static JsonObject extractBlockParams(SpinCADBlock block) {
		JsonObject params = new JsonObject();
		Class<?> clazz = block.getClass();

		// Walk up to SpinCADBlock but don't include SpinCADBlock's own fields
		while (clazz != null && clazz != SpinCADBlock.class) {
			for (Field field : clazz.getDeclaredFields()) {
				int mods = field.getModifiers();
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

				// skip control panel references and non-serializable UI types
				Class<?> ft = field.getType();
				if (javax.swing.JFrame.class.isAssignableFrom(ft)) continue;
				if (javax.swing.JPanel.class.isAssignableFrom(ft)) continue;
				if (spinCADControlPanel.class.isAssignableFrom(ft)) continue;
				// skip fields whose type name contains "ControlPanel"
				if (ft.getSimpleName().contains("ControlPanel")) continue;

				field.setAccessible(true);
				try {
					Object value = field.get(block);
					addFieldToJson(params, field.getName(), ft, value);
				} catch (IllegalAccessException e) {
					System.err.println("WARNING: Could not read field " + field.getName() +
						" on " + clazz.getSimpleName());
				}
			}
			clazz = clazz.getSuperclass();
		}
		return params;
	}

	private static void addFieldToJson(JsonObject obj, String name, Class<?> type, Object value) {
		if (value == null) {
			obj.add(name, JsonNull.INSTANCE);
		} else if (type == int.class || type == Integer.class) {
			obj.addProperty(name, (Number) value);
		} else if (type == long.class || type == Long.class) {
			obj.addProperty(name, (Number) value);
		} else if (type == double.class || type == Double.class) {
			obj.addProperty(name, (Number) value);
		} else if (type == float.class || type == Float.class) {
			obj.addProperty(name, (Number) value);
		} else if (type == boolean.class || type == Boolean.class) {
			obj.addProperty(name, (Boolean) value);
		} else if (type == String.class) {
			obj.addProperty(name, (String) value);
		}
		// other types (arrays, objects) are skipped — they're either
		// runtime state or handled separately (connections, etc.)
	}

	/**
	 * Applies saved parameter values back to a block's fields via reflection.
	 */
	private static void applyBlockParams(SpinCADBlock block, JsonObject params) {
		Class<?> clazz = block.getClass();

		while (clazz != null && clazz != SpinCADBlock.class) {
			for (Field field : clazz.getDeclaredFields()) {
				int mods = field.getModifiers();
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

				String fieldName = field.getName();
				if (!params.has(fieldName)) continue;

				JsonElement elem = params.get(fieldName);
				if (elem.isJsonNull()) continue;

				field.setAccessible(true);
				try {
					Class<?> ft = field.getType();
					if (ft == int.class) {
						field.setInt(block, elem.getAsInt());
					} else if (ft == long.class) {
						field.setLong(block, elem.getAsLong());
					} else if (ft == double.class) {
						field.setDouble(block, elem.getAsDouble());
					} else if (ft == float.class) {
						field.setFloat(block, elem.getAsFloat());
					} else if (ft == boolean.class) {
						field.setBoolean(block, elem.getAsBoolean());
					} else if (ft == String.class) {
						field.set(block, elem.getAsString());
					} else if (ft == Integer.class) {
						field.set(block, elem.getAsInt());
					} else if (ft == Long.class) {
						field.set(block, elem.getAsLong());
					} else if (ft == Double.class) {
						field.set(block, elem.getAsDouble());
					} else if (ft == Float.class) {
						field.set(block, elem.getAsFloat());
					} else if (ft == Boolean.class) {
						field.set(block, elem.getAsBoolean());
					}
				} catch (Exception e) {
					System.err.println("WARNING: Could not set field " + fieldName +
						" on " + clazz.getSimpleName() + ": " + e.getMessage());
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

	/**
	 * Instantiates a SpinCADBlock subclass by class name using the (int, int) constructor.
	 */
	private static SpinCADBlock instantiateBlock(String className, int x, int y) {
		try {
			Class<?> clazz = Class.forName(className);
			java.lang.reflect.Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
			return (SpinCADBlock) ctor.newInstance(x, y);
		} catch (Exception e) {
			System.err.println("WARNING: Failed to instantiate " + className + ": " + e.getMessage());
			return null;
		}
	}

	// ==================== UTILITY ==================================

	private static String getStringOrDefault(JsonObject obj, String key, String defaultVal) {
		if (obj.has(key) && !obj.get(key).isJsonNull()) {
			return obj.get(key).getAsString();
		}
		return defaultVal;
	}
}
