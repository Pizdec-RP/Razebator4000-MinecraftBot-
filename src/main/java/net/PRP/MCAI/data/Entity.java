package net.PRP.MCAI.data;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;

public class Entity {
	public int EntityID;
    public EntityType type;
    public Vector3D Position;
    public float Yaw;
    public float Pitch;
    public UUID uuid;
    public boolean alive = true;

    public Entity(int entityID, UUID uuid, EntityType type, Vector3D position, float yaw, float pitch) {
        EntityID = entityID;
        this.type = type;
        this.Position = position;
        Yaw = yaw;
        Pitch = pitch;
        this.uuid = uuid;
    }

    public Entity() {
    }
}
