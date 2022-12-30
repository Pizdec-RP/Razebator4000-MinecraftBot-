package net.PRP.MCAI.data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.entity.DefaultEntity;
import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class Block {
	public int id, subid;
	public Vector3D pos;
	public Type type;
	public int state;
	public String name = "";
	public AABB hitbox;
	public boolean waterlogged = false;
	
	public Block() {
		this.id = 0;
		this.subid = 0;
		this.pos = null;
		this.type = Type.VOID;
		setHitbox();
	}
	
	public Block(int state, int id, Vector3D pos, Type type) {
		this.id = id;
		this.subid = 0;
		this.pos = pos;
		this.type = type;
		this.state = state;
		String nm = Main.getMCData().blockData.get(id).name;
		if (nm != null) this.name = nm;
		setHitbox();
	}
	
	public Block(int state, Vector3D pos) {
		this.pos = pos;
		this.state = state;
		this.id = Main.getMCData().blockStates.get(state).id;
		this.type = Main.getMCData().bt(this.id);
		String nm = Main.getMCData().blockData.get(id).name;
		if (nm != null) this.name = nm;
		setHitbox();
	}
	
	public String getName() {
		String nm = Main.getMCData().blockData.get(id).name;
		if (nm != null) this.name = nm;
		return name;
	}
	
	public Block getRelative(int x, int y, int z, Bot client) {
		return client.getWorld().getBlock(new Vector3D(pos.x+x,pos.y+y,pos.z+z));
	}
	
	private void setHitbox() {
		if (isSlab()) {
			slabState ss = Main.getMCData().slabstates.get(state);
			if (ss.type.equals("top")) {
				hitbox = new AABB(Math.floor(pos.x), Math.floor(pos.y)+0.5, Math.floor(pos.z), Math.floor(pos.x)+1, Math.floor(pos.y)+1, Math.floor(pos.z)+1);
			} else if (ss.type.equals("bottom")) {
				hitbox = new AABB(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z), Math.floor(pos.x)+1, Math.floor(pos.y)+0.5, Math.floor(pos.z)+1);
			} else if (ss.type.equals("double")) {
				hitbox = new AABB(pos.x, pos.y, pos.z, Math.floor(pos.x)+1, Math.floor(pos.y)+1, Math.floor(pos.z)+1).floor();
			}
			waterlogged = ss.waterlogged;
		} else if (isWater()) {
			hitbox = new AABB(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z), Math.floor(pos.x)+1, getFluidHeight(), Math.floor(pos.z)+1);
			//BotU.log(hitbox.toString());
		} else if (isLava()) {
			hitbox = new AABB(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z), Math.floor(pos.x)+1, getFluidHeight(), Math.floor(pos.z)+1);
		} else {
			if (type == Type.VOID ||type == Type.AIR || type == Type.AVOID) {
				hitbox = null;
			} else if (type == Type.DOOR || type == Type.GATE || type == Type.GOAWAY || type == Type.HARD || type == Type.UNBREAKABLE || type == Type.UNKNOWN) {
				hitbox = new AABB(pos.x, pos.y, pos.z, Math.floor(pos.x)+1, Math.floor(pos.y)+1, Math.floor(pos.z)+1).floor();
			} else if (type == Type.CARPET) {
				hitbox = new AABB(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z), Math.floor(pos.x)+1, Math.floor(pos.y)+0.0625, Math.floor(pos.z)+1);
			} else if (type == Type.LIQUID) {
				hitbox = null;
			} else {
				hitbox = new AABB(pos.x, pos.y, pos.z, Math.floor(pos.x)+1, Math.floor(pos.y)+1, Math.floor(pos.z)+1).floor();
			}
		}
	}
	
	public float getfriction() {
		if (id == 377) {
			return 0.8F;
		} else if (id == 409) {
			return 0.98F;
		} else if (id == 619) {
			return 0.989F;
		} else if (id == 185) {
			return 0.98F;
		} else if (id == 502) {
			return 0.98F;
		} else {
			return 0.6F;
		}
	}
	
	
	public boolean isSlab() {
		return name.contains("slab");
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + this.pos.hashCode();
		hash = 31 * hash + (int)this.id;
		hash = 31 * hash + (int)this.state;
		return hash;
	}
	
	public AABB getHitbox() {
		return hitbox;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
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
	
	public List<Vector3D> getNeighbors() {
		List<Vector3D> blocks = new CopyOnWriteArrayList<>();
		blocks.add(pos.add(1,0,0));
		blocks.add(pos.add(-1,0,0));
		blocks.add(pos.add(0,0,1));
		blocks.add(pos.add(0,0,-1));
		blocks.add(pos.add(0,1,0));
		blocks.add(pos.add(0,-1,0));
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
	
	public boolean touchAir(Bot client) {
		List<Block> blocks = getNeighbors(client);
		for (Block block : blocks) {
			if (block.isAvoid()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean touch(Bot client, Vector3D poss) {
		List<Block> blocks = getNeighbors(client);
		for (Block block : blocks) {
			if (VectorUtils.equalsInt(block.pos, poss)) {
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
		return type == Type.LIQUID;
	}
	
	public boolean isAir() {
		return type == Type.AIR | type == Type.VOID;
	}
	
	public boolean isWater() {
		return id == 26;
	}
	
	public boolean isLava() {
		return id == 27;
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

	public double getResistance() {
		return Main.getMCData().blockData.get(this.id).resistance;
	}
	
	//                        test server only
	public void addVelocityToEntity(DefaultEntity entity, Vector3D vector) {
        if (this.isLiquid()) {
            Vector3D flow = this.getFlowVector(null);
            vector.x += flow.x;
            vector.y += flow.y;
            vector.z += flow.z;
        }
    }
	
	public void onEntityCollide(DefaultEntity entity) {
        if (this.isLiquid()) entity.resetFallDistance();
    }
	
	//                        liquid only
	
	
	public double getFluidHeight() {
		return (pos.y+1)-(getFluidHeightPercent() - 0.1111111);
	}
	
	public boolean canBeFlowedInto() {
        return this.isLiquid();
    }
	
	public int getMeta() {
		if (this.isWater()) {
			return this.state-34;
		} else if (this.isLava()) {
			return this.state - 50;
		} else {
			return 0;
		}
    }

	public float getFluidHeightPercent() {
        float d = (float) this.getMeta();
        if (d >= 8) {
            d = 0;
        }

        return (d + 1) / 9f;
    }
	
	protected boolean canFlowInto(Block block) {
        return block.canBeFlowedInto() && !(block.isLiquid() && block.getMeta() == 0);
    }
	
	public int getEffectiveFlowDecay(Block block) {
        if (block.getId() != this.getId()) {
            return -1;
        }
        int decay = block.getMeta();
        if (decay >= 8) {
            decay = 0;
        }
        return decay;
    }
	
	public Block getBlock(double x, double y, double z, Bot client) {
		return (client==null?Multiworld.getBlock(x, y, z):client.getWorld().getBlock(x, y, z));
	}
	
	public Vector3D getFlowVector(Bot client) {
        if (!this.isLiquid()) {
            return null;
        }
        Vector3D vector = new Vector3D(0, 0, 0);
        int decay = this.getEffectiveFlowDecay(this);
        for (int j = 0; j < 4; ++j) {
            int x = (int) this.pos.x;
            int y = (int) this.pos.y;
            int z = (int) this.pos.z;
            switch (j) {
                case 0:
                    --x;
                    break;
                case 1:
                    x++;
                    break;
                case 2:
                    z--;
                    break;
                default:
                    z++;
            }
            Block sideBlock = (client==null?Multiworld.getBlock(x, y, z):client.getWorld().getBlock(x, y, z));
            int blockDecay = this.getEffectiveFlowDecay(sideBlock);
            if (blockDecay < 0) {
                if (!sideBlock.isLiquid()) {
                    continue;
                }
                blockDecay = this.getEffectiveFlowDecay((client==null?Multiworld.getBlock(x, y-1, z):client.getWorld().getBlock(x, y-1, z)));
                if (blockDecay >= 0) {
                    int realDecay = blockDecay - (decay - 8);
                    vector.x += (sideBlock.pos.x - this.pos.x) * realDecay;
                    vector.y += (sideBlock.pos.y - this.pos.y) * realDecay;
                    vector.z += (sideBlock.pos.z - this.pos.z) * realDecay;
                }
            } else {
                int realDecay = blockDecay - decay;
                vector.x += (sideBlock.pos.x - this.pos.x) * realDecay;
                vector.y += (sideBlock.pos.y - this.pos.y) * realDecay;
                vector.z += (sideBlock.pos.z - this.pos.z) * realDecay;
            }
        }
        if (this.getMeta() >= 8) {
            if (!this.canFlowInto(this.getBlock((int) this.pos.x, (int) this.pos.y, (int) this.pos.z - 1, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x, (int) this.pos.y, (int) this.pos.z + 1, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x - 1, (int) this.pos.y, (int) this.pos.z, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x + 1, (int) this.pos.y, (int) this.pos.z, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x, (int) this.pos.y + 1, (int) this.pos.z - 1, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x, (int) this.pos.y + 1, (int) this.pos.z + 1, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x - 1, (int) this.pos.y + 1, (int) this.pos.z, client)) ||
                    !this.canFlowInto(this.getBlock((int) this.pos.x + 1, (int) this.pos.y + 1, (int) this.pos.z, client))) {
                vector = vector.normalize().add(0, -6, 0);
            }
        }
        return vector.normalize();
    }
}
