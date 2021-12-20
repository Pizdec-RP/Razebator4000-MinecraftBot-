package net.PRP.MCAI.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;

public class PositionUtils {
	public static Position findNearestBlockById(Bot client, int id) {
    	List<Position> positions = new CopyOnWriteArrayList<>();
    	int x = (int)client.getPosX();
    	int y = (int)client.getPosY();
    	int z = (int)client.getPosZ();
    	int radius = 50;
    	Position pos = null;
    	for (int i = 1; i < radius; i++) {
    		int localf = 0;
    		int xs = x-i;
    		int ys = y-i;
    		if (ys < 1) ys = 0;
    		int yi = y+i;
    		if (yi > 255) yi = 255;
    		//int ys = 1;
    		//int yi = 255;
    		int zs = z-i;
    		for (int y1 = ys; y1 < yi; y1++) {
    			for (int x1 = xs; x1 < x+i; x1++) {
                    for (int z1 = zs; z1 < z+i; z1++) {
                    	int b;
                    	//log("ищу на x:"+x1+" y:"+y1+" z:"+z1);
                    	b = getblockid(x1,y1,z1);
                        if (b == id && !VectorUtils.equalsInt(new Vector3D(x1,y1+1,z1), client.getPosition())) {
                        	localf++;
                        	pos = new Position(x1,y1,z1);
                        	positions.add(pos);
                        }
                    }
                }
            }
    		if (localf > 0 && !positions.isEmpty()) {
    			pos = getNear(new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	    	return pos;
    		}
    	}
    	pos = getNear(new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    	return pos;
    }
	
	public static Position getNear(Position botPos, List<Position> allPos) {
        double min = -1;
        Position minPos = null;
        for (int i = 0; i < allPos.size(); i++) {
            Position position = allPos.get(i);
            double distance = Math.sqrt(Math.pow(position.getX() - botPos.getX(), 2) + Math.pow(position.getY() - botPos.getY(), 2) + Math.pow(position.getZ() - botPos.getZ(), 2));
            if (i == 0 || distance < min) {
                min = distance;
                minPos = position;
            }
        }
        return minPos;
    }
	
	public static int getblockid (double x,double y,double z) {
    	BlockState block = Main.getWorld().getBlock(new Position((int)x,(int)y,(int)z));
    	if (block == null) return 0;
    	int blockid = block.getId();
		return (int) blockid;
    }
	
	public static int getblockid (Position pos) {
    	try {
	    	BlockState block = Main.getWorld().getBlock(pos);
	    	if (block == null) System.out.println("cant get block id");
	    	int blockid = block.getId();
			return (int) blockid;
    	} catch (Exception e) {
    		e.printStackTrace();
			return 0;
		}
    }
	
	public static boolean blockIsEmpty(int bid) {
    	switch (bid) {
    		case 0://air
    			return true;
    		case 6://tree sp
    			return true;
    		case 31://grass
    			return true;
    		case 32://dead brush
    			return true;
    		case 37://flower
    			return true;
    		case 38://too
    			return true;
    		case 39://mushroom
    			return true;
    		case 55://redstone dust
    			return true;
    		case 68://sign
    			return true;
    		case 69://рычаг
    			return true;
    		case 70://плита
    			return true;
    		case 72://плита
    			return true;
    		case 75://torch
    			return true;
    		case 76:
    			return true;
    		case 77://button
    			return true;
    		case 78://snow
    			return true;
    		default:
    			return false;
    	}
    }
	
	public static Position botCanTouchBlockAt(Bot client, Position blockPos) {
    	List<Position> positions = new CopyOnWriteArrayList<>();
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX(),blockPos.getY()+1,blockPos.getZ())))) {
    		positions.add(new Position(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    	}
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1)))) {
    		positions.add(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    	}
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX()-1,blockPos.getY(),blockPos.getZ())))) {
    		positions.add(new Position(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1)))) {
    		positions.add(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    	}
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX()+1,blockPos.getY(),blockPos.getZ())))) {
    		positions.add(new Position(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    	}
    	if (blockIsEmpty(PositionUtils.getblockid(new Position(blockPos.getX(),blockPos.getY()-2,blockPos.getZ())))) {
    		positions.add(new Position(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	}
    	if (positions.size() <= 0) {
    		positions.add(new Position(blockPos.getX(),blockPos.getY()+1,blockPos.getZ()));
    		positions.add(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()-1));
    		positions.add(new Position(blockPos.getX()-1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Position(blockPos.getX(),blockPos.getY(),blockPos.getZ()+1));
    		positions.add(new Position(blockPos.getX()+1,blockPos.getY(),blockPos.getZ()));
    		positions.add(new Position(blockPos.getX(),blockPos.getY()-2,blockPos.getZ()));
    	} else {}
    	return PositionUtils.getNear(new Position((int)client.getPosX(),(int)client.getPosY(),(int)client.getPosZ()),positions);
    }
}
