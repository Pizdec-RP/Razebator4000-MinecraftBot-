package net.PRP.MCAI.TestServer.entity;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.physics;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class Item implements Tickable {
	private Vector3D beforepos;
	public Entity en;
	public AABB collider;
	public ItemStack item;
	private dataSend d = dataSend.non;
	private boolean velsended = false;
	
	
	
	public Item(ItemStack item, Vector3D pos, Vector3D vel) {
		this.item = item;
		this.beforepos = pos;
		collider = new AABB(0.125,0.125,0.125,-0.125,-0.125,-0.125);
		this.en = new Entity(Server.nextEID(),UUID.randomUUID(),EntityType.ITEM, pos, 0f,0f);
		en.vel = vel;
	}
	
	private void airfall() {
		en.pos.y -= physics.gravity;
		en.pos.y *= physics.airdrag;
		en.pos.x *= physics.airborneInertia;
        en.pos.z *= physics.airborneInertia;
        en.vel = en.pos.subtract(beforepos);
        if (Math.abs(en.vel.x) < physics.negligeableVelocity) en.vel.x = 0;
		if (Math.abs(en.vel.y) < physics.negligeableVelocity) en.vel.y = 0;
		if (Math.abs(en.vel.z) < physics.negligeableVelocity) en.vel.z = 0;
	}

	@Override
	public void tick() {
		AABB h = Multiworld.getBlock(en.pos.add(en.vel).floor()).getHitbox();
		if (h == null) {
			beforepos = en.pos;
			airfall();
			d = dataSend.velandpos;
			velsended = false;
			en.onGround=false;
		} else {
			if (collider.clone().offset(en.pos.add(en.vel)).collide(h)) {
				en.vel.origin();
				en.onGround=true;
				if (!velsended) {
					d = dataSend.velandpos;
					velsended = true;
				}
			} else {
				beforepos = en.pos;
				airfall();
				d = dataSend.velandpos;
				velsended = false;
				en.onGround=false;
			}
		}
		
	}

	@Override
	public Entity getEntity() {
		return this.en;
	}

	@Override
	public void packettick() {
		if (d == dataSend.pos) {
			Server.sendForEver(new ServerEntityTeleportPacket(en.eid,en.pos.x,en.pos.y,en.pos.z,en.yaw,en.pitch,en.onGround));
		} else if (d == dataSend.vel) {
			Server.sendForEver(new ServerEntityVelocityPacket(en.eid,en.vel.x,en.vel.y,en.vel.z));
		} else if (d == dataSend.velandpos) {
			Server.sendForEver(new ServerEntityVelocityPacket(en.eid,en.vel.x,en.vel.y,en.vel.z));
			Server.sendForEver(new ServerEntityTeleportPacket(en.eid,en.pos.x,en.pos.y,en.pos.z,en.yaw,en.pitch,en.onGround));
		}
	}

}
