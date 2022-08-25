package net.PRP.MCAI.TestServer.entity;

import net.PRP.MCAI.data.Entity;

public interface Tickable {
	 
	public void tick();
	
	public Entity getEntity();

	public void packettick();

}
