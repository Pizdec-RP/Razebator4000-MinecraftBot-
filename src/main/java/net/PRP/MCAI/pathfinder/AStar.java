package net.PRP.MCAI.pathfinder;

import java.util.ArrayList;
import java.util.List;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.movements.Movements;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;

@SuppressWarnings("unused")
public class AStar {
	public List<Vector3D> used = new ArrayList<>();
	public List<Vector3D> toWalk = new ArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Movements mv;
	public Bot client;
	
	public AStar(Bot client, Vector3D start, Vector3D end) {
		this.start = start;
		this.end = end;
		this.mv = new Movements(client, 6);
	}
	
	public void startCalc3D(Bot client) {
		try {
			if (!VectorUtils.equalsInt(start, end)) {
				Waypoint cursor = wp(start);
				while (!VectorUtils.equals(cursor.loc, end)) {
					List<Waypoint> neighbors = getWalkableWPAround(cursor);
					cursor = getNear(neighbors);
					used.add(cursor.loc);
					toWalk.add(cursor.loc);
				}
				client.setmovelocked(true);
				for(Vector3D p : toWalk) {
					boolean t = this.mv.move(client.getPositionInt(), p);
				}
				client.setmovelocked(false);
			}
		} catch (NullPointerException e) {
			//pass
		}
	}
	
	public void startCalc2D(Bot client) {
		try {
			if (!VectorUtils.equalsInt(start, end)) {
				Waypoint cursor = wp(start);
				while (!VectorUtils.equals(cursor.loc, end)) {
					List<Waypoint> neighbors = getWalkableWPAround(cursor);
					cursor = getNear(neighbors);
					used.add(cursor.loc);
					toWalk.add(cursor.loc);
				}
				client.setmovelocked(true);
				for(Vector3D p : toWalk) {
					boolean t = this.mv.move(client.getPositionInt(), p);
				}
				client.setmovelocked(false);
			}
		} catch (NullPointerException e) {
			//pass
		}
	}
	
	public Waypoint getNear(List<Waypoint> allPos) {
		Waypoint minpos = null;
        for (Waypoint pos : allPos) {
        	Vector3D position = pos.loc;
        	double distance = Math.sqrt(Math.pow(position.getX() - this.end.getX(), 2) + Math.pow(position.getY() - this.end.getY(), 2) + Math.pow(position.getZ() - this.end.getZ(), 2));
        	if (minpos == null) {
        		minpos = pos;
        	} else {
        		double distanceminpos = Math.sqrt(Math.pow(minpos.loc.getX() - this.end.getX(), 2) + Math.pow(minpos.loc.getY() - this.end.getY(), 2) + Math.pow(minpos.loc.getZ() - this.end.getZ(), 2));
        		if (distance < distanceminpos) {
        			minpos = pos;
        		}
        	}
        }
        return minpos;
    }
	
	public Waypoint getNear2D(List<Waypoint> allPos) {
		Waypoint minpos = null;
        for (Waypoint pos : allPos) {
        	Vector3D position = pos.loc;
        	double distance = Math.sqrt(Math.pow(position.getX() - this.end.getX(), 2) + Math.pow(position.getY(), 2) + Math.pow(position.getZ() - this.end.getZ(), 2));
        	if (minpos == null) {
        		minpos = pos;
        	} else {
        		double distanceminpos = Math.sqrt(Math.pow(minpos.loc.getX() - this.end.getX(), 2) + Math.pow(minpos.loc.getY(), 2) + Math.pow(minpos.loc.getZ() - this.end.getZ(), 2));
        		if (distance < distanceminpos) {
        			minpos = pos;
        		}
        	}
        }
        return minpos;
    }
	
	//Zalypa ne rabochaya hz pochemu
	public Waypoint pickCloser(List<Waypoint> neighbors) {
		System.out.println("start");
		Waypoint last = null;
		for (Waypoint wp : neighbors) {
			//System.out.println("curcost "+wp.cost);
			if (last == null) {
				System.out.println("last is empty => "+wp.cost);
				last = wp;
			} else {
				if (wp.cost < last.cost) {
					System.out.println(wp.cost+" < "+last.cost);
					last = wp;
				} else {
					System.out.println(wp.cost+" >= "+last.cost);
				}
			}
		}
		System.out.println("end");
		return last;
	}
	
	public boolean wpAlreadyUsed(Waypoint wp) {
		boolean isused = false;
		for (Vector3D pos : used) {
			if (VectorUtils.equals(wp.loc,pos)) {
				isused = true;
				break;
			}
		}
		return isused;
	}
	
	public Waypoint wp(Vector3D curpos) {
		Waypoint wp = new Waypoint();
		wp.loc = curpos;
		wp.active = true;
		int hr = (int)Math.sqrt(Math.pow(curpos.getX() - this.end.getX(), 2) + Math.pow(curpos.getY() - this.end.getY(), 2) + Math.pow(curpos.getZ() - this.end.getZ(), 2));
		wp.cost = hr;
		return wp;
	}
	
	public List<Waypoint> getWalkableWPAround(Waypoint ps) {
		Vector3D pos = ps.loc;
		List<Waypoint> neighbors = new ArrayList<>();
		for (int y = -1; y <= 1; y++) {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					//System.out.println(x+" "+y+" "+z);
					Vector3D curpos = new Vector3D((int)(pos.x + x), (int)(pos.y + y),(int)(pos.z + z));
					if (!VectorUtils.equals(this.start, curpos) || !VectorUtils.equals(pos, curpos)) {
						Waypoint wp = wp(curpos);
						if (!wpAlreadyUsed(wp) && VectorUtils.positionIsSafe(wp.loc)) {
							neighbors.add(wp);
							//System.out.println("neighbours add"+wp.loc.toString());
						}
					}
				}
			}
		}
		return neighbors;
	}
}
