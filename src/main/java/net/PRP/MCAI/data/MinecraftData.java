package net.PRP.MCAI.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.window.WindowType;

import net.PRP.MCAI.oldMinecraftBlocks;

public class MinecraftData {
	public enum Type {
		HARD, VOID, AVOID, AIR, LIQUID, DOOR, GATE, DOOR_GATE, UNKNOWN, LADDER, GOAWAY, UNBREAKABLE;
	}
	
	public Map<Integer, Type> bts = new HashMap<>();
	public Map<Integer, oldMinecraftBlocks> blockStates = new HashMap<>();// key - newid
	public Map<Integer, BlockData> blockData = new HashMap<>();//key - oldid
	public Map<String, List<materialsBreakTime>> materialToolMultipliers = new HashMap<>();// key - material
	public Map<Integer, ItemData> items = new HashMap<>(); // itemid | itemdata
	public Map<WindowType, Integer> slotMultipiler = new HashMap<>() {
	private static final long serialVersionUID = 1L;
	{
		put(WindowType.CRAFTING, 1);
	}};
	
	public MinecraftData() {
		
	}
	
	public Type bt(int id) {
		Type aye = null;
		aye = bts.get(id);
		if (aye == null) return Type.UNKNOWN;
		return aye;
	}
}
