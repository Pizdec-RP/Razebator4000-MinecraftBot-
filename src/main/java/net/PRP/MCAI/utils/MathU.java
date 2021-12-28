package net.PRP.MCAI.utils;

public class MathU {
	public static int rnd(int min, int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
}
