package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;

public interface IPathfinder {
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Bot client = null;
	
	public boolean buildPath(boolean addsleepticks);
}
