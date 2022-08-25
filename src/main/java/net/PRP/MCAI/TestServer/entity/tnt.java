package net.PRP.MCAI.TestServer.entity;

import net.PRP.MCAI.data.Entity;
import net.PRP.MCAI.data.Vector3D;

public class tnt implements Tickable {
	
	public int timeout = 100;
	public Entity localentity;
	
	public tnt(Vector3D pos, Entity e) {
		this.localentity.pos = pos;
		this.localentity = e;
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
		
	}

	@Override
	public Entity getEntity() {
		return localentity;
	}
	
}
