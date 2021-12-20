package world;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.MobType;

import net.PRP.MCAI.utils.Vector3D;

public class Entity {
	public Vector3D pos;
	public UUID uuid;
	public int id;
	public MobType type;
	
	public String toString() {
		return pos.toString();
	}
}
