package net.PRP.MCAI.bot;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnLivingEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;

public class EntityListener extends SessionAdapter {
	
	private Bot client;
	private boolean recordpos;
	private UUID recorduuid;
	
	public EntityListener(Bot client) {
		this.client = client;
	}
	
	@Override
    public void packetReceived(PacketReceivedEvent event) {
		try {
			if (event.getPacket() instanceof ServerEntityPositionPacket) {
				final ServerEntityPositionPacket p = (ServerEntityPositionPacket) event.getPacket();
	
	            Entity entity = client.getWorld().Entities.get(p.getEntityId());
	            
	            if (this.recordpos && client.getWorld().Entities.get(p.getEntityId()).uuid.toString().equalsIgnoreCase(recorduuid.toString())) {
	        		System.out.println("onGround:"+p.isOnGround()+" x:"+p.getMoveX()+"  y:"+String.format("%.2f",p.getMoveY())+"  z:"+String.format("%.2f",p.getMoveZ())+"  milis:"+System.currentTimeMillis());
	            }
            	entity.pos.x += p.getMoveX();
            	entity.vel.x = p.getMoveX();
                entity.pos.y += p.getMoveY();
                entity.vel.y = p.getMoveY();
                entity.pos.z += p.getMoveZ();
                entity.vel.z = p.getMoveZ();
	            
            } else if (event.getPacket() instanceof ServerSpawnPlayerPacket) {
                final ServerSpawnPlayerPacket p = event.getPacket();
                client.getWorld().Entities.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), EntityType.PLAYER, new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));

            } else if (event.getPacket() instanceof ServerSpawnEntityPacket) {
                final ServerSpawnEntityPacket p = event.getPacket();
                client.getWorld().Entities.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), p.getType(), new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));
            } else if (event.getPacket() instanceof ServerSpawnLivingEntityPacket) {
                final ServerSpawnLivingEntityPacket p = event.getPacket();
                client.getWorld().Entities.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), p.getType(), new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));
            } else if (event.getPacket() instanceof ServerSpawnExpOrbPacket) {
            	final ServerSpawnExpOrbPacket p = (ServerSpawnExpOrbPacket) event.getPacket();
            	client.getWorld().Entities.put(p.getEntityId(), new Entity(p.getEntityId(), new UUID(0L, 0L), EntityType.EXPERIENCE_ORB, new Vector3D(p.getX(), p.getY(), p.getZ()), 0, 0));
            
            } else if (event.getPacket() instanceof ServerSpawnPaintingPacket) {
            	final ServerSpawnPaintingPacket p = (ServerSpawnPaintingPacket) event.getPacket();
            	client.getWorld().Entities.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), EntityType.PAINTING, new Vector3D(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ()), 0, 0));
            
            } else if (event.getPacket() instanceof ServerEntityDestroyPacket) {
                final ServerEntityDestroyPacket p = event.getPacket();
                for (int i : p.getEntityIds()) {
                    client.getWorld().Entities.remove(i);
                }
            } else if (event.getPacket() instanceof ServerEntityTeleportPacket) {
                final ServerEntityTeleportPacket p = event.getPacket();
                Entity entity = client.getWorld().Entities.get(p.getEntityId());
                entity.pos = new Vector3D(p.getX(), p.getY(), p.getZ());
                entity.pitch = p.getPitch();
                entity.yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityPositionRotationPacket) {
                final ServerEntityPositionRotationPacket p = event.getPacket();
                Entity entity = client.getWorld().Entities.get(p.getEntityId());
                entity.pos.setX(entity.pos.getX() + p.getMoveX());
                entity.pos.setY(entity.pos.getY() + p.getMoveY());
                entity.pos.setZ(entity.pos.getZ() + p.getMoveZ());
                entity.pitch = p.getPitch();
                entity.yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityRotationPacket) {
                final ServerEntityRotationPacket p = event.getPacket();
                Entity entity = client.getWorld().Entities.get(p.getEntityId());
                entity.pitch = p.getPitch();
                entity.yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityVelocityPacket) {
            	final ServerEntityVelocityPacket p = (ServerEntityVelocityPacket)event.getPacket();
            	if (p.getEntityId() == client.getId()) {
            		client.pm.resetVel();
            		client.pm.vel = new Vector3D(p.getMotionX(),p.getMotionY(),p.getMotionZ());
            	} else {
            		client.getWorld().Entities.get(p.getEntityId()).vel=new Vector3D(p.getMotionX(),p.getMotionY(),p.getMotionZ());
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UUID getRecorduuid() {
		return recorduuid;
	}

	public void setRecorduuid(UUID recorduuid) {
		this.recorduuid = recorduuid;
	}

	public boolean isRecordpos() {
		return recordpos;
	}

	public void setRecordpos(boolean recordpos) {
		this.recordpos = recordpos;
	}
}
