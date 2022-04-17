package net.PRP.MCAI.bot.specific;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.utils.physics;
import net.PRP.MCAI.bot.AABB;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.MathU;

public class PhysicsListener extends SessionAdapter {
	public Vector3D before;
	private float beforeYaw;
    private float beforePitch;
	private Bot client;
	public Vector3D vel = new Vector3D(0,0,0);
	public int sleepticks = 0;
	public int autojumpcooldown = 0;
	
	public PhysicsListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
        	before = client.getPosition();
        } 
	}
	
	public void airfall() {
		//int slowFalling = 0;//client.effects.slowFalling
        //double gravityMultiplier = (vel.y <= 0 && slowFalling > 0) ? physics.slowFalling : 1;
		client.onGround = false;
    	vel.y -= physics.gravity;// * gravityMultiplier;
    	vel.y *= physics.airdrag;
	}
	
	public double calcnextairfall() {
        //double gravityMultiplier = 1;
    	double y = vel.y;
		y -= physics.gravity;// * gravityMultiplier;
    	y *= physics.airdrag;
    	return y;
	}
	
	public Block calcnexttickblock() {
		return client.getWorld().getBlock(client.getPosition().floorXZ().add(0, calcnextairfall(), 0));
	}
	
	public void waterfall() {
		client.onGround = false;
		//int slowFalling = 0;//client.effects.slowFalling
        //double gravityMultiplier = (vel.y <= 0 && slowFalling > 0) ? physics.slowFalling : 1;
		vel.y *= client.isInWater() ? physics.waterInertia : physics.lavaInertia;
		vel.y -= (client.isInWater() ? physics.waterGravity : physics.lavaGravity);// * gravityMultiplier;
	}
	
	public AABB nexttickX() {
		return client.getHitbox().offset(vel.x, 0, 0);
	}
	
	public AABB nexttickZ() {
		return client.getHitbox().offset(0, 0, vel.z);
	}
	
	public AABB nexttickY() {
		return client.getHitbox().offset(0, vel.y, 0);
	}
	
	public void jump() {
		if (client.onGround && autojumpcooldown <= 0) {
			autojumpcooldown = 10;
			client.onGround = false;
			vel.y = 0.52;
			//System.out.println("jump pos: "+client.getPositionInt()+" vel: "+vel.toString()+" onGround:"+client.onGround);
		}
	}
	
	private void PhysicsUpdate() {
		if (this.sleepticks > 0) {
			this.sleepticks--;
			return;
		}
		//System.out.println(client.isOnline()+" "+ (client.pathfinder.state == State.WALKING && client.pathfinder.sleepticks == 0));
        if (!client.isOnline())
            return;
        
        if (calcnexttickblock().isAvoid()) {
        	airfall();
        	//if (client.getPosX() != ((int)client.getPosX()+0.5) || client.getPosZ() != ((int)client.getPosZ()+0.5)) BotU.calibratePosition(client);
        } else if (calcnexttickblock().isLiquid()) {
        	waterfall();
        } else {
        	if (client.posY > MathU.Truncate(client.posY)) {
        		vel.y = 0;
            	client.setPosY(MathU.Truncate(client.posY));
            	client.onGround = true;
        	} else {
        		vel.y = 0;
        		client.onGround = true;
        	}
        }
        
        if (vel.y != 0) {
        	for (Vector3D a : client.getHitbox(0,vel.y,0).getCorners()) {
        		Block n = a.getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickY()) && !n.isLiquid()) {
        				if (vel.y > 0) {
        					vel.y = Math.floor(n.pos.y)-(client.posY+1.8);
        					//System.out.println("vel.y++");
        				} else {
	        				vel.y = 0;
	        				//System.out.println("y reset");
        				}
        			}
        		} 
        	}
        }
        
        if (vel.x != 0) {
        	for (Vector3D a : client.getHitbox(vel.x,0,0).getCorners()) {
        		Block n = a.getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickX())) {
        				vel.x = 0;
        				if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					jump();
        				}
        			}
        		}
        	}
        }
        
        if (vel.z != 0) {
        	for (Vector3D a : client.getHitbox(0,0,vel.z).getCorners()) {
        		Block n = a.getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickZ())) {
        				vel.z = 0;
        				if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					jump();
        				}
        			}
        		}
        	}
        }
        
        if (vel.x == 0 && vel.y == 0 && vel.z == 0) return;
        //System.out.println("pos: "+client.getPosition()+" vel: "+vel.toString()+" onGround:"+client.onGround+" ajc"+autojumpcooldown);
        client.setposto(client.getPosition().add(vel));
    }
	
	public void tick() {
		if (!client.isOnline() || !client.isGameReady()) return;
		if (autojumpcooldown > 0) autojumpcooldown--;
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
