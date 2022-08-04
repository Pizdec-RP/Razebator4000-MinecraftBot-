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
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.physics;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;

public class Physics extends SessionAdapter {
	public Vector3D before;
	public float beforeYaw;
	public float beforePitch;
	public Bot client;
	public Vector3D vel = new Vector3D(0,0,0);
	public int sleepticks = 0;
	private int autojumpcooldown = 0;
	private Block blockUnder = null;
	private boolean WALK = false;
	private boolean RUN = false;
	private boolean SNEAK = false;
	boolean xzcollided = false;
	private boolean jumpQueued;
	
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
		vel.y -= physics.gravity;
		vel.y *= physics.airdrag;
	}
	
	private void waterfall() {
		vel.y *= client.isInWater() ? physics.waterInertia : physics.lavaInertia;
		vel.y -= client.isInWater() ? physics.waterGravity : physics.lavaGravity;
	}
	
	private AABB nexttickX() {
		return client.getHitbox().offset(vel.x, 0, 0);
	}
	
	private AABB nexttickZ() {
		return client.getHitbox().offset(0, 0, vel.z);
	}
	
	private AABB nexttickY() {
		return client.getHitbox().offset(0, vel.y, 0);
	}
	
	public void jump() {
		this.jumpQueued = true;
	}
	
	public Block getBlockPosBelowThatAffectsMyMovement() {
		return client.getWorld().getBlock(client.posX, client.posY-0.5000001, client.posZ);
	}
	
	public void Walk() {
		WALK = true;
	}
	
	public void Sprint() {
		WALK = true;
		RUN = true;
	}
	
	private double getMoveSpeed() {
		if (RUN) return 0.38985D;
		else if (WALK) return 0.3473D;
		else return 0D;
    }
	
	public void moveRelative(double forward, double strafe, double friction) {
        double distance = strafe * strafe + forward * forward;

        if (distance >= 1.0E-4F) {
            distance = Math.sqrt(distance);

            if (distance < 1.0F) {
                distance = 1.0F;
            }

            distance = friction / distance;
            strafe = strafe * distance;
            forward = forward * distance;

            double yawRadians = Math.toRadians(client.getYaw());
            double sin = Math.sin(yawRadians);
            double cos = Math.cos(yawRadians);

            vel.x += strafe * cos - forward * sin;
            vel.z += forward * cos + strafe * sin;
        }
    }
	
	public void moveEntityWithHeading(double forward, double strafe) {
		float prevSlipperiness = (float) physics.airborneInertia;//inertia
        double value = physics.airborneAcceleration;//acceleration
        
        if (!client.isInLiquid()) {
            if (client.onGround) {
            	prevSlipperiness = (blockUnder.getfriction() == 0.6F ? getBlockPosBelowThatAffectsMyMovement().getfriction() : blockUnder.getfriction()) * 0.91F;//inertiа
        		value = getMoveSpeed() * (0.1627714F / (prevSlipperiness * prevSlipperiness * prevSlipperiness));//acceleration
            }
            
            vel.x *= prevSlipperiness;
            vel.z *= prevSlipperiness;

            moveRelative(forward, strafe, value);//apply heading
            
        } else {
        	double acceleration = physics.liquidAcceleration;
        	double inertia = client.isInWater() ? physics.waterInertia : physics.lavaInertia;
        	double horizontalInertia = inertia;
        	
        	moveRelative(strafe, forward, acceleration);
            
        	vel.y *= inertia;
	        vel.y -= client.isInWater() ? physics.waterGravity : physics.lavaGravity;// * gravityMultiplier;
	        vel.x *= horizontalInertia;
	        vel.z *= horizontalInertia;
        }
       
	}
	
	public void walksAndOtherShit() {
		if (Math.abs(vel.x) < physics.negligeableVelocity) vel.x = 0;
		if (Math.abs(vel.y) < physics.negligeableVelocity) vel.y = 0;
		if (Math.abs(vel.z) < physics.negligeableVelocity) vel.z = 0;
		
		if (jumpQueued && autojumpcooldown <= 0) {
			if (client.isInLiquid()) {
				vel.y += 0.03999999910593033F;
				autojumpcooldown = 1;
			} else {
				if (client.onGround) {
					vel.y = 0.5099999904632568F;
					if (client.effects.jumpBoost > 0) {
			            vel.y += 0.1f * (float)(client.effects.jumpBoost + 1);
			        }
					if (RUN) {
						float yaw = client.getYaw() * 0.017453292f;
			            vel.add(-Math.sin(yaw) * 0.2f, 0.0, Math.cos(yaw) * 0.2f);
					}
				}
			}
			jumpQueued = false;
		}
		
		double strafe = 0;
		double forward = (RUN || WALK) ? getMoveSpeed() : 0 * 0.98;
		
		if (SNEAK || client.isHoldSlowdownItem) {
			strafe *= physics.sneakSpeed;
			forward *= physics.sneakSpeed;
		}
		
		moveEntityWithHeading(forward,strafe);
	}
	
	private void PhysicsUpdate() {
		if (sleepticks > 0) {
			sleepticks--;
			return;
		}
		xzcollided = false;
		
		blockUnder = client.getWorld().getBlock(client.getPosition().floor().add(0,-1,0));
		if (client.foodlvl <= 6) RUN = false;
		
		walksAndOtherShit();
		
		RUN = false;
		WALK = false;
		SNEAK = false;
		
		if (client.isInLiquid()) {
			waterfall();
		} else {
			airfall();
		}
		client.onGround = false;
		
		if (vel.y != 0) {
        	for (Vector3D a : client.getHitbox(0,vel.y,0).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null && !n.isLiquid()) {
        			if (n.getHitbox().collide(nexttickY())) {
        				if (vel.y > 0) {
        					if (n.getHitbox().minY < client.getHitbox(vel).maxY) {
        						vel.y = 0;
        						client.setPosY(client.getPosY()+(n.getHitbox().minY-client.getHitbox().maxY));
        					}
        				} else {
        					if (n.getHitbox().maxY > client.posY+vel.y) {
        						vel.y = 0;
        						client.setPosY(n.getHitbox().maxY);
        						client.onGround = true;
        					} else {
	        					vel.y = 0;
		        				client.onGround = true;
        					}
        				}
        				break;
        			}
        		} 
        	}
        }
        
        //bad
        if (vel.x != 0) {
        	for (Vector3D a : client.getHitbox(vel.x,0,0).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null && !n.isLiquid()) {
        			if (n.getHitbox().collide(nexttickX())) {
        				xzcollided = true;
        				//System.out.println(n.getHitbox().maxY - Math.floor(client.posY));
        				if (n.getHitbox().maxY - Math.floor(client.posY)<=physics.stepHeight) {
        					vel.y = 0;
        					client.setPosY(n.getHitbox().maxY);
        				} else if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					vel.x = 0;
        					jump();
        				} else {
        					vel.x = 0;
        				}
        				break;
        			}
        		}
        	}
        }
        
        if (vel.z != 0) {
        	for (Vector3D a : client.getHitbox(0,0,vel.z).getCorners()) {
        		Block n = a.func_vf().getBlock(client);
        		if (n.getHitbox() != null && !n.isLiquid()) {
        			if (n.getHitbox().collide(nexttickZ())) {
        				xzcollided = true;
        				if (n.getHitbox().maxY - Math.floor(client.posY) <= physics.stepHeight) {
        					vel.y = 0;
        					client.setPosY(n.getHitbox().maxY);
        				} else if (a.up().getBlock(client).isAvoid() && Math.floor(a.y) == Math.floor(client.posY)) {
        					vel.z = 0;
        					jump();
        				} else {
        					vel.z = 0;
        				}
        				break;
        			}
        		}
        	}
        }
        
        
        
        if (vel.x == 0 && vel.y == 0 && vel.z == 0) return;
        //System.out.println(client.name+" velocity: "+vel.toString()+" onGround:"+client.onGround+" ajc"+autojumpcooldown);
        client.setposto(client.getPosition().add(vel.x, vel.y, vel.z));
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
				client.getSession().send(new ClientPlayerPositionRotationPacket(client.onGround, (float)client.posX, (float)client.posY, (float)client.posZ, client.getYaw(), client.getPitch()));
				//BotU.log("cpprp x"+client.posX+" y"+client.posY+" z"+client.posZ+" yaw"+client.getYaw()+" pitch"+client.getPitch());
			} else {
				client.getSession().send(new ClientPlayerPositionPacket(client.onGround, (float)client.posX, (float)client.posY, (float)client.posZ));
				//BotU.log("cppp x"+client.posX+" y"+client.posY+" z"+client.posZ);
			}
		} else if (nowYaw != beforeYaw || nowPitch != beforePitch) {
			client.getSession().send(new ClientPlayerRotationPacket(client.onGround, client.getYaw(), client.getPitch()));
			//BotU.log("cprp yaw"+client.getYaw()+" pitch"+client.getPitch());
		}
		before = nowPos;
		beforePitch = nowPitch;
		beforeYaw = nowYaw;
	}

	public void reset() {
		before = client.getPosition();
		beforePitch = 0;
		beforeYaw = 0;
		vel.origin();
	}
	
	public void resetVel() {
		vel.origin();
	}
	
	public void setVelX(double i) {
		vel.x = i;
	}
	public void setVelY(double i) {
		vel.y = i;
	}
	public void setVelZ(double i) {
		vel.z = i;
	}
	
	public Vector3D getVel() {
		return vel;
	}
	
	public Vector3D getDeltaMovement() {
		return vel;
	}
	
	public void setVel(Vector3D v) {
		this.vel = v;
	}
	
	public void setDeltaMovement(Vector3D v) {
		this.vel = v;
	}
}
