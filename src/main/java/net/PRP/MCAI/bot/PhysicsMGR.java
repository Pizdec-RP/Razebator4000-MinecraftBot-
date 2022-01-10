package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;
import world.Block;

public class PhysicsMGR extends SessionAdapter {
	
	private Bot client;
	private double fallMultipiler = 0.98;
	private double startfallspeed = 0.08;
	private double maxfallspeed = 3.92;
	private double currentfallspeed = 0;
	private double maxFlightTicks = 60;//20 ticks - 1sec
	private double currentFlightTicks = 0;
	private boolean PhysicsAllowed;
	private Vector3D before;
	
	public PhysicsMGR(Bot client) {
		this.client = client;
		this.PhysicsAllowed = false;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerJoinGamePacket) {
        	if (PhysicsAllowed) return;
        	new Thread(()->{
        		ThreadU.sleep(4000);
        		this.PhysicsAllowed = true;
        	}).start();
        }
	}
	
	@Override
    public void disconnected(DisconnectedEvent event) {
		this.PhysicsAllowed = false;
	}
	
	public void fall() {
		client.onGround = false;
		if (currentfallspeed == 0) currentfallspeed = startfallspeed;
		client.remY(currentfallspeed);
		System.out.println("cfs:"+currentfallspeed);
		currentfallspeed += fallMultipiler;
		if (currentfallspeed > maxfallspeed) {
			currentfallspeed = maxfallspeed;
		}
	}
	
	//x/z move double d0 = MathHelper.clamp(this.posX, -2.9999999E7D, 2.9999999E7D);
	
	public void tick() {
		if (!this.PhysicsAllowed) return;
		if (this.before == null) before = client.getPosition();
		Block bub = client.getPositionInt().add(0, -1, 0).getBlock(client);
		if (VectorUtils.BTavoid(bub.type)) {
			//System.out.println(bub.type+" "+bub.pos.toStringInt());
			if (!client.isInAction()) {
				fall();
			} else if (currentFlightTicks >= maxFlightTicks) {
				fall();
				currentFlightTicks = 0;
			} else {
				currentFlightTicks++;
			}
		}
		
		Vector3D now = client.getPosition();
		if (!VectorUtils.equals(before, now)) {
			client.getSession().send(new ClientPlayerPositionPacket(client.onGround, client.posX, client.posY, client.posZ));
		}
		
		if (currentfallspeed > 0 && (int)before.y == (int)now.y) {
			currentfallspeed = 0;
			client.onGround = true;
			BotU.calibrateY(client);
		}
		this.before = now;
	}
	
}
