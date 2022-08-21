package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import net.PRP.MCAI.data.Vector3D;

public interface IPathfinder {
	List<Vector3D> toWalk = null;

	public boolean buildPath(boolean addsleepticks);
}
