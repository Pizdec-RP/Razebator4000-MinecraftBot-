package pizdecrp.MCAI.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;

import pizdecrp.MCAI.bot.Bot;
import pizdecrp.MCAI.utils.CraftingRecipe;

public interface IInventory {

	public void deconstuctItemArrayToIvn(ItemStack[] array, Bot bot);
	
	public void updateSlot(int slot, ItemStack item);

	//void craft(CraftingRecipe recipe);
	
}
