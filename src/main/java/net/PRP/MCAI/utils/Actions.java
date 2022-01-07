package net.PRP.MCAI.utils;


import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.pathfinder.AStar;
import world.BlockType.Type;

public class Actions {
	public static void mineWood(Bot client, int howmany) {
		try {
			if (client.isInAction()) {
				BotU.chat(client, "я занят");
				return;
			}
			client.setInAction(true);
			new Thread(() -> {
				for (int i = 0; i < howmany; i++) {
					Vector3D pos12 = VectorUtils.findNearestBlockById(client, 17);
					Vector3D end = VectorUtils.botCanTouchBlockAt(client, pos12);
	    			AStar pf = new AStar(client, client.getPositionInt(), end);
	    			pf.startCalc2D(client);
					while (true) {
	    				if (VectorUtils.sqrt(client.getPositionInt(), end) <= 4.5) {
	        				client.setmovelocked(false);
	        				BotU.mineBlock(client, pos12);
	        	        	while (!VectorUtils.BTavoid(pos12.getBlock(client).type)) {
	        	        		ThreadU.sleep(100);
	        	        	}
	        	        	break;
	    				} else {
	    					//System.out.println("nu");
	    					ThreadU.sleep(1000);
	    				}
					}
				}
				client.setInAction(false);
			}).start();
		} catch (Exception passed) {
			client.setInAction(false);
		}
	}
	
	public static void walkTo(Bot client, Vector3D end) {
		try {
			client.setInAction(true);
			AStar pf = new AStar(client, client.getPositionInt(), end);
			pf.startCalc3D(client);
			client.setInAction(false);
		} catch (Exception passed) {
			client.setInAction(false);
		}
	}
	
	public static boolean walkTo2d(Bot client, Vector3D end, boolean inThread) {
		if (inThread) {
			try {
				new Thread(()->{
					client.setInAction(true);
					AStar pf = new AStar(client, client.getPositionInt(), end);
					pf.startCalc2D(client);
					client.setInAction(false);
				}).start();
				return true;
			} catch (Exception passed) {
				//passed.printStackTrace();
				client.setInAction(false);
				return false;
			}
		} else {
			try {
				client.setInAction(true);
				AStar pf = new AStar(client, client.getPositionInt(), end);
				pf.startCalc2D(client);
				client.setInAction(false);
				return true;
			} catch (Exception passed) {
				//passed.printStackTrace();
				client.setInAction(false);
				return false;
			}
		}
	}
	
	public static boolean mine2D(Bot client, int blockId, int howmany, boolean inThread) {
		if (inThread) {
			try {
				if (client.isInAction()) {
					BotU.chat(client, "я занят");
					return false;
				}
				client.setInAction(true);
				new Thread(() -> {
					for (int i = 0; i < howmany; i++) {
						Vector3D pos12 = VectorUtils.findNearestBlockById(client, blockId);
						Vector3D end = VectorUtils.botCanTouchBlockAt(client, pos12);
		    			AStar pf = new AStar(client, client.getPositionInt(), end);
		    			pf.startCalc2D(client);
						while (true) {
		    				if (VectorUtils.sqrt(client.getPositionInt(), end) <= 4.5) {
		        				client.setmovelocked(false);
		        				BotU.mineBlock(client, pos12);
		        	        	while (!VectorUtils.BTavoid(pos12.getBlock(client).type)) {
		        	        		ThreadU.sleep(100);
		        	        	}
		        	        	break;
		    				} else {
		    					System.out.println("nu");
		    					ThreadU.sleep(1000);
		    				}
						}
					}
					client.setInAction(false);
				}).start();
			} catch (Exception passed) {
				client.setInAction(false);
				return false;
			}
		} else {
			try {
				if (client.isInAction()) {
					BotU.chat(client, "я занят");
					return false;
				}
				client.setInAction(true);
				for (int i = 0; i < howmany; i++) {
					Vector3D pos12 = VectorUtils.findNearestBlockById(client, blockId);
					Vector3D end = VectorUtils.botCanTouchBlockAt(client, pos12);
	    			AStar pf = new AStar(client, client.getPositionInt(), end);
	    			pf.startCalc2D(client);
					while (true) {
	    				if (VectorUtils.sqrt(client.getPositionInt(), end) <= 4.5) {
	        				client.setmovelocked(false);
	        				BotU.mineBlock(client, pos12);
	        	        	while (!VectorUtils.BTavoid(pos12.getBlock(client).type)) {
	        	        		ThreadU.sleep(100);
	        	        	}
	        	        	break;
	    				} else {
	    					System.out.println("nu");
	    					ThreadU.sleep(1000);
	    				}
					}
				}
				client.setInAction(false);
				return true;
			} catch (Exception passed) {
				client.setInAction(false);
				return false;
			}
		}
		return true;
	}
	
	public static void mine3D(Bot client, int blockId, int howmany) {
		try {
			if (client.isInAction()) {
				BotU.chat(client, "я занят");
				return;
			}
			client.setInAction(true);
			new Thread(() -> {
				for (int i = 0; i < howmany; i++) {
					Vector3D pos12 = VectorUtils.findNearestBlockById(client, blockId);
					Vector3D end = VectorUtils.botCanTouchBlockAt(client, pos12);
	    			AStar pf = new AStar(client, client.getPositionInt(), end);
	    			pf.startCalc3D(client);
					while (true) {
	    				if (VectorUtils.sqrt(client.getPositionInt(), end) <= 4.5) {
	        				client.setmovelocked(false);
	        				BotU.mineBlock(client, pos12);
	        	        	while (!(pos12.getBlock(client).type == Type.AIR)) {
	        	        		ThreadU.sleep(100);
	        	        	}
	        	        	break;
	    				} else {
	    					System.out.println("nu");
	    					ThreadU.sleep(1000);
	    				}
					}
				}
				client.setInAction(false);
			}).start();
		} catch (Exception passed) {
			passed.printStackTrace();
			client.setInAction(false);
		}
	}
}
