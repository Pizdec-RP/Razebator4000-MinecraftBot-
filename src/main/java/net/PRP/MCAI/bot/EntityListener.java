package net.PRP.MCAI.bot;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EntityMetadata;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.player.InteractAction;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
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
import net.PRP.MCAI.utils.BotU;

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
	
	            Entity entity = client.getWorld().Entites.get(p.getEntityId());
	            
	            if (this.recordpos && client.getWorld().Entites.get(p.getEntityId()).uuid.toString().equalsIgnoreCase(recorduuid.toString())) {
	        		System.out.println("onGround:"+p.isOnGround()+" x:"+p.getMoveX()+"  y:"+String.format("%.2f",p.getMoveY())+"  z:"+String.format("%.2f",p.getMoveZ())+"  milis:"+System.currentTimeMillis());
	            }
            	entity.Position.x += p.getMoveX();
            	entity.velocity.x = p.getMoveX();
                entity.Position.y += p.getMoveY();
                entity.velocity.y = p.getMoveY();
                entity.Position.z += p.getMoveZ();
                entity.velocity.z = p.getMoveZ();
	            
            } else if (event.getPacket() instanceof ServerSpawnPlayerPacket) {
                final ServerSpawnPlayerPacket p = event.getPacket();
                client.getWorld().Entites.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), EntityType.PLAYER, new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));

            } else if (event.getPacket() instanceof ServerSpawnEntityPacket) {
                final ServerSpawnEntityPacket p = event.getPacket();
                client.getWorld().Entites.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), p.getType(), new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));
            } else if (event.getPacket() instanceof ServerSpawnLivingEntityPacket) {
                final ServerSpawnLivingEntityPacket p = event.getPacket();
                client.getWorld().Entites.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), p.getType(), new Vector3D(p.getX(), p.getY(), p.getZ()), p.getYaw(), p.getPitch()));
            } else if (event.getPacket() instanceof ServerSpawnExpOrbPacket) {
            	final ServerSpawnExpOrbPacket p = (ServerSpawnExpOrbPacket) event.getPacket();
            	client.getWorld().Entites.put(p.getEntityId(), new Entity(p.getEntityId(), new UUID(0L, 0L), EntityType.EXPERIENCE_ORB, new Vector3D(p.getX(), p.getY(), p.getZ()), 0, 0));
            
            } else if (event.getPacket() instanceof ServerSpawnPaintingPacket) {
            	final ServerSpawnPaintingPacket p = (ServerSpawnPaintingPacket) event.getPacket();
            	client.getWorld().Entites.put(p.getEntityId(), new Entity(p.getEntityId(), p.getUuid(), EntityType.PAINTING, new Vector3D(p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ()), 0, 0));
            
            } else if (event.getPacket() instanceof ServerEntityDestroyPacket) {
                final ServerEntityDestroyPacket p = event.getPacket();
                for (int i : p.getEntityIds()) {
                    client.getWorld().Entites.remove(i);
                }
            } else if (event.getPacket() instanceof ServerEntityTeleportPacket) {
                final ServerEntityTeleportPacket p = event.getPacket();
                Entity entity = client.getWorld().Entites.get(p.getEntityId());
                entity.Position = new Vector3D(p.getX(), p.getY(), p.getZ());
                entity.Pitch = p.getPitch();
                entity.Yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityPositionRotationPacket) {
                final ServerEntityPositionRotationPacket p = event.getPacket();
                Entity entity = client.getWorld().Entites.get(p.getEntityId());
                entity.Position.setX(entity.Position.getX() + p.getMoveX());
                entity.Position.setY(entity.Position.getY() + p.getMoveY());
                entity.Position.setZ(entity.Position.getZ() + p.getMoveZ());
                entity.Pitch = p.getPitch();
                entity.Yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityRotationPacket) {
                final ServerEntityRotationPacket p = event.getPacket();
                Entity entity = client.getWorld().Entites.get(p.getEntityId());
                entity.Pitch = p.getPitch();
                entity.Yaw = p.getYaw();
            } else if (event.getPacket() instanceof ServerEntityVelocityPacket) {
            	final ServerEntityVelocityPacket p = (ServerEntityVelocityPacket)event.getPacket();
            	if (p.getEntityId() == client.getId()) {
            		client.pm.resetVel();
            		client.pm.velocity = new Vector3D(p.getMotionX(),p.getMotionY(),p.getMotionZ());
            	} else {
            		client.getWorld().Entites.get(p.getEntityId()).velocity=new Vector3D(p.getMotionX(),p.getMotionY(),p.getMotionZ());
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
