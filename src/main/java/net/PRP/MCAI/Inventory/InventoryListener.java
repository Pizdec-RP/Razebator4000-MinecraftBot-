package net.PRP.MCAI.Inventory;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;

public class InventoryListener extends SessionAdapter {
	
	private Bot client;
	
	public InventoryListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
			ThreadU.sleep(1000);
			BotU.SetSlot(client, 0);
		} else if (receiveEvent.getPacket() instanceof ServerWindowItemsPacket) {
			final ServerWindowItemsPacket p = (ServerWindowItemsPacket) receiveEvent.getPacket();
			if (p.getWindowId() == 0) {
				client.playerInventory.setup(p.getItems());
			}
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			
			final ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			//if (p.getItem() != null) System.out.println("sssp slot:"+p.getSlot()+" item:"+p.getItem().getId());
			if (p.getWindowId() == 0) {
				client.playerInventory.setSlot(p.getSlot(), p.getItem());
				//System.out.println(p.getSlot()+" "+p.getItem());
			}
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			
		} else if (receiveEvent.getPacket() instanceof ServerWindowPropertyPacket) {
			
		}
	}
}
