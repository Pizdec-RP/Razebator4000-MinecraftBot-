package world;

public enum BlockType {
	HARD, VOID, AVOID, AIR, LIQUID, DOOR, UNKNOWN, LADDER;
	
	public static BlockType bt(int id) {
    	switch (id) {
    		case 0://air
    			return VOID;
    		case 6://tree sp
    			return AVOID;
    		case 31://grass
    			return AVOID;
    		case 32://dead brush
    			return AVOID;
    		case 37://flower
    			return AVOID;
    		case 38://too
    			return AVOID;
    		case 39://mushroom
    			return AVOID;
    		case 55://redstone dust
    			return AVOID;
    		case 68://sign
    			return AVOID;
    		case 69://рычаг
    			return AVOID;
    		case 70://плита
    			return AVOID;
    		case 72://плита
    			return AVOID;
    		case 75://torch
    			return AVOID;
    		case 76:
    			return AVOID;
    		case 77://button
    			return AVOID;
    		case 78://snow
    			return AVOID;
    		case 5:
    			return HARD;
    		case 65://ladder
    			return LADDER;
    		default:
    			return HARD;
    	}
	}
}
