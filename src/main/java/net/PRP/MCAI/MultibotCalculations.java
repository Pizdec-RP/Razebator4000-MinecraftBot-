package net.PRP.MCAI;

import java.util.List;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.Vector3D;

public class MultibotCalculations {
	
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
				
			}
		}
		return -1;
	}
}
