package net.PRP.MCAI.bot.specific;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.Block;
import net.PRP.MCAI.data.Vector3D;
import net.PRP.MCAI.data.MinecraftData.Type;

public class Vision {
	public int width, height; // width-yaw-left/right height-pitch-up/down
	public double amplifer;
	public Bot client;

	public Vision(Bot client, int width, int height) {
		this.width = width;
		this.height = height;
		this.client = client;
		this.amplifer = 0.5;
	}
	
	public List<Block> getVisibleBlocks() {
		List<Block> blocks = new CopyOnWriteArrayList<>();
		for (int y = -width; y <= width; y++) {
			for (int p = -height; p <= height; p++) {
				Block a = getLookingBlock(96, y*amplifer, p*amplifer);
				if (a != null && !blocks.contains(a) && a.type != Type.VOID) blocks.add(a);
			}
		}
		return blocks;
	}
	
	public Block getLookingBlock(final int distance,double yaw, double pitch) {
        Vector3D loc = client.getEyeLocation();
        final Vector3D v = getDirection(yaw, pitch).normalize();
        for (int i = 1; i <= distance; i++) {
            loc = loc.add(v);
            Block b = loc.getBlock(client);
            if (b.type != Type.AIR) return b;
            else if (b.type != Type.AIR) return null;
        }
        return null;
    }
	
	public Vector3D getDirection(double yaw, double pitch) {
        Vector3D vector = Vector3D.ORIGIN;
        double rotX = yaw;
        double rotY = pitch;
        vector.setY(-Math.sin(Math.toRadians(rotY)));
        double xz = Math.cos(Math.toRadians(rotY));
        vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
        vector.setZ(xz * Math.cos(Math.toRadians(rotX)));
        return vector;
    }
	
	public Vector3D GetLookingBlockVector3D(double yaw, double pitch) {
		if (yaw > 180) {
			yaw = 360-yaw;
			yaw = -yaw;
		}
        double rotX = (Math.PI / 180) * yaw;
        double rotY = (Math.PI / 180) * pitch;
        double x = -Math.cos(rotY) * Math.sin(rotX);
        double y = -Math.sin(rotY);
        double z = Math.cos(rotY) * Math.cos(rotX);
        Vector3D vector = new Vector3D(x, y, z);
        int i = 0;
        while (true) {
        	i++;
            Vector3D newVector = vector.multiply(i);
            Vector3D blockVector3D = client.getEyeLocation().add(new Vector3D(newVector.x, newVector.y, newVector.z));
            blockVector3D.x = Math.floor(blockVector3D.x);
            blockVector3D.y = Math.floor(blockVector3D.y);
            blockVector3D.z = Math.floor(blockVector3D.z);
            Block b = blockVector3D.getBlock(client);
            if (b.type != Type.AIR) {
                return blockVector3D;
            }
        }
    }
}
