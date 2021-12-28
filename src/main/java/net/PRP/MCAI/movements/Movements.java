package net.PRP.MCAI.movements;

import georegression.struct.point.Point3D_F64;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.pathfinder.Waypoint;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;

public class Movements {
	public Bot client;
	public double BPS; // 6-walk X-run

	public Movements(Bot client, double BPS) {
		this.client = client;
		this.BPS = BPS;
	}
	
	public String EnumMove(Waypoint point) {
		Vector3D from = point.beforeLoc;
		Vector3D to = point.loc;
		BotU.LookHead(client, new Point3D_F64(to.x,to.y,to.z));
		
		if (e(from,to)) {
			return "";
		} else if (e(from.add(1,0,0),to)) {
			return "x";
		} else if (e(from.add(0,0,1),to)) {
			return "z";
		} else if (e(from.add(-1,0,0),to)) {
			return "-x";
		} else if (e(from.add(0,0,-1),to)) {
			return "-z";
			
			
		} else if (e(from.add(1,0,1),to)) {
			return "xz";
		} else if (e(from.add(-1,0,1),to)) {
			return "-xz";
		} else if (e(from.add(1,0,-1),to)) {
			return "x-z";
		} else if (e(from.add(-1,0,-1),to)) {
			return "-x-z";
			
			
		} else if (e(from.add(1,1,0),to)) {
			return "xy";
		} else if (e(from.add(0,1,1),to)) {
			return "zy";
		} else if (e(from.add(-1,1,0),to)) {
			return "-xy";
		} else if (e(from.add(0,1,-1),to)) {
			return "-zy";
			
			
		} else if (e(from.add(1,-1,0),to)) {
			return "x-y";
		} else if (e(from.add(0,-1,1),to)) {
			return "z-y";
		} else if (e(from.add(-1,-1,0),to)) {
			return "-x-y";
		} else if (e(from.add(0,-1,-1),to)) {
			return "-z-y";
			
			
		} else if (e(from.add(1,-1,1),to)) {
			return "xz-y";
		} else if (e(from.add(-1,-1,1),to)) {
			return "-xz-y";
		} else if (e(from.add(1,-1,-1),to)) {
			return "x-z-y";
		} else if (e(from.add(-1,-1,-1),to)) {
			return "-x-z-y";
			
			
		} else if (e(from.add(1,1,1),to)) {
			return "xzy";
		} else if (e(from.add(-1,1,1),to)) {
			return "-xzy";
		} else if (e(from.add(1,1,-1),to)) {
			return "x-zy";
		} else if (e(from.add(-1,1,-1),to)) {
			return "-x-zy";
		} else {
			//System.out.println("pizec");
			//System.out.println("from: "+from.toString());
			//System.out.println("to: "+to.toString());
			return "unknown";
		}
	}
	
	public boolean e(Vector3D one, Vector3D two) {
		return VectorUtils.equalsInt(one,two);
	}
	
	public boolean s(Vector3D pos) {
		return VectorUtils.positionIsSafe(pos);
	}
	
	public boolean avoid(Vector3D pos) {
		return VectorUtils.BTavoid(pos.getBlock().type) && VectorUtils.BTavoid(pos.add(0, 1, 0).getBlock().type);
	}
	
	public void warn() {System.out.println("идиот чини хуйню свою");}
	
	public double spb() {
		return 1000 / BPS;
	}
	
	public void moveAct(String ax) {
		BotU.calibratePosition(client);
		int pps =  130;
		switch (ax) {
			case "xzy":
				client.addY(0.5);
				ThreadU.sleep(pps);
				client.addY(0.4);
				client.addZ(0.1);
				client.addX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.1);
				client.addZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				break;
            case "-xzy":
            	client.addY(0.5);
				ThreadU.sleep(pps);
				client.addY(0.4);
				client.addZ(0.1);
				client.remX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.1);
				client.addZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				break;
            case "x-zy":
            	client.addY(0.5);
				ThreadU.sleep(pps);
				client.addY(0.4);
				client.remZ(0.1);
				client.addX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.1);
				client.remZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				client.addX(0.3);
				ThreadU.sleep(pps);
				break;
            case "-x-zy":
            	client.addY(0.5);
				ThreadU.sleep(pps);
				client.addY(0.4);
				client.remZ(0.1);
				client.remX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.1);
				client.remZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				client.remX(0.3);
				ThreadU.sleep(pps);
				break;
            case "xz-y":
				client.addX(0.3);
				client.addZ(0.3);
				ThreadU.sleep(pps);
				client.addX(0.3);
				client.addZ(0.3);
				ThreadU.sleep(pps);
				client.addX(0.4);
				client.addZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "-xz-y":
            	client.remX(0.3);
				client.addZ(0.3);
				ThreadU.sleep(pps);
				client.remX(0.3);
				client.addZ(0.3);
				ThreadU.sleep(pps);
				client.remX(0.4);
				client.addZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "x-z-y":
            	client.addX(0.3);
				client.remZ(0.3);
				ThreadU.sleep(pps);
				client.addX(0.3);
				client.remZ(0.3);
				ThreadU.sleep(pps);
				client.addX(0.4);
				client.remZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "-x-z-y":
            	client.remX(0.3);
				client.remZ(0.3);
				ThreadU.sleep(pps);
				client.remX(0.3);
				client.remZ(0.3);
				ThreadU.sleep(pps);
				client.remX(0.4);
				client.remZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "x-y":
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.addX(0.3);
				ThreadU.sleep(pps);
				client.addX(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "z-y":
            	client.addZ(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				ThreadU.sleep(pps);
				client.addZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "-x-y":
            	client.remX(0.3);
				ThreadU.sleep(pps);
				client.remX(0.3);
				ThreadU.sleep(pps);
				client.remX(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "-z-y":
            	client.remZ(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				ThreadU.sleep(pps);
				client.remZ(0.4);
				client.remY(0.1);
				ThreadU.sleep(pps);
				client.remY(0.4);
				ThreadU.sleep(pps);
				client.remY(0.5);
				ThreadU.sleep(pps);
				break;
            case "xy":
            	client.addX(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.75);
            	client.addX(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.42);
            	client.addX(0.06);
            	ThreadU.sleep(100);
            	client.addY(0.08);
            	client.addX(0.1);
            	ThreadU.sleep(100);
            	client.remY(0.23);
            	client.addX(0.17);
            	ThreadU.sleep(100);
            	client.remY(0.02);
            	client.addX(0.45);
            	ThreadU.sleep(100);
				break;
            case "zy":
            	client.addZ(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.75);
            	client.addZ(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.42);
            	client.addZ(0.06);
            	ThreadU.sleep(100);
            	client.addY(0.08);
            	client.addZ(0.1);
            	ThreadU.sleep(100);
            	client.remY(0.23);
            	client.addZ(0.17);
            	ThreadU.sleep(100);
            	client.remY(0.02);
            	client.addZ(0.45);
            	ThreadU.sleep(100);
				break;
            case "-xy":
            	client.remX(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.75);
            	client.remX(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.42);
            	client.remX(0.06);
            	ThreadU.sleep(100);
            	client.addY(0.08);
            	client.remX(0.1);
            	ThreadU.sleep(100);
            	client.remY(0.23);
            	client.remX(0.17);
            	ThreadU.sleep(100);
            	client.remY(0.02);
            	client.remX(0.45);
            	ThreadU.sleep(100);
				break;
            case "-zy":
            	client.remZ(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.75);
            	client.remZ(0.1);
            	ThreadU.sleep(100);
            	client.addY(0.42);
            	client.remZ(0.06);
            	ThreadU.sleep(100);
            	client.addY(0.08);
            	client.remZ(0.1);
            	ThreadU.sleep(100);
            	client.remY(0.23);
            	client.remZ(0.17);
            	ThreadU.sleep(100);
            	client.remY(0.02);
            	client.remZ(0.45);
            	ThreadU.sleep(100);
				break;
            case "xz":
				if (avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					for (int o = 0; o < 5; o++) {
						client.addX(0.2);
						client.addZ(0.2);
						ThreadU.sleep((long)(this.spb() / 5.0) * 2);
					}
				} else if (avoid(client.getPosition().add(1, 0, 0)) && !avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//186.7 30.7   1
					client.addX(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.0 30.65   2
					client.addX(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.35 30.65   3
					client.addX(0.05);
					client.addZ(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.4 31.2   4
					client.addX(0.1);
					client.addZ(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);
					client.addX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.05);
					client.addX(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.1);
					client.addX(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
				} else {
					if (!avoid(client.getPosition().add(1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(1, 0, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, 1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, 1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					}
				}
				
				break;
			case "-xz":
				if (avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					for (int o = 0; o < 5; o++) {
						client.remX(0.2);
						client.addZ(0.2);
						ThreadU.sleep((long)(this.spb() / 5.0) * 2);
					}
				} else if (avoid(client.getPosition().add(-1, 0, 0)) && !avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.remX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//186.7 30.7   1
					client.remX(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.0 30.65   2
					client.remX(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.35 30.65   3
					client.remX(0.05);
					client.addZ(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.4 31.2   4
					client.remX(0.1);
					client.addZ(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.remX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.05);
					client.remX(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.addZ(0.1);
					client.remX(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
				} else {
					if (!avoid(client.getPosition().add(-1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(-1, 0, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(-1, 1, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, 1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, 1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					}
				}
				break;
			case "-x-z":
				if (avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					for (int o = 0; o < 5; o++) {
						client.remX(0.2);
						client.remZ(0.2);
						ThreadU.sleep((long)(this.spb() / 5.0) * 2);
					}
				} else if (avoid(client.getPosition().add(-1, 0, 0)) && !avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);
					client.remX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remX(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remX(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remX(0.05);
					client.remZ(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remX(0.1);
					client.remZ(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
				} else if (!avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);
					client.remX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.05);
					client.remX(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.1);
					client.remX(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
				} else {
					if (!avoid(client.getPosition().add(-1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(-1, 0, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, -1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, -1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					}
				}
				break;
			case "x-z":
				if (avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					for (int o = 0; o < 5; o++) {
						client.addX(0.2);
						client.remZ(0.2);
						ThreadU.sleep((long)(this.spb() / 5.0) * 2);
					}
				} else if (avoid(client.getPosition().add(1, 0, 0)) && !avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//186.7 30.7   1
					client.addX(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.0 30.65   2
					client.addX(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.35 30.65   3
					client.addX(0.05);
					client.remZ(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.4 31.2   4
					client.addX(0.1);
					client.remZ(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.25);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.35);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.05);
					client.addX(0.55);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
					client.remZ(0.1);
					client.addX(0.3);
					ThreadU.sleep((long)(this.spb() / 5.0) * 3);
				} else {
					if (!avoid(client.getPosition().add(1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(1, 0, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0));
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					} else if (!avoid(client.getPosition().add(0, 0, -1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, -1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, -1));
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveAct(ax);
					}
				}
				break;
			case "x":
				for (int o = 0; o < 5; o++) {
					client.addX(0.2);
					ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
            case "y":
            	for (int o = 0; o < 5; o++) {
            		client.addY(0.2);
            		ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
            case "z":
            	for (int o = 0; o < 5; o++) {
            		client.addZ(0.2);
            		ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
            case "-x":
				for (int o = 0; o < 5; o++) {
					client.addX(-0.2);
					ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
            case "-y":
            	for (int o = 0; o < 5; o++) {
            		client.addY(-0.2);
            		ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
            case "-z":
            	for (int o = 0; o < 5; o++) {
            		client.addZ(-0.2);
            		ThreadU.sleep((long)((this.spb() / 5.0) * 1.5));
				}
				break;
		}
		BotU.calibratePosition(client);
	}
	
}
