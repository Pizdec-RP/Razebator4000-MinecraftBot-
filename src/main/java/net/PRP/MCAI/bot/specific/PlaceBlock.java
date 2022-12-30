package net.PRP.MCAI.bot.specific;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class PlaceBlock {
	
	private Bot client;
	public states state = states.afk;
	
	public Vector3D to = null;
	public int slot = -1;
	
	
	public PlaceBlock(Bot client) {
		this.client = client;
	}
	
	public void sstart(Vector3D to, int slot) {
		BotU.log(9);
		this.to = to;
		this.state = states.proc;
		ItemStack item = client.playerInventory.getSlot(slot);
		if (item == null || item.getId()==0) {
			
		} else {
			this.slot = slot;
		}
		BotU.log(10);
	}
	
	public void istart(Vector3D to, int itemid) {
		BotU.log(11);
		this.to = to;
		this.state = states.proc;
		int tempslot = client.playerInventory.getSlotWithItem(itemid, 1);
		ItemStack item = client.playerInventory.getSlot(tempslot);
		if (item == null || item.getId()==0) {
			
		} else {
			this.slot = tempslot;
		}
		BotU.log(12);
	}
	
	public void reset() {
		to = null;
		slot=-1;
		state=states.afk;
	}
	
	public enum states {
		afk, proc;
	}
	
	public void tick() {
		if (state == states.proc) {
			BotU.LookHead(client, to);
			if (slot == -1) {
				reset();
				return;
			} else {
				BotU.log(1);
				Block bl = to.getBlock(client);
				BotU.log(to.toStringInt());
				BotU.log(bl.name);
				BotU.log(bl.type);
				BotU.log(bl.pos);
				if (bl.type != Type.AIR && bl.type != Type.VOID) {
					reset();
					return;
				}
				BotU.log(1.1);
				boolean allowplace = false;
				for (Vector3D n : bl.getNeighbors()) {
					Block b = n.getBlock(client);
					if (b.type != Type.AIR && b.type != Type.VOID) {
						allowplace = true;
					}
				}
				BotU.log(1.2);
				if (!allowplace) {
					reset();
					return;
				}
				BotU.log(2);
				ItemStack item = client.playerInventory.getSlot(slot);
				if (item != null && item.getId() != 0) {
					if (slot >= 9 && slot <= 35) {
						BotU.log(3);
						client.crafter.fromSlotToSlotStack(slot, 5);
						BotU.SetSlot(client, 5);
						client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
						client.getSession().send(new ClientPlayerPlaceBlockPacket(to.translate(), VectorUtils.rbf(client, to), Hand.MAIN_HAND, 0,0,0, false));
						try {
							client.getWorld().setBlock(to.translate(), Main.getMCData().oldIdToNew(Main.getMCData().itemToOldId(item.getId())));
						} catch (Exception e) {
							BotU.log("error while translating from itemid:"+item.getId()+" to state");
							e.printStackTrace();
						}
						BotU.log(4);
					} else {
						BotU.log(5);
						BotU.SetSlot(client, slot);
						client.getSession().send(new ClientPlayerSwingArmPacket(Hand.MAIN_HAND));
						client.getSession().send(new ClientPlayerPlaceBlockPacket(to.translate(), VectorUtils.rbf(client, to), Hand.MAIN_HAND, 0,0,0, false));
						try {
							client.getWorld().setBlock(to.translate(), Main.getMCData().oldIdToNew(Main.getMCData().itemToOldId(item.getId())));
						} catch (Exception e) {
							BotU.log("error while translating from itemid:"+item.getId()+" to state");
							e.printStackTrace();
						}
						BotU.log(6);
					}
					reset();
				} else {
					reset();
				}
			}
		}
	}
}
