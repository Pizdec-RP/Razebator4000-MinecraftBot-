package net.PRP.MCAI.TestServer.entity;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;

public class Arrow implements Tickable {
	public Entity en;
	private boolean sended = false;
	
	public Arrow(Vector3D pos, Vector3D vel) {
		this.en = new Entity(Server.nextEID(), UUID.randomUUID(), EntityType.ARROW, pos, 0,0);
		this.en.vel = vel;
		
	}
	
	@Override
	public void packettick() {
		
	}
	
	@Override
	public void tick() {
		AABB h = Multiworld.getBlock(en.pos.add(en.vel).floor()).getHitbox();
		if (h==null) {
			en.pos = en.pos.add(en.vel);
			sended=false;
		} else {
			if (!h.collide(getCollider())) {
				en.pos = en.pos.add(en.vel);
				sended=false;
			} else {
				if (!sended) {
					sended = true;
					en.vel.setX(0);
					en.vel.setZ(0);
					en.vel.setY(-0.4);
					
				}
			}
		}
	}
	
	public AABB getCollider() {
		return new AABB(en.pos.x-0.25,en.pos.y-0.25,en.pos.z-0.25,en.pos.x+0.25,en.pos.y+0.25,en.pos.z+0.25);
	}

	@Override
	public Entity getEntity() {
		return this.en;
	}
}
