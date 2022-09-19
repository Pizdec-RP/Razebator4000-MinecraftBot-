package net.PRP.MCAI.TestServer.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.block.ExplodedBlockRecord;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.TestServer.level.Explosion;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.VectorUtils;

public class tnt {
	
	public int timeout = 70;
	public Entity en;
	public Server Server;
	
	public tnt(Vector3D pos, Server s) {
		this.Server = s;
		this.en = new Entity(Server.nextEID(), UUID.randomUUID(), EntityType.PRIMED_TNT, pos.add(0.5, 0, 0.5), 0.0F, 0.0F);
		Server.sendForEver(new ServerEntityTeleportPacket(en.eid,en.pos.x+0.5,en.pos.y,en.pos.z+0.5,0.0F,0.0F,true));
		Server.sendForEver(new ServerEntityVelocityPacket(en.eid,0,0,0));
	}

	public void tick() {
		timeout--;
		//ep.tick();
		if (timeout <= 0) {
			explode();
		}
	}
	
	public void packettick() {
		Server.sendForEver(new ServerEntityTeleportPacket(en.eid,en.pos.x,en.pos.y,en.pos.z,0.0F,0.0F,en.onGround));
		//BotU.log("packetpos: "+en.pos.x+" "+en.pos.y+" "+en.pos.z);
	}
	
	public void explode() {
		Server.sendForEver(new ServerEntityDestroyPacket(new int[] {en.getEid()}));
		Multiworld.Entities.remove(en.eid);
		//Server.tickable.remove(this);
		Explosion ex = new Explosion(this.en.pos,4);
		ex.explode();
		List<Vector3D> list = ex.getAffectedBlocks();
		List<ExplodedBlockRecord> l = new ArrayList<>();
		for (Vector3D pos : list) {
			if (Multiworld.getBlock(pos).id == 137) {
				//tnt t = new tnt(pos);
				//Server.spawnTickable(t);
				//t.getEntity().setVel(t.getEntity().vel.add(VectorUtils.getVector(en.pos, pos)));
				//t.timeout = MathU.rnd(10,20);
			}
			Server.setBlock(pos, 0);
			l.add(new ExplodedBlockRecord((int)Math.floor(pos.x),(int)Math.floor(pos.y),(int)Math.floor(pos.z)));
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

	public Entity getEntity() {
		return en;
	}

	public AABB getHitbox() {
		return new AABB(en.pos.x-0.5,en.pos.y,en.pos.z-0.5,en.pos.x+0.5,en.pos.y+1,en.pos.z+0.5);
	}
	
	
}
