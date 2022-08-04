package net.PRP.MCAI.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.window.WindowType;

import net.PRP.MCAI.Main;

public class MinecraftData {
	public enum Type {
		HARD, VOID, AVOID, AIR, LIQUID, DOOR, GATE, DOOR_GATE, UNKNOWN, LADDER, GOAWAY, UNBREAKABLE, CARPET;
	}
	
	public Map<Integer, Type> bts = new HashMap<>();
	public Map<Integer, oldMinecraftBlocks> blockStates = new HashMap<>();// key - newid
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
	
	public static Type getTypeByState(int state) {
		return Main.getMCData().bt(Main.getMCData().blockStates.get(state).id);
	}
	
	public Type bt(int id) {
		Type aye = null;
		aye = bts.get(id);
		if (aye == null) return Type.UNKNOWN;
		return aye;
	}
}
