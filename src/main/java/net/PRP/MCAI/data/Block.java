package net.PRP.MCAI.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.bot.AABB;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.VectorUtils;

public class Block {
	public int id, subid;
	public Vector3D pos;
	public Type type;
	public int state;
	public List<AABB> shapes;
	
	public Block() {
		this.id = -1;
		this.subid = -1;
		this.pos = null;
		this.type = Type.VOID;
		//setupShapes();
	}
	
	public Block(int state, int id, Vector3D pos, Type type) {
		this.id = id;
		this.subid = -1;
		this.pos = pos;
		this.type = type;
		this.state = state;
		//setupShapes();
	}
	
	public Block(int state, Vector3D pos) {
		this.pos = pos;
		this.state = state;
		this.id = Main.getMCData().blockStates.get(state).id;
		this.type = Main.getMCData().bt(this.id);
		//setupShapes();
	}
	
	public Block getRelative(int x, int y, int z, Bot client) {
		return client.getWorld().getBlock(new Vector3D(pos.x+x,pos.y+y,pos.z+z));
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.pos.hashCode();
		hash = 31 * hash + (int)this.id;
		hash = 31 * hash + (int)this.state;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Block)) {
			return false;
		}
		Block co = (Block) obj;
		return VectorUtils.equalsInt(co.pos, pos) && co.id == id && co.state == state &&co.type == type;
	}
	
	public List<Block> getNeighbors(Bot client) {
		List<Block> blocks = new CopyOnWriteArrayList<>();
		blocks.add(getRelative(1,0,0,client));
		blocks.add(getRelative(-1,0,0,client));
		blocks.add(getRelative(0,0,1,client));
		blocks.add(getRelative(0,0,-1,client));
		blocks.add(getRelative(0,1,0,client));
		blocks.add(getRelative(0,-1,0,client));
		return blocks;
	}
	
	public boolean touchLiquid(Bot client) {
		List<Block> blocks = getNeighbors(client);
		for (Block block : blocks) {
			if (block.isLiquid()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAvoid() {
		return VectorUtils.BTavoid(type);
	}
	
	public boolean ishard() {
		return VectorUtils.BThard(type);
	}
	
	public boolean isFence() {
		return type == Type.GATE || type == Type.DOOR_GATE;
	}
	
	public boolean isLiquid() {
		return type == Type.LIQUID || id == 26 || id == 27;
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
