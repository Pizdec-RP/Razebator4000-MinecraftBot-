package world;

import java.util.HashMap;
import java.util.Map;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import net.PRP.MCAI.utils.Vector3D;


public class World {

	private int dimension;
	
	private boolean hardcore;
	private Difficulty difficulty;

	private long age;
	private long time;
	
	private HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
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
		columns.put(coords, column);
	}
	
	public void setBlock(Position pos, int state) {
		ChunkCoordinates coords = new ChunkCoordinates((int) (pos.getX() / 16.0), (int) (pos.getZ() / 16.0));
		int yPos = (int) Math.floor(pos.getY() / 16.0);
		if (!columns.containsKey(coords)) {
			return;
		}
		columns.get(coords).getChunks()[yPos].set(Math.abs(pos.getX() % 16), Math.abs(pos.getY() % 16), Math.abs(pos.getZ() % 16), state);
	}
	
	public Block getBlock(Vector3D pos) {
		try {
			int bx = (int)pos.getX() & 15;
            int by = (int)pos.getY() & 15;
            int bz = (int)pos.getZ() & 15;
            int chunkX = (int)pos.getX() >> 4;
            int chunkY = (int)pos.getY() >> 4;
            int chunkZ = (int)pos.getZ() >> 4;
            //System.out.println("x: "+bx+"y: "+by+"z: "+bz);
            int bsb = columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY].get(bx, by, bz);
			return new Block(bsb, pos, BlockType.bt(bsb));
    	} catch (Exception e) {
    		
			return new Block(0, pos, BlockType.AIR);
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
