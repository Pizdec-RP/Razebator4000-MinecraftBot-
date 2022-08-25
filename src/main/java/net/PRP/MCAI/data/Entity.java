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
}
