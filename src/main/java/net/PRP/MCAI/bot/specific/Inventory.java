package net.PRP.MCAI.bot.specific;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.ThreadU;

public class Inventory extends SessionAdapter {

	private Bot client;
	public Map<Integer, ItemStack> slots = new HashMap<>();
	public iState state = iState.IDLE;
	public int currentWindowId = 0;
	private List<invTask> ActionPool = new CopyOnWriteArrayList<>();
	private int armorTickTest = 0;
	//netherite,diamond,iron,golden,chain,leather
	int[] helmets = new int[] {642,634,638,630,626,622,570};
	int[] chestplates = new int[] {643,635,631,639,627,623};
	int[] leggings = new int[] {644,636,632,640,628,624};
	int[] boots = new int[] {645,637,633,641,629,625};
	
	public Inventory(Bot client) {
		this.client = client;
	}
	
	public enum iState {
		IDLE;
	}
	
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
			ThreadU.sleep(1000);
			BotU.SetSlot(client, 0);
		} else if (receiveEvent.getPacket() instanceof ServerWindowItemsPacket) {
			final ServerWindowItemsPacket p = (ServerWindowItemsPacket) receiveEvent.getPacket();
			client.playerInventory.setup(p.getItems());
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			final ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			//if (client.crafter.windowType != null) client.playerInventory.setSlot(p.getSlot()-Main.getMCData().slotMultipiler.get(client.crafter.windowType), p.getItem());
			/*else*/client.playerInventory.setSlot(p.getSlot(), p.getItem());
			currentWindowId = p.getWindowId();
			if (Main.debug && p.getItem() != null) System.out.println("sssp slot:"+p.getSlot()+ " item:"+Main.getMCData().items.get(p.getItem().getId()).name);
		}
	}
	
	public void tick() {
		armorTickTest++;
		if (!ActionPool.isEmpty()) {
			invTask act = ActionPool.get(0);
			if (act instanceof finvtohb) {
				finvtohb cl = (finvtohb)act;
				fromInventoryToHotbarExec(cl.names,cl.count);
				ActionPool.remove(0);
			}
		} else {
			if (armorTickTest >= 100) {
				armorTickTest = 0;
				for (Entry<Integer, ItemStack> p : getAllInventory().entrySet()) {
					if (p.getValue() != null && client.crafter.windowType == null) {
						for (int i : helmets) {
							if (getSlot(5) != null) break;
							if (i == p.getValue().getId()) {
								client.crafter.ShiftClick(p.getKey());
								break;
							}
						}
						for (int i : chestplates) {
							if (getSlot(6) != null) break;
							if (i == p.getValue().getId()) {
								client.crafter.ShiftClick(p.getKey());
								break;
							}
						}
						for (int i : leggings) {
							if (getSlot(7) != null) break;
							if (i == p.getValue().getId()) {
								client.crafter.ShiftClick(p.getKey());
								break;
							}
						}
						for (int i : boots) {
							if (getSlot(8) != null) break;
							if (i == p.getValue().getId()) {
								client.crafter.ShiftClick(p.getKey());
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public void addAction() {
		
	}
	
	
	//--------------------------------------------------------------------------
	//------------------------------CHECKERS------------------------------------
	//--------------------------------------------------------------------------
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
	
	
	
	//--------------------------------------------------------------------------
	//------------------------------GETTERS-------------------------------------
	//--------------------------------------------------------------------------
	
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
	
	public Map<Integer, ItemStack> getHotbar() {
		Map<Integer, ItemStack> temp = new HashMap<>();
		int m = 0;
		if (client.crafter.windowType != null) m = Main.getMCData().slotMultipiler.get(client.crafter.windowType);
		for (int i = 36+m; i <= 44+m; i++) {
			temp.put(i, slots.get(i));
		}
		return temp;
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
	
	
	
	
	//--------------------------------------------------------------------------
	//------------------------------SETTERS-------------------------------------
	//--------------------------------------------------------------------------
	
	public void setSlot(int slot, ItemStack item) {
		slots.replace(slot, item);
	}
	
	public void setSlotInHotbar(int slot) {
		BotU.SetSlot(client, slot - 36);
	}
	
	
	
	//--------------------------------------------------------------------------
	//--------------------------MOMENTAL-RESPONSE-------------------------------
	//--------------------------------------------------------------------------
	
	@SuppressWarnings("deprecation")
	public void dropItem(boolean stack, int slot) {
		if (stack) {
			client.crafter.fromSlotToSlotStack(slot, -999);
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.DROP_ITEM_STACK, client.getPosition().translate(), BlockFace.UP));
		} else {
			client.crafter.fromSlotToSlot(slot, -999);
			client.getSession().send(new ClientPlayerActionPacket(PlayerAction.DROP_ITEM, client.getPosition().translate(), BlockFace.UP));
		}
	}
	
	public void fromInventoryToHotbar(List<String> names, int count) {
		finvtohb a = new finvtohb();
		a.count = count;
		a.names = names;
		ActionPool.add(a);
	}
	
	private boolean fromInventoryToHotbarExec(List<String> names, int count) {
		Integer slotwithitem = getSlotsWithItem(getInventory(), names, count);
		if (slotwithitem == null) return false;
		client.crafter.fromSlotToSlotStack(slotwithitem, getRandomEmptySlot(getHotbar()));
		return true;
	}
	
	
	public interface invTask {}
	
	public class finvtohb implements invTask {
		List<String> names;
		int count;
	}
}
