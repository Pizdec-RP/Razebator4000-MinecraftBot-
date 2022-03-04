package net.PRP.MCAI.bot.pathfinder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.player.Hand;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockFace;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.bot.specific.BlockBreakManager.bbmct;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.*;

public class AStar {
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
	
	public List<Vector3D> ignored = new CopyOnWriteArrayList<Vector3D>();
	
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
	
	private List<Double> jump = new ArrayList<>() {
		private static final long serialVersionUID = 4114558308453593709L;

	{
		add(0.3);
		add(0.3);
		add(0.2);
		add(0.2);
	}};
	public PathObject path = null;
	private int err;
	
	public AStar(Bot client) {
		this.client = client;
		this.end = new Vector3D(0, 0, 0);
	}
	
	public void setup(Vector3D end) {
		if (state == State.WALKING) return;
		this.start = client.getPositionInt();
		this.end = end;
		this.from = start;
		this.state = State.SEARCHING;
		if (Main.debug) System.out.println("pf: "+end.forCommnad());
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
				this.path = new PathObject(client, this.end);
				if (path.buildPath(true)) {
					state = State.WALKING;
					if (path.toWalk.isEmpty()) return;
					this.sleepticks = path.sleepticks;
					this.to = path.toWalk.get(0);
				} else {
					finish();
				}
			} else if (state == State.WALKING) {
				if (path == null) {
					finish();
					return;
				}
				moveEntity();
			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}
	
	public void finish() {
		this.state = State.FINISHED;
		curMoveTick = 0;
		MaxMoveTicks = 0;
		err = 0;
		BotU.calibratePosition(client);
		if (Main.debug) System.out.println("ended from"+this.start+" to:"+this.end);
		this.start = null;
		this.end = null;
		ignored.clear();
	}
	
	public void reset() {
		this.state = State.FINISHED;
		path = null;
		curMoveTick = 0;
		MaxMoveTicks = 0;
		err = 0;
		BotU.calibratePosition(client);
		this.start = client.getPositionInt();
		ignored.clear();
	}
	
	public boolean testBuildPath(boolean addsleepticks, Vector3D starta, Vector3D enda) {
		PathObject temp = new PathObject(client, starta, enda);
		return temp.buildPath(addsleepticks);
	}
	
	@SuppressWarnings("deprecation")
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
		
		else if (moveType == "+y") {
			if (curMoveTick == 0) {
				BotU.LookHead(client, client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 1) {
				client.bbm.setup(client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else if (curMoveTick >= 3 && curMoveTick <= 6) {
				client.addY(jump.get(curMoveTick-3));
			} else if (curMoveTick == 7) {
				if (client.playerInventory.fromInventoryToHotbar(new ArrayList<String>() {
					private static final long serialVersionUID = 1L;{
						add("dirt");
						add("cobblestone");
						add("sand");
						add("gravel");
						add("granite");
						add("diorite");
						add("andesite");
						add("planks");
						add("log");
					}}, 1)) {
					client.getSession().send(new ClientPlayerPlaceBlockPacket(client.getPositionInt().add(0,-1,0).translate(), BlockFace.UP, Hand.MAIN_HAND, 0, 0, 0, false));
				} else {
					finish();
				}
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
		
		
		else if (moveType == "xym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(1,2,0));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addX(iy.get(curMoveTick-6)[0]);
				client.addY(iy.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "zym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,1,1));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(0,2,1));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addZ(iy.get(curMoveTick-6)[0]);
				client.addY(iy.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "-xym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(-1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(-1,2,0));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remX(iy.get(curMoveTick-6)[0]);
				client.addY(iy.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "-zym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,1,-1));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,2,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(0,2,-1));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remZ(iy.get(curMoveTick-6)[0]);
				client.addY(iy.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
			
			
			
			
			
			
		} else if (moveType == "x-ym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(1,0,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(1,-1,0));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addX(iMinusY.get(curMoveTick-6)[0]);
				client.remY(iMinusY.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "z-ym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,1,1));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,0,1));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(0,-1,1));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.addZ(iMinusY.get(curMoveTick-6)[0]);
				client.remY(iMinusY.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "-x-ym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(-1,1,0));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(-1,0,0));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(-1,-1,0));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remX(iMinusY.get(curMoveTick-6)[0]);
				client.remY(iMinusY.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else if (moveType == "-z-ym") {
			if (curMoveTick == 0) {
				client.bbm.setup(client.getPositionInt().add(0,1,-1));
				curMoveTick++;
			} else if (curMoveTick == 2) {
				client.bbm.setup(client.getPositionInt().add(0,0,-1));
				curMoveTick++;
			} else if (curMoveTick == 4) {
				client.bbm.setup(client.getPositionInt().add(0,-1,-1));
				curMoveTick++;
			} else if (curMoveTick == 1 || curMoveTick == 5 || curMoveTick == 3) {
				if (client.bbm.state == bbmct.ENDED) {
					curMoveTick++;
				}
			} else {
				client.remZ(iMinusY.get(curMoveTick-6)[0]);
				client.remY(iMinusY.get(curMoveTick-6)[1]);
				curMoveTick++;
			}
		} else {
			reset();
			return;
		}
		
		//System.out.println(curMoveTick+" < "+MaxMoveTicks);
		if (curMoveTick >= MaxMoveTicks) {
			client.onGround = true;
			if (!e(to, client.getPositionInt())) {
				err++;
				if (err > 8) {
					//System.out.println("err");
					//BotU.calibratePosition(client);
					finish();
					return;
				}
			} else {
				err=0;
			}
			curMoveTick = 0;
			path.toWalk.remove(to);
			if (path.toWalk.size() == 0) {
				//System.out.println("towalk empty");
				finish();
				return;
			}
			from = to;
			//System.out.println(toWalk.toString());
			to = path.toWalk.get(0);
			BotU.calibratePosition(client);
			BotU.LookHead(client, to.add(0, 1, 0));
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
			
			
			
			
		} else if (e(from.add(0,1,0),to)) {
			MaxMoveTicks = 7;
			return "+y";
		
		} else if (e(from.add(0,-1,0),to)) {
			MaxMoveTicks = 4;
			return "-y";
			
			
			
			
			
		} else if (e(from.add(1,1,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+7;
				return "xym";
			}
			MaxMoveTicks = iy.size();
			return "xy";
		} else if (e(from.add(0,1,1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+7;
				return "zym";
			}
			MaxMoveTicks = iy.size();
			return "zy";
		} else if (e(from.add(-1,1,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+7;
				return "-xym";
			}
			MaxMoveTicks = iy.size();
			return "-xy";
		} else if (e(from.add(0,1,-1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+7;
				return "-zym";
			}
			MaxMoveTicks = iy.size();
			return "-zy";
			
			
			
			
		} else if (e(from.add(1,-1,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+6;
				return "x-ym";
			}
			MaxMoveTicks = iMinusY.size();
			return "x-y";
		} else if (e(from.add(0,-1,1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+6;
				return "z-ym";
			}
			MaxMoveTicks = iMinusY.size();
			return "z-y";
		} else if (e(from.add(-1,-1,0),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+6;
				return "-x-ym";
			}
			MaxMoveTicks = iMinusY.size();
			return "-x-y";
		} else if (e(from.add(0,-1,-1),to)) {
			if (to.hasheddata == 1) {
				MaxMoveTicks = iMinusY.size()+6;
				return "-z-ym";
			}
			MaxMoveTicks = iMinusY.size();
			return "-z-y";
		}
		return "unknown";
	}
}
