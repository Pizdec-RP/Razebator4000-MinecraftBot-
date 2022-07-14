package net.PRP.MCAI.bot.specific;

import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.physics;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;

public class Physics extends SessionAdapter {
	public Vector3D before;
	private float beforeYaw;
    private float beforePitch;
	private Bot client;
	public Vector3D velocity = new Vector3D(0,0,0);
	public int sleepticks = 0;
	private int autojumpcooldown = 0;
	private Block nexttickblock = null;
	
	private boolean WALK = false;
	private boolean RUN = false;
	private double playerSpeed = 0;
	private int cd = 0;
	public boolean fly = false;
	
	public Physics(Bot client) {
		this.client = client;
		BotU.log(client.name+" pl");
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent receiveEvent) {
        if (receiveEvent.getPacket() instanceof ServerPlayerPositionRotationPacket) {
        	before = client.getPosition();
        } 
	}
	
	private void airfall() {
		//int slowFalling = 0;//client.effects.slowFalling
        //double gravityMultiplier = (vel.y <= 0 && slowFalling > 0) ? physics.slowFalling : 1;
		if (!client.isInLiquid()) client.onGround = false;
		velocity.y -= physics.gravity;// * gravityMultiplier;
		velocity.y *= physics.airdrag;
	}
	
	private double calcnextairfall() {
        //double gravityMultiplier = 1;
    	double y = velocity.y;
		y -= physics.gravity;// * gravityMultiplier;
    	y *= physics.airdrag;
    	return y;
	}
	
	private Block calcnexttickblock() {
		return client.getWorld().getBlock(client.getPosition().floorXZ().add(0, calcnextairfall(), 0));
	}
	
	private void waterfall() {
		if (!client.isInLiquid()) client.onGround = false;
		//int slowFalling = 0;//client.effects.slowFalling
        //double gravityMultiplier = (velocity.y <= 0 && slowFalling > 0) ? physics.slowFalling : 1;
		velocity.y *= client.isInWater() ? physics.waterInertia : physics.lavaInertia;
		velocity.y -= client.isInWater() ? physics.waterGravity : physics.lavaGravity;// * gravityMultiplier;
	}
	
	private AABB nexttickX() {
		return client.getHitbox().offset(velocity.x, 0, 0);
	}
	
	private AABB nexttickZ() {
		return client.getHitbox().offset(0, 0, velocity.z);
	}
	
	private AABB nexttickY() {
		return client.getHitbox().offset(0, velocity.y, 0);
	}
	
	public void jump() {
		if (client.onGround && autojumpcooldown <= 0 && velocity.y <= 0) {
			if (!client.isInLiquid())autojumpcooldown = 10;
			client.onGround = false;
			velocity.y = 0.53;
		}
	}
		
	public void inWaterJump(int cd) {
		//System.out.println(client.onGround +" "+ autojumpcooldown +" "+ velocity.y);
		if (autojumpcooldown <= 0 && velocity.y <= 0) {
			if (!client.isInLiquid())autojumpcooldown = cd;
			client.onGround = false;
			velocity.y = 0.53;
		}
	}
	
	public void Walk() {
		WALK = true;
	}
	
	private void PhysicsUpdate() {
		if (!client.isOnline()) return;
		
		if (sleepticks > 0) {
			sleepticks--;
			return;
		}
		if (client.isInWater()) {
			cd--;
			if (cd<=0) {
				velocity.y += 0.3;
				cd = 20;
			}
		}
		
		if (RUN) {
			
		} else if (WALK) {
			if (client.isInLiquid()) {
				if (playerSpeed <= 0) playerSpeed = physics.playerSpeed;
				Vector3D nextvel = VectorUtils.vector(client.getYaw(), client.getPitch(), playerSpeed,client);
				setVelX(nextvel.x);
				setVelZ(nextvel.z);
				playerSpeed += 0.098;
				playerSpeed *= 0.666;
				WALK = false;
			} else {
				if (playerSpeed <= 0) playerSpeed = physics.playerSpeed;
				Vector3D nextvel = VectorUtils.vector(client.getYaw(), client.getPitch(), playerSpeed,client);
				setVelX(nextvel.x);
				setVelZ(nextvel.z);
				playerSpeed += 0.098;
				playerSpeed *= 0.7;
				WALK = false;
			}
		} else {
			playerSpeed = 0;
		}
		
		nexttickblock = calcnexttickblock();
        if (nexttickblock.isAvoid() || nexttickblock.type == Type.CARPET) {
        	if (!fly) airfall();
        	//if (client.getPosX() != ((int)client.getPosX()+0.5) || client.getPosZ() != ((int)client.getPosZ()+0.5)) BotU.calibratePosition(client);
        } else if (nexttickblock.isLiquid()) {
        	if (!fly) waterfall();
        } else {
        	
        	if (velocity.y < 0) {
        		if (nexttickblock.getHitbox() != null && nexttickblock.getHitbox().maxY > client.posY+velocity.y) {
        			velocity.y = 0;
        			client.setPosY(nexttickblock.getHitbox().maxY);
        			client.onGround = true;
        		}
        	}
        }
        
        
        
        if (velocity.x != 0) {
        	for (Vector3D a : client.getHitbox(velocity.x,0,0).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickX())) {
        				//System.out.println(n.getHitbox().maxY - Math.floor(client.posY));
        				if (n.getHitbox().maxY - Math.floor(client.posY)<=physics.stepHeight) {
        					velocity.y = 0;
        					client.setPosY(n.getHitbox().maxY);
        				} else if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					velocity.x = 0;
        					jump();
        				} else {
        					velocity.x = 0;
        				}
        			}
        		}
        	}
        }
        
        if (velocity.z != 0) {
        	for (Vector3D a : client.getHitbox(0,0,velocity.z).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickZ())) {
        				
        				if (n.getHitbox().maxY - Math.floor(client.posY) <= physics.stepHeight) {
        					velocity.y = 0;
        					client.setPosY(n.getHitbox().maxY);
        				} else if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					velocity.z = 0;
        					jump();
        				} else {
        					velocity.z = 0;
        				}
        			}
        		}
        	}
        }
        
        if (velocity.y != 0) {
        	for (Vector3D a : client.getHitbox(0,velocity.y,0).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null) {
        			if (n.getHitbox().collide(nexttickY())) {
        				if (velocity.y > 0) {
        					velocity.y = n.getHitbox().minY-(client.posY+1.8);
        				} else {
        					if (fly) {
        						velocity.y = 0;
        					} else {
	        					if (n.getHitbox().maxY > client.posY+velocity.y) {
	        						velocity.y = 0;
	        						client.setPosY(n.getHitbox().maxY);
	        						client.onGround = true;
	        					} else {
		        					velocity.y = 0;
			        				client.onGround = true;
	        					}
        					}
        				}
        			}
        		} 
        	}
        }
        
        if (velocity.x == 0 && velocity.y == 0 && velocity.z == 0) return;
        //System.out.println(client.name+" pos: "+client.getPosition()+" velocity: "+velocity.toString()+" onGround:"+client.onGround+" ajc"+autojumpcooldown);
        client.setposto(client.getPosition().add(velocity.x, velocity.y, velocity.z));
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
		before = nowPos;
		beforePitch = nowPitch;
		beforeYaw = nowYaw;
	}

	public void reset() {
		before = client.getPosition();
		beforePitch = 0;
		beforeYaw = 0;
		velocity.origin();
	}
	
	public void resetVel() {
		velocity.origin();
	}
	
	public void setVelX(double i) {
		velocity.x = i;
	}
	public void setVelY(double i) {
		velocity.y = i;
	}
	public void setVelZ(double i) {
		velocity.z = i;
	}
}
