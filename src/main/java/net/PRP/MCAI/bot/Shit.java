package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import net.PRP.MCAI.Main;

public class Shit extends SessionAdapter {

	@Override
    public void packetReceived(PacketReceivedEvent event) {
		if (event.getPacket() instanceof ServerKeepAlivePacket) {
			if (!Main.debug) {
				if ((boolean) Main.getsett("KeepAlivePackets")) {
					final ServerKeepAlivePacket p = (ServerKeepAlivePacket) event.getPacket();
				    event.getSession().send(new ClientKeepAlivePacket(p.getPingId()));
				}
			}
		}
	}
        
}
