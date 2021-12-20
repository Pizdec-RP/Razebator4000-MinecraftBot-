package net.PRP.MCAI;

import java.util.List;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.Vector3D;

public class MultibotCalculations {
	public static void fill(Vector3D start, Vector3D end, int blockid) {
		
	}
	
	public static void pickMainhost() {
		List<Bot> bots = getBots();
		Bot mainhost = bots.get(random(0,bots.size()));
		mainhost.setMainhost(true);
	}
	
	public static int random(int min, int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
	
	public static List<Bot> getBots() {
		return Main.bots;
	}
}
