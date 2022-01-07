package world;

import com.github.steveice10.mc.protocol.data.game.world.block.BlockChangeRecord;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.utils.Vector3D;
import world.BlockType.Type;

public class Block {
	public int id, subid;
	public Vector3D pos;
	public Type type;
	public int state;
	
	public Block() {
		this.id = -1;
		this.subid = -1;
		this.pos = null;
		this.type = Type.VOID;
	}
	
	public Block(int state, int id, Vector3D pos, Type type) {
		this.id = id;
		this.subid = -1;
		this.pos = pos;
		this.type = type;
		this.state = state;
	}
	
	/*@SuppressWarnings("deprecation")
	public Block(BlockChangeRecord data) {
		Vector3D vector = new Vector3D(data.getPosition());
		this.pos = vector;
		int id = data.getBlock();
		this.id = id;
		Type bt = Main.getBlockType().bt(id);
		this.type = bt;
		int subid = -1;
		this.subid = subid;
	}*/
	
	public Block getRelative(int x, int y, int z, Bot client) {
		return client.getWorld().getBlock(new Vector3D(pos.x+x,pos.y+y,pos.z+z));
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
}
