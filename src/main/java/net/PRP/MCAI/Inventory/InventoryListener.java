package net.PRP.MCAI.Inventory;

import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.BotU;

public class InventoryListener extends SessionAdapter {
	
	private Bot client;
	
	public InventoryListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
			BotU.SetSlot(client, 0);
		} else if (receiveEvent.getPacket() instanceof ServerWindowItemsPacket) {
			final ServerWindowItemsPacket p = (ServerWindowItemsPacket) receiveEvent.getPacket();
			if (p.getWindowId() == 0) {
				client.playerInventory.setup(p.getItems());
			}
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			
			final ServerSetSlotPacket p = (ServerSetSlotPacket) receiveEvent.getPacket();
			//System.out.println("sssp "+p.getWindowId());
			if (p.getWindowId() == 0) {
				client.playerInventory.setSlot(p.getSlot(), p.getItem());
				//System.out.println(p.getSlot()+" "+p.getItem());
			}
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			//System.out.println("sowp");
			//final ServerOpenWindowPacket p = (ServerOpenWindowPacket) receiveEvent.getPacket();
			//System.out.println(p.getType());
			
		} else if (receiveEvent.getPacket() instanceof ServerWindowPropertyPacket) {
			//final ServerWindowPropertyPacket p = (ServerWindowPropertyPacket) receiveEvent.getPacket();
			//System.out.println("swpp");
			
		} else if (receiveEvent.getPacket() instanceof ServerConfirmTransactionPacket) {
            final ServerConfirmTransactionPacket p = (ServerConfirmTransactionPacket) receiveEvent.getPacket();
            client.getSession().send(new ClientConfirmTransactionPacket(p.getWindowId(), p.getActionId(), true));
		}
	}
}
