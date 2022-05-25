package net.PRP.MCAI.utils;

import java.util.List;

public class MathU {
	public static int rnd(int min, int max) {
		max -= min;
		return (int) (Math.random() * ++max) + min;
	}
	public static Object random(List<?> list) {
		return list.get(rnd(0,list.size()-1));
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
	
	public static int clamp(int num, int min, int max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    /**
     * Returns the value of the first parameter, clamped to be within the lower and upper limits given by the second and
     * third parameters
     */
    public static float clamp(float num, float min, float max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }

    public static double clamp(double num, double min, double max) {
        if (num < min) {
            return min;
        } else {
            return num > max ? max : num;
        }
    }
    
    public static int ceil(float value) {
        int i = (int)value;
        return value > (float)i ? i + 1 : i;
    }

    public static int ceil(double value) {
        int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }
    
    public static double Truncate(double value) {
    	if (value < 0) {
            return Math.ceil(value);
        } else {
            return Math.floor(value);
        }
    }
}
