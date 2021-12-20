package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

import net.PRP.MCAI.utils.EntityLocation;
import net.PRP.MCAI.utils.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;


public class World {

	private int dimension;
	
	private boolean hardcore;
	private Difficulty difficulty;
	private WorldType worldType;

	private long age;
	private long time;
	
	private HashMap<ChunkCoordinates, Column> columns = new HashMap<>();
	private HashMap<ChunkCoordinates, byte[]> biomeData = new HashMap<>();
	private HashMap<ChunkCoordinates, CompoundTag[]> tileEntities = new HashMap<>();
	public List<Block> blocks = new CopyOnWriteArrayList<Block>();
	public List<Entity> entities = new ArrayList<Entity>();
	
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
	
	public void setTileEntities(ChunkCoordinates coords, CompoundTag[] tileEnts) {
		tileEntities.put(coords, tileEnts);
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
	
	public CompoundTag[] getTileEntities(ChunkCoordinates coords) {
		return tileEntities.get(coords);
	}
	
	public BlockState getBlock(Position pos) {
    	try {
    		int cx = (int)Math.floor(pos.getX()/16.0);
    		int cy = (int)Math.floor(pos.getY()/16.0);
    		int cz = (int)Math.floor(pos.getZ()/16.0);
    		//log("chunk pos x "+cx+" z "+cz+" y "+cy);
			ChunkCoordinates coords = new ChunkCoordinates(cx, cz);
			Column c = columns.get(coords);
			if (c == null) {
				return null;
			}
			int yPos = cy;
			Chunk chunk = c.getChunks()[yPos];
			if (chunk == null) return null;
			BlockStorage blocks = chunk.getBlocks();
			int xb = pos.getX() % 16;
			int yb = pos.getY() % 16;
			int zb = pos.getZ() % 16;
			if (xb < 0) {
				xb = 16 - xb;
			}
			if (zb < 0) {
				zb = 16 - zb;
			}
			//log("x "+xb+" z "+zb+" y "+yb);
			return blocks.get(xb, yb, zb);
    	} catch (Exception e) {
    		//e.printStackTrace();
			return null;
		}
	}
	
	public BlockState getBlock(EntityLocation loc) {
		return getBlock(new Position((int)loc.getX(), (int)loc.getY(), (int)loc.getZ()));
	}
	
	public void setBlock(Position pos, BlockState state) {
		ChunkCoordinates coords = new ChunkCoordinates((int) (pos.getX() / 16.0), (int) (pos.getZ() / 16.0));
		int yPos = (int) Math.floor(pos.getY() / 16.0);
		if (!columns.containsKey(coords)) {
			return;
		}
		columns.get(coords).getChunks()[yPos].getBlocks().set(Math.abs(pos.getX() % 16), Math.abs(pos.getY() % 16), Math.abs(pos.getZ() % 16), state);
	}
	
	public void addBlock(Block block) {
		for (Block bl : blocks) {
			if (VectorUtils.equals(bl.pos, block.pos)) {
				if (block.id == bl.id && block.subid == bl.subid && block.type == bl.type) return;
				blocks.remove(bl);
			}
		}
		blocks.add(block);
	}
	
	public void columnToBlocks(int cox, int coz, Column column) {
		new Thread(()->{
			//int size = column.getChunks().length;
			for (int hgt = 0; hgt <= 16; hgt++) {
				for (int chy = 0; chy <= 16*hgt; chy++) {
					System.out.println("column at x:"+cox+" z:"+coz+" loaded y:"+chy);
					for (int chx = 0; chx <= cox*16; chx++) {
						for (int chz = 0; chz <= coz*16; chz++) {
							int x = cox*16+chx;
							int y = hgt*16+chy;
							int z = coz*16+chz;
							Block block = new Block();
							block.pos = new Vector3D(x,y,z);
							BlockState bl = getBlock(new Position(x,y,z));
							if (bl == null) break;
							block.id = bl.getId();
							block.subid = bl.getData();
							block.type = BlockType.bt(bl.getId());
							blocks.add(block);
							//System.out.println("block added id:"+bl.getId()+" at x:"+x+" y:"+y+" z:"+z);
						}
					}
				}
			}
			System.out.println("column loaded");
		}).start();
	}
	
	public Block getBlock(Vector3D pos) {
		try {
    		int cx = (int)Math.floor(pos.getX()/16.0);
    		int cy = (int)Math.floor(pos.getY()/16.0);
    		int cz = (int)Math.floor(pos.getZ()/16.0);
    		//log("chunk pos x "+cx+" z "+cz+" y "+cy);
			ChunkCoordinates coords = new ChunkCoordinates(cx, cz);
			Column c = columns.get(coords);
			if (c == null) {
				return null;
			}
			int yPos = cy;
			Chunk chunk = c.getChunks()[yPos];
			if (chunk == null) return null;
			BlockStorage blockss = chunk.getBlocks();
			int xb = (int)pos.getX() % 16;
			int yb = (int)pos.getY() % 16;
			int zb = (int)pos.getZ() % 16;
			if (xb < 0) {
				xb = 16 - xb;
			}
			if (zb < 0) {
				zb = 16 - zb;
			}
			BlockState bsb = blockss.get(xb, yb, zb);
			return new Block(bsb.getId(), pos, BlockType.bt(bsb.getId()));
    	} catch (Exception e) {
    		//e.printStackTrace();
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

	public WorldType getWorldType() {
		return worldType;
	}


	public void setWorldType(WorldType worldType) {
		this.worldType = worldType;
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

	public Entity getEntity(int entityId) {
		for (Entity entity : entities) {
			if (entity.id == entityId) {
				return entity;
			}
		}
		return null;
	}
	
	public Entity getEntity(UUID entityUuid) {
		for (Entity entity : entities) {
			if (entity.uuid == entityUuid) {
				return entity;
			}
		}
		return null;
	}
	
}
