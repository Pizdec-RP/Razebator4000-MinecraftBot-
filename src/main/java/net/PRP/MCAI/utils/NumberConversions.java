package net.PRP.MCAI.utils;

public final class NumberConversions {
    private NumberConversions() {
    }

    public static int floor(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int ceil(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor + (int)((Double.doubleToRawLongBits(num) ^ 0xFFFFFFFFFFFFFFFFL) >>> 63);
    }

    public static int round(double num) {
        return NumberConversions.floor(num + 0.5);
    }

    public static double square(double num) {
        return num * num;
    }

    public static boolean isFinite(double d2) {
        return Math.abs(d2) <= Double.MAX_VALUE;
    }

    public static boolean isFinite(float f2) {
        return Math.abs(f2) <= Float.MAX_VALUE;
    }

    public static void checkFinite(double d2, String message) {
        if (!NumberConversions.isFinite(d2)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void checkFinite(float d2, String message) {
        if (!NumberConversions.isFinite(d2)) {
            throw new IllegalArgumentException(message);
        }
    }
}


