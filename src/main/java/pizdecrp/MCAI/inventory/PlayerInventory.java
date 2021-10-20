package pizdecrp.MCAI.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;

import pizdecrp.MCAI.bot.Bot;
import pizdecrp.MCAI.utils.CraftingRecipe;

public class PlayerInventory implements IInventory {

	private ItemStack inventoryArray[];
	private ItemStack hotbar[];
	private ItemStack armor[];
	private ItemStack crafting[];
	private ItemStack offhand;

	private Bot bot;

	@Override
	public void deconstuctItemArrayToIvn(ItemStack[] array, Bot bot) {
		try {
			this.bot = bot;
			crafting = new ItemStack[4];
			armor = new ItemStack[4];
			inventoryArray = new ItemStack[27];
			hotbar = new ItemStack[9];
	
			for (int i = 0; i < 4; i++) {
				crafting[i] = array[i];
			}
			int armorInt = 0;
			for (int i = 5; i < 8; i++) {
				armor[armorInt] = array[i];
				armorInt++;
			}
			int invInt = 0;
			for (int i = 9; i < 35; i++) {
				inventoryArray[invInt] = array[i];
				invInt++;
			}
			int hotbarInt = 0;
			for (int i = 36; i < 44; i++) {
				hotbar[hotbarInt] = array[i];
				hotbarInt++;
			}
			offhand = array[45];
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateSlot(int slot, ItemStack item) {
		try {
			////переделать ---------------------------------------------------
			if (slot < 0) {
				return;
			}
			//  1-4 craft, 5-8 armor, 9-35 inv, 36-44 hotbar
			if (slot <= 4) {
				int arrayId = slot - 1;
				System.out.println("craft slot id:"+slot+" array id:"+arrayId);
				crafting[slot] = item;
			} else if (slot < 9 && slot > 4) {
				int arrayId = slot - 5;
				armor[arrayId] = item;
				System.out.println("inventory slot id:"+slot+" array id:"+arrayId);
			} else if (slot >= 9 && slot <= 35) {
				int arrayId = slot - 9;
				inventoryArray[arrayId] = item;
				System.out.println("inventory slot id:"+slot+" array id:"+arrayId);
			} else if (slot >= 36 && slot <= 44) {
				int arrayId = slot - 36;
				hotbar[arrayId] = item;
				System.out.println("hotbar slot id:"+slot+" array id:"+arrayId);
			} else if (slot == 45) {
				offhand = item;
				System.out.println("offhand "+slot);
			} else {
				System.out.println("Invalid lenght of slot index(slot: " + slot + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ItemStack getItemInOffHand() {
		return offhand;
	}

	public ItemStack getItemInhand() {
		return hotbar[bot.currentSlotInHand];
	}

	public ItemStack getItemInInventoryAt(int index) {
		return inventoryArray[index];
	}

	public ItemStack getItemInHotbar(int index) {
		return hotbar[index];
	}

	public ItemStack getItemInArmor(int index) {
		return armor[index];
	}

	public ItemStack getItemInCrafting(int index) {
		return crafting[index];
	}

	public void setItemInOffhand(ItemStack item) {
		offhand = item;
	}

	public void setItemInHand(ItemStack item) {
		hotbar[bot.currentSlotInHand] = item;
	}

	public void setItemInInventoryAt(int index, ItemStack item) {
		inventoryArray[index] = item;
	}

	public void setItemInArmor(int index, ItemStack item) {
		armor[index] = item;
	}

	public void setItemInHotBar(int index, ItemStack item) {
		hotbar[index] = item;
	}

	public void setItemInCrafting(int index, ItemStack item) {
		crafting[index] = item;
	}

	public ItemStack[] getInventory() {
		return inventoryArray;
	}

	public ItemStack[] getHotbar() {
		return hotbar;
	}

	public ItemStack[] getCrafting() {
		return crafting;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public void moveHotbar(int index) {
		ClientPlayerChangeHeldItemPacket p = new ClientPlayerChangeHeldItemPacket(index);
		bot.getSession().send(p);
	}

	@Override
	public void craft(CraftingRecipe recipe) {
		// TODO Auto-generated method stub
		
	}
}
