package net.PRP.MCAI.TestServer.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.MathU;
import net.minecraft.server.v1_12_R1.BlockFire;

public abstract class DefaultEntity {
	public int hp;
	public UUID uuid;
	protected double x,y,z;
	protected double motionX, motionY, motionZ;
	private List<Block> collisionBlocks;
	public AABB boundingBox;
	private List<Block> blocksAround;
	protected boolean closed;
	private int deadTicks = 0;
	public EntityType type;
	private boolean isPlayer;
	public float pitch;
	public float yaw;
	public double lastX,lastY,lastZ,lastPitch,lastYaw,lastMotionX,lastMotionY,lastMotionZ;
	public int id;
	protected boolean onGround = true;
	protected int age = 0;
	private double ySize = 0;
	private boolean keepMovement = false;
	private boolean isCollidedVertically = false;
	private boolean isCollidedHorizontally = false;
	protected boolean isCollided = false;
	private float fallDistance;
	private double highestPosition = 0;
	
	
	public DefaultEntity(int id, UUID uuid, EntityType type,double x, double y, double z, int hp, AABB hitbox, float pitch, float yaw, double mx, double my, double mz) {
		this.uuid = uuid;
		this.id = id;
		this.x=x;
		this.lastX = x;
		this.y=y;
		this.lastY = y;
		this.z=z;
		this.lastZ = z;
		this.hp=hp;
		this.closed = false;
		this.boundingBox = hitbox;
		this.type = type;
		this.isPlayer = (type == EntityType.PLAYER);
		this.yaw = yaw;
		this.lastYaw = yaw;
		this.pitch = pitch;
		this.lastPitch = pitch;
		this.motionX = mx;
		this.motionY = my;
		this.motionZ = mz;
		this.lastMotionX = mx;
		this.lastMotionY = my;
		this.lastMotionZ = mz;
	}
	
	public void onUpdate() {//main update
        if (this.closed) {
            return;
        }
        if (!this.isAlive()) {
            ++this.deadTicks ;
            if (this.deadTicks >= 10) {
                this.despawnFromAll();
                if (!this.isPlayer) {
                    this.close();
                }
            }
            return;
        }
        this.entityBaseTick();

        this.updateMovement();
    }
	
	public void close() {
		if (closed) return;
		Multiworld.Entities.remove(this.id);
		this.closed = true;
		despawnFromAll();
	}
	
	public void despawnFromAll() {
		Server.sendForEver(new ServerEntityDestroyPacket(new int[] {this.id}));
	}
	
	public void updateMovement() {
        double diffPosition = (this.x - this.lastX) * (this.x - this.lastX) + (this.y - this.lastY) * (this.y - this.lastY) + (this.z - this.lastZ) * (this.z - this.lastZ);
        double diffRotation = (this.yaw - this.lastYaw) * (this.yaw - this.lastYaw) + (this.pitch - this.lastPitch) * (this.pitch - this.lastPitch);

        double diffMotion = (this.motionX - this.lastMotionX) * (this.motionX - this.lastMotionX) + (this.motionY - this.lastMotionY) * (this.motionY - this.lastMotionY) + (this.motionZ - this.lastMotionZ) * (this.motionZ - this.lastMotionZ);
        
        if (diffPosition > 0.0001 || diffRotation > 1.0) {
            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;

            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;

            
        }
        this.addMovement(this.x, this.y, this.z, this.yaw, this.pitch, this.yaw);
        if (diffMotion > 0.0025 || (diffMotion > 0.0001 && this.getMotion().lengthSquared() <= 0.0001)) {
            this.lastMotionX = this.motionX;
            this.lastMotionY = this.motionY;
            this.lastMotionZ = this.motionZ;
            
        }
        this.addMotion(this.motionX, this.motionY, this.motionZ);
    }
	
	public void addMotion(double motionX, double motionY, double motionZ) {
        ServerEntityVelocityPacket pk = new ServerEntityVelocityPacket(this.id, motionX, motionY, motionZ);
        Server.sendForEver(pk);
    }

    public void addMovement(double x, double y, double z, float yaw, float pitch, double headYaw) {
    	 ServerEntityTeleportPacket pk = new ServerEntityTeleportPacket(this.id,x,y,z,yaw,pitch,this.onGround);
         Server.sendForEver(pk);
    }
    
    public Vector3D getMotion() {
        return new Vector3D(this.motionX, this.motionY, this.motionZ);
    }
	
	public void entityBaseTick() {
		this.checkBlockCollision();
		if (this.y <= -16 && this.isAlive()) {
			this.damage(4);
		}
	}

	public void checkBlockCollision() {
		Vector3D vector = new Vector3D(0,0,0);
		for (Block block : this.getCollisionBlocks()) {
			block.onEntityCollide(this);
            block.addVelocityToEntity(this, vector);
		}
		
		if (!vector.isZero()) {
            vector = vector.normalize();
            double d = 0.014d;
            this.motionX += vector.x * d;
            this.motionY += vector.y * d;
            this.motionZ += vector.z * d;
        }
	}
	
	public List<Block> getBlocksAround() {
        if (this.blocksAround == null) {
            int minX = MathU.floorDouble(this.boundingBox.getMinX());
            int minY = MathU.floorDouble(this.boundingBox.getMinY());
            int minZ = MathU.floorDouble(this.boundingBox.getMinZ());
            int maxX = MathU.ceilDouble(this.boundingBox.getMaxX());
            int maxY = MathU.ceilDouble(this.boundingBox.getMaxY());
            int maxZ = MathU.ceilDouble(this.boundingBox.getMaxZ());

            this.blocksAround = new ArrayList<>();

            for (int z = minZ; z <= maxZ; ++z) {
                for (int x = minX; x <= maxX; ++x) {
                    for (int y = minY; y <= maxY; ++y) {
                        Block block = Multiworld.getBlock(x,y,z);
                        this.blocksAround.add(block);
                    }
                }
            }
        }

        return this.blocksAround;
    }
	
	public List<Block> getCollisionBlocks() {
        if (this.collisionBlocks == null) {
            this.collisionBlocks = new ArrayList<>();

            for (Block b : getBlocksAround()) {
                if (b.hitbox != null && b.hitbox.collide(this.getBoundingBox())) {
                    this.collisionBlocks.add(b);
                }
            }
        }

        return this.collisionBlocks;
    }
	
	public void recalculateBoundingBox() {
		float height = this.getHeight();
        double radius = this.getWidth() / 2d;
		this.boundingBox.setBounds(x - radius, y, z - radius, x + radius, y + height, z + radius);
	}
	
	public void setVel(Vector3D newpos) {
		this.motionX = newpos.x;
		this.motionY = newpos.y;
		this.motionZ = newpos.z;
		recalculateBoundingBox();
	}
	
	public void setVel(double dx, double dy, double dz) {
		this.motionX = dx;
		this.motionY = dy;
		this.motionZ = dz;
		recalculateBoundingBox();
	}
	
	public void setPos(Vector3D newpos) {
		this.x = newpos.x;
		this.y = newpos.y;
		this.z = newpos.z;
		recalculateBoundingBox();
	}
	
	public void setPos(double dx, double dy, double dz) {
		this.x = dx;
		this.y = dy;
		this.z = dz;
		recalculateBoundingBox();
	}
	
	public void move(double dx, double dy, double dz) {
        if (dx == 0 && dz == 0 && dy == 0) {
            return;
        }

        if (this.keepMovement) {
            this.boundingBox.offset(dx, dy, dz);
            this.setPosition(new Vector3D((this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2, this.boundingBox.getMinY(), (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2));
            this.onGround = this.isPlayer;
            return;
        } else {

            this.ySize *= 0.4;

            double movX = dx;
            double movY = dy;
            double movZ = dz;

            AABB aAbb = this.boundingBox.clone();

            AABB[] list = Multiworld.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);
            
            for (AABB bb : list) {
                dy = bb.calculateYOffset(this.boundingBox, dy);
            }

            this.boundingBox.offset(0, dy, 0);

            boolean fallingFlag = (this.onGround || (dy != movY && movY < 0));

            for (AABB bb : list) {
                dx = bb.calculateXOffset(this.boundingBox, dx);
            }

            this.boundingBox.offset(dx, 0, 0);

            for (AABB bb : list) {
                dz = bb.calculateZOffset(this.boundingBox, dz);
            }

            this.boundingBox.offset(0, 0, dz);

            if (this.getStepHeight() > 0 && fallingFlag && this.ySize < 0.05 && (movX != dx || movZ != dz)) {
                double cx = dx;
                double cy = dy;
                double cz = dz;
                dx = movX;
                dy = this.getStepHeight();
                dz = movZ;

                AABB aAbb1 = this.boundingBox.clone();

                this.boundingBox.setBB(aAbb);

                list = Multiworld.getCollisionCubes(this, this.boundingBox.addCoord(dx, dy, dz), false);

                for (AABB bb : list) {
                    dy = bb.calculateYOffset(this.boundingBox, dy);
                }

                this.boundingBox.offset(0, dy, 0);

                for (AABB bb : list) {
                    dx = bb.calculateXOffset(this.boundingBox, dx);
                }

                this.boundingBox.offset(dx, 0, 0);

                for (AABB bb : list) {
                    dz = bb.calculateZOffset(this.boundingBox, dz);
                }

                this.boundingBox.offset(0, 0, dz);

                this.boundingBox.offset(0, 0, dz);

                if ((cx * cx + cz * cz) >= (dx * dx + dz * dz)) {
                    dx = cx;
                    dy = cy;
                    dz = cz;
                    this.boundingBox.setBB(aAbb1);
                } else {
                    this.ySize += 0.5;
                }

            }

            this.x = (this.boundingBox.getMinX() + this.boundingBox.getMaxX()) / 2;
            this.y = this.boundingBox.getMinY() - this.ySize;
            this.z = (this.boundingBox.getMinZ() + this.boundingBox.getMaxZ()) / 2;

            //this.checkChunks(); 

            this.checkGroundState(movX, movY, movZ, dx, dy, dz);
            this.updateFallState(this.onGround);

            if (movX != dx) {
                this.motionX = 0;
            }

            if (movY != dy) {
                this.motionY = 0;
            }

            if (movZ != dz) {
                this.motionZ = 0;
            }
            
            return;
        }
    }
	
	public void setPosition(Vector3D v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public boolean isAlive() {
		return this.hp > 0;
	}
	
	public void damage(int dmg) {
		this.hp -= dmg;
	}
	
	public void resetFallDistance() {
        this.highestPosition = 0;
    }
	
	protected void updateFallState(boolean onGround) {
        if (onGround) {
            fallDistance = (float) (this.highestPosition  - this.y);

            if (fallDistance > 0) {
                // check if we fell into at least 1 block of water
                if (/*this instanceof EntityLiving && */!(Multiworld.getBlock(this.x,this.y,this.z).isWater())) {
                    this.fall(fallDistance);
                }
                this.resetFallDistance();
            }
        }
    }
	
	public void fall(float fallDistance) {
        //checks for slow falling effect

        float damage = (float) Math.floor(fallDistance - 3);
        if (damage > 0) {
            this.damage((int) damage);
        }
        //farmland breaks
        /*if (fallDistance > 0.75) {
            Block down = Multiworld.getBlock(this.floor().down());

            if (down.getId() == Item.FARMLAND) {
                Event ev;

                if (this instanceof Player) {
                    ev = new PlayerInteractEvent((Player) this, null, down, null, Action.PHYSICAL);
                } else {
                    ev = new EntityInteractEvent(this, down);
                }

                this.server.getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return;
                }
                this.level.setBlock(down, Block.get(BlockID.DIRT), false, true);
            }
        }*/
    }
	
	public AABB getBoundingBox() {
		return this.boundingBox;
	}
	
	public float getHeight() {
        return 0;
    }

    public float getEyeHeight() {
        return this.getHeight() / 2 + 0.1f;
    }

    public float getWidth() {
        return 0;
    }

    public float getLength() {
        return 0;
    }

    protected double getStepHeight() {
        return 0;
    }

    public boolean canCollide() {
        return true;
    }

    protected float getGravity() {
        return 0;
    }

    protected float getDrag() {
        return 0;
    }

    protected float getBaseOffset() {
        return 0;
    }
    
    
    
    public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public double getMotionX() {
		return motionX;
	}

	public double getMotionY() {
		return motionY;
	}

	public double getMotionZ() {
		return motionZ;
	}

	public Vector3D getPos() {
    	return new Vector3D(this.x,this.y,this.z);
    }

	public boolean isOnGround() {
		return this.onGround;
	}
	
	public boolean canPassThrough() {
        return true;
    }
	
	protected void checkGroundState(double movX, double movY, double movZ, double dx, double dy, double dz) {
        this.isCollidedVertically = movY != dy;
        this.isCollidedHorizontally = (movX != dx || movZ != dz);
        this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
        this.onGround = (movY != dy && movY < 0);
    }
	
	public void kill() {
        this.hp = 0;
        this.close();
	}
	
	public boolean isInsideOfFire() {
        for (Block block : this.getCollisionBlocks()) {
            if (block.id == 143) {//fire id
                return true;
            }
        }

        return false;
    }
	
	public boolean isInsideOfWater() {
        double y = this.y + this.getEyeHeight();
        Block block = Multiworld.getBlock(new Vector3D(MathU.floorDouble(this.x), MathU.floorDouble(y), MathU.floorDouble(this.z)));

        if (block.isWater()) {
            double f = (block.pos.y + 1) - (block.getFluidHeightPercent() - 0.1111111);
            return y < f;
        }

        return false;
    }
	
	public boolean canCollideWith(DefaultEntity entity) {
        return this != entity;
    }
}
