package net.PRP.MCAI.TestServer.entity;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.ExplodedBlockRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;

public class tnt implements Tickable {
	
	public int timeout = 70;
	public Entity en;
	
	public tnt(Vector3D pos) {
		this.en = new Entity(Server.nextEID(), UUID.randomUUID(), EntityType.PRIMED_TNT, pos.add(0.5, 0, 0.5), 0.0F, 0.0F);
		Server.sendForEver(new ServerEntityTeleportPacket(en.eid,en.pos.x+0.5,en.pos.y,en.pos.z+0.5,0.0F,0.0F,true));
		Server.sendForEver(new ServerEntityVelocityPacket(en.eid,0,0,0));
	}

	@Override
	public void tick() {
		timeout--;
		if (timeout <= 0) {
			explode();
		}
	}
	
	@Override
	public void packettick() {
		
	}
	
	public void explode() {
		Multiworld.Entities.remove(en.eid);
		Server.tickable.remove(this);
		List<ExplodedBlockRecord> l = new CopyOnWriteArrayList<ExplodedBlockRecord>();
		for (int x = (int)en.pos.x-3; x <=en.pos.x+3;x++) {
			for (int z = (int)en.pos.z-3; z <=en.pos.z+3;z++) {
				for (int y = (int)en.pos.y-3; y <=en.pos.y+3;y++) {
					l.add(new ExplodedBlockRecord(x,y,z));
				}
			}
		}
		for (ExplodedBlockRecord pos : l) {
			Vector3D trnsltd = new Vector3D(pos.getX(),pos.getY(),pos.getZ());
			if (VectorUtils.sqrt(trnsltd, en.pos)>3) {
				l.remove(pos);
			} else {
				Server.setBlock(trnsltd, 0);
			}
		}
		Server.sendForEver(new ServerExplosionPacket(
				(float)en.pos.x,
				(float)en.pos.y,
				(float)en.pos.z,
				5,
				l,
				0,0,0
				));
	}

	@Override
	public Entity getEntity() {
		return en;
	}
	
}
