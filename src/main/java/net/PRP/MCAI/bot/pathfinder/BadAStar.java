package net.PRP.MCAI.bot.pathfinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.PRP.MCAI.utils.VectorUtils;
public class BadAStar implements IPathfinder{
	public List<Vector3D> used = new CopyOnWriteArrayList<>();
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	public int sleepticks = 0;
	private boolean skipused = false;
	
	public BadAStar(Bot client, Vector3D end) {
		this.start = client.getPositionInt();
		this.end = end;
		this.client = client;
	}
	
	public BadAStar(Bot client, Vector3D start,Vector3D end) {
		this.start = start;
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
		Vector3D cursor = start;
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
				shortcut();
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
		Vector3D cursor = start;
		toWalk.add(start);
		while (true) {
			if (toWalk.size() > maxpath) {
				if (Main.debug) System.out.println("maxpath2");
				return false;
			}
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
				shortcut();
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
	
	public void shortcut() {
		skipused = true;
		for (int i = 0; i <= toWalk.size()-1; i++) {
			Vector3D curs = toWalk.get(i);
			List<Vector3D> rawn = getNeighbors(curs,1);
			int i1 = i+1;
			for (; i1 <= toWalk.size()-1; i1++) {
				Vector3D afcu = toWalk.get(i1);
				if (rawn.contains(afcu)) {
					boolean b = false;
					for (Vector3D p : toWalk) {
						if (b) {
							if (VectorUtils.equalsInt(p, afcu)) {
								b = false;
							} else {
								toWalk.remove(p);
								used.remove(p);
							}
						} else {
							if (VectorUtils.equalsInt(curs, p)) {
								b = true;
							}
						}
					}
				}
			}
		}
		skipused = false;
	}
	
	public Vector3D getLastPoint() {
		return toWalk.get(toWalk.size()-1==-1?0:toWalk.size()-1);
	}
	
	public void deleteLastSilent() {
		toWalk.remove(toWalk.size()-1==-1?0:toWalk.size()-1);
	}
	
	public boolean sfwPATTERN(Vector3D pos) {
		return pos.getBlock(client).isAvoid()
				&&
				pos.add(0,1,0).getBlock(client).isAvoid()
				&&
				pos.add(0,-1,0).getBlock(client).ishard();
	}
	
	public boolean sfsPATTERN(Vector3D pos) {
		//BotU.log(pos.getBlock(client).isWater()+" "+pos.add(0,1,0).getBlock(client).isAvoid()+" "+pos.add(0,2,0).getBlock(client).isAvoid());
		return pos.getBlock(client).isWater() 
				&& 
				pos.add(0,1,0).getBlock(client).isAvoid()
				&& 
				pos.add(0,2,0).getBlock(client).isAvoid();
	}
	
	public boolean sfsdPATTERN(Vector3D pos) {
		return pos.getBlock(client).isAvoid()
				&& 
				pos.add(0,1,0).getBlock(client).isAvoid()
				&& 
				pos.add(0,2,0).getBlock(client).isAvoid()
				&&
				pos.add(0,-1,0).getBlock(client).isWater();
	}
	
	public boolean sdsPATTERN(Vector3D pos) {
		return pos.getBlock(client).isAvoid()
				&&
				pos.add(0,1,0).getBlock(client).isAvoid()
				&&
				pos.add(0,2,0).getBlock(client).isAvoid()
				&&
				pos.add(0,-1,0).getBlock(client).ishard();
	}
	
	public boolean susPATTERN(Vector3D pos) {
		return pos.getBlock(client).isAvoid()
				&&
				pos.add(0,-1,0).getBlock(client).ishard()
				&&
				pos.add(0,1,0).getBlock(client).isAvoid();
	}
	
	public boolean sdiwPATTERN(Vector3D pos) {
		return pos.getBlock(client).isAvoid()
				&&
				pos.add(0,1,0).getBlock(client).isAvoid()
				&&
				pos.add(0,-1,0).getBlock(client).isWater();
	}
	
	public boolean stepupIsFromWater(Vector3D pos) {
		return sfsPATTERN(getLastPoint()) && sfwPATTERN(pos);
	}
	
	public List<Vector3D> getNeighbors(Vector3D ps, int method) {
		List<Vector3D> neighbors = new CopyOnWriteArrayList<>();
		neighbors.add(ps.add(1, 0, 0));
		neighbors.add(ps.add(0, 0, 1));
		neighbors.add(ps.add(-1, 0, 0));
		neighbors.add(ps.add(0, 0, -1));
		for (Vector3D n : neighbors) {
			if (pointIsUsed(n)) {
				neighbors.remove(n);
			} else {
				if (sfwPATTERN(n)) {
					
				} else if (sfsPATTERN(n)) {
					
				} else if (sdiwPATTERN(n)) {
					neighbors.remove(n);
					neighbors.add(n.add(0,-1,0));
				} else {
					neighbors.remove(n);
				}
			}
		}
		
		List<Vector3D> temp = new CopyOnWriteArrayList<>();
		
		temp.add(ps.add(1,-1,0));
		temp.add(ps.add(0,-1,1));
		temp.add(ps.add(-1,-1,0));
		temp.add(ps.add(0,-1,-1));
		
		for (Vector3D n : temp) {
			//if (VectorUtils.equalsInt(new Vector3D(-81,65,-1488),n)) BotU.log("test");
			if (pointIsUsed(n)) {
				temp.remove(n);
			} else {
				if (sdsPATTERN(n)) {
					
				} else if (sfsdPATTERN(n)) {
					
				} else if (sfsPATTERN(n)) {
					
				} else {
					temp.remove(n);
				}
			}
		}
		
		neighbors.addAll(temp);
		temp.clear();
		
		temp.add(ps.add(1,1,0));
		temp.add(ps.add(0,1,1));
		temp.add(ps.add(-1,1,0));
		temp.add(ps.add(0,1,-1));
		
		
		for (Vector3D n : temp) {
			
			if (pointIsUsed(n)) {
				temp.remove(n);
			} else if (sdsPATTERN(ps)) {
				if (susPATTERN(n)) {
					
				} else {
					temp.remove(n);
				}
			} else {
				temp.remove(n);
			}
		}
		
		neighbors.addAll(temp);
		temp.clear();
		
		if (!neighbors.isEmpty()) {
			return neighbors;
		}
		
		if (neighbors.isEmpty() && method == 2) {
			Vector3D psy;
			psy = ps.add(0,-1,0);
			psy.hasheddata = 4;
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
				if (poss.isCanStayHere(client)) {
					if (ps.y - poss.y == -1) {
						poss.hasheddata = 2;
						if (poss.isMineable(client) && poss.add(0,1,0).isMineable(client) && poss.add(0,2,0).isMineable(client)) {
							neighbors.add(poss);
						}
					} else if (ps.y - poss.y == 1) {
						poss.hasheddata = 3;
						if (poss.isMineable(client) && poss.add(0,1,0).isMineable(client) && ps.add(0,2,0).isMineable(client)) {
							neighbors.add(poss);
						}
					} else {
						System.out.println("godddamn man");
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
		if (skipused) return false;
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
