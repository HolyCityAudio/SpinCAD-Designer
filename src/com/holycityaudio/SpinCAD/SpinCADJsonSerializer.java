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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles reading and writing SpinCADPatch and SpinCADBank objects
 * in JSON format using only built-in Java (no Gson dependency).
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

	// ========================= PATCH WRITE =========================

	public static void writePatch(SpinCADPatch patch, String filePath) throws IOException {
		Map<String, Object> root = new LinkedHashMap<>();
		root.put("formatVersion", FORMAT_VERSION);
		root.put("type", "patch");
		root.put("patchFileName", patch.patchFileName);

		root.put("comments", serializeCommentBlock(patch.cb));

		List<Object> potVals = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			potVals.add(patch.getPotVal(i));
		}
		root.put("potValues", potVals);

		if (patch.isHexFile) {
			root.put("isHexFile", true);
			List<Object> hexArr = new ArrayList<>();
			for (int h : patch.hexFile) {
				hexArr.add(h);
			}
			root.put("hexFile", hexArr);
		}

		if (patch.patchModel != null && patch.patchModel.blockList != null) {
			serializeModel(patch.patchModel, root);
		}

		try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
			writeJson(root, writer, 0);
		}
	}

	// ========================= PATCH READ ==========================

	public static SpinCADPatch readPatch(String filePath) throws IOException {
		String json;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
			json = sb.toString();
		}

		Map<String, Object> root = parseJsonObject(json);
		SpinCADPatch patch = new SpinCADPatch();
		patch.patchFileName = getStringOrDefault(root, "patchFileName", "Untitled");

		if (root.containsKey("comments")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> comments = (Map<String, Object>) root.get("comments");
			deserializeCommentBlock(comments, patch.cb);
		}

		if (root.containsKey("potValues")) {
			@SuppressWarnings("unchecked")
			List<Object> potVals = (List<Object>) root.get("potValues");
			for (int i = 0; i < Math.min(3, potVals.size()); i++) {
				patch.setPotVal(i, toDouble(potVals.get(i)));
			}
		}

		if (root.containsKey("isHexFile") && Boolean.TRUE.equals(root.get("isHexFile"))) {
			patch.isHexFile = true;
			if (root.containsKey("hexFile")) {
				@SuppressWarnings("unchecked")
				List<Object> hexArr = (List<Object>) root.get("hexFile");
				for (int i = 0; i < Math.min(128, hexArr.size()); i++) {
					patch.hexFile[i] = toInt(hexArr.get(i));
				}
			}
		}

		if (root.containsKey("blocks")) {
			deserializeModel(root, patch.patchModel);
		}

		return patch;
	}

	// ========================= BANK WRITE ==========================

	public static void writeBank(SpinCADBank bank, String filePath) throws IOException {
		Map<String, Object> root = new LinkedHashMap<>();
		root.put("formatVersion", FORMAT_VERSION);
		root.put("type", "bank");
		root.put("bankFileName", bank.bankFileName);
		root.put("comments", serializeCommentBlock(bank.cb));

		List<Object> patches = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			patches.add(serializePatchInline(bank.patch[i]));
		}
		root.put("patches", patches);

		try (Writer writer = new BufferedWriter(new FileWriter(filePath))) {
			writeJson(root, writer, 0);
		}
	}

	// ========================= BANK READ ===========================

	public static SpinCADBank readBank(String filePath) throws IOException {
		String json;
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append('\n');
			}
			json = sb.toString();
		}

		Map<String, Object> root = parseJsonObject(json);
		SpinCADBank bank = new SpinCADBank();
		bank.bankFileName = getStringOrDefault(root, "bankFileName", "Untitled");

		if (root.containsKey("comments")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> comments = (Map<String, Object>) root.get("comments");
			deserializeCommentBlock(comments, bank.cb);
		}

		if (root.containsKey("patches")) {
			@SuppressWarnings("unchecked")
			List<Object> patches = (List<Object>) root.get("patches");
			for (int i = 0; i < Math.min(8, patches.size()); i++) {
				@SuppressWarnings("unchecked")
				Map<String, Object> patchObj = (Map<String, Object>) patches.get(i);
				bank.patch[i] = deserializePatchInline(patchObj);
			}
		}

		return bank;
	}

	// ==================== COMMENT BLOCK HELPERS ====================

	private static Map<String, Object> serializeCommentBlock(SpinCADCommentBlock cb) {
		Map<String, Object> obj = new LinkedHashMap<>();
		obj.put("fileName", cb.fileName);
		if (cb.version != null) {
			obj.put("version", cb.version);
		}
		List<Object> lines = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			lines.add(cb.line[i] != null ? cb.line[i] : "");
		}
		obj.put("lines", lines);
		return obj;
	}

	private static void deserializeCommentBlock(Map<String, Object> obj, SpinCADCommentBlock cb) {
		if (obj.containsKey("fileName")) cb.fileName = (String) obj.get("fileName");
		if (obj.containsKey("version")) cb.version = (String) obj.get("version");
		if (obj.containsKey("lines")) {
			@SuppressWarnings("unchecked")
			List<Object> lines = (List<Object>) obj.get("lines");
			for (int i = 0; i < Math.min(5, lines.size()); i++) {
				cb.line[i] = (String) lines.get(i);
			}
		}
	}

	// ==================== MODEL SERIALIZATION ======================

	private static void serializeModel(SpinCADModel model, Map<String, Object> root) {
		ArrayList<SpinCADBlock> blockList = model.blockList;
		List<Object> blocksArr = new ArrayList<>();
		List<Object> connectionsArr = new ArrayList<>();

		for (int i = 0; i < blockList.size(); i++) {
			SpinCADBlock block = blockList.get(i);
			Map<String, Object> blockObj = new LinkedHashMap<>();

			blockObj.put("className", block.getClass().getName());
			blockObj.put("blockNum", block.getBlockNum());
			blockObj.put("x", block.getX());
			blockObj.put("y", block.getY());
			if (block.getName() != null) {
				blockObj.put("name", block.getName());
			}

			Map<String, Object> params = extractBlockParams(block);
			if (!params.isEmpty()) {
				blockObj.put("params", params);
			}

			blocksArr.add(blockObj);

			for (SpinCADPin pin : block.pinList) {
				if (pin.isInputPin() && pin.isConnected()) {
					SpinCADPin sourcePin = pin.getPinConnection();
					SpinCADBlock sourceBlock = pin.getBlockConnection();
					if (sourceBlock != null && sourcePin != null) {
						Map<String, Object> conn = new LinkedHashMap<>();
						conn.put("fromBlockIndex", blockList.indexOf(sourceBlock));
						conn.put("fromPin", sourcePin.getName());
						conn.put("toBlockIndex", i);
						conn.put("toPin", pin.getName());
						connectionsArr.add(conn);
					}
				}
			}
		}

		root.put("blocks", blocksArr);
		root.put("connections", connectionsArr);
	}

	private static void deserializeModel(Map<String, Object> root, SpinCADModel model) {
		model.newModel();
		@SuppressWarnings("unchecked")
		List<Object> blocksArr = (List<Object>) root.get("blocks");
		ArrayList<SpinCADBlock> blockList = new ArrayList<>();

		for (int i = 0; i < blocksArr.size(); i++) {
			@SuppressWarnings("unchecked")
			Map<String, Object> blockObj = (Map<String, Object>) blocksArr.get(i);
			String className = (String) blockObj.get("className");
			int x = toInt(blockObj.get("x"));
			int y = toInt(blockObj.get("y"));

			SpinCADBlock block = instantiateBlock(className, x, y);
			if (block == null) {
				System.err.println("WARNING: Could not instantiate block: " + className);
				continue;
			}

			if (blockObj.containsKey("name")) {
				block.setName((String) blockObj.get("name"));
			}

			if (blockObj.containsKey("params")) {
				@SuppressWarnings("unchecked")
				Map<String, Object> params = (Map<String, Object>) blockObj.get("params");
				applyBlockParams(block, params);
			}

			model.addBlock(block);
			blockList.add(block);
		}

		if (root.containsKey("connections")) {
			@SuppressWarnings("unchecked")
			List<Object> connectionsArr = (List<Object>) root.get("connections");
			for (int i = 0; i < connectionsArr.size(); i++) {
				@SuppressWarnings("unchecked")
				Map<String, Object> conn = (Map<String, Object>) connectionsArr.get(i);
				int fromIdx = toInt(conn.get("fromBlockIndex"));
				String fromPinName = (String) conn.get("fromPin");
				int toIdx = toInt(conn.get("toBlockIndex"));
				String toPinName = (String) conn.get("toPin");

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

	private static Map<String, Object> serializePatchInline(SpinCADPatch patch) {
		Map<String, Object> obj = new LinkedHashMap<>();
		obj.put("patchFileName", patch.patchFileName);
		obj.put("comments", serializeCommentBlock(patch.cb));

		List<Object> potVals = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			potVals.add(patch.getPotVal(i));
		}
		obj.put("potValues", potVals);

		if (patch.isHexFile) {
			obj.put("isHexFile", true);
			List<Object> hexArr = new ArrayList<>();
			for (int h : patch.hexFile) {
				hexArr.add(h);
			}
			obj.put("hexFile", hexArr);
		}

		if (patch.patchModel != null && patch.patchModel.blockList != null) {
			serializeModel(patch.patchModel, obj);
		}

		return obj;
	}

	private static SpinCADPatch deserializePatchInline(Map<String, Object> obj) {
		SpinCADPatch patch = new SpinCADPatch();
		patch.patchFileName = getStringOrDefault(obj, "patchFileName", "Untitled");

		if (obj.containsKey("comments")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> comments = (Map<String, Object>) obj.get("comments");
			deserializeCommentBlock(comments, patch.cb);
		}

		if (obj.containsKey("potValues")) {
			@SuppressWarnings("unchecked")
			List<Object> potVals = (List<Object>) obj.get("potValues");
			for (int i = 0; i < Math.min(3, potVals.size()); i++) {
				patch.setPotVal(i, toDouble(potVals.get(i)));
			}
		}

		if (obj.containsKey("isHexFile") && Boolean.TRUE.equals(obj.get("isHexFile"))) {
			patch.isHexFile = true;
			if (obj.containsKey("hexFile")) {
				@SuppressWarnings("unchecked")
				List<Object> hexArr = (List<Object>) obj.get("hexFile");
				for (int i = 0; i < Math.min(128, hexArr.size()); i++) {
					patch.hexFile[i] = toInt(hexArr.get(i));
				}
			}
		}

		if (obj.containsKey("blocks")) {
			deserializeModel(obj, patch.patchModel);
		}

		return patch;
	}

	// ==================== REFLECTION HELPERS ========================

	private static Map<String, Object> extractBlockParams(SpinCADBlock block) {
		Map<String, Object> params = new LinkedHashMap<>();
		Class<?> clazz = block.getClass();

		while (clazz != null && clazz != SpinCADBlock.class) {
			for (Field field : clazz.getDeclaredFields()) {
				int mods = field.getModifiers();
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

				Class<?> ft = field.getType();
				if (javax.swing.JFrame.class.isAssignableFrom(ft)) continue;
				if (javax.swing.JDialog.class.isAssignableFrom(ft)) continue;
				if (javax.swing.JPanel.class.isAssignableFrom(ft)) continue;
				if (spinCADControlPanel.class.isAssignableFrom(ft)) continue;
				if (ft.getSimpleName().contains("ControlPanel")) continue;

				field.setAccessible(true);
				try {
					Object value = field.get(block);
					if (value == null) {
						params.put(field.getName(), null);
					} else if (ft == int.class || ft == Integer.class ||
							   ft == long.class || ft == Long.class ||
							   ft == double.class || ft == Double.class ||
							   ft == float.class || ft == Float.class) {
						params.put(field.getName(), value);
					} else if (ft == boolean.class || ft == Boolean.class) {
						params.put(field.getName(), value);
					} else if (ft == String.class) {
						params.put(field.getName(), value);
					}
				} catch (IllegalAccessException e) {
					System.err.println("WARNING: Could not read field " + field.getName() +
						" on " + clazz.getSimpleName());
				}
			}
			clazz = clazz.getSuperclass();
		}
		return params;
	}

	private static void applyBlockParams(SpinCADBlock block, Map<String, Object> params) {
		Class<?> clazz = block.getClass();

		while (clazz != null && clazz != SpinCADBlock.class) {
			for (Field field : clazz.getDeclaredFields()) {
				int mods = field.getModifiers();
				if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

				String fieldName = field.getName();
				if (!params.containsKey(fieldName)) continue;

				Object value = params.get(fieldName);
				if (value == null) continue;

				field.setAccessible(true);
				try {
					Class<?> ft = field.getType();
					if (ft == int.class || ft == Integer.class) {
						field.setInt(block, toInt(value));
					} else if (ft == long.class || ft == Long.class) {
						field.setLong(block, toLong(value));
					} else if (ft == double.class || ft == Double.class) {
						field.setDouble(block, toDouble(value));
					} else if (ft == float.class || ft == Float.class) {
						field.setFloat(block, toFloat(value));
					} else if (ft == boolean.class || ft == Boolean.class) {
						field.setBoolean(block, (Boolean) value);
					} else if (ft == String.class) {
						field.set(block, value);
					}
				} catch (Exception e) {
					System.err.println("WARNING: Could not set field " + fieldName +
						" on " + clazz.getSimpleName() + ": " + e.getMessage());
				}
			}
			clazz = clazz.getSuperclass();
		}
	}

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

	// ==================== NUMBER CONVERSION ========================

	private static int toInt(Object o) {
		if (o instanceof Number) return ((Number) o).intValue();
		return Integer.parseInt(o.toString());
	}

	private static long toLong(Object o) {
		if (o instanceof Number) return ((Number) o).longValue();
		return Long.parseLong(o.toString());
	}

	private static double toDouble(Object o) {
		if (o instanceof Number) return ((Number) o).doubleValue();
		return Double.parseDouble(o.toString());
	}

	private static float toFloat(Object o) {
		if (o instanceof Number) return ((Number) o).floatValue();
		return Float.parseFloat(o.toString());
	}

	private static String getStringOrDefault(Map<String, Object> obj, String key, String defaultVal) {
		if (obj.containsKey(key) && obj.get(key) != null) {
			return obj.get(key).toString();
		}
		return defaultVal;
	}

	// ==================== JSON WRITER ==============================

	@SuppressWarnings("unchecked")
	private static void writeJson(Object value, Writer writer, int indent) throws IOException {
		if (value == null) {
			writer.write("null");
		} else if (value instanceof Map) {
			writeJsonObject((Map<String, Object>) value, writer, indent);
		} else if (value instanceof List) {
			writeJsonArray((List<Object>) value, writer, indent);
		} else if (value instanceof String) {
			writer.write('"');
			writer.write(escapeJsonString((String) value));
			writer.write('"');
		} else if (value instanceof Boolean) {
			writer.write(value.toString());
		} else if (value instanceof Number) {
			writer.write(value.toString());
		} else {
			writer.write('"');
			writer.write(escapeJsonString(value.toString()));
			writer.write('"');
		}
	}

	private static void writeJsonObject(Map<String, Object> map, Writer writer, int indent) throws IOException {
		writer.write("{\n");
		String pad = repeat("  ", indent + 1);
		int count = 0;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (count > 0) writer.write(",\n");
			writer.write(pad);
			writer.write('"');
			writer.write(escapeJsonString(entry.getKey()));
			writer.write("\": ");
			writeJson(entry.getValue(), writer, indent + 1);
			count++;
		}
		writer.write('\n');
		writer.write(repeat("  ", indent));
		writer.write('}');
	}

	private static void writeJsonArray(List<Object> list, Writer writer, int indent) throws IOException {
		writer.write("[\n");
		String pad = repeat("  ", indent + 1);
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) writer.write(",\n");
			writer.write(pad);
			writeJson(list.get(i), writer, indent + 1);
		}
		writer.write('\n');
		writer.write(repeat("  ", indent));
		writer.write(']');
	}

	private static String escapeJsonString(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '"': sb.append("\\\""); break;
				case '\\': sb.append("\\\\"); break;
				case '\n': sb.append("\\n"); break;
				case '\r': sb.append("\\r"); break;
				case '\t': sb.append("\\t"); break;
				default:
					if (c < 0x20) {
						sb.append(String.format("\\u%04x", (int) c));
					} else {
						sb.append(c);
					}
			}
		}
		return sb.toString();
	}

	private static String repeat(String s, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) sb.append(s);
		return sb.toString();
	}

	// ==================== JSON PARSER ==============================

	private static Map<String, Object> parseJsonObject(String json) {
		int[] pos = {0};
		skipWhitespace(json, pos);
		return readObject(json, pos);
	}

	@SuppressWarnings("unchecked")
	private static Object readValue(String json, int[] pos) {
		skipWhitespace(json, pos);
		char c = json.charAt(pos[0]);
		if (c == '{') return readObject(json, pos);
		if (c == '[') return readArray(json, pos);
		if (c == '"') return readString(json, pos);
		if (c == 't' || c == 'f') return readBoolean(json, pos);
		if (c == 'n') return readNull(json, pos);
		return readNumber(json, pos);
	}

	private static Map<String, Object> readObject(String json, int[] pos) {
		Map<String, Object> map = new LinkedHashMap<>();
		pos[0]++; // skip '{'
		skipWhitespace(json, pos);
		if (json.charAt(pos[0]) == '}') { pos[0]++; return map; }
		while (true) {
			skipWhitespace(json, pos);
			String key = readString(json, pos);
			skipWhitespace(json, pos);
			pos[0]++; // skip ':'
			Object value = readValue(json, pos);
			map.put(key, value);
			skipWhitespace(json, pos);
			if (json.charAt(pos[0]) == ',') { pos[0]++; continue; }
			if (json.charAt(pos[0]) == '}') { pos[0]++; break; }
		}
		return map;
	}

	private static List<Object> readArray(String json, int[] pos) {
		List<Object> list = new ArrayList<>();
		pos[0]++; // skip '['
		skipWhitespace(json, pos);
		if (json.charAt(pos[0]) == ']') { pos[0]++; return list; }
		while (true) {
			list.add(readValue(json, pos));
			skipWhitespace(json, pos);
			if (json.charAt(pos[0]) == ',') { pos[0]++; continue; }
			if (json.charAt(pos[0]) == ']') { pos[0]++; break; }
		}
		return list;
	}

	private static String readString(String json, int[] pos) {
		pos[0]++; // skip opening '"'
		StringBuilder sb = new StringBuilder();
		while (pos[0] < json.length()) {
			char c = json.charAt(pos[0]++);
			if (c == '"') return sb.toString();
			if (c == '\\') {
				char esc = json.charAt(pos[0]++);
				switch (esc) {
					case '"': sb.append('"'); break;
					case '\\': sb.append('\\'); break;
					case '/': sb.append('/'); break;
					case 'n': sb.append('\n'); break;
					case 'r': sb.append('\r'); break;
					case 't': sb.append('\t'); break;
					case 'u':
						String hex = json.substring(pos[0], pos[0] + 4);
						sb.append((char) Integer.parseInt(hex, 16));
						pos[0] += 4;
						break;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private static Number readNumber(String json, int[] pos) {
		int start = pos[0];
		boolean isFloat = false;
		if (json.charAt(pos[0]) == '-') pos[0]++;
		while (pos[0] < json.length()) {
			char c = json.charAt(pos[0]);
			if (c == '.' || c == 'e' || c == 'E') isFloat = true;
			if (Character.isDigit(c) || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-' && pos[0] > start) {
				pos[0]++;
			} else {
				break;
			}
		}
		String numStr = json.substring(start, pos[0]);
		if (isFloat) return Double.parseDouble(numStr);
		long val = Long.parseLong(numStr);
		if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) return (int) val;
		return val;
	}

	private static Boolean readBoolean(String json, int[] pos) {
		if (json.startsWith("true", pos[0])) { pos[0] += 4; return Boolean.TRUE; }
		if (json.startsWith("false", pos[0])) { pos[0] += 5; return Boolean.FALSE; }
		throw new RuntimeException("Invalid boolean at position " + pos[0]);
	}

	private static Object readNull(String json, int[] pos) {
		if (json.startsWith("null", pos[0])) { pos[0] += 4; return null; }
		throw new RuntimeException("Invalid null at position " + pos[0]);
	}

	private static void skipWhitespace(String json, int[] pos) {
		while (pos[0] < json.length() && Character.isWhitespace(json.charAt(pos[0]))) {
			pos[0]++;
		}
	}
}
