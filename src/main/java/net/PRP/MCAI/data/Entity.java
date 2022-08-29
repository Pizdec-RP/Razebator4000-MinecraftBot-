package net.PRP.MCAI.data;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;

public class Entity {
	public boolean onGround = true;
	public ItemStack mainhand = null;
	public ItemStack offhand = null;
	public int eid;
    public EntityType type;
    public Vector3D pos;
    public Vector3D vel;
    public float yaw;
    public float pitch;
    public UUID uuid;
    public boolean alive = true;

    public Entity(int entityID, UUID uuid, EntityType type, Vector3D position, float yaw, float pitch) {
        this.eid = entityID;
        this.type = type;
        this.pos = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.uuid = uuid;
        this.vel = new Vector3D(0,0,0);
    }

    
    
    @Override
    public String toString() {
    	return "id: "+eid+" uuid:"+uuid+" pos:"+pos.toStringInt();
    }



	public boolean isOnGround() {
		return onGround;
	}



	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}



	public ItemStack getMainhand() {
		return mainhand;
	}



	public void setMainhand(ItemStack mainhand) {
		this.mainhand = mainhand;
	}



	public ItemStack getOffhand() {
		return offhand;
	}



	public void setOffhand(ItemStack offhand) {
		this.offhand = offhand;
	}



	public int getEid() {
		return eid;
	}



	public void setEid(int eid) {
		this.eid = eid;
	}



	public EntityType getType() {
		return type;
	}



	public void setType(EntityType type) {
		this.type = type;
	}



	public Vector3D getPos() {
		return pos;
	}



	public void setPos(Vector3D pos) {
		this.pos = pos;
	}



	public Vector3D getVel() {
		return vel;
	}



	public void setVel(Vector3D vel) {
		this.vel = vel;
	}



	public float getYaw() {
		return yaw;
	}



	public void setYaw(float yaw) {
		this.yaw = yaw;
	}



	public float getPitch() {
		return pitch;
	}



	public void setPitch(float pitch) {
		this.pitch = pitch;
	}



	public UUID getUuid() {
		return uuid;
	}



	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
    
    
}
