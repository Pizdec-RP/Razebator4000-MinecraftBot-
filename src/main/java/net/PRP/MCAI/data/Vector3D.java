package net.PRP.MCAI.data;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position;

import net.PRP.MCAI.bot.Bot;
import net.PRP.MCAI.data.MinecraftData.Type;
import net.PRP.MCAI.utils.NumberConversions;
import net.PRP.MCAI.utils.VectorUtils;

public class Vector3D {

	public static final Vector3D ORIGIN = new Vector3D(0, 0, 0);

	public double x;
	public double y;
	public double z;
	public int hasheddata = 0;

	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (int)x;
		hash = 31 * hash + (int)y;
		hash = 31 * hash + (int)z;
		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector3D)) {
			return false;
		}
		Vector3D co = (Vector3D) obj;
		return co.x == x && co.y == y && co.z == z;
	}
	
	public Vector3D down() {
		return new Vector3D(x,y-1,z);
	}
	
	public boolean IsOnGround(Bot client) {
		return VectorUtils.BTavoid(this.add(0,-1,0).getBlock(client).type);
	}
	
	
	/**
	 * возвратит true если блок под этой п0зицией идеально подходит для того чтобы стоять на нем
	 */
	public boolean isCanStayHere(Bot client) {
		return VectorUtils.icanstayhere(this.add(0,-1,0).getBlock(client).type);
	}
	
	public boolean isMineable(Bot client) {
		return getBlock(client).type != Type.UNBREAKABLE || !getBlock(client).isLiquid();
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
	
	public Vector3D floorXZ() {
		this.x = Math.floor(x);
		this.z = Math.floor(z);
		return this;
	}
	
	public Vector3D VecToInt() {
		return new Vector3D((int)x,(int)y,(int)z);
	}
	
	@Deprecated
	public Vector3D(Position pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
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
	
	public String forCommnad() {
		return (int)x+" "+(int)y+" "+(int)z;
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
	
	public Vector3D normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }
	
	public double length() {
        return Math.sqrt(NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z));
    }
	
	public World getWorld(Bot client) {
		return client.getWorld();
	}
	
	public Vector3D clone() {
		return new Vector3D(x,y,z);
	}
	
	/*public double heuristic(int x, int y, int z) {
        int xDiff = (int) (x - this.x);
        int yDiff = (int) (y - this.y);
        int zDiff = (int) (z - this.z);
        return calculate(xDiff, yDiff, zDiff);
    }
	
	public static double calculate(double xDiff, int yDiff, double zDiff) {
        double heuristic = 0;
        heuristic += gylcalculate(yDiff, 0);
        heuristic += xzcalculate(xDiff, zDiff);
        return heuristic;
    }*/
	
	public static double xzcalculate(double xDiff, double zDiff) {
        double x = Math.abs(xDiff);
        double z = Math.abs(zDiff);
        double straight;
        double diagonal;
        if (x < z) {
            straight = z - x;
            diagonal = x;
        } else {
            straight = x - z;
            diagonal = z;
        }
        diagonal *= Math.sqrt(2);
        return (diagonal + straight) * 3.5D;
    }
	
	/*public static double gylcalculate(int goalY, int currentY) {
        if (currentY > goalY) {
            return ActionCosts.FALL_N_BLOCKS_COST[2] / 2 * (currentY - goalY);
        }
        if (currentY < goalY) {
            return (goalY - currentY) * ActionCosts.JUMP_ONE_BLOCK_COST;
        }
        return 0;
    }*/

}
