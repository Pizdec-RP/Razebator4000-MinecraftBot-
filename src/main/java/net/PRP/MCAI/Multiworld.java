package net.PRP.MCAI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.ChunkCoordinates;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;
import net.PRP.MCAI.data.MinecraftData.Type;

public class Multiworld {
	public static Map<Vector3D, Block> blocks = new ConcurrentHashMap<>();
	public static Map<ChunkCoordinates, Column> columns = new ConcurrentHashMap<>();
	
	public static void addChunkColumn(ChunkCoordinates coords, Column column) {
		if (columns.containsKey(coords)) {
			if (columns.get(coords).hashCode() != column.hashCode()) 
				columns.replace(coords, column);
		} else {
			columns.put(coords, column);
		}
	}
	
	public static void setBlock(Position pos, int state) {
		try {
			int blockX = pos.getX() & 15;
            int blockY = pos.getY() & 15;
            int blockZ = pos.getZ() & 15;

            int chunkX = pos.getX() >> 4;
            int chunkY = pos.getY() >> 4;
            int chunkZ = pos.getZ() >> 4;
            columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY].set(blockX, blockY, blockZ, state);
        } catch (Exception e) {
			//e.printStackTrace();
		}
		try {
			if (blocks.containsKey(VectorUtils.convert(pos))) {
				if (blocks.get(VectorUtils.convert(pos)).state != state) {
					blocks.put(VectorUtils.convert(pos), new Block(state,VectorUtils.convert(pos)));
				}
			} else {
				blocks.put(VectorUtils.convert(pos), new Block(state,VectorUtils.convert(pos)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Block getBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y > 256) {
			return new Block(0 , 0, pos, Type.VOID);
		}
		try {
			int bx = (int)pos.getX() & 15;
            int by = (int)pos.getY() & 15;
            int bz = (int)pos.getZ() & 15;
            int chunkX = (int)Math.floor(pos.getX()) >> 4;
            int chunkY = (int)Math.floor(pos.getY()) >> 4;
            int chunkZ = (int)Math.floor(pos.getZ()) >> 4;
			Chunk cc = columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY];
            if (cc == null) return new Block();
            int state = cc.get(bx, by, bz);
            int id = Main.getMCData().blockStates.get(state).id;
			return new Block(state, id, pos, Main.getMCData().bt(id));
    	} catch (Exception e) {
    		if (blocks.get(pos) != null) return blocks.get(pos);
			return new Block();
		}
	}
}
