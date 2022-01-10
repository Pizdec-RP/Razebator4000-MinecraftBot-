package net.PRP.MCAI;

import java.util.List;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.ThreadU;
import net.PRP.MCAI.utils.Vector3D;

public class Obshak {//петушинный общак
	
	public static void startTickLoop() {
		System.out.println("tick loop started");
		new Thread(()->{
			int curcomp = 0;
			while (true) {
				long timeone = System.currentTimeMillis();
				for (Bot client : getBots()) {
					client.tick();
				}
				long timetwo = System.currentTimeMillis();
				int raznica = (int) (timetwo - timeone);
				if (raznica > 0 && raznica < 50) {
					curcomp = 50-raznica;
					//System.out.println("comp "+curcomp+"ms");
					ThreadU.sleep(curcomp);
				} else if (raznica == 0){
					ThreadU.sleep(50);
				}
			}
		}).start();
	}
	
	public static void pickMainhost() {
		try {
			List<Bot> bots = getBots();
			//int r = random(0,bots.size());
			//System.out.println(r);
			Bot mainhost = bots.get(0);
			mainhost.setMainhost(true);
		} catch (Exception e) {}
	}
	
	public static int random(int min, int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
	
	public static List<Bot> getBots() {
		return Main.bots;
	}
	
	public static int tellid(Vector3D gde) {
		int bid = -2;
		for (Bot client : getBots()) {
			try {
				bid = gde.getBlock(client).id;
				if (!(bid == -2 || bid == -1)) {
					return bid;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
}
