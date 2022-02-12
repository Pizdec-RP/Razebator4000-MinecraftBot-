package net.PRP.MCAI.bot.pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.specific.BlockBreakManager.bbmct;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;

public class AStar {
	public enum State {
		SEARCHING, WALKING, FINISHED;
	}
	public List<Vector3D> used = new CopyOnWriteArrayList<>();
	public List<Vector3D> toWalk = new CopyOnWriteArrayList<>();
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	public boolean pathIsReady;
	public State state = State.FINISHED;
	public int sleepticks = 0;
	
	private Vector3D from;
	private Vector3D to;
	
	//x or z or -x or -z
	private double iMoveEveryTick = 0.2;
	
	private int curMoveTick = 0;
	private int MaxMoveTicks = 0;
	
	private List<double[]> iy = new ArrayList<>() {
		private static final long serialVersionUID = 2649835325268333898L;
	{
		add(new double[] {0,0.3});
		add(new double[] {0.2,0.3});
		add(new double[] {0,0.2});
		add(new double[] {0,0.2});
		add(new double[] {0.3,0});
		add(new double[] {0.3,0});
		add(new double[] {0.2,0});
	}};
	
	private List<double[]> iMinusY = new ArrayList<>() {
		private static final long serialVersionUID = -3649514727610880193L;
	{
		add(new double[] {0.2D,0D});//0.7 1.0
		add(new double[] {0.3D,0D});//0.95 1.0
		add(new double[] {0.2D,0D});//1.2 1.0
		add(new double[] {0.2D,0.2D});//1.4 0.8
		add(new double[] {0D,0.3D});//0.5 0.5
		add(new double[] {0D,0.5D});//0.5 0.0
	}};
	private int err;
	
	public AStar(Bot client) {
		this.client = client;
		this.end = new Vector3D(0, 0, 0);
	}
	
	public void setup(Vector3D start, Vector3D end) {
		if (state == State.WALKING) return;
		this.pathIsReady = false;
		this.start = start;
		this.end = end;
		this.from = start;
		this.state = State.SEARCHING;
	}
	
	public void setup(Vector3D end) {
		if (state == State.WALKING) return;
		this.pathIsReady = false;
		this.start = client.getPositionInt();
		this.end = end;
		this.from = start;
		this.state = State.SEARCHING;
	}
	
	public boolean testForPath(Vector3D end) {
		return testBuildPath(false, client.getPositionInt(), end);
	}
	
	public void smoothPath() {
		
	}
	
	public void func_2(Vector3D pos) {
		if (state == State.WALKING) {
			for (Vector3D p : toWalk) {
				if (VectorUtils.equalsInt(p, pos)) {
					state = State.SEARCHING;
					return;
				}
			}
		}
	}
	
	public void func_3() {
		if (state == State.WALKING) {
			for (Vector3D p : toWalk) {
				if (VectorUtils.equalsInt(p, client.getPositionInt())) {
					state = State.SEARCHING;
					return;
				}
			}
		}
		state = State.FINISHED;
	}
	
	public void tick() {
		try {
			if (sleepticks > 0) {
				sleepticks--;
				return;
			}
			if (state == State.FINISHED) return;
			if (state == State.SEARCHING) {
				if (buildPath(true)) {
					pathIsReady = true;
					state = State.WALKING;
					if (toWalk.isEmpty()) return;
					this.to = toWalk.get(0);
				} else {
					finish();
				}
			} else if (state == State.WALKING) {
				moveEntity();
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}
	
	public void reset() {
		curMoveTick = 0;
		MaxMoveTicks = 0;
		err = 0;
		this.pathIsReady = false;
		BotU.calibratePosition(client);
		this.start = client.getPositionInt();
	}
	
	public boolean testBuildPath(boolean addsleepticks, Vector3D starta, Vector3D enda) {
		List<Vector3D> usd = new CopyOnWriteArrayList<>();
		List<Vector3D> tw = new CopyOnWriteArrayList<>();
		Vector3D cursor = starta;
		int l = 0;
		while (true) {
			if (VectorUtils.equalsInt(cursor, enda)) {
				return true;
			}
			l++;
			if (l > 200) return false;
			List<Vector3D> neighbors = new CopyOnWriteArrayList<>();
			neighbors.add(cursor.add(1,1,0));
			neighbors.add(cursor.add(0,1,1));
			neighbors.add(cursor.add(-1,1,0));
			neighbors.add(cursor.add(0,1,-1));
			neighbors.add(cursor.add(1,-1,0));
			neighbors.add(cursor.add(0,-1,1));
			neighbors.add(cursor.add(-1,-1,0));
			neighbors.add(cursor.add(0,-1,-1));
			
			for (Vector3D n : neighbors) {
				if (func_1(n)) {
					neighbors.remove(n);
				}
			}
			
			neighbors.add(cursor.add(1, 0, 0));
			neighbors.add(cursor.add(0, 0, 1));
			neighbors.add(cursor.add(-1, 0, 0));
			neighbors.add(cursor.add(0, 0, -1));
			for (Vector3D n : neighbors) {
				if (usd.contains(n) || !VectorUtils.positionIsSafe(n, client)) neighbors.remove(n);
			}
			
			if (neighbors.size() == 0) {
				return false;
			}
			
			Vector3D currentlyPicked = null;
			for (Vector3D pos : neighbors) {
				if (currentlyPicked == null) {
					currentlyPicked = pos;
				} else if (VectorUtils.sqrt(pos, enda) < VectorUtils.sqrt(currentlyPicked, enda)) {
					currentlyPicked = pos;
				} else if (VectorUtils.sqrt(pos, enda) == VectorUtils.sqrt(currentlyPicked, enda)) {
					if (MathU.rnd(1, 2) == 1)
						currentlyPicked = pos;
				}
			}
			cursor = currentlyPicked;
			
			usd.add(cursor);
			//BotU.chat(client, "/setblock "+cursor.forCommnad()+" stone");
			//ThreadU.sleep(1000);
			//BotU.chat(client, "/setblock "+cursor.forCommnad()+" air");
			tw.add(cursor);
		}
	}
	
	public boolean buildPath(boolean addsleepticks) {
		toWalk.clear();
		used.clear();
		reset();
		Vector3D cursor = this.start;
		while (true) {
			if (VectorUtils.equalsInt(cursor, end)) {
				if (addsleepticks) sleepticks = 10;
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
	
	public boolean func_1(Vector3D pos) {
		return !VectorUtils.positionIsSafe(pos, client) && !VectorUtils.BTavoid(pos.add(0,2,0).getBlock(client).type);
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
	
	public void moveEntity() {
		String moveType = enumMoveCord(from, to);
		if (moveType == "x") {
			client.addX(iMoveEveryTick);
			curMoveTick++;
		} else if (moveType == "z") {
			client.addZ(iMoveEveryTick);
			curMoveTick++;
		} else if (moveType == "-x") {
			client.remX(iMoveEveryTick);
			curMoveTick++;
		} else if (moveType == "-z") {
			client.remZ(iMoveEveryTick);
			curMoveTick++;
		}
		
		else if (moveType == "-y") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,-1,0));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				} else {
					
				}
			} else if (curMoveTick == 2) {
				client.remY(0.5);
				curMoveTick++;
			} else if (curMoveTick == 3) {
				client.remY(0.5);
				curMoveTick++;
			}
		}
		
		
		else if (moveType == "xm") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(1,0,0));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addX(iMoveEveryTick);
				curMoveTick++;
			}
		} else if (moveType == "zm") {
			
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,0,1));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,1,1));
				curMoveTick++;
			} else if (curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addZ(iMoveEveryTick);
				curMoveTick++;
			}
			
		} else if (moveType == "-xm") {
			
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(-1,0,0));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(-1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remX(iMoveEveryTick);
				curMoveTick++;
			}
			
		} else if (moveType == "-zm") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,0,-1));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,1,-1));
				curMoveTick++;
			} else if (curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remZ(iMoveEveryTick);
				curMoveTick++;
			}
		}
		
		
		else if (moveType == "xy") {
			client.addX(iy.get(curMoveTick)[0]);
			client.addY(iy.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "zy") {
			client.addZ(iy.get(curMoveTick)[0]);
			client.addY(iy.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "-xy") {
			client.remX(iy.get(curMoveTick)[0]);
			client.addY(iy.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "-zy") {
			client.remZ(iy.get(curMoveTick)[0]);
			client.addY(iy.get(curMoveTick)[1]);
			curMoveTick++;
		}
		
		else if (moveType == "x-y") {
			client.addX(iMinusY.get(curMoveTick)[0]);
			client.remY(iMinusY.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "z-y") {
			client.addZ(iMinusY.get(curMoveTick)[0]);
			client.remY(iMinusY.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "-x-y") {
			client.remX(iMinusY.get(curMoveTick)[0]);
			client.remY(iMinusY.get(curMoveTick)[1]);
			curMoveTick++;
		} else if (moveType == "-z-y") {
			client.remZ(iMinusY.get(curMoveTick)[0]);
			client.remY(iMinusY.get(curMoveTick)[1]);
			curMoveTick++;
		}
		
		if (moveType.contains("y")) client.onGround = false;
		
		if (curMoveTick >= MaxMoveTicks) {
			client.onGround = true;
			if (!e(to, client.getPositionInt())) {
				err++;
				if (err > 8) {
					//BotU.calibratePosition(client);
					finish();
					return;
				}
			} else {
				err=0;
			}
			curMoveTick = 0;
			toWalk.remove(to);
			if (toWalk.size() == 0) {
				finish();
				return;
			}
			from = to;
			//System.out.println(toWalk.toString());
			to = toWalk.get(0);
			BotU.calibratePosition(client);
			BotU.LookHead(client, to.add(0, 1, 0));
		}
		
	}
	
	public void finish() {
		this.state = State.FINISHED;
		curMoveTick = 0;
		MaxMoveTicks = 0;
		err = 0;
		this.pathIsReady = false;
		BotU.calibratePosition(client);
		this.start = null;
		this.end = null;
		if (Main.debug) {
			System.out.println("ended");
		}
	}
	
	public boolean e(Vector3D one, Vector3D two) {
		return VectorUtils.equalsInt(one,two);
	}
	
	public String enumMoveCord(Vector3D from, Vector3D to) {
		if (e(from,to)) {
			return "";
		} else if (e(from.add(1,0,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = 9;
				return "xm";
			} else {
				MaxMoveTicks = 5;
				return "x";
			}
		} else if (e(from.add(0,0,1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = 9;
				return "zm";
			} else {
				MaxMoveTicks = 5;
				return "z";
			}
		} else if (e(from.add(-1,0,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = 9;
				return "-xm";
			} else {
				MaxMoveTicks = 5;
				return "-x";
			}
		} else if (e(from.add(0,0,-1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = 9;
				return "-zm";
			} else {
				MaxMoveTicks = 5;
				return "-z";
			}
			
		
		} else if (e(from.add(0,-1,0),to)) {
			MaxMoveTicks = 4;
			return "-y";
			
		} else if (e(from.add(1,1,0),to)) {
			MaxMoveTicks = iy.size();
			return "xy";
		} else if (e(from.add(0,1,1),to)) {
			MaxMoveTicks = iy.size();
			return "zy";
		} else if (e(from.add(-1,1,0),to)) {
			MaxMoveTicks = iy.size();
			return "-xy";
		} else if (e(from.add(0,1,-1),to)) {
			MaxMoveTicks = iy.size();
			return "-zy";
			
			
		} else if (e(from.add(1,-1,0),to)) {
			MaxMoveTicks = iMinusY.size();
			return "x-y";
		} else if (e(from.add(0,-1,1),to)) {
			MaxMoveTicks = iMinusY.size();
			return "z-y";
		} else if (e(from.add(-1,-1,0),to)) {
			MaxMoveTicks = iMinusY.size();
			return "-x-y";
		} else if (e(from.add(0,-1,-1),to)) {
			MaxMoveTicks = iMinusY.size();
			return "-z-y";
		}
		return "unknown";
	}
}
