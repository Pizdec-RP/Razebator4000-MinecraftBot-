package net.PRP.MCAI.TestServer.entity;

import java.util.Map.Entry;
import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;

import net.PRP.MCAI.Main;
import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.ClientSession;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.VectorUtils;

public class EntityItem extends DefaultEntity {

    protected ItemStack item;

    protected int pickupDelay = 20;
    
    public EntityItem(ItemStack item, UUID uuid,int id, Vector3D pos) {
    	this(item, uuid, id, pos.x,pos.y,pos.z,0,0,0);
    }
    
    public EntityItem(ItemStack item, UUID uuid,int id, Vector3D pos, Vector3D vel) {
    	this(item, uuid, id, pos.x,pos.y,pos.z,vel.x,vel.y,vel.z);
    }

	public EntityItem(ItemStack item, UUID uuid,int id, double x, double y, double z, double mx, double my, double mz) {
		super(id, uuid, EntityType.ITEM, x, y, z, 5, null, 0, 0, mx, my, mz);
		this.item = item;
		float height = this.getHeight();
        double radius = this.getWidth() / 2d;
        AABB bb = new AABB(x - radius, y, z - radius, x + radius, y + height, z + radius);
        this.boundingBox = bb;
	}
	
	@Override
    public float getWidth() {
        return 0.25f;
    }

    @Override
    public float getLength() {
        return 0.25f;
    }

    @Override
    public float getHeight() {
        return 0.25f;
    }

    @Override
    public float getGravity() {
        return 0.04f;
    }

    @Override
    public float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.125f;
    }
    
    public ItemStack getItem() {
    	return this.item;
    }
    
    @Override
    public void onUpdate() {
        if (this.closed) {
            return;
        }

        if (this.age % 60 == 0 && this.onGround && item != null && this.isAlive()) {
            if (this.getItem().getAmount() < Main.getMCData().items.get(getItem().getId()).stackSize) {
                for (Entry<Integer, DefaultEntity> entity : Multiworld.Entities.entrySet()) {
                    if (entity.getValue() instanceof EntityItem && VectorUtils.sqrt(entity.getValue().getPos(), this.getPos()) <= 1) {
                        if (!entity.getValue().isAlive()) {
                            continue;
                        }
                        ItemStack closeItem = ((EntityItem) entity.getValue()).getItem();
                        if (closeItem.getId() != getItem().getId()) {
                            continue;
                        }
                        if (!entity.getValue().isOnGround()) {
                            continue;
                        }
                        int newAmount = this.getItem().getAmount() + closeItem.getAmount();
                        if (newAmount > Main.getMCData().items.get(this.getItem().getId()).stackSize) {
                            continue;
                        }
                        entity.getValue().close();
                        this.item = new ItemStack(getItem().getId(),newAmount,getItem().getNbt());
                    }
                }
            }
        }

        if (isInsideOfFire()) {
            this.kill();
        }

        if (this.isAlive()) {
            if (this.pickupDelay > 0 && this.pickupDelay < 32767) {
                this.pickupDelay--;
                if (this.pickupDelay < 0) {
                    this.pickupDelay = 0;
                }
            } else {
                for (ClientSession player : Server.players) {
                	if (VectorUtils.sqrt(player.pos, this.getPos()) <= 1) {
	                    if (player.pickupEntity(this)) {
	                        return;
	                    }
                	}
                }
            }

            if (Multiworld.getBlock((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z).id == 8 || Multiworld.getBlock((int) this.x, (int) this.boundingBox.getMaxY(), (int) this.z).id == 9) { //item is fully in water or in still water
                this.motionY -= this.getGravity() * -0.015;
            } else if (this.isInsideOfWater()) {
                this.motionY = this.getGravity() - 0.06; //item is going up in water, don't let it go back down too fast
            } else {
                this.motionY -= this.getGravity(); //item is not in water
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            double friction = 1 - this.getDrag();

            if (this.onGround && (Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionZ) > 0.00001)) {
                friction *= Multiworld.getBlock(new Vector3D((int) Math.floor(this.x), (int) Math.floor(this.y - 1), (int) Math.floor(this.z) - 1)).getfriction();
            }

            this.motionX *= friction;
            this.motionY *= 1 - this.getDrag();
            this.motionZ *= friction;

            if (this.onGround) {
                this.motionY *= -0.5;
            }

            this.updateMovement();

            if (this.age > 6000) {
                this.close();
            }
        }
        

        //return hasUpdate || !this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001;
    }

	public int getPickupDelay() {
		return this.pickupDelay;
	}
}
