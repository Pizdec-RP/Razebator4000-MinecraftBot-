package net.PRP.MCAI.Inventory;

import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;

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
