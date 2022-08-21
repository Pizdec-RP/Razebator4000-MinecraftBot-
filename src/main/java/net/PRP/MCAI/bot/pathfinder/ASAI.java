package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D; 
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class ASAI implements IPathfinder {
	public List<Vector3D> used = new CopyOnWriteArrayList<>();
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	public int sleepticks = 0;
	private Vector3D cursor;
	
	public ASAI(Bot client, Vector3D start, Vector3D end) {
		this.start = start;
		this.end = end;
		this.client = client;
	}
	
	public ASAI(Bot client, Vector3D end) {
		this.start = client.getPositionInt();
		this.end = end;
		this.client = client;
	}
	
	
	
	public List<Vector3D> getPath() {
		return this.toWalk;
	}
	
	@Override
	public boolean buildPath(boolean addsleepticks) {
		boolean a = buildPathMethod1(addsleepticks);
		if (!a) {
			a = buildPathMethod2(addsleepticks);
		}
		return a;
	}
	
	public boolean bm1(boolean addsleepticks) {
		return buildPathMethod1(addsleepticks);
	}
	
	public boolean buildPathMethod1(boolean addsleepticks) {
		int maxpath = (client.getWorld().renderDistance * 16) * 2;
		toWalk.clear();
		used.clear();
		sleepticks = 0;
		cursor = start;
		toWalk.add(start);
		boolean shortcuted = false;
		while (true) {
			if (toWalk.size() > maxpath) {
				BotU.log("maxpath1");
				if (!shortcuted) {
					shortcuted = true;
				} else {
					return false;
				}
			}
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
				return true;
			}
			List<Vector3D> neighbors;
			neighbors = getNeighbors(cursor,1);
			if (neighbors.isEmpty()) {
				if (Main.debug) System.out.println("pizdec1");
				return false;
			}
			cursor = pickCloser(neighbors);
			used.add(cursor);
			toWalk.add(cursor);
		}
	}
	
	public boolean buildPathMethod2(boolean addsleepticks) {
		int maxpath = (client.getWorld().renderDistance * 16) * 2;
		toWalk.clear();
		used.clear();
		sleepticks = 0;
		cursor = start;
		toWalk.add(start);
		while (true) {
			if (toWalk.size() > maxpath) {
				if (Main.debug) System.out.println("maxpath2");
				return false;
			}
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
				return true;
			}
			List<Vector3D> neighbors;
			neighbors = getNeighbors(cursor,2);
			if (neighbors.isEmpty()) {
				if (Main.debug) System.out.println("pizdec2");
				return false;
			}
			cursor = pickCloser(neighbors);
			used.add(cursor);
			toWalk.add(cursor);
		}
	}
	
	public boolean func_1(Vector3D from, Vector3D to) {
		boolean a = true;
		if (from.y < to.y) {
			a = !VectorUtils.positionIsSafe(to, client) || !VectorUtils.BTavoid(from.add(0,2,0).getBlock(client).type);
		} else if (from.y > to.y) {
			a = !VectorUtils.BTavoid(to.add(0,2,0).getBlock(client).type) || !VectorUtils.positionIsSafe(to, client);
		}
		return a;
	}
	
	public Vector3D getLastPoint() {
		return toWalk.get(toWalk.size()-1==-1?0:toWalk.size()-1);
	}
	
	public void deleteLastSilent() {
		toWalk.remove(toWalk.size()-1==-1?0:toWalk.size()-1);
	}
	
	public List<Vector3D> getNeighbors(Vector3D ps, int method) {
		List<Vector3D> neighbors = new CopyOnWriteArrayList<>();
		neighbors.add(ps.add(1, 0, 0));//100
		neighbors.add(ps.add(0, 0, 1));//1
		neighbors.add(ps.add(-1, 0, 0));//-1
		neighbors.add(ps.add(0, 0, -1));//-100
		neighbors.add(ps.add(1,-1,0));//-9
		neighbors.add(ps.add(0,-1,1));//-11
		neighbors.add(ps.add(-1,-1,0));//-11
		neighbors.add(ps.add(0,-1,-1));//-2
		neighbors.add(ps.add(1,1,0));//110
		neighbors.add(ps.add(0,1,1));//11
		neighbors.add(ps.add(-1,1,0));//-110
		neighbors.add(ps.add(0,1,-1));//0
		
		return neighbors;
	}
	
	public boolean pointIsUsed(Vector3D wp) {
		return used.contains(wp);
	}
	
	public Vector3D pickCloser(List<Vector3D> list) {
		if (list.isEmpty()) return null;
		if (list.size() == 1) return list.get(0);
		//Vector3D currentlyPicked = Main.nn.pickOne(list, this.cursor, client);
		return null;//currentlyPicked;
	}
}
