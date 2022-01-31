package net.PRP.MCAI.data;

import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;

import net.PRP.MCAI.*;
import net.PRP.MCAI.data.MinecraftData.Type;


public class World {

	private int dimension;
	
	private boolean hardcore;
	private Difficulty difficulty;

	private long age;
	private long time;
	
	public HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
	private HashMap<ChunkCoordinates, byte[]> biomeData = new HashMap<>();
	public final Map<Integer, Entity> Entites = new HashMap<>();
	
	public int getDimension() {
		return dimension;
	}
	
	public Column getColumnAt(Position pos) {
		int chunkZ = (int) (pos.getZ() / 16.0);
		int chunkX = (int) (pos.getX() / 16.0);
		ChunkCoordinates coords = new ChunkCoordinates(chunkX, chunkZ);
		return columns.get(coords);
	}
	
	public void setBiomeData(ChunkCoordinates coords, byte[] data) {
		biomeData.put(coords, data);
	}
	
	public byte[] getBiomeData(ChunkCoordinates coords) {
		return biomeData.get(coords);
	}

	public void unloadColumn(ChunkCoordinates coords) {
		columns.remove(coords);
	}
	
	public void addChunkColumn(ChunkCoordinates coords, Column column) {
		if (columns.containsKey(coords)) {
			columns.replace(coords, column);
		} else {
			columns.put(coords, column);
		}
	}
	
	public void setBlock(Position pos, int state) {
		try {
			int blockX = pos.getX() & 15;
            int blockY = pos.getY() & 15;
            int blockZ = pos.getZ() & 15;

            int chunkX = pos.getX() >> 4;
            int chunkY = pos.getY() >> 4;
            int chunkZ = pos.getZ() >> 4;
            
            columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY].set(blockX, blockY, blockZ, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Block getBlock(Vector3D pos) {
		if (pos.y < 0 || pos.y > 256) {
			return new Block(0 , 0, pos, Type.VOID);
		}
		try {
			int bx = (int)pos.getX() & 15;
            int by = (int)pos.getY() & 15;
            int bz = (int)pos.getZ() & 15;
            int chunkX = (int)pos.getX() >> 4;
            int chunkY = (int)pos.getY() >> 4;
            int chunkZ = (int)pos.getZ() >> 4;
            //System.out.println("x: "+bx+"y: "+by+"z: "+bz);
			Chunk cc = columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY];
            if (cc == null) return new Block(0, 0, pos, Type.VOID);
            int state = cc.get(bx, by, bz);
            int id = Main.getMCData().blockStates.get(state).id;
			return new Block(state, id, pos, Main.getMCData().bt(id));
    	} catch (Exception e) {
    		//e.printStackTrace();
			return new Block(-1 , -1, pos, Type.UNKNOWN);
		}
	}
	
	public boolean isBlockLoaded(Position b) {
		int chunkX = (int) (b.getX() / 16.0);
		int chunkZ = (int) (b.getZ() / 16.0);
		ChunkCoordinates coords = new ChunkCoordinates(chunkX, chunkZ);
		return columns.containsKey(coords);
	}
	
	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public boolean isHardcore() {
		return hardcore;
	}
	
	public void setHardcore(boolean hardcore) {
		this.hardcore = hardcore;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
