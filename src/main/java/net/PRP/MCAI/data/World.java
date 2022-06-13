package net.PRP.MCAI.data;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;
import net.PRP.MCAI.*;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.VectorUtils;


public class World {
	public Map<ChunkCoordinates, Column> columns = new ConcurrentHashMap<>();
	public Map<Integer, Entity> Entites = new ConcurrentHashMap<>();
	public List<ServerPlayerObject> ServerTabPanel = new CopyOnWriteArrayList<>();
	public int renderDistance = 2;
	public Bot client;
	
	public World(Bot client) {
		this.client = client;
	}
	
	public void unloadColumn(ChunkCoordinates coords) {
		columns.remove(coords);
	}
	
	public void addChunkColumn(ChunkCoordinates coords, Column column) {
		if ((boolean) Main.gamerule("multiworld")) {
			Multiworld.addChunkColumn(coords, column);
		} else {
			if (columns.containsKey(coords)) {
				columns.replace(coords, column);
			} else {
				columns.put(coords, column);
			}
		}
	}
	
	public void setBlock(Position pos, int state) {
		if ((boolean) Main.gamerule("multiworld")) {
			Multiworld.setBlock(pos, state);
		} else {
			try {
				int blockX = pos.getX() & 15;
	            int blockY = pos.getY() & 15;
	            int blockZ = pos.getZ() & 15;
	
	            int chunkX = pos.getX() >> 4;
	            int chunkY = pos.getY() >> 4;
	            int chunkZ = pos.getZ() >> 4;
	            
	            //if (columns.containsKey(new ChunkCoordinates(chunkX, chunkZ))) {
	            	columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY].set(blockX, blockY, blockZ, state);
	            //} else {
	            //	columns.replace(new ChunkCoordinates(chunkX, chunkZ), new Column(chunkX, chunkZ, new Chunk[] {new Chunk(chunkY, new Palette(), new BitStorage())}, new CompoundTag[] {}, new CompoundTag("")));
	            //	columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY].set(blockX, blockY, blockZ, state);
	            //}
	        } catch (Exception e) {
				//e.printStackTrace();
			}
		}
		if ((boolean)Main.gamerule("mineonlyiftouchair"))client.rl.blacklist.forEach((block)->{
			if (block.getBlock(client).getNeighbors().contains(VectorUtils.convert(pos))) {
				client.rl.blacklist.remove(block);
			}
		});
	}
	
	public Block getBlock(double x, double y, double z) {
		if ((boolean) Main.gamerule("multiworld")) {
			return Multiworld.getBlock(x,y,z);
		} else {
			if (y < 0 || y > 256) {
				return new Block(0 , 0, new Vector3D(x,y,z).func_vf(), Type.VOID);
			}
			try {
				int bx = (int)x & 15;
	            int by = (int)y & 15;
	            int bz = (int)z & 15;
	            int chunkX = (int)Math.floor(x) >> 4;
	            int chunkY = (int)Math.floor(y) >> 4;
	            int chunkZ = (int)Math.floor(z) >> 4;
				Chunk cc = columns.get(new ChunkCoordinates(chunkX, chunkZ)).getChunks()[chunkY];
	            if (cc == null) return new Block();
	            int state = cc.get(bx, by, bz);
	            int id = Main.getMCData().blockStates.get(state).id;
				return new Block(state, id, new Vector3D(x,y,z).func_vf(), Main.getMCData().bt(id));
	    	} catch (Exception e) {
	    		if (Main.debug) e.printStackTrace();
				return new Block();
			}
		}
	}
	
	public Block getBlock(Vector3D pos) {
		if ((boolean) Main.gamerule("multiworld")) {
			return Multiworld.getBlock(pos);
		} else {
			if (pos.y < 0 || pos.y > 256) {
				return new Block(0 , 0, pos.floor(), Type.VOID);
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
				return new Block(state, id, pos.floor(), Main.getMCData().bt(id));
	    	} catch (Exception e) {
	    		if (Main.debug) e.printStackTrace();
				return new Block();
			}
		}
	}
}
