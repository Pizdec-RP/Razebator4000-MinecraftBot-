package net.PRP.MCAI.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;

public class GenericInventory {
	
	public boolean isOpened = false;
	public Map<Integer, ItemStack> slots = new HashMap<>();
	public Bot client;
	
	public GenericInventory(Bot client) {
		this.client = client;
	}
	
	public void setup(ItemStack[] items) {
		for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            slots.put(i, item);
        }
	}
	
	public boolean contain(int id, int count) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue().getId() == id && entry.getValue().getAmount() >= count) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contain(String name, int count) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name) && entry.getValue().getAmount() >= count) {
				return true;
			}
		}
		return false;
	}
	
	public boolean invContain(String name, int count) {
		for(Entry<Integer, ItemStack> entry : getInventory().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name) && entry.getValue().getAmount() >= count) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hotbarContain(String name, int count) {
		for(Entry<Integer, ItemStack> entry : getHotbar().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name) && entry.getValue().getAmount() >= count) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getHotbarContain(String name, int count) {
		for(Entry<Integer, ItemStack> entry : getHotbar().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name) && entry.getValue().getAmount() >= count) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public boolean fromInventoryToHotbar(List<String> names, int count) {
		Integer slotwithitem = getSlotsWithItem(getInventory(), names, count);
		if (slotwithitem == null) return false;
		client.crafter.fromSlotToSlotStack(slotwithitem, getRandomEmptySlot(getHotbar()));
		return true;
	}
	
	public Integer getRandomEmptySlot(Map<Integer,ItemStack> sts) {
		List<Integer> temp = new ArrayList<>();
		for (Entry<Integer, ItemStack> entry : sts.entrySet()) {
			if (entry.getValue() == null) {
				temp.add(entry.getKey());
			}
		}
		if (temp.size() == 0) {
			for (Entry<Integer, ItemStack> entry : sts.entrySet()) {
				temp.add(entry.getKey());
			}
		}
		return temp.get(MathU.rnd(0, temp.size()-1));
	}
	
	public boolean contain(int id) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (entry.getValue().getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getSlotWithItem(String name) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name)) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public boolean contain(String name) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name)) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getSlotsWithItem(Map<Integer,ItemStack> sts, List<String> names, int count) {
		for (String name : names) {
			for(Entry<Integer, ItemStack> entry : sts.entrySet()) {
				if (entry.getValue() != null ) {
				String bname = Main.getMCData().items.get(entry.getValue().getId()).name;
				if (entry.getValue() != null) if (bname.contains(name) && entry.getValue().getAmount() >= count) {
					return entry.getKey();
				}}
			}
		}
		return null;
	}
	
	public Integer getSlotWithItem(String name, int count) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (Main.getMCData().items.get(entry.getValue().getId()).name.contains(name) && entry.getValue().getAmount() >= count) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public Integer getSlotWithItem(int id, int count) {
		for(Entry<Integer, ItemStack> entry : getAllInventory().entrySet()) {
			if (entry.getValue() != null) if (entry.getValue().getId() == id && entry.getValue().getAmount() >= count) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public ItemStack getSlot(int id) {
		return slots.get(id);
	}
	
	public void fromSlotToHotbar() {
		
	}
	
	public Map<Integer, ItemStack> getHotbar() {
		Map<Integer, ItemStack> temp = new HashMap<>();
		int m = 0;
		if (client.crafter.windowType != null) m = Main.getMCData().slotMultipiler.get(client.crafter.windowType);
		for (int i = 36+m; i <= 44+m; i++) {
			temp.put(i, slots.get(i));
		}
		return temp;
	}
	
	public void setSlot(int slot, ItemStack item) {
		slots.replace(slot, item);
	}
	
	public void setSlotInHotbar(int slot) {
		BotU.SetSlot(client, slot - 36);
	}
	
	public Map<Integer, ItemStack> getInventory() {
		Map<Integer, ItemStack> temp = new HashMap<>();
		int m = 0;
		if (client.crafter.windowType != null) m = Main.getMCData().slotMultipiler.get(client.crafter.windowType);
		for (int i = 9+m; i <= 35+m; i++) {
			temp.put(i, slots.get(i));
		}
		return temp;
	}
	
	public Map<Integer, ItemStack> getAllInventory() {
		Map<Integer, ItemStack> temp = new HashMap<>();
		int m = 0;
		if (client.crafter.windowType != null) m = Main.getMCData().slotMultipiler.get(client.crafter.windowType);
		for (int i = 9+m; i <= 44+m; i++) {
			temp.put(i, slots.get(i));
		}
		return temp;
	}
}
