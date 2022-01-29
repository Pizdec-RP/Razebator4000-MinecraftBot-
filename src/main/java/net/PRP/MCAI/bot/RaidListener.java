package net.PRP.MCAI.bot;

import java.util.ArrayList;
import java.util.List;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.pathfinder.AStar.State;
import net.PRP.MCAI.bot.BlockBreakManager.bbmct;
import net.PRP.MCAI.utils.*;

public class RaidListener extends SessionAdapter {
	
	private Bot client;
	private int sleep;
	private int curtocleep = 0;
	private boolean firstJoin = false;
	public raidState state = raidState.IDLE;
	public Vector3D asd = Vector3D.ORIGIN;

	public RaidListener(Bot client) {
        this.client = client;
    }
	
	public enum raidState {
		IDLE, GOING, MINING;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	if ((int)Main.getsett("walkeverymilseconds")<=0)
        		this.sleep = 0;
        	else 
        		this.sleep = (int)Main.getsett("walkeverymilseconds");
        	if (firstJoin) return;
        	ThreadU.sleep((int) Main.getsett("timebeforeraidon"));
        	firstJoin = true;
        	if ((boolean) Main.getsett("raidspam")) {
				new Thread(()-> {
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
        } else if (receiveEvent.getPacket() instanceof ServerMapDataPacket) {
        	//final ServerMapDataPacket p = (ServerMapDataPacket) receiveEvent.getPacket();
        }
	}
	
	public void tick() {
		if (!firstJoin || !client.isOnline()) return;
		//System.out.println("pf:"+client.pathfinder.clientIsOnFinish+" bbm:"+client.bbm.state+" action:"+action);
		if (!((boolean) Main.getsett("mining"))) return;
		this.curtocleep += 50;
		if (curtocleep < sleep) return;
		curtocleep = 0;
		
		if (state == raidState.IDLE) {
			@SuppressWarnings("unchecked")
			List<Integer> d1 = (ArrayList<Integer>)Main.getsett("minertargetid");
			Vector3D block = VectorUtils.findNearestBlockByArrayId(client, d1);
			if (block == null) return;
			if (VectorUtils.sqrt(client.getPosition(), block) <= 3.8) {
				client.bbm.setup(block);
				this.state = raidState.MINING;
		    } else {
		    	Vector3D pos = func_31(block);
		    	if (pos == null) throw new NullPointerException("nullblock");
		    	this.asd = block;
		    	client.pathfinder.setup(pos);
		    	this.state = raidState.GOING;
		    }
		} else if (state == raidState.GOING) {
			if (client.pathfinder.state == State.FINISHED) {
				this.state = raidState.MINING;
				client.bbm.setup(asd);
			}
		} else if (state == raidState.MINING) {
			if (client.bbm.state == bbmct.ENDED) {
				this.state = raidState.IDLE;
			}
		}
	}
	
	public Vector3D func_31(Vector3D pos) {
		List<Vector3D> positions = VectorUtils.getAllInBox(pos, 5);
		//System.out.println(positions.size());
		for (Vector3D position : positions) {
			if (!VectorUtils.positionIsSafe(position, client)) {
				positions.remove(position);
				//System.out.println("pos is not safe "+position.toStringInt());
			}
		}
		//System.out.println(positions.size());
		for (Vector3D position : positions) {
			if (client.pathfinder.testForPath(position)) {
				return position;
			}
		}
		return null;
	}
}
