package net.PRP.MCAI.Inventory;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.*;
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
			client.playerInventory.setup(p.getItems());
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			final ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			//if (client.crafter.windowType != null) client.playerInventory.setSlot(p.getSlot()-Main.getMCData().slotMultipiler.get(client.crafter.windowType), p.getItem());
			/*else*/client.playerInventory.setSlot(p.getSlot(), p.getItem());
			if (Main.debug && p.getItem() != null) System.out.println("sssp slot:"+p.getSlot()+ " item:"+Main.getMCData().items.get(p.getItem().getId()).name);
		}
	}
}
