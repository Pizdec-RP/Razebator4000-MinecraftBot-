package net.PRP.MCAI.bot.pathfinder;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.specific.Miner.bbmct;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;

//@SuppressWarnings("static-access")
public class PathExecutor {
	public enum State {
		SEARCHING, WALKING, FINISHED;
	}
	public Vector3D start;
	public Vector3D end;
	public Bot client;
	public State state = State.FINISHED;
	public int sleepticks = 0;
	private Vector3D from;
	private Vector3D to;
	public BadAStar path = null;
	int pticks = 0;
	
	public PathExecutor(Bot client) {
		this.client = client;
		this.end = new Vector3D(0, 0, 0);
	}
	
	public void setup(Vector3D end) {
		if (state == State.WALKING) return;
		if (end == null) {
			BotU.log("null path target");
			return;
		}
		this.start = client.getPositionInt();
		this.end = end;
		this.from = start;
		this.state = State.SEARCHING;
		//if (Main.debug) System.out.println("pf: "+end.forCommand());
	}
	
	public void setupNoBreak(Vector3D end) {
		if (state == State.WALKING) return;
		this.start = client.getPositionInt();
		this.end = end;
		this.from = start;
		this.state = State.WALKING;
		//if (Main.debug) System.out.println("pf: "+end.forCommand());
		this.path = new BadAStar(client, this.end);
		if (path.buildPath(true)) {
			if (path.toWalk.isEmpty()) return;
			this.to = path.toWalk.get(0);
		} else {
			finish("bpf1");
		}
	}
	
	public void setupOnlyBreak(Vector3D end) {
		if (state == State.WALKING) return;
		this.start = client.getPositionInt();
		this.end = end;
		this.from = start;
		this.state = State.WALKING;
		if (Main.debug) System.out.println("pf: "+end.forCommand());
		this.path = new BadAStar(client, this.end);
		if (path.buildPath(true)) {
			if (path.toWalk.isEmpty()) return;
			this.to = path.toWalk.get(0);
		} else {
			finish("bpf2");
		}
	}
	
	public boolean testForPath(Vector3D end) {
		return testBuildPath(false, client.getPositionInt(), end);
	}
	
	public void smoothPath() {
		
	}
	
	public void func_2(Vector3D pos) {
		if (state == State.WALKING) {
			for (Vector3D p : path.toWalk) {
				if (VectorUtils.equalsInt(p, pos)) {
					state = State.SEARCHING;
					return;
				}
			}
		}
	}
	
	public void func_3() {
		if (state == State.WALKING) {
			for (Vector3D p : path.toWalk) {
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
				this.path = new BadAStar(client, this.end);
				if (path.buildPath(false)) {
					state = State.WALKING;
					if (path.toWalk.isEmpty()) return;
					this.to = path.toWalk.get(0);
					tick();
				} else {
					if ((boolean)Main.getset("visualizePath")) {
						if (path.toWalk.isEmpty()) {
							return;
						}
						state = State.WALKING;
						this.to = path.toWalk.get(0);
						tick();
					} else {
						finish("cant build path");
					}
					
				}
			} else if (state == State.WALKING) {
				if (path == null) {
					finish("null");
					return;
				}
				moveEntity();
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish("tick exception");
		}
	}
	
	public void finish(String reason) {
		this.state = State.FINISHED;
		pticks = 0;
		//if (Main.debug) System.out.println("ended, ref:("+reason+"). from"+this.start+" to:"+this.end);
		this.start = null;
		this.end = null;
		//client.pm.Back();
	}
	
	public void reset() {
		this.state = State.FINISHED;
		path = null;
		pticks = 0;
		this.start = client.getPositionInt();
		//client.pm.Back();
	}
	
	
	private boolean testBuildPath(boolean addsleepticks, Vector3D starta, Vector3D enda) {
		IPathfinder temp = new BadAStar(client, starta, enda);
		return temp.buildPath(addsleepticks);
	}
	
	public boolean botinpos(Vector3D pos) {
		return client.getPosX() >= pos.x+0.2 && client.getPosX() <= pos.x+0.7 && client.getPosZ() >= pos.z+0.2 && client.getPosZ() <= pos.z+0.7 && client.getPosY() >= pos.y-1 && client.getPosY() <= pos.y+1.5;
		//System.out.println(a+" x: "+client.getPosX() +" >= "+ (pos.x+0.3) +" and "+ client.getPosX() +"<="+ (pos.x+0.6) +" and "+ client.getPosZ() +">="+ (pos.z+0.3) +" and "+ client.getPosZ() +"<="+ (pos.z+0.6) +" and "+ client.getPosY() +">="+ (pos.y-1) +" and "+ client.getPosY() +"<="+ (pos.y+1.5));
		//return a;
	}
	
	public Vector3D getVector(Vector3D from, Vector3D to) {
		return new Vector3D(to.x-from.x, to.y-from.y, to.z-from.z);
	}
	
	public void moveEntity() {
		if (botinpos(to) || (boolean)Main.getset("visualizePath")) {
			if ((boolean)Main.getset("visualizePath")) BotU.chat(client, "/particle minecraft:barrier "+to.forCommand()+" 0 0 0 200 1");
			pticks = 0;
			/*if (!e(to, client.getPositionInt())) {
				err++;
				if (err > 8) {
					finish("err1");
				}
				return;
			} else {
				err=0;
			}*/
			path.toWalk.remove(to);
			if (path.toWalk.size() == 0) {
				//BotU.log("1");
				finish("end");
				return;
			}
			from = to;
			to = path.toWalk.get(0);
		} else {
			pticks++;
			if (pticks > 100) {
				finish("err1");
			}
		}
		
		if ((boolean)Main.getset("visualizePath")) return;
		
		if (to.hasheddata > 0) {
			if (client.bbm.state != bbmct.ENDED) return;
			if (to.hasheddata == 1) {
				if (!to.getBlock(client).isAvoid()) {
					client.bbm.setup(to);
					return;
				} else if (!to.add(0,1,0).getBlock(client).isAvoid()) {
					client.bbm.setup(to.add(0,1,0));
					return;
				}
			} else if (to.hasheddata == 2) {
				if (!from.add(0,2,0).getBlock(client).isAvoid()) {
					client.bbm.setup(client.getPositionInt().add(0,2,0));
					return;
				} else if (!to.getBlock(client).isAvoid()) {
					client.bbm.setup(to);
					return;
				} else if (!to.add(0,1,0).getBlock(client).isAvoid()) {
					client.bbm.setup(to.add(0,1,0));
					return;
				}
			} else if (to.hasheddata == 3) {
				if (!to.add(0,2,0).getBlock(client).isAvoid()) {
					client.bbm.setup(to.add(0,2,0));
					return;
				} else if (!to.add(0,1,0).getBlock(client).isAvoid()) {
					client.bbm.setup(to.add(0,1,0));
					return;
				} else if (!to.getBlock(client).isAvoid()) {
					client.bbm.setup(to);
					return;
				}
			} else if (to.hasheddata == 4) {
				if (!to.getBlock(client).isAvoid()) {
					client.bbm.setup(to);
					return;
				}
			}
		}
		
		BotU.LookHead(client, to.add(0.5, 1, 0.5));
		client.pm.Sprint();
	}
	
	public boolean e(Vector3D one, Vector3D two) {
		return VectorUtils.equalsInt(one,two);
	}
}
