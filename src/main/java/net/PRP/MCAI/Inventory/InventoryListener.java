package net.PRP.MCAI.Inventory;

import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.bot.Bot;

public class InventoryListener extends SessionAdapter {
	
	private Bot client;
	
	public InventoryListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
		if (receiveEvent.getPacket() instanceof ServerWindowItemsPacket) {
			//final ServerWindowItemsPacket packet = (ServerWindowItemsPacket) receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerSetSlotPacket) {
			//final ServerSetSlotPacket packet = (ServerSetSlotPacket) receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerOpenWindowPacket) {
			final ServerOpenWindowPacket packet = (ServerOpenWindowPacket) receiveEvent.getPacket();
			Inventory window = new Inventory();
			window.name = packet.getName();
			window.id = packet.getWindowId();
			window.type = packet.getType();
			
		} else if (receiveEvent.getPacket() instanceof ServerWindowPropertyPacket) {
			//final ServerWindowPropertyPacket packet = (ServerWindowPropertyPacket) receiveEvent.getPacket();
			
		} else if (receiveEvent.getPacket() instanceof ServerConfirmTransactionPacket) {
            final ServerConfirmTransactionPacket p = (ServerConfirmTransactionPacket) receiveEvent.getPacket();
            client.getSession().send(new ClientConfirmTransactionPacket(p.getWindowId(), p.getActionId(), true));
		}
	}
}
