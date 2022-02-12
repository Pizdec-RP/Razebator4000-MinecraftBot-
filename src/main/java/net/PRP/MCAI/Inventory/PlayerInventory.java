package net.PRP.MCAI.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;

public class PlayerInventory {
	
	public boolean isOpened = false;
	public Map<Integer, ItemStack> slots = new HashMap<>();
	public Bot client;
	
	public PlayerInventory(Bot client) {
		this.client = client;
	}
	
	public void setup(ItemStack[] items) {
		for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            slots.put(i, item);
        }
	}
	
	public boolean contain(int id, int count) {
		for(Entry<Integer, ItemStack> entry : slots.entrySet()) {
			if (entry.getValue().getId() == id && entry.getValue().getAmount() >= count) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getRandomEmptySlot() {
		List<Integer> temp = new ArrayList<>();
		for (int i = 9; i <= 44; i++) {
			if (slots.get(i) == null || !slots.containsKey(i)) {
				temp.add(i);
			}
		}
		if (temp.size() == 0) return null;
		return temp.get(MathU.rnd(0, temp.size()-1));
	}
	
	public void oneItemFromSlotToSlot(int fromslot, int toslot) {
		
	}
	
	public boolean contain(int id) {
		for(Entry<Integer, ItemStack> entry : slots.entrySet()) {
			if (entry.getValue().getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public Integer getSlotWithItem(int id, int count) {
		for(Entry<Integer, ItemStack> entry : slots.entrySet()) {
			if (entry.getValue().getId() == id && entry.getValue().getAmount() >= count) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	public ItemStack getSlot(int id) {
		return slots.get(id);
	}
	
	public Map<Integer, ItemStack> getHotbar() {
		Map<Integer, ItemStack> temp = new HashMap<>();
		for (int i = 36; i <= 44; i++) {
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
}