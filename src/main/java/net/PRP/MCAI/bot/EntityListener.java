package net.PRP.MCAI.bot;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.Vector3D;
import world.Entity;

public class EntityListener extends SessionAdapter {
	private Bot client;

	public EntityListener(Bot client) {
        this.client = client;
    }
	
	@Override
    public void packetReceived(PacketReceivedEvent event) {
		if (event.getPacket() instanceof ServerEntityPositionPacket) {
			//System.out.println("sepp");
            final ServerEntityPositionPacket p = event.getPacket();
            Entity entity = null;
            entity = Main.getWorld().getEntity(p.getEntityId());
            if (entity == null) {
            	entity = new Entity();
            	entity.pos = new Vector3D(p.getMovementX(),p.getMovementY(),p.getMovementZ());
                Main.world.entities.add(entity);
            } else {
            	entity.pos.setX(p.getMovementX());
                entity.pos.setY(p.getMovementY());
                entity.pos.setZ(p.getMovementZ());
            }
		} else if (event.getPacket() instanceof ServerSpawnPlayerPacket) {
			//System.out.println("sspp");
	        final ServerSpawnPlayerPacket p = event.getPacket();
	        if (p.getUUID() == client.getUUID()) {
	            //pass
	        } else {
	        	Entity en = new Entity();
	        	en.pos = new Vector3D(p.getX(), p.getY(), p.getZ());
	        	en.id = p.getEntityId();
	        	System.out.println(p.getMetadata().length);
	        	en.uuid = p.getUUID();
	        }
		} else if (event.getPacket() instanceof ServerSpawnMobPacket) {
            final ServerSpawnMobPacket p = event.getPacket();
            Entity en;
        	en = new Entity();
        	en.id = p.getEntityId();
            en.pos = new Vector3D(p.getX(),p.getY(),p.getZ());
            en.type = p.getType();
            en.uuid = p.getUUID();
            Main.getWorld().entities.add(en);
		}
	}
}
