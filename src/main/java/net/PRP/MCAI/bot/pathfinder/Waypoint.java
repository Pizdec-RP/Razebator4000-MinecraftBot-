package net.PRP.MCAI.bot.pathfinder;

import net.PRP.MCAI.data.Vector3D;

public class Waypoint {
	
	public enum WType {
		STARTPOS, NORMAL;
	}
	
	public Vector3D loc;
	public Vector3D beforeLoc;
	public String movetype;
	public int cost;
	public boolean active;
	public WType type;
}
