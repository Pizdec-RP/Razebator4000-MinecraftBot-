package net.PRP.MCAI.movements;

import georegression.struct.point.Point3D_F64;
import net.PRP.MCAI.bot.Bot;
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
	
	public boolean move(Vector3D from, Vector3D to) {
		BotU.LookHead(client, new Point3D_F64(to.x,to.y,to.z));
		//System.out.println("from: "+from.toString());
		//System.out.println("to: "+to.toString());
		
		if (e(from,to)) {
		
		} else if (e(from.add(1,0,0),to)) {
			moveOne("x");
		} else if (e(from.add(0,0,1),to)) {
			moveOne("z");
		} else if (e(from.add(-1,0,0),to)) {
			moveOne("-x");
		} else if (e(from.add(0,0,-1),to)) {
			moveOne("-z");
			
			
		} else if (e(from.add(1,0,1),to)) {
			moveDiag("xz");
		} else if (e(from.add(-1,0,1),to)) {
			moveDiag("-xz");
		} else if (e(from.add(1,0,-1),to)) {
			moveDiag("x-z");
		} else if (e(from.add(-1,0,-1),to)) {
			moveDiag("-x-z");
			
			
		} else if (e(from.add(1,1,0),to)) {
			moveJump("xy");
		} else if (e(from.add(0,1,1),to)) {
			moveJump("zy");
		} else if (e(from.add(-1,1,0),to)) {
			moveJump("-xy");
		} else if (e(from.add(0,1,-1),to)) {
			moveJump("-zy");
			
			
		} else if (e(from.add(1,-1,0),to)) {
			moveDown("x-y");
		} else if (e(from.add(0,-1,1),to)) {
			moveDown("z-y");
		} else if (e(from.add(-1,-1,0),to)) {
			moveDown("-x-y");
		} else if (e(from.add(0,-1,-1),to)) {
			moveDown("-z-y");
			
			
		} else if (e(from.add(1,-1,1),to)) {
			moveDiagDown("xz-y");
		} else if (e(from.add(-1,-1,1),to)) {
			moveDiagDown("-xz-y");
		} else if (e(from.add(1,-1,-1),to)) {
			moveDiagDown("x-z-y");
		} else if (e(from.add(-1,-1,-1),to)) {
			moveDiagDown("-x-z-y");
			
			
		} else if (e(from.add(1,1,1),to)) {
			moveDiagUp("xzy");
		} else if (e(from.add(-1,1,1),to)) {
			moveDiagUp("-xzy");
		} else if (e(from.add(1,1,-1),to)) {
			moveDiagUp("x-zy");
		} else if (e(from.add(-1,1,-1),to)) {
			moveDiagUp("-x-zy");
		} else {
			System.out.println("pizec");
			ThreadU.sleep(5000);
			move(from, to);
		}
		
		return true;
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
	
	public void moveDiagUp(String ax) {
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
		}
		BotU.calibratePosition(client);
	}
	
	public void moveDiagDown(String ax) {
		BotU.calibratePosition(client);
		int pps =  130;
		switch (ax) {
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
		}
		BotU.calibratePosition(client);
	}
	
	public void moveDown(String ax) {
		BotU.calibratePosition(client);
		int pps =  130;
		switch (ax) {
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
		}
		BotU.calibratePosition(client);
	}
	
	public void moveJump(String ax) {
		BotU.calibratePosition(client);
		int pps =  130;
		switch (ax) {
			case "xy":
				client.addY(0.5);
				ThreadU.sleep(pps);
				client.addY(0.5);
				client.addX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.25);
				client.addX(0.4);
				ThreadU.sleep(pps);
				client.addX(0.2);
				client.remY(0.15);
				ThreadU.sleep(pps);
				client.addX(0.3);
				client.remY(0.1);
				ThreadU.sleep(pps);
				break;
            case "zy":
            	client.addY(0.5);
            	ThreadU.sleep(pps);
				client.addY(0.5);
				client.addZ(0.1);
				ThreadU.sleep(pps);
				client.addY(0.25);
				client.addZ(0.4);
				ThreadU.sleep(pps);
				client.addZ(0.2);
				client.remY(0.15);
				ThreadU.sleep(pps);
				client.addZ(0.3);
				client.remY(0.1);
				ThreadU.sleep(pps);
				break;
            case "-xy":
            	client.addY(0.5);
            	ThreadU.sleep(pps);
				client.addY(0.5);
				client.remX(0.1);
				ThreadU.sleep(pps);
				client.addY(0.25);
				client.remX(0.4);
				ThreadU.sleep(pps);
				client.remX(0.2);
				client.remY(0.15);
				ThreadU.sleep(pps);
				client.remX(0.3);
				client.remY(0.1);
				ThreadU.sleep(pps);
				break;
            case "-zy":
            	client.addY(0.5);
            	ThreadU.sleep(pps);
				client.addY(0.5);
				client.remZ(0.1);
				ThreadU.sleep(pps);
				client.addY(0.25);
				client.remZ(0.4);
				ThreadU.sleep(pps);
				client.remZ(0.2);
				client.remY(0.15);
				ThreadU.sleep(pps);
				client.remZ(0.3);
				client.remY(0.1);
				ThreadU.sleep(pps);
				break;
		}
		BotU.calibratePosition(client);
	}
	
	
	
	public void moveDiag(String ax) {
		BotU.calibratePosition(client);
		double pps =  spb() / 5;
		switch (ax) {
			case "xz":
				if (avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					for (int o = 0; o < 5; o++) {
						client.addX(0.2);
						client.addZ(0.2);
						ThreadU.sleep((long)pps * 2);
					}
				} else if (avoid(client.getPosition().add(1, 0, 0)) && !avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)pps * 3);//186.7 30.7   1
					client.addX(0.25);
					ThreadU.sleep((long)pps * 3);//187.0 30.65   2
					client.addX(0.35);
					ThreadU.sleep((long)pps * 3);//187.35 30.65   3
					client.addX(0.05);
					client.addZ(0.55);
					ThreadU.sleep((long)pps * 3);//187.4 31.2   4
					client.addX(0.1);
					client.addZ(0.3);
					ThreadU.sleep((long)pps * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);
					client.addX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.25);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.35);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.05);
					client.addX(0.55);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.1);
					client.addX(0.3);
					ThreadU.sleep((long)pps * 3);
				} else {
					if (!avoid(client.getPosition().add(1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(1, 0, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, 1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, 1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					}
				}
				
				break;
			case "-xz":
				if (avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					for (int o = 0; o < 5; o++) {
						client.remX(0.2);
						client.addZ(0.2);
						ThreadU.sleep((long)pps * 2);
					}
				} else if (avoid(client.getPosition().add(-1, 0, 0)) && !avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.remX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)pps * 3);//186.7 30.7   1
					client.remX(0.25);
					ThreadU.sleep((long)pps * 3);//187.0 30.65   2
					client.remX(0.35);
					ThreadU.sleep((long)pps * 3);//187.35 30.65   3
					client.remX(0.05);
					client.addZ(0.55);
					ThreadU.sleep((long)pps * 3);//187.4 31.2   4
					client.remX(0.1);
					client.addZ(0.3);
					ThreadU.sleep((long)pps * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, 1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.remX(0.2);
					client.addZ(0.2);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.25);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.35);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.05);
					client.remX(0.55);
					ThreadU.sleep((long)pps * 3);
					client.addZ(0.1);
					client.remX(0.3);
					ThreadU.sleep((long)pps * 3);
				} else {
					if (!avoid(client.getPosition().add(-1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(-1, 0, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(-1, 1, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, 1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, 1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, 1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					}
				}
				break;
			case "-x-z":
				if (avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					for (int o = 0; o < 5; o++) {
						client.remX(0.2);
						client.remZ(0.2);
						ThreadU.sleep((long)pps * 2);
					}
				} else if (avoid(client.getPosition().add(-1, 0, 0)) && !avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);
					client.remX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)pps * 3);
					client.remX(0.25);
					ThreadU.sleep((long)pps * 3);
					client.remX(0.35);
					ThreadU.sleep((long)pps * 3);
					client.remX(0.05);
					client.remZ(0.55);
					ThreadU.sleep((long)pps * 3);
					client.remX(0.1);
					client.remZ(0.3);
					ThreadU.sleep((long)pps * 3);
				} else if (!avoid(client.getPosition().add(-1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);
					client.remX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.25);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.35);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.05);
					client.remX(0.55);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.1);
					client.remX(0.3);
					ThreadU.sleep((long)pps * 3);
				} else {
					if (!avoid(client.getPosition().add(-1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(-1, 0, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(-1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					} else if (!avoid(client.getPosition().add(0, 0, 1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, -1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, -1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					}
				}
				break;
			case "x-z":
				if (avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					for (int o = 0; o < 5; o++) {
						client.addX(0.2);
						client.remZ(0.2);
						ThreadU.sleep((long)pps * 2);
					}
				} else if (avoid(client.getPosition().add(1, 0, 0)) && !avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)pps * 3);//186.7 30.7   1
					client.addX(0.25);
					ThreadU.sleep((long)pps * 3);//187.0 30.65   2
					client.addX(0.35);
					ThreadU.sleep((long)pps * 3);//187.35 30.65   3
					client.addX(0.05);
					client.remZ(0.55);
					ThreadU.sleep((long)pps * 3);//187.4 31.2   4
					client.addX(0.1);
					client.remZ(0.3);
					ThreadU.sleep((long)pps * 3);//187.5 31.5   5
				} else if (!avoid(client.getPosition().add(1, 0, 0)) && avoid(client.getPosition().add(0, 0, -1))) {
					BotU.calibratePosition(client);//186.5 30.5
					client.addX(0.2);
					client.remZ(0.2);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.25);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.35);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.05);
					client.addX(0.55);
					ThreadU.sleep((long)pps * 3);
					client.remZ(0.1);
					client.addX(0.3);
					ThreadU.sleep((long)pps * 3);
				} else {
					if (!avoid(client.getPosition().add(1, 0, 0))) {
						BotU.mineBlock(client, client.getPosition().add(1, 0, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 0, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(1, 1, 0).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(1, 1, 0).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					} else if (!avoid(client.getPosition().add(0, 0, -1))) {
						BotU.mineBlock(client, client.getPosition().add(0, 0, -1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 0, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						BotU.mineBlock(client, client.getPosition().add(0, 1, -1).translate(), false);
						while (!VectorUtils.BTavoid(client.getPosition().add(0, 1, -1).getBlock().type)) {
			        		ThreadU.sleep(200);
			        	}
						moveDiag(ax);
					}
				}
				break;
		}
		BotU.calibratePosition(client);
	}
	
	
	public void moveOne(String ax) {
		BotU.calibratePosition(client);
		double pps =  spb() / 5;
		switch (ax) {
			case "x":
				for (int o = 0; o < 5; o++) {
					client.addX(0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
            case "y":
            	for (int o = 0; o < 5; o++) {
            		client.addY(0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
            case "z":
            	for (int o = 0; o < 5; o++) {
            		client.addZ(0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
            case "-x":
				for (int o = 0; o < 5; o++) {
					client.addX(-0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
            case "-y":
            	for (int o = 0; o < 5; o++) {
            		client.addY(-0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
            case "-z":
            	for (int o = 0; o < 5; o++) {
            		client.addZ(-0.2);
					ThreadU.sleep((long)(pps * 1.5));
				}
				break;
		}
		BotU.calibratePosition(client);
	}
	
}
