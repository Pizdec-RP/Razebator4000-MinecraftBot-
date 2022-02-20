package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.VectorUtils;

public class PathBuilder {
	public List<Vector3D> used = new CopyOnWriteArrayList<>();
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	
	public PathBuilder(Bot client, Vector3D end) {
		this.start = client.getPositionInt();
		this.end = end;
	}
	
	public List<Vector3D> getPath() {
		return this.toWalk;
	}
	
	public boolean build() {
		toWalk.clear();
		used.clear();
		Vector3D cursor = this.start;
		int l = 0;
		while (true) {
			l++; if (l>200) return false;
			if (VectorUtils.equalsInt(cursor, end)) {
				return true;
			}
			List<Vector3D> neighbors = getNeighbors(cursor);
			if (neighbors.size() == 0) {
				return false;
			}
			cursor = pickCloser(neighbors);
			used.add(cursor);
			//BotU.chat(client, "/setblock "+cursor.forCommnad()+" stone");
			//ThreadU.sleep(1000);
			//BotU.chat(client, "/setblock "+cursor.forCommnad()+" air");
			toWalk.add(cursor);
		}
	}
	
	public boolean func_1(Vector3D pos) {
		return !VectorUtils.positionIsSafe(pos, client) && !VectorUtils.BTavoid(pos.add(0,2,0).getBlock(client).type);
	}
	
	public List<Vector3D> getNeighbors(Vector3D ps) {
		List<Vector3D> neighbors = new CopyOnWriteArrayList<>();
		neighbors.add(ps.add(1,1,0));
		neighbors.add(ps.add(0,1,1));
		neighbors.add(ps.add(-1,1,0));
		neighbors.add(ps.add(0,1,-1));
		neighbors.add(ps.add(1,-1,0));
		neighbors.add(ps.add(0,-1,1));
		neighbors.add(ps.add(-1,-1,0));
		neighbors.add(ps.add(0,-1,-1));
		
		for (Vector3D n : neighbors) {
			if (func_1(n)) {
				neighbors.remove(n);
			}
		}
		
		neighbors.add(ps.add(1, 0, 0));
		neighbors.add(ps.add(0, 0, 1));
		neighbors.add(ps.add(-1, 0, 0));
		neighbors.add(ps.add(0, 0, -1));
		for (Vector3D n : neighbors) {
			if (pointIsUsed(n) || !VectorUtils.positionIsSafe(n, client)) neighbors.remove(n);
		}
		if (neighbors.isEmpty()) {
			Vector3D psy;
			psy = ps.add(0,-1,0);
			if (VectorUtils.sqrt(psy, this.end) < VectorUtils.sqrt(client.getPositionInt(), this.end) && VectorUtils.icanstayhere(psy.add(0,-1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
			
			psy = ps.add(1,0,0);
			psy.hasheddata = 1;
			if (VectorUtils.sqrt(psy, this.end) < VectorUtils.sqrt(client.getPositionInt(), this.end) && VectorUtils.icanstayhere(psy.add(0, -1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
			
			psy = ps.add(0,0,1);
			psy.hasheddata = 1;
			if (VectorUtils.sqrt(psy, this.end) < VectorUtils.sqrt(client.getPositionInt(), this.end) && VectorUtils.icanstayhere(psy.add(0, -1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
			
			psy = ps.add(-1,0,0);
			psy.hasheddata = 1;
			if (VectorUtils.sqrt(psy, this.end) < VectorUtils.sqrt(client.getPositionInt(), this.end) && VectorUtils.icanstayhere(psy.add(0, -1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
			
			psy = ps.add(0,0,-1);
			psy.hasheddata = 1;
			if (VectorUtils.sqrt(psy, this.end) < VectorUtils.sqrt(client.getPositionInt(), this.end) && VectorUtils.icanstayhere(psy.add(0, -1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
		}
		return neighbors;
	}
	
	public boolean pointIsUsed(Vector3D wp) {
		return used.contains(wp);
	}
	
	public Vector3D pickCloser(List<Vector3D> list) {
		if (list.size() == 1) return list.get(0);
		Vector3D currentlyPicked = null;
		for (Vector3D pos : list) {
			if (currentlyPicked == null) {
				currentlyPicked = pos;
			} else if (VectorUtils.sqrt(pos, end) < VectorUtils.sqrt(currentlyPicked, end)) {
				currentlyPicked = pos;
			} else if (VectorUtils.sqrt(pos, end) == VectorUtils.sqrt(currentlyPicked, end)) {
				if (MathU.rnd(1, 2) == 1)
					currentlyPicked = pos;
			}
		}
		return currentlyPicked;
	}
}
