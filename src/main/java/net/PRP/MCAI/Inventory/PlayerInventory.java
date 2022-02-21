package net.PRP.MCAI.Inventory;

import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;

public interface PlayerInventory {
	
	public Map<Integer, ItemStack> slots = new HashMap<>();
	
	public void setup(ItemStack[] items);
	
	public void translate(WindowType it);

	public void setSlot(int slot, ItemStack item);

	public void setSlotInHotbar(Integer key);
}
