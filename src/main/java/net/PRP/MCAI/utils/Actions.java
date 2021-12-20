package net.PRP.MCAI.utils;


import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.pathfinder.AStar;

public class Actions {
	public static void mineWood(Bot client, int howmany) {
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
        				BotU.mineBlock(client, pos12.translate(), false);
        	        	while (!VectorUtils.BTavoid(pos12.getBlock().type)) {
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
	}
	
	public static void mine2D(Bot client, int blockId, int howmany) {
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
    			pf.startCalc2D(client);
				while (true) {
    				if (VectorUtils.sqrt(client.getPositionInt(), end) <= 4.5) {
        				client.setmovelocked(false);
        				BotU.mineBlock(client, pos12.translate(), false);
        	        	while (!VectorUtils.BTavoid(pos12.getBlock().type)) {
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
	}
	
	public static void mine3D(Bot client, int blockId, int howmany) {
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
        				BotU.mineBlock(client, pos12.translate(), false);
        	        	while (!VectorUtils.BTavoid(pos12.getBlock().type)) {
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
	}
}
