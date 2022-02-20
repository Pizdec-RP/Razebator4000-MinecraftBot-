package net.PRP.MCAI.bot.specific;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.window.WindowAction;
import com.github.steveice10.mc.protocol.data.game.window.WindowType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.VectorUtils;

public class Crafting extends SessionAdapter {
	
	private Bot client;
	public crState state = crState.START;
	public int currentWindowId = 0;
	public WindowType windowType = null;
	public int actionId = 0;
	public Vector3D craftingBlock = null;
	public craftingRecepie recepie = null;
	public ItemStack itemOnMouse = null;
	public Map<String, craftingRecepie> Recepies = new HashMap<>() {
	private static final long serialVersionUID = -6379467632960849503L;{
		put("planks", new craftingRecepie("inv", "log-.-.-.", new String[] {"log-1"}));
		put("bench",  new craftingRecepie("inv", "planks-planks-planks-planks", new String[] {"planks-4"}));
		put("stick",  new craftingRecepie("inv", "planks-.-planks-.", new String[] {"planks-4"}));
		put("torch",  new craftingRecepie("inv", "coal-.-stick-.", new String[] {"coal-1","stick-1"}));
		put("wooden_pickaxe",  new craftingRecepie("ct", "planks-planks-planks-.-stick-.-.-stick-.", new String[] {"planks-3","stick-2"}));
	}};
	public int plitstate = 0;
	public int timeout = 0;
	
	
	public class craftingRecepie {
		public String inventoryType;
		public String recepie;
		public String[] needItems;
		public craftingRecepie() {}
		public craftingRecepie(String it, String rec, String[] ni) {this.inventoryType = it; this.recepie = rec; this.needItems = ni;}
		public boolean isInventoried() {return this.inventoryType.equalsIgnoreCase("inv");}
		public boolean isWorkbenched() {return this.inventoryType.equalsIgnoreCase("ct");}
	}
	
	public Crafting(Bot client) {
		this.client = client;
	}
	
	public enum crState {
		START, OPENINGINV, WAITFOROPEN, PLACINGITEMS, GETTINGCRAFTED, ENDED;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			final ServerOpenWindowPacket p = (ServerOpenWindowPacket) receiveEvent.getPacket();
			currentWindowId = p.getWindowId();
			windowType = p.getType();
			
		} else if (receiveEvent.getPacket() instanceof ServerWindowPropertyPacket) {
			//final ServerWindowPropertyPacket p = (ServerWindowPropertyPacket) receiveEvent.getPacket();
			
			
		} else if (receiveEvent.getPacket() instanceof ServerConfirmTransactionPacket) {
            final ServerConfirmTransactionPacket p = (ServerConfirmTransactionPacket) receiveEvent.getPacket();
            client.getSession().send(new ClientConfirmTransactionPacket(p.getWindowId(), p.getActionId(), true));
		} else if (receiveEvent.getPacket() instanceof ServerCloseWindowPacket) {
			final ServerCloseWindowPacket p = (ServerCloseWindowPacket) receiveEvent.getPacket();
			currentWindowId = p.getWindowId();
			windowType = null;
		}
	}
	
	public void setup(String item, Vector3D cb) {
		if (state != crState.ENDED) throw new ConcurrentModificationException("craft already running");
		this.recepie = Recepies.get(item);
		this.craftingBlock = cb;
		this.state = crState.START;
		if (Main.debug) System.out.println("crafitng started");
	}
	
	public void finish() {
		if (Main.debug) System.out.println("crafitng finished");
		recepie = null;
		craftingBlock = null;
		state = crState.ENDED;
		plitstate = 0;
		timeout = 0;
	}
	
	public int nextActionId() {
		return actionId++;
	}
	
	public void fromSlotToSlotStack(int from, int to, int idMultipiler) {
		if (client.playerInventory.getSlot(to) != null) {
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				from+idMultipiler,
				client.playerInventory.getSlot(from+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				to+idMultipiler,
				client.playerInventory.getSlot(to+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				from+idMultipiler,
				client.playerInventory.getSlot(from+idMultipiler),
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
		} else {
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				from+idMultipiler,
				client.playerInventory.getSlot(from+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				to+idMultipiler,
				client.playerInventory.getSlot(to+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
		}
	}
	
	public void fromSlotToSlot(int from, int to, int idMultipiler) {
		client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				from+idMultipiler,
				client.playerInventory.getSlot(from+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				to+idMultipiler,
				client.playerInventory.getSlot(to+idMultipiler), 
				WindowAction.CLICK_ITEM,
				ClickItemParam.RIGHT_CLICK
			));
			client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
				nextActionId(),
				from+idMultipiler,
				client.playerInventory.getSlot(from+idMultipiler),
				WindowAction.CLICK_ITEM,
				ClickItemParam.LEFT_CLICK
			));
	}
	
	public void ShiftClick(int slot, int idMultipiler) {
		client.getSession().send(new ClientWindowActionPacket(client.crafter.currentWindowId,
			client.crafter.nextActionId(),
			slot+idMultipiler,
			client.playerInventory.getSlot(slot+idMultipiler),
			WindowAction.SHIFT_CLICK_ITEM,
			ClickItemParam.LEFT_CLICK
		));
	}
	
	@SuppressWarnings("deprecation")
	public void tick() {
		if (state == crState.START) {
			if (recepie == null) {
				finish();
				return;
			} else if (recepie.isInventoried()) {
				plitstate = 8;
				for (String item : recepie.needItems) {
					if (!client.playerInventory.contain(item.split("-")[0], Integer.parseInt(item.split("-")[1]))) {
						finish();
						return;
					}
				}
				//all OK
				state = crState.PLACINGITEMS;
			} else if (recepie.isWorkbenched()) {
				plitstate = 18;
				for (String item : recepie.needItems) {
					if (!client.playerInventory.contain(item.split("-")[0], Integer.parseInt(item.split("-")[1]))) {
						finish();
						return;
					}
				}
				state = crState.OPENINGINV;
			}
		} else if (state == crState.OPENINGINV) {
			if (craftingBlock == null) {
				finish();
				return;
			}
			if (VectorUtils.sqrt(client.getEyeLocation(), craftingBlock) > 5) {
				client.getSession().send(new ClientPlayerPlaceBlockPacket(craftingBlock.translate(), BlockFace.UP, Hand.MAIN_HAND, 0.5F, 1F, 0.5F, false));
				state = crState.WAITFOROPEN;
			} else {
				finish();
				return;
			}
			
		} else if (state == crState.WAITFOROPEN) {
			if (recepie.isInventoried()) {
				//dammnnn wtf
			} if (recepie.isWorkbenched()) {
				if (windowType == WindowType.CRAFTING) {
					state = crState.PLACINGITEMS;
				} else {
					timeout++;
					if (timeout > 200) {
						finish();
					}
				}
			}
			
		} else if (state == crState.PLACINGITEMS) {
			
			if (recepie.isInventoried()) {
				if (plitstate <= 0) {
					state = crState.GETTINGCRAFTED;
					return;
				}
				if (plitstate%2 == 1) {
					plitstate--;
				} else {
					int i = plitstate / 2;
					if (!recepie.recepie.split("-")[i-1].equalsIgnoreCase(".")) fromSlotToSlot(client.playerInventory.getSlotWithItem(recepie.recepie.split("-")[i-1]), i, 0);
					plitstate--;
				}
			} else if (recepie.isWorkbenched()) {
				if (plitstate <= 0) {
					state = crState.GETTINGCRAFTED;
					return;
				}
				
				if (plitstate%2 == 1) {
					plitstate--;
				} else {
					int i = plitstate / 2;
					if (!recepie.recepie.split("-")[i-1].equalsIgnoreCase(".")) fromSlotToSlot(client.playerInventory.getSlotWithItem(recepie.recepie.split("-")[i-1]), i, 1);
					plitstate--;
				}
			}
			
		} else if (state == crState.GETTINGCRAFTED) {
			if (recepie.isInventoried()) {
				if (client.playerInventory.getSlot(0) != null) {
					ShiftClick(0,0);
					finish();
					return;
				} else {
					timeout++;
					if (timeout > 200) {
						ShiftClick(1,0);
						ThreadU.sleep(50);//tak nepravilno no mne pohuy
						ShiftClick(2,0);
						ThreadU.sleep(50);
						ShiftClick(3,0);
						ThreadU.sleep(50);
						ShiftClick(4,0);
						finish();
					}
				}
			} else if (recepie.isWorkbenched()) {
				if (client.playerInventory.getSlot(0) != null) {
					ShiftClick(0,0);
					finish();
					return;
				} else {
					timeout++;
					if (timeout > 200) {
						ShiftClick(1,0);
						ThreadU.sleep(50);
						ShiftClick(2,0);
						ThreadU.sleep(50);
						ShiftClick(3,0);
						ThreadU.sleep(50);
						ShiftClick(4,0);
						finish();
					}
				}
			}
		}
	}
}
