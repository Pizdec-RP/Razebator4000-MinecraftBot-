package world;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.utils.Vector3D;

public class Block {
	public int id, subid;
	public Vector3D pos;
	public BlockType type;
	
	public Block() {
		this.id = -1;
		this.subid = -1;
		this.pos = null;
		this.type = BlockType.VOID;
	}
	
	public Block(int id, Vector3D pos, BlockType type) {
		this.id = id;
		this.subid = -1;
		this.pos = pos;
		this.type = type;
	}
	
	@SuppressWarnings("deprecation")
	public Block(BlockChangeRecord data) {
		Vector3D vector = new Vector3D(data.getPosition());
		this.pos = vector;
		int id = data.getBlock();
		this.id = id;
		BlockType bt = BlockType.bt(id);
		this.type = bt;
		int subid = -1;
		this.subid = subid;
	}
	
	public Block getRelative(int x, int y, int z) {
		return Main.getWorld().getBlock(new Vector3D(pos.x+x,pos.y+y,pos.z+z));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSubid() {
		return subid;
	}

	public void setSubid(int subid) {
		this.subid = subid;
	}

	public Vector3D getPos() {
		return pos;
	}

	public void setPos(Vector3D pos) {
		this.pos = pos;
	}

	public BlockType getType() {
		return type;
	}

	public void setType(BlockType type) {
		this.type = type;
	}
	
	
}
