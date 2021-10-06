package world;

public class ChunkCoordinates {
	
	private final int chunkX;
	private final int chunkZ;
	
	public ChunkCoordinates(int chunkX, int chunkZ) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
	}

	public int getChunkX() {
		return chunkX;
	}

	public int getChunkZ() {
		return chunkZ;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + chunkX;
		hash = 31 * hash + chunkZ;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChunkCoordinates)) {
			return false;
		}
		ChunkCoordinates coordsObj = (ChunkCoordinates) obj;
		if (coordsObj.getChunkX() == chunkX && coordsObj.getChunkZ() == chunkZ) {
			return true;
		} else {
			return false;
		}
	}
}