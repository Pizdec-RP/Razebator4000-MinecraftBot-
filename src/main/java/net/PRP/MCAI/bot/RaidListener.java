package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import net.PRP.MCAI.Main;
import net.PRP.MCAI.pathfinder.AStar;
import net.PRP.MCAI.utils.*;

public class RaidListener extends SessionAdapter {
	
	private Bot client;

	public RaidListener(Bot client) {
        this.client = client;
    }
	
	@SuppressWarnings("deprecation")
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	client.register();
        	if ((boolean) Main.getsett("raidspam")) {
				new Thread(()-> {
					ThreadU.sleep((int) Main.getsett("timebeforeraidon"));
					while (true) {
						if (client.isOnline() && Main.pasti.size() > 0 && (boolean)Main.getsett("raidspam")) {
	                        int rand = MathU.rnd(0, Main.pasti.size());
	                        String pasta = (String)Main.pasti.get(rand);
	                        BotU.chat(this.client, pasta);
	                        int sr = 1;
	                        sr = (int) Main.getsett("spamrange");
	                        ThreadU.sleep(sr);
	                    } else {
	                    	ThreadU.sleep(5000);
	                    }
					}
				}).start();
			}
        	new Thread(()-> {
        		ThreadU.sleep((int) Main.getsett("timebeforeraidon"));
        		client.register();
        		while (true) {
        			Thread t = new Thread(()-> {
        				int ix = 0;
        				int iz = 0;
        				if (MathU.rnd(0,1) == 1) {
        					ix = MathU.rnd(0, (int) Main.getsett("raidwalkradius"));
        				} else {
        					ix = -MathU.rnd(0, (int) Main.getsett("raidwalkradius"));
        				}
        				if (MathU.rnd(0,1) == 1) {
        					iz = MathU.rnd(0, (int) Main.getsett("raidwalkradius"));
        				} else {
        					iz = -MathU.rnd(0, (int) Main.getsett("raidwalkradius"));
        				}
        				Vector3D end = new Vector3D((int)client.posX-ix, (int) client.posY, (int)client.posZ-iz);
        				AStar pf = new AStar(client, client.getPositionInt(), end);
        				pf.startCalc(client, true);
        			});
        			t.start();
    				ThreadU.sleep((int)Main.getsett("walkeverymilseconds"));
    				t.stop();
        		}
        	}).start();
        }
	}

}
