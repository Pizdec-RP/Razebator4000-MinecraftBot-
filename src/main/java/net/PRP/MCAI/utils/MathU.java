package net.PRP.MCAI.utils;

public class MathU {
	public static int rnd(int min, int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
	
	public static double round(double numberToRound, int decimalPlaces) {
		if(Double.isNaN(numberToRound))
			return Double.NaN;
		
		double factor = 1;
		for(int i = 0; i < Math.abs(decimalPlaces); i++)
			if(decimalPlaces > 0)
				factor *= 10;
			else
				factor /= 10;
		
		return (double) Math.round(numberToRound*factor)/factor;
	}
}
