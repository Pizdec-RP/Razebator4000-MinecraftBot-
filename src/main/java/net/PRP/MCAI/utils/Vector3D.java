package net.PRP.MCAI.utils;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

import net.PRP.MCAI.bot.Bot;
import world.Block;
import world.BlockType.Type;
import world.World;

public class Vector3D {

	public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

	public double x;
	public double y;
	public double z;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getZ() {
		return this.z;
	}
	
	public double getPosX() {
		return this.x;
	}

	public double getPosY() {
		return this.y;
	}

	public double getPosZ() {
		return this.z;
	}
	
	public double getBlockX() {
		return this.x;
	}
	
	public double getBlockY() {
		return this.y;
	}
	
	public double getBlockZ() {
		return this.z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public Vector3D VecToInt() {
		return new Vector3D((int)x,(int)y,(int)z);
	}
	
	@Deprecated
	public Vector3D(Position pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector3D)) {
			return false;
		}
		Vector3D vec = (Vector3D) obj;
		if (VectorUtils.equalsInt(vec, this)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Deprecated
	public Position translate() {
		return new Position((int)x,(int)y,(int)z);
	}

	public Vector3D add(Vector3D other) {
		if (other == null) throw new IllegalArgumentException("other cannot be NULL");
		return new Vector3D(x + other.x, y + other.y, z + other.z);
	}

	public Vector3D add(double x, double y, double z) {
		return new Vector3D(this.x + x, this.y + y, this.z + z);
	}

	public Vector3D subtract(Vector3D other) {
		if (other == null) throw new IllegalArgumentException("other cannot be NULL");
		return new Vector3D(x - other.x, y - other.y, z - other.z);
	}

	public Vector3D subtract(double x, double y, double z) {
		return new Vector3D(this.x - x, this.y - y, this.z - z);
	}

	public Vector3D multiply(int factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D multiply(double factor) {
		return new Vector3D(x * factor, y * factor, z * factor);
	}

	public Vector3D divide(int divisor) {
		if (divisor == 0) throw new IllegalArgumentException("Cannot divide by null.");
		return new Vector3D(x / divisor, y / divisor, z / divisor);
	}

	public Vector3D divide(double divisor) {
		if (divisor == 0) throw new IllegalArgumentException("Cannot divide by null.");
		return new Vector3D(x / divisor, y / divisor, z / divisor);
	}

	public Vector3D abs() {
		return new Vector3D(Math.abs(x), Math.abs(y), Math.abs(z));
	}
	
	public String toString() {
		return "x:"+x+" y:"+y+" z:"+z;
	}
	
	public String toStringInt() {
		return "x:"+(int)x+" y:"+(int)y+" z:"+(int)z;
	}
	
	public double distanceSq(double toX, double toY, double toZ) {
        double var7 = (double)this.getX() - toX;
        double var9 = (double)this.getY() - toY;
        double var11 = (double)this.getZ() - toZ;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }
	
	public double distanceSq(Vector3D to) {
        return this.distanceSq((double)to.getX(), (double)to.getY(), (double)to.getZ());
    }
	
	public Block getBlock(Bot client) {
		Block b = client.getWorld().getBlock(this);
		if (b == null) {
			b = new Block();
			b.id = 0;
			b.type = Type.UNKNOWN;
			b.pos = this;
		}
		return b;
	}
	
	public World getWorld(Bot client) {
		return client.getWorld();
	}
	
	public Vector3D clone() {
		return new Vector3D(x,y,z);
	}

}
