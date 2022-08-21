package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

public class Shit extends SessionAdapter {
	boolean ns = true;
	@Override
    public void packetReceived(PacketReceivedEvent event) {
		if (event.getPacket() instanceof ServerKeepAlivePacket) {
			if (ns) {
				ServerKeepAlivePacket p = (ServerKeepAlivePacket) event.getPacket();
			    event.getSession().send(new ClientKeepAlivePacket(p.getPingId()));
			    ns = false;
			}
		}
	}
        
}
