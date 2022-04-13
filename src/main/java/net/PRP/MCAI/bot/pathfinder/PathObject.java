package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.VectorUtils;
public class PathObject {
	public List<Vector3D> used = new CopyOnWriteArrayList<>();
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	public int sleepticks = 0;
	
	public PathObject(Bot client, Vector3D end) {
		this.start = client.getPositionInt();
		this.end = end;
		this.client = client;
	}
	
	public PathObject(Bot client, Vector3D start,Vector3D end) {
		this.start = start;
		this.end = end;
		this.client = client;
	}
	
	public List<Vector3D> getPath() {
		return this.toWalk;
	}
	
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
		Vector3D cursor = this.start;
		int l = 0;
		while (true) {
			l++; if (l>maxpath) {
				if (Main.debug) System.out.println("maxpath");
				return false;
			}
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
				if (Main.debug) System.out.println(cursor.toStringInt()+" == "+end.toStringInt());
				return true;
			}
			List<Vector3D> neighbors;
			neighbors = getNeighbors(cursor,1);
			if (neighbors.isEmpty()) {
				if (Main.debug) System.out.println("pizdec");
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
	
	public boolean buildPathMethod2(boolean addsleepticks) {
		int maxpath = (client.getWorld().renderDistance * 16) * 2;
		toWalk.clear();
		used.clear();
		sleepticks = 0;
		Vector3D cursor = this.start;
		int l = 0;
		while (true) {
			l++; if (l>maxpath) {
				if (Main.debug) System.out.println("maxpath");
				return false;
			}
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
				return true;
			}
			List<Vector3D> neighbors;
			neighbors = getNeighbors(cursor,2);
			if (neighbors.isEmpty()) {
				if (Main.debug) System.out.println("pizdec");
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
	
	public List<Vector3D> getNeighbors(Vector3D ps, int method) {
		List<Vector3D> neighbors = new CopyOnWriteArrayList<>();
		neighbors.add(ps.add(1, 0, 0));
		neighbors.add(ps.add(0, 0, 1));
		neighbors.add(ps.add(-1, 0, 0));
		neighbors.add(ps.add(0, 0, -1));
		for (Vector3D n : neighbors) {
			if (pointIsUsed(n) || !VectorUtils.positionIsSafe(n, client)) neighbors.remove(n);
		}
		
		List<Vector3D> temp = new CopyOnWriteArrayList<>();
		temp.add(ps.add(1,1,0));
		temp.add(ps.add(0,1,1));
		temp.add(ps.add(-1,1,0));
		temp.add(ps.add(0,1,-1));
		temp.add(ps.add(1,-1,0));
		temp.add(ps.add(0,-1,1));
		temp.add(ps.add(-1,-1,0));
		temp.add(ps.add(0,-1,-1));
		
		for (Vector3D n : temp) {
			if (func_1(ps,n)) {
				temp.remove(n);
			}
		}
		neighbors.addAll(temp);
		
		if (!neighbors.isEmpty() && method != 2) {
			for (Vector3D n : neighbors) {
				if (n.getBlock(client).touchLiquid(client)) neighbors.remove(n);
			}
			return neighbors;
		}
		
		if (neighbors.isEmpty() && method == 2) {
			Vector3D psy;
			psy = ps.add(0,-1,0);
			if (VectorUtils.icanstayhere(psy.add(0,-1,0).getBlock(client).type)) {
				neighbors.add(psy);
			}
			
			psy = ps.add(1,0,0);
			psy.hasheddata = 1;
			if (psy.isCanStayHere(client)) neighbors.add(psy);
			
			psy = ps.add(0,0,1);
			psy.hasheddata = 1;
			if (psy.isCanStayHere(client)) neighbors.add(psy);
			
			psy = ps.add(-1,0,0);
			psy.hasheddata = 1;
			if (psy.isCanStayHere(client)) neighbors.add(psy);
			
			psy = ps.add(0,0,-1);
			psy.hasheddata = 1;
			if (psy.isCanStayHere(client)) neighbors.add(psy);
			
			
			temp.clear();
			temp.add(ps.add(1,1,0));
			temp.add(ps.add(0,1,1));
			temp.add(ps.add(-1,1,0));
			temp.add(ps.add(0,1,-1));
			temp.add(ps.add(1,-1,0));
			temp.add(ps.add(0,-1,1));
			temp.add(ps.add(-1,-1,0));
			temp.add(ps.add(0,-1,-1));
			
			temp.forEach((poss) -> {
				poss.hasheddata = 1;
				if (poss.isCanStayHere(client)) {
					if (ps.y - poss.y == -1) {
						if (poss.isMineable(client) && poss.add(0,1,0).isMineable(client) && poss.add(0,2,0).isMineable(client)) {
							neighbors.add(poss);
						}
					} else if (ps.y - poss.y == 1) {
						if (poss.isMineable(client) && poss.add(0,1,0).isMineable(client) && ps.add(0,2,0).isMineable(client)) {
							neighbors.add(poss);
						}
					} else {
						System.out.println(". "+poss.y);
					}
				}
			});
			
		}
		for (Vector3D n : neighbors) {
			if (n.getBlock(client).touchLiquid(client)) neighbors.remove(n);
		}
		return neighbors;
	}
	
	public boolean pointIsUsed(Vector3D wp) {
		return used.contains(wp);
	}
	
	public Vector3D pickCloser(List<Vector3D> list) {
		if (list.isEmpty()) return null;
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
