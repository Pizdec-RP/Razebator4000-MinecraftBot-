package net.PRP.MCAI.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.google.gson.JsonObject;

import net.PRP.MCAI.Main;

public class MinecraftData {
	public enum Type {
		HARD, VOID, AVOID, AIR, LIQUID, DOOR, GATE, DOOR_GATE, UNKNOWN, LADDER, GOAWAY, UNBREAKABLE, CARPET;
	}
	public JsonObject blocksJson;
	public Map<Integer, Type> bts = new HashMap<>();
	public Map<Integer, oldMinecraftBlocks> blockStates = new HashMap<>();// key - state, value - oldid,name
	public Map<Integer, BlockData> blockData = new HashMap<>();//key - oldid
	public Map<String, List<materialsBreakTime>> materialToolMultipliers = new HashMap<>();// key - material
	public Map<Integer, ItemData> items = new HashMap<>(); // itemid | itemdata
	public Map<Integer, slabState> slabstates = new HashMap<>(); //newid | slabstate
	public Map<WindowType, Integer> slotMultipiler = new HashMap<WindowType, Integer>() {
	private static final long serialVersionUID = 1L;
	{
		put(WindowType.CRAFTING, 1);
		put(WindowType.GENERIC_9X3, 18);
		put(WindowType.GENERIC_9X4, 27);
		put(WindowType.GENERIC_9X5, 36);
		put(WindowType.GENERIC_9X6, 45);
	}};
	public static String codecc = "===================1===========1=================21=221==221==221=";
	public MinecraftData() {
		
	}
	
	public int oldIdToNew(int oldid) {
		for (Entry<Integer, oldMinecraftBlocks> temp : blockStates.entrySet()) {
			if (temp.getValue().id == oldid) return temp.getKey();
		}
		return -1;
	}
	
	public int itemToOldId(int itemid) {
		int i = -1;
		for (Entry<Integer, BlockData> entry:blockData.entrySet()) {
			for (int iid:entry.getValue().drops) {
				if (iid == itemid) i = entry.getKey();
			}
		}
		return i;
	}
	
	public Type getTypeByState(int state) {
		return Main.getMCData().bt(Main.getMCData().blockStates.get(state).id);
	}
	
	public short TypeToInt(Type t) {
		short i = 0;
		switch (t) {
		case AIR:
			i = 1;
			break;
		case AVOID:
			i = 1;
			break;
		case CARPET:
			i = 2;
			break;
		case DOOR:
			i = 3;
			break;
		case DOOR_GATE:
			i = 4;
			break;
		case GATE:
			i = 5;
			break;
		case GOAWAY:
			i = 6;
			break;
		case HARD:
			i = 7;
			break;
		case LADDER:
			i = 8;
			break;
		case LIQUID:
			i = 9;
			break;
		case UNBREAKABLE:
			i = 10;
			break;
		case UNKNOWN:
			i = 11;
			break;
		case VOID:
			i = 12;
			break;
		default:
			i = 11;
			break;
		}
		return i;
	}
	
	public Type bt(int id) {
		Type aye = null;
		aye = bts.get(id);
		if (aye == null) return Type.UNKNOWN;
		return aye;
	}

	public int nameToState(String blockname) {
		return 0;
	}
}
