package net.PRP.MCAI.TestServer.entity;

import java.util.UUID;

import com.github.steveice10.mc.protocol.data.game.entity.type.EntityType;
import com.github.steveice10.mc.protocol.data.game.world.particle.BlockParticleData;
import com.github.steveice10.mc.protocol.data.game.world.particle.Particle;
import com.github.steveice10.mc.protocol.data.game.world.particle.ParticleType;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;

import net.PRP.MCAI.Multiworld;
import net.PRP.MCAI.TestServer.ClientSession;
import net.PRP.MCAI.TestServer.Server;
import net.PRP.MCAI.data.AABB;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.utils.BotU;
import net.PRP.MCAI.utils.VectorUtils;

public class BlockEntity extends DefaultEntity {
	
	public boolean captured = false;
	public int blockState;

	public BlockEntity(int id, Vector3D pos, int blockState) {
		this(id,pos,blockState,new Vector3D(0,0,0));
	}
	
	public BlockEntity(int id, Vector3D pos, int blockState, Vector3D vel) {
		super(id, UUID.randomUUID(), EntityType.FALLING_BLOCK, pos.x, pos.y, pos.z, 1000, null, 0, 0, vel.x, vel.y, vel.z);
		this.blockState = blockState;
		float height = this.getHeight();
        double radius = this.getWidth() / 2d;
        AABB bb = new AABB(x - radius, y, z - radius, x + radius, y + height, z + radius);
        this.boundingBox = bb;
	}
	
	@Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    protected float getGravity() {
        return (captured?0f:0.04f);
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }
    
    @Override
    public void onUpdate() {
    	if (this.closed) return;
    	entityBaseTick();
        if (isAlive()) {
            motionY -= getGravity();
            move(motionX, motionY, motionZ);
            float friction = 1 - getDrag();
            motionX *= friction;
            motionY *= 1 - getDrag();
            motionZ *= friction;
            
            if (!captured && this.onGround) {
        		close();
        		BotU.log("closing");
        		Server.setBlock(this.getPos(), blockState);
        	}

            updateMovement();
            if (Server.tickCounter%3==0) Server.sendForEver(new ServerSpawnParticlePacket(new Particle(ParticleType.END_ROD,new BlockParticleData(1)),true,x,y,z,0f,0f,0f,0f,1));
        }
    }
}
