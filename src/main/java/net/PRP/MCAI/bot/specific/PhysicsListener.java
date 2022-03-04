package net.PRP.MCAI.bot.specific;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.utils.physics;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.pathfinder.AStar.State;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;

public class PhysicsListener extends SessionAdapter {
	public Vector3D before;
	private float beforeYaw;
    private float beforePitch;
	private Bot client;
	private Vector3D vel = new Vector3D(0,0,0);
	public int sleepticks = 0;
	
	public PhysicsListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
        	before = client.getPosition();
        } 
	}
	
	private void PhysicsUpdate() {
		if (this.sleepticks > 0) {
			this.sleepticks--;
			return;
		}
		//System.out.println(client.isOnline()+" "+ (client.pathfinder.state == State.WALKING && client.pathfinder.sleepticks == 0));
        if (!client.isOnline() || (client.pathfinder.state == State.WALKING && client.pathfinder.sleepticks == 0))
            return;
        
        Block blockUnder = client.getPositionInt().down().getBlock(client);
        int slowFalling = 0;//client.effects.slowFalling
        double gravityMultiplier = (vel.y <= 0 && slowFalling > 0) ? physics.slowFalling : 1;
        if (blockUnder.isAvoid()) {
	        	client.onGround = false;
	        	vel.y -= physics.gravity * gravityMultiplier;
	        	vel.y *= physics.airdrag;
	        	if (!client.getPosition().add(vel).getBlock(client).isAvoid()) {
	        		vel.y = -0.5;
	        	}
	        	if (client.getPosX() != ((int)client.getPosX()+0.5) || client.getPosZ() != ((int)client.getPosZ()+0.5)) BotU.calibratePosition(client);
        	
        } else if (client.getPositionInt().getBlock(client).isLiquid()) {

        	if (blockUnder.isLiquid()) {
        		
		    		//double acceleration = physics.liquidAcceleration;
		    		double inertia = client.isInWater() ? physics.waterInertia : physics.lavaInertia;
		    		vel.y *= inertia;
		    		vel.y -= (client.isInWater() ? physics.waterGravity : physics.lavaGravity) * gravityMultiplier;
	    		
        	} else if (blockUnder.ishard()) {
        		
	        		vel.y = 0;
	            	client.onGround = true;
	            	client.setPosY((float)MathU.Truncate(client.posY));
            	
        	} else if (blockUnder.isAvoid()) {
        		
	        		double inertia = client.isInWater() ? physics.waterInertia : physics.lavaInertia;
		    		vel.y *= inertia;
		    		vel.y -= (client.isInWater() ? physics.waterGravity : physics.lavaGravity) * gravityMultiplier;
	    		
        	}
        	
        } else if (blockUnder.isLiquid()) {
	        	double inertia = client.isInWater() ? physics.waterInertia : physics.lavaInertia;
	    		vel.y *= inertia;
	    		vel.y -= (client.isInWater() ? physics.waterGravity : physics.lavaGravity) * gravityMultiplier;
    		
        } else if (blockUnder.ishard()) {
        	if (client.posY > MathU.Truncate(client.posY)) {
        		vel.y = 0;
            	client.setPosY(MathU.Truncate(client.posY));
        	} else {
        		client.onGround = true;
        	}
        }
        //System.out.println("pos: "+client.getPosition().toString()+" vel: "+vel.toString()+" onGround:"+client.onGround);
        if (vel.x == 0 && vel.y == 0 && vel.z == 0) return;
        client.setposto(client.getPosition().add(vel));
    }
	
	public void tick() {
		if (!client.isOnline() || !client.isGameReady()) return;
		PhysicsUpdate();
		Vector3D nowPos = client.getPosition();
		float nowYaw = client.getYaw();
	    float nowPitch = client.getPitch();
	    if (before == null) return;
		if (!VectorUtils.equals(before, nowPos)) {
			if (nowYaw != beforeYaw || nowPitch != beforePitch) {
				client.getSession().send(new ClientPlayerPositionRotationPacket(client.onGround, client.posX, client.posY, client.posZ, client.getYaw(), client.getPitch()));
			} else {
				client.getSession().send(new ClientPlayerPositionPacket(client.onGround, client.posX, client.posY, client.posZ));
			}
		} else if (nowYaw != beforeYaw || nowPitch != beforePitch) {
			client.getSession().send(new ClientPlayerRotationPacket(client.onGround, client.getYaw(), client.getPitch()));
		}
		this.before = nowPos;
		this.beforePitch = nowPitch;
		this.beforeYaw = nowYaw;
	}

	public void reset() {
		this.before = client.getPosition();
		this.beforePitch = 0;
		this.beforeYaw = 0;
		this.vel = Vector3D.ORIGIN;
	}
}
